
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")


addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)