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
  
  /** compile task **/
  
  private def toOutputPath(fromDir: File, template: File, targetDir: File) = 
    Some(new File(targetDir, IO.relativize(fromDir, template).get.replace(".dust",".js")))
  
  private def compile(dust: File, js: File, log: Logger) = 
    try {
      Compiler().compile(io.Source.fromFile(dust).mkString, js.name.replace(".js","")).fold(
        error => sys.error(error),
        compiled => {
          IO.write(js, compiled)
          log.debug("Wrote to file %s" format js)
          js
      })
    } catch {
      case e: Exception => 
        sys.error("Unexpected error whilst compilling dust template")
    }
    
  private def compiledTo(location: File) = (location ** "*.js").get
  
  def dustCompileTask = (streams, sourceDirectory in dust, resourceManaged in dust, 
    include in dust, exclude in dust) map { 
      (out, sourceDir, targetDir, incl, excl) => 
        (for {
          templatePath <- sourceDir.descendentsExcept(incl, excl).get
          jsPath <- toOutputPath(sourceDir, templatePath, targetDir) 
        } yield (templatePath, jsPath)) match {
          case Nil => 
            out.log.info("No dust templates to compile")
            compiledTo(targetDir)
          case files => 
            files.map { case (template, output) => 
              compile(template, output, out.log)
            }
            compiledTo(targetDir)
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
    dust <<= dustCompileTask
  )
  
  val dustSettings: Seq[Setting[_]] =
    dustSettingsIn(Compile) ++ dustSettingsIn(Test)
}