sbtPlugin := true

name := "sbt-dustjs"

organization := "eu.getintheloop"

version := "0.0.2-SNAPSHOT"

libraryDependencies += "rhino" % "js" % "1.7R2"

scalacOptions += "-deprecation"

// publishing
publishTo <<= version { (v: String) =>
  if(v endsWith "-SNAPSHOT") Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/")
  else Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
}

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