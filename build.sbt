name := "poc-kantan.csv"

version := "0.1"

scalaVersion := "2.13.3"

val kantanVersion = "0.6.1"
val scalaTestVersion = "3.2.0"

libraryDependencies ++= Seq(
  "com.nrinaudo" %% "kantan.csv" % kantanVersion,
  "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)
