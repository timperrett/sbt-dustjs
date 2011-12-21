sbtPlugin := true

name := "sbt-dustjs"

organization := "eu.getintheloop"

version := "0.0.1-SNAPSHOT"

libraryDependencies += "rhino" % "js" % "1.7R2"

scalacOptions += "-deprecation"

// sbt test framework
seq(ScriptedPlugin.scriptedSettings: _*)
