package laserdisc.sbt.category

import laserdisc.sbt.CompileTarget.Scala3Only
import laserdisc.sbt.{CompileTarget, DefaultsCategory, LoggerOps, PluginContext}
import sbt.*
import sbt.Keys.*

object Compiler {
  private val FailOnWarnDefault: Boolean          = true
  private val CompileTargetDefault: CompileTarget = Scala3Only

  val FailOnWarnKeyDesc: String    = s"Fail the build if any warnings are present (default: $FailOnWarnDefault)"
  val CompileTargetKeyDesc: String = s"'Scala2Only', 'Scala3Only', or 'Scala2And3' to cross-compile (default:$CompileTargetDefault)"
}

/** Applies scala compiler settings, including scala version and compiler flags
  * @param failOnWarnKey If this key is true, the compiler will emit warnings as errors, failing the build
  * @param compilerTargetKey This value controls which scala version (and associated compiler flags) will be set
  */
case class Compiler(
    failOnWarnKey: SettingKey[Boolean],
    compilerTargetKey: SettingKey[CompileTarget]
)(implicit val ctx: PluginContext)
    extends DefaultsCategory {

  import laserdisc.sbt.category.Compiler.*

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
      "-Wconf:src=src_managed/.*:silent",
      // 2.13.17 brings in a bug - https://github.com/scala/bug/issues/13128
      "-Wconf:cat=lint-infer-any&msg=kind-polymorphic:s"
    )

    val Test: Seq[String] = Seq(
      "-Wconf:msg=is not declared infix:s,msg=is declared 'open':s"
    )
  }

  private object DefaultDeps {

    lazy val All: Seq[ModuleID] = Seq("org.scala-lang.modules" %% "scala-collection-compat" % "2.14.0")

    lazy val Scala2_13: Seq[ModuleID] = Seq(
      compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.4").cross(CrossVersion.full)),
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
      compilerTargetKey  := CompileTargetDefault,
      scalaVersion       := compilerTargetKey.value.defaultScalaVersion,
      crossScalaVersions := compilerTargetKey.value.crossVersions,
      failOnWarnKey      := getSystemPropBoolean(failOnWarnKey, default = FailOnWarnDefault, logger.value),
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
