package pers.px

import java.sql._
import java.text.{MessageFormat, SimpleDateFormat}

import org.apache.commons.lang3.StringUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.immutable.{HashMap, List}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object TidbUtils {

  @transient
  private val log: Logger = LoggerFactory.getLogger(getClass.getName)

  private val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  private val sdfNoHms = new SimpleDateFormat("yyyy-MM-dd")
  private var connection: Connection = null
  private var statement: Statement = null

  def close(): Unit = {

  }

  def getConnection(): Unit = {
    this.synchronized {
      try {
        Class.forName("com.mysql.jdbc.Driver")
        connection = DriverManager.getConnection("jdbc:mysql://172.16.116.213:3390/data_warehouse", "i&u&d", "i&u&d")
        connection.setAutoCommit(false)
        statement = connection.createStatement
      } catch {
        case e: ClassNotFoundException =>
          log.error(e.getMessage)
        case throwables: SQLException =>
          log.error(throwables.getMessage)
      }
    }
  }

  /**
   * batch ingest data to tidb
   */
  def batchIngestDataByTupleList(tableName: String, dataList: List[(String, Map[String, String])]): Unit = {
    val sqlList = buildUpsertSqlByTupleList(tableName, dataList)
    val start_time = System.currentTimeMillis
    executeUpdate(sqlList.mkString)
    log.info("table {} upsert [{}] size data to tidb in [{}ms] time", tableName, String.valueOf(sqlList.size), String.valueOf(System.currentTimeMillis - start_time))
  }

  /**
   * batch ingest data to tidb
   */
  def batchIngestDataByMapList(tableName: String, dataList: List[Map[String, String]]): Unit = {
    val sqlList = buildUpsertSqlByMapList(tableName, dataList)
    val start_time = System.currentTimeMillis
    executeUpdate(sqlList.mkString)
    log.info("table {} upsert [{}] size data to tidb in [{}ms] time", tableName, String.valueOf(sqlList.size), String.valueOf(System.currentTimeMillis - start_time))
  }

  /**
   * batch ingest data to tidb
   */
  def batchIngestDataByMapList(tableName: String, dataList: List[Map[String, String]], tableInfo: Map[String, Field]): Unit = {
    val sqlList = buildUpsertSqlByMapList(tableName, dataList, tableInfo)
    val start_time = System.currentTimeMillis
    executeUpdate(sqlList.mkString)
    log.info("table {} upsert [{}] size data to tidb in [{}ms] time", tableName, String.valueOf(sqlList.size), String.valueOf(System.currentTimeMillis - start_time))
  }

  /**
   * query with output field list
   */
  def queryData(querySql: String, columnNames: List[String]): List[Map[String, Any]] = {
    executeSelect(querySql)
  }

  def executeUpdate(sql: String): Int = {
    var result = 0
    if (StringUtils.isEmpty(StringUtils.trim(sql))) {
      return result
    }
    if (connection == null || connection.isClosed || statement == null || statement.isClosed) {
      getConnection
    }
    try {
      result = statement.executeUpdate(sql)
      connection.commit()
    } catch {
      case e: Exception => log.error("executeUpdate " + e + " update sql is " + sql)
    }
    result
  }


  def executeDelete(sql: String): Unit = {
    executeUpdate(sql)
  }

  def executeSelect(sql: String): List[Map[String, String]] = {
    var result: ListBuffer[Map[String, String]] = new ListBuffer[Map[String, String]]()
    var rowMap: mutable.Map[String, String] = null
    var resultSet: ResultSet = null
    var metaData: ResultSetMetaData = null
    if (connection == null || connection.isClosed || statement == null || statement.isClosed) {
      getConnection
    }
    try {
      resultSet = statement.executeQuery(sql)
      metaData = resultSet.getMetaData
      while (resultSet.next()) {
        rowMap = new mutable.HashMap[String, String]()
        for (i <- 1 to metaData.getColumnCount) {
          if (resultSet.getObject(i) != null && resultSet.getObject(i).getClass.equals(classOf[Timestamp]))
            rowMap += (metaData.getColumnName(i) -> sdf.format(resultSet.getTimestamp(i)))
          else if (resultSet.getObject(i) != null && resultSet.getObject(i).getClass.equals(classOf[Date]))
            rowMap += (metaData.getColumnName(i) -> sdfNoHms.format(resultSet.getTimestamp(i)))
          else
            rowMap += (metaData.getColumnName(i) -> resultSet.getString(i))
        }
        result += rowMap.toMap
      }
      connection.commit()
    } catch {
      case e: Exception => log.error("executeSelect" + e + " select sql is " + sql)
    }
    result.toList
  }

  def executeSelect(sql: String, columnNames: List[String]): List[Map[String, String]] = {
    var result = new ListBuffer[Map[String, String]]()
    var rowMap: mutable.Map[String, String] = null
    var resultSet: ResultSet = null
    if (connection == null || connection.isClosed || statement == null || statement.isClosed) {
      getConnection
    }
    try {
      resultSet = statement.executeQuery(sql)
      while (resultSet.next()) {
        rowMap = new mutable.HashMap[String, String]
        columnNames.foreach(columnName => {
          rowMap += (columnName -> resultSet.getString(columnName))
        })
        result += rowMap.toMap
      }
      connection.commit()
    } catch {
      case e: Exception => log.error("executeSelect" + e + " select sql is " + sql)
    }
    result.toList
  }

  private def buildUpsertSql(tableName: String, dataList: List[Map[String, String]], tableInfo: Map[String, Field]): List[String] = {
    dataList.map(data => {
      buildUpsertSql(tableName, data, tableInfo)
    })
  }

  private def buildUpsertSql(tableName: String, data: Map[String, String], tableInfo: Map[String, Field]): String = {
    val upsertTemplate = "insert into {0} ({1}) values({2}) ON DUPLICATE KEY UPDATE {3};"
    val fields = new StringBuilder
    val values = new StringBuilder
    val kvs = new StringBuilder
    data.foreach(f = t => {
      val fieldName = t._1
      var fieldValue = t._2
      if (tableInfo.keySet.contains(fieldName)) {
        val columnType = if (null == tableInfo.getOrElse(fieldName, null)) {
          ""
        } else {
          tableInfo.getOrElse(fieldName, null).typeName
        }
        fields.append("`").append(fieldName).append("`").append(",")
        StringUtils.lowerCase(columnType) match {
          case "tinyint" =>
          case "int" =>
            values.append(DataTypeUtils.parseInt(fieldValue)).append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(DataTypeUtils.parseInt(fieldValue)).append(",")

          case "bigint" =>
            values.append(DataTypeUtils.parseLong(fieldValue)).append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(DataTypeUtils.parseLong(fieldValue)).append(",")

          case "flaot" =>
            values.append(DataTypeUtils.parseFloat(fieldValue)).append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(DataTypeUtils.parseFloat(fieldValue)).append(",")

          case "double" =>
            values.append(DataTypeUtils.parseDouble(fieldValue)).append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(DataTypeUtils.parseDouble(fieldValue)).append(",")

          case "decimal" =>
            values.append(DataTypeUtils.parseBigDecimal(fieldValue)).append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(DataTypeUtils.parseBigDecimal(fieldValue)).append(",")

          case "date" =>
            val date = DataTypeUtils.parseDate(fieldValue)
            values.append(if (null == date) null
            else "'" + date + "'").append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(if (null == date) null
            else "'" + date + "'").append(",")

          case "timestamp" =>
          case "datetime" =>
            val dateTime = DataTypeUtils.parseDateTime(fieldValue)
            values.append(if (null == dateTime) null
            else "'" + dateTime + "'").append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(if (null == dateTime) null
            else "'" + dateTime + "'").append(",")

          case _ =>
            if (fieldValue != null && escapeNeededForString(fieldValue, fieldValue.length)) fieldValue = hexEscape(fieldValue, fieldValue.length)
            values.append(if (null == fieldValue) null
            else "'" + fieldValue + "'").append(",")
            kvs.append("`").append(fieldName).append("`").append("=").append(if (null == fieldValue) null
            else "'" + fieldValue + "'").append(",")
        }
      }
    })
    if (fields.length < 1) {
      ""
    } else {
      MessageFormat.format(upsertTemplate, tableName, fields.deleteCharAt(fields.length - 1), values.deleteCharAt(values.length - 1), kvs.deleteCharAt(kvs.length - 1))
    }
  }

  def getFieldAndTypeMap(tableName: String): Map[String, Field] = {
    var result: HashMap[String, Field] = new HashMap[String, Field]
    if (connection == null || connection.isClosed || statement == null || statement.isClosed) {
      getConnection
    }
    val resultSet = statement.executeQuery(MessageFormat.format("desc {0};", tableName))
    var field: Field = null
    while (resultSet.next()) {
      field = new Field(resultSet.getString(1), StringUtils.substringBefore(resultSet.getString(2), "("), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6))
      result += (field.fieldName -> field)
    }
    connection.commit()
    result
  }

  private def buildUpsertSqlByTupleList(tableName: String, dataList: List[(String, Map[String, String])]): List[String] = {
    dataList.map(data => {
      if (!StringUtils.isEmpty(data._1)) {
        var dataMap = data._2
        buildUpsertSql(tableName, dataMap)
      } else {
        ""
      }
    })
  }

  private def buildUpsertSqlByMapList(tableName: String, dataList: List[Map[String, String]]): List[String] = {
    dataList.map(data => {
      buildUpsertSql(tableName, data)
    })
  }

  private def buildUpsertSqlByMapList(tableName: String, dataList: List[Map[String, String]], tableInfo: Map[String, Field]): List[String] = {
    dataList.map(data => {
      buildUpsertSql(tableName, data, tableInfo)
    })
  }

  private def buildUpsertSql(table: String, data: Map[String, String]): String = {
    val upsertTemplate = "insert into {0} ({1}) values({2}) ON DUPLICATE KEY UPDATE {3};"
    val fields = new StringBuilder
    val values = new StringBuilder
    val kvs = new StringBuilder
    data.foreach(f = t => {
      val fieldName = t._1
      var fieldValue = t._2
      if (fieldValue != null && escapeNeededForString(fieldValue, fieldValue.length)) {
        fieldValue = hexEscape(fieldValue, fieldValue.length)
      }
      fields.append("`").append(fieldName).append("`").append(",")
      if (StringUtils.isEmpty(StringUtils.trim(fieldValue))) {
        values.append("null").append(",")
        kvs.append("`").append(fieldName).append("`").append("=").append("null").append(",")
      }
      else {
        values.append("'").append(fieldValue).append("'").append(",")
        kvs.append("`").append(fieldName).append("`").append("=").append("'").append(fieldValue).append("'").append(",")
      }
    })
    if (fields.nonEmpty) {
      val sql = MessageFormat.format(upsertTemplate, table, fields.deleteCharAt(fields.length - 1), values.deleteCharAt(values.length - 1), kvs.deleteCharAt(kvs.length - 1))
      removeBadChars(sql)
    } else {
      ""
    }
  }

  def removeBadChars(s: String): String = {
    if (s == null) return null
    val sb = new StringBuilder
    for (i <- 0 until s.length) {
      if (!Character.isHighSurrogate(s.charAt(i)))
        sb.append(s.charAt(i))
    }
    sb.toString
  }

  private def escapeNeededForString(x: String, stringLength: Int) = {
    var needsHexEscape = false
    for (i <- 0 until stringLength; if !needsHexEscape) {
      val c = x.charAt(i)
      c match {
        case 0 =>
          needsHexEscape = true
        case '\n' =>
          needsHexEscape = true
        case '\r' =>
          needsHexEscape = true
        case '\\' =>
          needsHexEscape = true
        case '\'' =>
          needsHexEscape = true
        case '"' =>
          needsHexEscape = true
        case '\032' =>
          needsHexEscape = true
        case _ =>
      }
    }
    needsHexEscape
  }

  private def hexEscape(source: String, stringLength: Int) = {
    val buf = new StringBuilder((source.length * 1.1).toInt)
    for (i <- 0 until stringLength) {
      val c = source.charAt(i)
      c match {
        case 0 =>
          buf.append('\\')
          buf.append('0')
        case '\n' =>
          buf.append('\\')
          buf.append('n')
        case '\r' =>
          buf.append('\\')
          buf.append('r')
        case '\\' =>
          buf.append('\\')
          buf.append('\\')
        case '\'' =>
          buf.append('\\')
          buf.append('\'')
        case '"' =>
          buf.append('\\')
          buf.append('"')
        case '\032' =>
          buf.append('\\')
          buf.append('Z')
        case _ =>
          buf.append(c)
      }
    }
    buf.toString
  }

  case class Field(fieldName: String, typeName: String, nullFlag: String, keyType: String, defaultValue: String, extra: String)

}
