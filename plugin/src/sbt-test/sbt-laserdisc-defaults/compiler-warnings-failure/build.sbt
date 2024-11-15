
ThisBuild / laserdiscRepoName := "sbt-laserdisc-defaults"

lazy val root = (project in file("."))
  .enablePlugins(LaserDiscDefaultsPlugin)

