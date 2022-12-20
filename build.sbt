ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.2.1"

Runtime / unmanagedClasspath += baseDirectory.value / "src" / "main" / "resources"

lazy val root = (project in file(".")).settings(
  name := "json-template-qh2",
  libraryDependencies ++= Seq(
     "io.github.ollls" %% "quartz-h2" % "0.2.0",
     "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.19.1",
     "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.19.1" % "compile-internal"
  )
)
