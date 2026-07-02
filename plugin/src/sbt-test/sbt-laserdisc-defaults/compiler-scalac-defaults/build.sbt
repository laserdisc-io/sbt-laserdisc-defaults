import com.github.sbt.git.DefaultReadableGit
import com.github.sbt.git.SbtGit.GitKeys.gitReader
import complete.DefaultParsers.*
import org.scalatest.matchers.should.Matchers.*
import laserdisc.sbt.CompileTarget.*
import java.nio.file.Files
import scala.jdk.CollectionConverters.*

ThisBuild / laserdiscRepoName := "sbt-laserdisc-defaults"

// during scripted tests, the root of the project will not be the root of the repo so the git plugin will fail - this works around it
val ProjectRoot = sys.props.getOrElse(
  "plugin.project.rootdir",
  sys.error("expected system property \"plugin.project.rootdir\" to be provided")
)
ThisProject / gitReader := new DefaultReadableGit(file(ProjectRoot), None)

lazy val root = (project in file("."))
  .enablePlugins(LaserDiscDefaultsPlugin)
  .settings(
    InputKey[Unit]("hasCompilerFlags") := {

      val expectedFailOnWarnFlag = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _))                     => "-Werror"
        case Some((2, minor)) if minor >= 13 => "-Werror"
        case _                                => "-Xfatal-warnings"
      }

      val expectedScala3KindProjectorFlag = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, minor)) if minor <= 3 => "-Ykind-projector:underscores"
        case _                              => "-Xkind-projector:underscores"
      }

      val commonExpected = List(
        "-encoding",
        "UTF-8",
        "-deprecation",
        "-unchecked",
        "-feature",
        "-language:existentials,experimental.macros,higherKinds,implicitConversions,postfixOps",
        expectedFailOnWarnFlag
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
              expectedScala3KindProjectorFlag,
              "-source:future",
              "-language:adhocExtensions",
              "-Wconf:msg=`= _` has been deprecated; use `= uninitialized` instead.:s"
            )
          )

        case _ => throw new MessageOnlyException("Invalid argument for 'hasCompilerFlags'")
      }
    },
    InputKey[Unit]("hasCompileOutput") := {

      val Scala2Class     = s"target/out/jvm/scala-${Scala2Version}/root/classes/foo/Test2Thing.class"
      val Scala3Class     = s"target/out/jvm/scala-${Scala3Version}/root/classes/foo/Test3Thing.class"
      val Scala3LTSClass  = s"target/out/jvm/scala-${Scala3LTSVersion}/root/classes/foo/Test3Thing.class"
      val Scala2Common    = s"target/out/jvm/scala-${Scala2Version}/root/classes/foo/TestCommonThing.class"
      val Scala3Common    = s"target/out/jvm/scala-${Scala3Version}/root/classes/foo/TestCommonThing.class"
      val Scala3LTSCommon = s"target/out/jvm/scala-${Scala3LTSVersion}/root/classes/foo/TestCommonThing.class"

      spaceDelimited("<arg>").parsed.headOption
        .getOrElse(throw new MessageOnlyException("Missing argument for 'hasCompileOutput'")) match {

        case "shouldOnlyCompileForScala2" =>
          checkFor(
            expected = List(Scala2Common, Scala2Class),
            notExpected = List(Scala3Common, Scala3LTSCommon, Scala3Class, Scala3LTSClass)
          )

        case "shouldOnlyCompileForScala3" =>
          checkFor(
            expected = List(Scala3Common, Scala3Class),
            notExpected = List(Scala2Common, Scala2Class, Scala3LTSClass, Scala3LTSCommon)
          )

        case "shouldOnlyCompileForScala3LTS" =>
          checkFor(
            expected = List(Scala3LTSCommon, Scala3LTSClass),
            notExpected = List(Scala2Common, Scala2Class, Scala3Common, Scala3Class)
          )

        case "shouldCrossCompileFor2And3" =>
          checkFor(
            expected = List(Scala2Common, Scala3Common, Scala2Class, Scala3Class),
            notExpected = List()
          )

        case "shouldCrossCompileFor2And3LTS" =>
          checkFor(
            expected = List(Scala2Common, Scala3LTSCommon, Scala2Class, Scala3LTSClass),
            notExpected = List()
          )

        case _ => throw new MessageOnlyException("Invalid argument for 'hasCompileOutput'")

      }

      def checkFor(expected: List[String], notExpected: List[String]): Unit = {

        val foundClassFiles = Files
          .walk(file("target").toPath)
          .iterator()
          .asScala
          .filter(Files.isRegularFile(_))
          .map(_.toString)
          .filter(_.endsWith("class"))
          .toList

        val found = "\n" + foundClassFiles.map(s => s"found - ${s}").mkString("\n")

        expected.foreach { filename =>
          if (!foundClassFiles.exists(_ == filename))
            throw new MessageOnlyException(s"Could not find expected classfile '$filename' in compiled output:$found")
        }

        notExpected.foreach { filename =>
          if (foundClassFiles.exists(_ == filename))
            throw new MessageOnlyException(s"Did not expect to find classfile '$filename' in compiled output:$found")
        }
      }
    }
  )
