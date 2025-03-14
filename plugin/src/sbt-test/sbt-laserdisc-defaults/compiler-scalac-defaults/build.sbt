import complete.DefaultParsers.*
import org.scalatest.matchers.should.Matchers.*

import java.nio.file.Files
import scala.collection.JavaConverters.*
import com.github.sbt.git.SbtGit.GitKeys.gitReader
import com.github.sbt.git.DefaultReadableGit

ThisBuild / laserdiscRepoName := "sbt-laserdisc-defaults"

// during scripted tests, the root of the project will not be the root of the repo so the git plugin will fail - this works around it
val ProjectRoot = sys.props.getOrElse("plugin.project.rootdir", sys.error("expected system property \"plugin.project.rootdir\" to be provided"))
ThisProject / gitReader :=  new DefaultReadableGit(file(ProjectRoot),None)

lazy val root = (project in file("."))
  .enablePlugins(LaserDiscDefaultsPlugin)
  .settings(

    InputKey[Unit]("hasCompilerFlags") := {

      val commonExpected = List(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-unchecked",
        "-feature",
        "-language:existentials,experimental.macros,higherKinds,implicitConversions,postfixOps",
        "-Xfatal-warnings"
      )

      spaceDelimited("<arg>").parsed.headOption
        .getOrElse(throw new MessageOnlyException("Missing argument for 'hasCompilerFlags'")) match {

        case "2" =>
          scalacOptions.value should equal(
            commonExpected ++ List(
              "-Xlint:-unused,_",
              "-Ywarn-numeric-widen",
              "-Ywarn-value-discard",
              "-Ywarn-unused:implicits",
              "-Ywarn-unused:imports",
              "-Xsource:3",
              "-Xlint:_,-byname-implicit",
              "-P:kind-projector:underscore-placeholders",
              "-Xlint",
              "-Ywarn-macros:after",
              "-Wconf:src=src_managed/.*:silent"
            )
          )

        case "3" =>
          scalacOptions.value should equal(
            commonExpected ++ List(
              "-Yretain-trees",
              "-Xmax-inlines:100",
              "-Ykind-projector:underscores",
              "-source:future",
              "-language:adhocExtensions",
              "-Wconf:msg=`= _` has been deprecated; use `= uninitialized` instead.:s"
            )
          )

        case _ => throw new MessageOnlyException("Invalid argument for 'hasCompilerFlags'")
      }
    },
    InputKey[Unit]("hasCompileOutput") := {

      val Scala2Class  = "target/scala-2.13/classes/foo/Test2Thing.class"
      val Scala3Class  = "target/scala-3.3.5/classes/foo/Test3Thing.class"
      val Scala2Common = "target/scala-2.13/classes/foo/TestCommonThing.class"
      val Scala3Common = "target/scala-3.3.5/classes/foo/TestCommonThing.class"

      spaceDelimited("<arg>").parsed.headOption
        .getOrElse(throw new MessageOnlyException("Missing argument for 'hasCompileOutput'")) match {

        case "shouldOnlyCompileForScala2" =>
          checkFor(
            expected = List(Scala2Common, Scala2Class),
            notExpected = List(Scala3Common, Scala3Class)
          )

        case "shouldOnlyCompileForScala3" =>
          checkFor(
            expected = List(Scala3Common, Scala3Class),
            notExpected = List(Scala2Common, Scala2Class)
          )

        case "shouldCrossCompileFor2And3" =>
          checkFor(
            expected = List(Scala2Common, Scala3Common, Scala2Class, Scala3Class),
            notExpected = List()
          )

        case _ => throw new MessageOnlyException("Invalid argument for 'hasCompileOutput'")

      }

      def checkFor(expected: List[String], notExpected: List[String]): Unit = {

        val allClassFiles = Files
          .walk(file("target").toPath)
          .iterator()
          .asScala
          .filter(Files.isRegularFile(_))
          .map(_.toString)
          .filter(_.endsWith("class"))
          .toList

        val found = "\n" + allClassFiles.map(s => s"found - ${s}").mkString("\n")

        for (f <- expected if !allClassFiles.contains(f))
          throw new MessageOnlyException(s"Could not find expected classfile '$f' in compiled output:$found")

        for (f <- notExpected if allClassFiles.contains(f))
          throw new MessageOnlyException(s"Did not expect to see '$f' in compiled output:$found")

      }
    }
  )
