ThisBuild / organization     := "io.laserdisc"
ThisBuild / organizationName := "LaserDisc"

// these are plugins that are needed by the projects that use _this_ plugin (see `LaserDiscDefaultsPlugin.requires`)
addSbtPlugin("org.scalameta"                     % "sbt-scalafmt"        % "2.5.2")
addSbtPlugin("com.github.sbt"                    % "sbt-git"             % "2.1.0")
addSbtPlugin("com.github.sbt"                    % "sbt-native-packager" % "1.10.4")
addSbtPlugin("com.thoughtworks.sbt-api-mappings" % "sbt-api-mappings"    % "3.0.2")

lazy val root = project
  .in(file("."))
  .settings(
    version      := "BARRYTEST-SNAPSHOT",
    scalaVersion := "2.12.20",
    sbtPlugin    := true,
    name         := "sbt-laserdisc-defaults",
    moduleName   := "sbt-laserdisc-defaults",
    description  := "SBT defaults for LaserDisc projects",
    compileSettings,
    testSettings,
    publishSettings,
    dependencies,
    addCommandAlias("format", ";scalafmtAll;scalafmtSbt"),
    addCommandAlias("checkFormat", ";scalafmtCheckAll;scalafmtSbtCheck"),
    addCommandAlias("build", ";checkFormat;clean;scripted"), // note: `scripted` to invoke plugin tests
    addCommandAlias("release", ";build;publish")
  )
  .enablePlugins(SbtPlugin, JavaAppPackaging, ScalafmtPlugin, BuildInfoPlugin) // TODO: restore

def compileSettings = Seq(
  Compile / resourceGenerators += FileTemplates.copyToResources,
  buildInfoObject  := "PluginInfo",
  buildInfoPackage := "laserdisc.sbt.defaults",
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",                         // source files are in UTF-8
    "-deprecation",                  // warn about use of deprecated APIs
    "-unchecked",                    // warn about unchecked type parameters
    "-feature",                      // warn about misused language features
    "-language:higherKinds",         // allow higher kinded types without `import scala.language.higherKinds`
    "-language:implicitConversions", // allow use of implicit conversions
    "-language:postfixOps",          // enable postfix ops
    "-Xlint:_,",                     // enable handy linter warnings
    "-Ywarn-macros:after",           // allows the compiler to resolve implicit imports being flagged as unused
    "-Xfatal-warnings"               // fail the build if there are warnings
  )
)

def testSettings = Seq(
  scriptedLaunchOpts ++=
    Seq(
      "-Xmx1024M",
      "--add-opens=java.base/java.util=ALL-UNNAMED", // to support sbt-dotenv
      "--add-opens=java.base/java.lang=ALL-UNNAMED", // to support sbt-dotenv
      s"-Dplugin.version=${version.value}",
      s"-Dplugin.project.rootdir=${(ThisBuild / baseDirectory).value.absolutePath}",
      s"-Dsbt.boot.directory=${file(sys.props("user.home")) / ".sbt" / "boot"}" // https://github.com/sbt/sbt/issues/3469
    ),
  scriptedBufferLog := false // set to true to suppress detailed scripted test output
)

lazy val publishSettings = Seq(
  Test / publishArtifact := false,
  pomIncludeRepository   := (_ => false),
  organization           := "io.laserdisc",
  homepage               := Some(url("http://laserdisc.io/sbt-laserdisc-defaults")),
  developers             := List(Developer("barryoneill", "Barry O'Neill", "", url("https://github.com/barryoneill"))),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/laserdisc-io/sbt-laserdisc-defaults/tree/master"),
      "scm:git:git@github.com:laserdisc-io/sbt-laserdisc-defaults.git"
    )
  ),
  licenses := Seq("MIT" -> url("https://raw.githubusercontent.com/laserdisc-io/sbt-laserdisc-defaults/master/LICENSE"))
)

// local dependencies for this plugin
def dependencies = libraryDependencies ++= Seq(
  "org.apache.maven" % "maven-artifact" % "3.9.9"
)
