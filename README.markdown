SBT dust.js
===========

This plugin is a pre-compiler for the client-side templating system [dust.js](http://akdubya.github.com/dustjs/). The plugin builds your dust templates into javascript files that you can then include into your markup pages. For those who arn't familiar, dust.js recently achieved fame on the [linkedin engineering blog](http://engineering.linkedin.com/frontend/leaving-jsps-dust-moving-linkedin-dustjs-client-side-templates)

If you have a lot of templates, then you might want to considering merging them into a single file as an optimisation, but that is out of the scope of this plugin which is currently only concerned with template compilation. 

Usage
-----

Add the following to your project/plugins.sbt file:

<pre><code>
resolvers += Resolver.url("sbt-plugin-releases", 
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
    )(Resolver.ivyStylePatterns)

for scala 2.9.1 and sbt 0.11

addSbtPlugin("eu.getintheloop" % "sbt-dustjs" % "0.0.3")

for scala 2.9.2 and sbt 0.12, there is no published version, so you can build and publish locally

clone this project and type, use sbt publish-local to publish a local copy

addSbtPlugin("eu.getintheloop" %% "sbt-dustjs" % "0.0.4-SNAPSHOT")


</code></pre>

Then stuff this line onto the end of your build.sbt:

<pre><code>seq(dustSettings: _*)
</code></pre>

With the plugin added, you just need to place your dust templates in src/main/dust and then invoke <code>dust</code> from the SBT 11.x shell. Likewise, any call to <code>compile</code> should also trigger altered dust templates to be recompiled. 

If you want to publish the dust templates to somewhere other than the default managed resource file location, do something like this in your build.sbt file:

<pre><code>
(resourceManaged in (Compile, DustKeys.dust)) &lt;&lt;= (sourceDirectory in Compile){
    _ / &quot;resources&quot; / &quot;www&quot; / &quot;js&quot; / &quot;templates&quot;
}

</code></pre>


