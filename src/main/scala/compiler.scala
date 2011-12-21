package dustjs

import org.mozilla.javascript.{Context, ContextFactory, Function, JavaScriptException, NativeObject}
import java.io.InputStreamReader
import java.nio.charset.Charset

object Compiler {
  val utf8 = Charset.forName("utf-8")
}

/**
 * Origionally a CoffeeScript compiller,
 * but Modified by timperrett to compile dust.js templates
 *
 * @author daggerrz
 * @author timperrett
 */
case class Compiler() {
  import Compiler._

  /**
   * Compiles a dust.js template to Javascript.
   *
   * @param template the raw dust.js template
   * @param registerAs the by-name variable to register in the runtime
   * @return either a compilation error description or the compiled Javascript code
   */
  def compile(template: String, registerAs: String): Either[String, String] = withContext { ctx =>
    val scope = ctx.initStandardObjects()
    ctx.evaluateReader(scope,
      new InputStreamReader(getClass.getResourceAsStream("/dust.js"), utf8), "dust.js", 1, null)

    val prototype = scope.get("dust", scope).asInstanceOf[NativeObject]
    val compileFunc = prototype.get("compile", scope).asInstanceOf[Function]
    val opts = ctx.evaluateString(scope, "'%s'".format(registerAs), null, 1, null)
    
    try {
      Right(compileFunc.call(ctx, scope, prototype, Array(template, opts)).asInstanceOf[String])
    } catch {
      case e: JavaScriptException => Left(e.getValue.toString)
    }
  }

  private def withContext[T](f: Context => T): T = {
    val ctx = new ContextFactory().enterContext()
    try {
      ctx.setOptimizationLevel(-1) // Do not compile to byte code (max 64kb methods)
      f(ctx)
    } finally {
      Context.exit()
    }
  }
}
