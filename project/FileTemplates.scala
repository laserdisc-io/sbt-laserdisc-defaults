import _root_.sbt.Keys._
import _root_.sbt.{Def, _}

object FileTemplates {

  val FileNames: List[String] = List(".scalafmt.conf", ".gitignore", "project/build.properties")

  /** In order for the plugin to be able to template these config files at runtime, they need to
    * be packaged with the plugin (so that they will be available in the classpath).
    *
    * We could just maintain a separate set of files in the resources directory, but that's
    * a maintenance overhead, and why risk somebody updating one, but not the other.. :)
    *
    * This function copies the files in use by this project into managed resources, so
    * they'll be bundled into the plugin zip distribution.
    */
  def copyToResources: Def.Initialize[Task[List[File]]] = Def.task {

    val DestDir: File = (Compile / resourceManaged).value / "templates"

    FileNames.map { name =>
      val src = (ThisBuild / baseDirectory).value / name

      if (!src.exists() || !src.isFile || !src.canRead) {
        throw new MessageOnlyException(s"[copyToResources] File not found, or is unreadable: ${src.getAbsolutePath}")
      }

      Keys.sLog.value.info(s"[copyToResources] copying $src to $DestDir")

      val dest = DestDir / name
      IO.copyFile(src, dest)

      dest
    }

  }

}
