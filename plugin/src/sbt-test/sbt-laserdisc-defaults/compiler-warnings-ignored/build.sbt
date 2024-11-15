
ThisBuild / laserdiscRepoName := "sbt-laserdisc-defaults"
ThisBuild / laserdiscFailOnWarn := false

lazy val root = (project in file("."))
  .enablePlugins(LaserDiscDefaultsPlugin)

