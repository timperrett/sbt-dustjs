sbtPlugin := true

name := "sbt-dustjs"

organization := "eu.getintheloop"

version := "0.0.3"

libraryDependencies += "rhino" % "js" % "1.7R2"

scalacOptions += "-deprecation"

// publishing
publishTo := Some(Resolver.url("sbt-plugin-releases", 
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
    )(Resolver.ivyStylePatterns))

publishMavenStyle := false

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials.typesafe")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

// sbt test framework
seq(ScriptedPlugin.scriptedSettings: _*)

// disable the buffer for scripted
// so that the output shows in the console
scriptedBufferLog := false