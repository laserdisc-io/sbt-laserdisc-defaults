package laserdisc.sbt.category

import laserdisc.sbt.{CompileTarget, DefaultsCategory, LoggerOps}
import sbt.*
import sbt.Keys.*

case class Compiler(failOnWarnKey: SettingKey[Boolean], compilerTargetKey: SettingKey[CompileTarget]) extends DefaultsCategory {

  private object ScalacFlags {
    def Common(failOnWarn: Boolean): Seq[String] = Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:existentials,experimental.macros,higherKinds,implicitConversions,postfixOps"
    ) ++
      (if (failOnWarn) Seq("-Xfatal-warnings") else Seq())

    // remove -Wconf:src filter should be available in scala 3.5.x
    lazy val Version3x: Seq[String] = Seq(
      "-Yretain-trees",
      "-Xmax-inlines:100",
      "-Ykind-projector:underscores",
      "-source:future",
      "-language:adhocExtensions",
      "-Wconf:msg=`= _` has been deprecated; use `= uninitialized` instead.:s"
    )

    lazy val Version2_13: Seq[String] = Seq(
      "-Xlint:-unused,_",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Xsource:3",
      "-Xlint:_,-byname-implicit", // enable handy linter warnings except byname-implicit (see https://github.com/scala/bug/issues/12072)
      "-P:kind-projector:underscore-placeholders",
      "-Xlint",
      "-Ywarn-macros:after",
      "-Wconf:src=src_managed/.*:silent"
    )

    val Test: Seq[String] = Seq(
      "-Wconf:msg=is not declared infix:s,msg=is declared 'open':s"
    )
  }

  private object DefaultDeps {

    lazy val All: Seq[ModuleID] = Seq("org.scala-lang.modules" %% "scala-collection-compat" % "2.11.0")

    lazy val Scala2_13: Seq[ModuleID] = Seq(
      compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.3").cross(CrossVersion.full)),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )

  }

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    scalacOptions ++= ScalacFlags.Common(failOnWarnKey.value),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _))                    => ScalacFlags.Version3x
        case Some((2, minor)) if minor >= 13 => ScalacFlags.Version2_13
        case _                               => Seq.empty
      }
    },
    Test / scalacOptions ++= ScalacFlags.Test,
    Test / scalacOptions           := (Compile / scalacOptions).value,
    Test / console / scalacOptions := (Compile / console / scalacOptions).value,
    libraryDependencies ++= DefaultDeps.All,
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => DefaultDeps.Scala2_13
        case _            => Seq()
      }
    }
  )

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      compilerTargetKey  := CompileTarget.Default,
      scalaVersion       := compilerTargetKey.value.defaultScalaVersion,
      crossScalaVersions := compilerTargetKey.value.crossVersions,
      failOnWarnKey      := getSystemPropBoolean(failOnWarnKey, default = true, logger.value),
      displayInfo()
    )

  private def displayInfo() =
    // this setting key doesn't do anything, but is simply a hook to display a message when SBT loads the project
    SettingKey[Unit]("laserdiscDefaultsCompileInfo") := {

      val crossScala = if (crossScalaVersions.value.size < 2) "no" else s"yes(${crossScalaVersions.value.mkString(",")})"

      val compilers = List(
        s"scalaDefault:${scalaVersion.value}",
        s"scalaCross:$crossScala",
        s"java:${java.lang.Runtime.version()}"
      ).mkString(" ")

      logger.value.pluginInfo(s"selected compilers - $compilers")
    }

}
