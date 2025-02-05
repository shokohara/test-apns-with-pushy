ThisBuild / scalaVersion := "3.6.4-RC1"
ThisBuild / publish / skip := true
scalacOptions ++= Seq("-feature", "-deprecation")
libraryDependencies ++= Seq("com.eatthepath" % "pushy" % "0.15.0", "org.slf4j" % "slf4j-simple" % "2.0.16")
disablePlugins(AssemblyPlugin)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin) // https://github.com/sbt/sbt-assembly/issues/391#issuecomment-1930549029
