import org.scalatest.matchers.should.Matchers._

ThisBuild / laserdiscRepoName := "sbt-laserdisc-defaults"

lazy val root = (project in file("."))
  .enablePlugins(LaserDiscDefaultsPlugin)
  .settings(
    TaskKey[Unit]("checkCoreSettingsActive") := {

      organization.value should equal("io.laserdisc")
      organizationName.value should equal("LaserDisc")

    }
  )
