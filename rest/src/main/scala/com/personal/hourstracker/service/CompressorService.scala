package com.personal.hourstracker.service

import java.io.File

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants

class CompressorService(implicit executionContext: ExecutionContext) {

  def zip(targetFiles: Seq[File], destinationFile: File, optionalPassword: Option[String] = None): Future[Try[ZipFile]] = {
    implicit val parameters: ZipParameters = getZipParameters(optionalPassword)

    Future(zipTarget(targetFiles, destinationFile))
  }

  private def getZipParameters(optionalPassword: Option[String]): ZipParameters = {
    val parameters = new ZipParameters
    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE)
    parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL)

    optionalPassword match {
      case None => ()
      case Some(password) =>
        parameters.setEncryptFiles(true)
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES)
        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256)
        parameters.setPassword(password)
    }

    parameters
  }

  def zipTargetFile(implicit parameters: ZipParameters): (ZipFile, File) => ZipFile = (zipFile, target) => {
    target match {
      case file if file.isFile =>
        zipFile.addFile(file, parameters)

      case file if file.isDirectory =>
        zipFile.addFolder(file, parameters)
    }

    zipFile
  }

  private def zipTarget(targetFiles: Seq[File], destinationFile: File)(implicit parameters: ZipParameters): Try[ZipFile] = {
    Try(
      targetFiles.foldLeft(new ZipFile(destinationFile))(zipTargetFile))
  }
}
