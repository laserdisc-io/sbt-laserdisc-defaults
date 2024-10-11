package laserdisc.sbt

import _root_.sbt.*

import java.io.{FileNotFoundException, InputStream}
import java.nio.charset.StandardCharsets.UTF_8
import scala.io.Source
import scala.util.{Failure, Success, Try}

package object io {

  def readFile(log: Logger, file: File): Either[Throwable, String] = {
    log.pluginDebug(s"Attempting to read ${file.getAbsolutePath}")
    Try(IO.read(file, UTF_8)).toEither
  }

  def writeFile(log: Logger, outputFile: File, content: String): Unit = {
    log.pluginDebug(s"Attempting to write to ${outputFile.getAbsolutePath}")
    Try(IO.write(outputFile, content, UTF_8, append = false)) match {
      case Success(_) => ()
      case Failure(e) => fail(s"Failed to write content to $outputFile", e)
    }
  }

  def readResourceLines(log: Logger, resource: String): List[String] =
    Try {
      log.pluginDebug(s"Attempting to load resource '$resource'")
      Source.fromResource(resource, getClass.getClassLoader).getLines().toList
    } match {
      case Success(value) => value
      case Failure(e)     => fail(s"Failed to load resource $resource", e)
    }

  def readResourceStream(log: Logger, resource: String): InputStream = {
    log.pluginDebug(s"Attempting to load resource stream '$resource'")
    val res = getClass.getClassLoader.getResourceAsStream(resource)
    if (res == null) {
      throw new IllegalArgumentException(s"Resource '$resource' not found")
    }
    res
  }

  def verifyNonEmptyFileExists(log: Logger, file: File, isEmptyLine: String => Boolean, failureHelp: String): Unit = {
    log.pluginDebug(s"Attempting to read file: ${file.getAbsolutePath}")
    Try(IO.readLines(file, UTF_8)) match {
      case Success(value) =>
        if (value.count(l => !isEmptyLine(l)) < 1) {
          fail(failureHelp, new IllegalStateException(s"File '${file.getAbsolutePath}' exists, but is blank"))
        }
      case Failure(e) =>
        fail(failureHelp, new FileNotFoundException(s"Got ${e.getMessage} while trying to load '${file.getAbsolutePath}'"))
    }
  }
}
