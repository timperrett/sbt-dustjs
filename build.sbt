sbtPlugin := true

name := "sbt-dustjs"

organization := "eu.getintheloop"

version := "0.0.2-SNAPSHOT"

libraryDependencies += "rhino" % "js" % "1.7R2"

scalacOptions += "-deprecation"

// publishing
publishTo <<= version { (v: String) => 
  val nexus = "https://oss.sonatype.org/" 
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
  else Some("releases" at nexus + "service/local/staging/deploy/maven2") 
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { repo => false }

pomExtra := (
  <url>https://github.com/timperrett/sbt-dustjs</url>
  <licenses>
    <license>
      <name>Apache 2.0 License</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:timperrett/sbt-dustjs.git</url>
    <connection>scm:git@github.com:timperrett/sbt-dustjs.git</connection>
  </scm>
  <developers>
    <developer>
      <id>timperrett</id>
      <name>Timothy Perrett</name>
      <url>http://timperrett.com</url>
    </developer>
  </developers>)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

// sbt test framework
seq(ScriptedPlugin.scriptedSettings: _*)

// disable the buffer for scripted
// so that the output shows in the console
scriptedBufferLog := false