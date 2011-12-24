package dustjs

import sbt._, Keys._

object DustJsPlugin extends sbt.Plugin {
  object DustKeys {
    // tasks
    val dust = TaskKey[Seq[File]]("dust", "Compile dust.js templates")
    // settings
    val include = SettingKey[FileFilter]("include-filter", "Filter for selecting dust sources from default directories.")
    val exclude = SettingKey[FileFilter]("exclude-filter", "Filter for excluding files from default directories.")
  }
  import DustKeys._
  
  private def dustSourcesTask =
    (sourceDirectory in dust, include in dust, exclude in dust) map {
      (sourceDir, incl, excl) =>
        sourceDir.descendentsExcept(incl, excl).get
    }
  
  private def dustCleanTask = (streams, resourceManaged in dust) map {
    (out, target) =>
      out.log.info("Cleaning generated JavaScript under " + target)
      IO.delete(target)
    }
  
  /** compile task **/
  
  private def toOutputPath(fromDir: File, template: File, targetDir: File) = 
    Some(new File(targetDir, IO.relativize(fromDir, template).get.replace(".dust",".js")))
  
  private def compile(input: File, output: File, log: Logger) = {
    // IO.delete(output)
    try {
      Compiler().compile(io.Source.fromFile(input).mkString, output.name.replace(".js","")).fold(
        error => sys.error(error),
        compiled => {
          IO.write(output, compiled)
          log.debug("Wrote to file %s" format output)
          output
      })
    } catch {
      case e: Exception => 
        sys.error("Unexpected error whilst compilling dust template")
    }
  }
    
  def compileChanged(sources: File, target: File, cache: File,
    incl: FileFilter, excl: FileFilter, log: Logger) =
    (for {
      template <- sources.descendentsExcept(incl, excl).get
      output <- toOutputPath(sources, template, target) if (template newerThan output)
    } yield (template, output)) match {
        case Nil =>
          log.debug("No dust.js templates to compile")
          compiled(target)
        case xs =>
          log.info("Compiling %d dust.js templates to %s" format(xs.size, target))
          xs map { case (in,out) => 
            compile(in,out,log)
          }
          compiled(target)
      }
    // not sure why this doesnt work... need to find out.
    // FileFunction.cached(cache / "dust", FilesInfo.hash){ input =>
    //   (for {
    //     template <- input.descendentsExcept(incl, excl).get
    //     output <- toOutputPath(sources, template, target)
    //   } yield compile(template, output, log)).toSet
    // }
  
  def compiled(location: File) = (location ** "*.js").get
  
  def dustCompileTask = (streams, sourceDirectory in dust, resourceManaged in dust,
    include in dust, exclude in dust, cacheDirectory in dust) map {
      (out, sources, targetDir, incl, excl, cache) => {
        out.log.info("Compiling dust templates")
        compileChanged(sources, targetDir, cache, incl, excl, out.log)
      }
    }
  
  /** Settings Delivery **/
  
  def dustSettingsIn(configuration: Configuration): Seq[Setting[_]] =
    inConfig(configuration)(dustjsSettings0 ++ Seq(
      sourceDirectory in dust <<= (sourceDirectory in configuration) { _ / "dust" },
      resourceManaged in dust <<= (resourceManaged in configuration) { _ / "js" },
      cleanFiles in dust <<= (resourceManaged in dust)(_ :: Nil),
      watchSources in dust <<= (unmanagedSources in dust)
    ))
  
  def dustjsSettings0: Seq[Setting[_]] = Seq(
    include in dust := "*.dust",
    exclude in Global := (".*" - ".") || HiddenFileFilter,
    unmanagedSources in dust <<= dustSourcesTask,
    clean in dust <<= dustCleanTask,
    dust <<= dustCompileTask
  )
  
  val dustSettings: Seq[Setting[_]] =
    dustSettingsIn(Compile) ++ dustSettingsIn(Test)
}