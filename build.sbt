ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.2.2"

Runtime / unmanagedClasspath += baseDirectory.value / "src" / "main" / "resources"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _                                   => MergeStrategy.first
}

assembly / assemblyJarName := "qh2-http-run.jar"

lazy val root = (project in file(".")).settings(
  name := "json-template-qh2",
  libraryDependencies ++= Seq(
    "io.github.ollls" %% "quartz-h2" % "0.5.1",
    "org.typelevel" %% "cats-effect" % "3.4.7",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.19.1",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.19.1" % "compile-internal"
  )
)
