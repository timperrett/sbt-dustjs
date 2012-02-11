SBT dust.js
===========

This plugin is a pre-compiler for the client-side templating system [dust.js](http://akdubya.github.com/dustjs/). The plugin builds your dust templates into javascript files that you can then include into your markup pages. For those who arn't familiar, dust.js recently achieved fame on the [linkedin engineering blog](http://engineering.linkedin.com/frontend/leaving-jsps-dust-moving-linkedin-dustjs-client-side-templates)

If you have a lot of templates, then you might want to considering merging them into a single file as an optimisation, but that is out of the scope of this plugin which is currently only concerned with template compilation. 

Usage
-----

The plugin is currently unpublished and very alpha. With that in mind you need to build from source... like a boss! Once you've done this and <code>publish-local</code>'d you can add it to a project in project/plugins.sbt like so:

<pre><code>
resolvers += Resolver.url("sbt-plugin-releases", 
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
    )(Resolver.ivyStylePatterns)

addSbtPlugin("eu.getintheloop" % "sbt-dustjs" % "0.0.3")

</code></pre>

With the plugin added, you just need to place your dust templates in src/main/dust and then invoke <code>dust</code> from the SBT 11.x shell.


