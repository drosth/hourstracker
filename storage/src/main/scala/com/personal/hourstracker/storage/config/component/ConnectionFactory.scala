package com.personal.hourstracker.storage.config.component

import java.net.URI

import com.personal.hourstracker.storage.config.StorageConfiguration
import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.{ Logger, LoggerFactory }

object ConnectionFactory extends StorageConfiguration {
  private lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private lazy val connectionPool: (String, String, String) => BasicDataSource = (url, username, password) => {
    val basicDataSource = new BasicDataSource()

    basicDataSource.setUsername(username)
    basicDataSource.setPassword(password)

    basicDataSource.setDriverClassName(Storage.Registrations.driver)
    basicDataSource.setUrl(url)
    basicDataSource.setInitialSize(Storage.Registrations.ConnectionPool.initialSize)

    basicDataSource
  }

  def createDataSource(url: String)(constructUrlFromUri: URI => String): BasicDataSource = {
    createDataSource(new URI(url))(constructUrlFromUri)
  }

  def createDataSource(uri: URI)(constructUrlFromUri: URI => String): BasicDataSource = {
    val userInfo = uri.getUserInfo.split(":")
    createDataSource(constructUrlFromUri(uri), userInfo(0), userInfo(1))
  }

  def createDataSource(url: String, username: String, password: String): BasicDataSource = {
    logger.info(s"Creating DataSource for '$username@$url'...")
    connectionPool(url, username, password)
  }
}
