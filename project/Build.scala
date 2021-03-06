import java.nio.file.{StandardCopyOption, Files}

import sbt._
import Keys._

import com.typesafe.sbt.SbtScalariform._
import _root_.scalariform.formatter.preferences.IndentSpaces
import scalariform.formatter.preferences._

object Build extends Build {


  // Project information
  val ORGANIZATION = "io.shinto"
  val PROJECT_NAME = "amaterasu"
  val PROJECT_VERSION = "0.1.0"
  val SCALA_VERSION = "2.10.5"

  // Mesos native library path
  val pathToMesosLibs = "/usr/local/lib"

  lazy val root = Project(
    id = PROJECT_NAME,
    base = file("."),
    settings = commonSettings
  )

  lazy val commonSettings =
    basicSettings ++
      formatSettings

  lazy val copyScripts = TaskKey[Unit]("copyScripts")

  lazy val basicSettings = Seq(
    version := PROJECT_VERSION,
    organization := ORGANIZATION,
    scalaVersion := SCALA_VERSION,
    mainClass := Some("io.shinto.amaterasu.mesos.executors.ActionsExecutorLauncher"),

    copyScripts <<= (baseDirectory, target) map {
      (base, target) =>
        val file = new File(base, "src/main/scripts").listFiles().foreach(
          file => Files.copy(
            file.toPath,
            new File(target + "/scala-2.10", file.name).toPath,
            StandardCopyOption.REPLACE_EXISTING)
        )
    },


    libraryDependencies ++= Seq(

      //"org.apache.mesos" % "mesos" % "0.28.2",
      "com.typesafe" % "config" % "1.2.1",
      "org.slf4j" % "slf4j-api" % "1.7.9",
      "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
      "com.github.nscala-time" %% "nscala-time" % "2.2.0",
      "commons-io" % "commons-io" % "2.4",
      "org.apache.curator" % "curator-framework" % "2.9.1" exclude("io.netty", "netty") ,
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.2.0.201601211800-r",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.10.27",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.6.4",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.4",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.4",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.6.4",
      "com.github.scopt" %% "scopt" % "3.3.0",
      "com.jcabi" % "jcabi-aether" % "0.10.1",
      "org.apache.maven" % "maven-core" % "3.0.5",
      "org.eclipse.jetty" % "jetty-plus" % "9.2.19.v20160908",
      "org.eclipse.jetty" % "jetty-server" % "9.2.19.v20160908",
      "org.eclipse.jetty" % "jetty-http" % "9.2.19.v20160908",
      "org.eclipse.jetty" % "jetty-io" % "9.2.19.v20160908",
      "org.eclipse.jetty" % "jetty-servlet" % "9.2.19.v20160908",
      "javax.servlet" % "servlet-api" % "2.5",


      // execution engines dependencies
      "org.apache.spark" %% "spark-repl" % "1.6.3" % "provided",// exclude("io.netty", "netty"),

      "net.sf.py4j" % "py4j" % "0.9",

      // test dependencies
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.apache.curator" % "curator-test" % "2.9.1" % "test" exclude("io.netty", "netty") ,

      "org.apache.mesos" % "mesos" % "0.21.1" classifier "shaded-protobuf" exclude("com.google.protobuf", "protobuf-java")
    ),


    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),

    javaOptions in (test) += "-Djava.library.path=%s:%s".format(
      sys.props("java.library.path"),
      pathToMesosLibs
    ),

    fork in run := true,

    fork in Test := true,

    parallelExecution in Test := false
  )

  lazy val formatSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(IndentWithTabs, false)
      .setPreference(IndentSpaces, 2)
      .setPreference(AlignParameters, false)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(CompactControlReadability, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(FormatXml, true)
  )

  object Plugins extends Build {
    lazy val root = Project("root", file(".")) dependsOn
      uri("git://github.com/sbt/sbt-assembly.git#0.14.0")
  }

  parallelExecution in Test := false

}