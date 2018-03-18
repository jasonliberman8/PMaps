name := "PMaps"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "org.typelevel" %% "spire" % "0.14.1"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.20"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.2"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0"



