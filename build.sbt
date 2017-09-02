name := """rent-app"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" %  "3.0.0-M5"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-M5"
libraryDependencies += "com.h2database" % "h2" % "1.4.194"
libraryDependencies += specs2 % Test
libraryDependencies += evolutions
libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.6.0",
  "org.webjars" % "bootstrap" % "3.1.1-2"
)

libraryDependencies += specs2 % Test
  

