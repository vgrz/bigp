lazy val root = (project in file(".")).
  settings(
    name := "ex",
    version := "1.0",
    scalaVersion := "2.11.8"
  )

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "org.spire-math" %% "jawn-ast" % "0.8.4"

libraryDependencies += "net.databinder" %% "unfiltered-filter" % "0.8.4"

libraryDependencies += "net.databinder" %% "unfiltered-jetty" % "0.8.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

scalacOptions ++= Seq("-unchecked", "-deprecation")
