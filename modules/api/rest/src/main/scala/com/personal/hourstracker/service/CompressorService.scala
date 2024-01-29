package com.personal.hourstracker.service

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.{AesKeyStrength, CompressionLevel, CompressionMethod, EncryptionMethod}

import java.io.File
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CompressorService(implicit executionContext: ExecutionContext) {

  def zip(targetFiles: Seq[File], destinationFile: File, optionalPassword: Option[String] = None): Future[Try[ZipFile]] = {
    Future(zipTarget(targetFiles, destinationFile, optionalPassword)(getZipParameters))
  }

  private def getZipParameters(optionalPassword: Option[String]): ZipParameters = {
    val parameters = new ZipParameters
    parameters.setCompressionMethod(CompressionMethod.DEFLATE)
    parameters.setCompressionLevel(CompressionLevel.NORMAL)

    optionalPassword match {
      case None => ()
      case Some(password) =>
        parameters.setEncryptFiles(true)
        parameters.setEncryptionMethod(EncryptionMethod.AES)
        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256)
    }

    parameters
  }

  private def zipTarget(targetFiles: Seq[File], destinationFile: File, optionalPassword: Option[String])(
    fn: Option[String] => ZipParameters
  ): Try[ZipFile] = {
    val zipParameters = fn(optionalPassword)
    val zipFile = optionalPassword match {
      case Some(password) => new ZipFile(destinationFile, password.toCharArray)
      case None           => new ZipFile(destinationFile)
    }
    Try(
      targetFiles.foldLeft(zipFile)(zipTargetFile(zipParameters))
    )
  }

  private def zipTargetFile(parameters: ZipParameters): (ZipFile, File) => ZipFile = (zipFile, target) => {
    target match {
      case file if file.isFile =>
        zipFile.addFile(file, parameters)

      case file if file.isDirectory =>
        zipFile.addFolder(file, parameters)
    }

    zipFile
  }

}
