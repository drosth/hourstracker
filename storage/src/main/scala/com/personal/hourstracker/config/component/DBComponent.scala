package com.personal.hourstracker.config.component

import java.io.PrintWriter
import java.sql.Connection
import java.util.logging.Logger

import javax.sql.DataSource
import scalikejdbc.{ AutoSession, ConnectionPool, DBSession }

private[component] trait DBComponent {
  lazy val dataSource: DataSource = new DataSource() {
    override def getConnection: Connection = {
      val connection = parentDataSource.getConnection
      connection.setReadOnly(false)
      connection
    }

    override def getConnection(username: String, password: String): Connection = getConnection()

    override def getLogWriter: PrintWriter = parentDataSource.getLogWriter

    override def setLogWriter(printWriter: PrintWriter): Unit = parentDataSource.setLogWriter(printWriter)

    override def setLoginTimeout(i: Int): Unit = parentDataSource.setLoginTimeout(i)

    override def getLoginTimeout: Int = parentDataSource.getLoginTimeout

    override def getParentLogger: Logger = parentDataSource.getParentLogger

    override def unwrap[T](aClass: Class[T]): T = parentDataSource.unwrap(aClass)

    override def isWrapperFor(aClass: Class[_]): Boolean = parentDataSource.isWrapperFor(aClass)
  }
  private lazy val parentDataSource = ConnectionPool.dataSource()

  implicit val session: DBSession = AutoSession
}
