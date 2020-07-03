package pers.px

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.LoggerFactory

object Hikari {

  val logger = LoggerFactory.getLogger(getClass)

  @transient private var instance: HikariDataSource = _

  def getHikariInstance: HikariDataSource = {
    if (instance == null) {
      try {
        val config = new HikariConfig
        config.setJdbcUrl("jdbc:mysql://172.16.116.213:3390/data_warehouse")
        config.setUsername("i&u&d")
        config.setPassword("i&u&d")
        config.addDataSourceProperty("maxActive", "100")
        config.addDataSourceProperty("maxIdle", "10")
        config.addDataSourceProperty("maxWait", "-1")
        instance = new HikariDataSource(config)
      } catch {
        case ex: Exception =>
          logger.error(s"get connection pool error,message is ex.printStackTrace")
      }
    }
    instance
  }
}
