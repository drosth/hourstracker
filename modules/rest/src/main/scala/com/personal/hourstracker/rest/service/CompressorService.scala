package com.personal.hourstracker.rest.service

import java.io.File

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CompressorService(implicit executionContext: ExecutionContext) {

  def zip(targetFiles: Seq[File], destinationFile: File, optionalPassword: Option[String] = None): Future[Try[ZipFile]] = {
    implicit val parameters: ZipParameters = optionalPassword match {
      case Some(_) =>
        val parameters = new ZipParameters
        parameters.setEncryptFiles(true)
        parameters.setEncryptionMethod(EncryptionMethod.AES)
        parameters

      case None => new ZipParameters
    }

    Future(zipTarget(targetFiles, destinationFile, optionalPassword))
  }

  private def zipTarget(targetFiles: Seq[File], destinationFile: File, optionalPassword: Option[String] = None)(
    implicit parameters: ZipParameters
  ): Try[ZipFile] = optionalPassword match {
    case None =>
      Try(targetFiles.foldLeft(new ZipFile(destinationFile))(zipTargetFile))

    case Some(password) =>
      Try(targetFiles.foldLeft(new ZipFile(destinationFile, password.toCharArray))(zipTargetFile))
  }

  private def zipTargetFile(implicit parameters: ZipParameters): (ZipFile, File) => ZipFile = (zipFile, target) => {
    target match {
      case file if file.isFile =>
        zipFile.addFile(file, parameters)

      case file if file.isDirectory =>
        zipFile.addFolder(file, parameters)
    }

    zipFile
  }
}
