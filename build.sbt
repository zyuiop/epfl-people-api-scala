name := "epfl-people-api-scala"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.mavenCentral



libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % Test