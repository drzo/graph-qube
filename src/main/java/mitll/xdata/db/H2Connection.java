/*
 * Copyright 2013-2016 MIT Lincoln Laboratory, Massachusetts Institute of Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mitll.xdata.db;

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Makes H2 Connections.
 * <p>
 * User: GO22670
 * Date: 12/31/12
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class H2Connection implements DBConnection {
  private static final int CACHE_SIZE = 65536;
  private static final Logger logger = Logger.getLogger(H2Connection.class);

  private Connection conn;
  // private int cacheSizeKB;
  private final int queryCacheSize;
  private int maxMemoryRows = 50000;

  /**
   * @see mitll.xdata.dataset.bitcoin.binding.BitcoinBinding#main(String[])
   * @param dbName
   */
  public H2Connection(String dbName) {
    this(".", dbName, 50000, 8, 50000, false);
  }

  /**
   * @see mitll.xdata.dataset.bitcoin.features.BitcoinFeaturesUncharted
   * @param dbName
   * @param maxMemoryRows
   */
  public H2Connection(String dbName, int maxMemoryRows) {
    this(".", dbName, 50000, 8, maxMemoryRows, false);
  }

  /**
   * @see mitll.xdata.dataset.bitcoin.ingest.IngestSql
   * @param dbName
   * @param maxMemoryRows
   * @param createDB
   */
  public H2Connection(String dbName, int maxMemoryRows, boolean createDB) {
    this(".", dbName, 50000, 8, maxMemoryRows, createDB);
  }

/*
  public H2Connection(String dbName, boolean createDB) {
    this(".", dbName, 50000, 8, 50000, createDB);
  }
*/

  public H2Connection(String configDir, String dbName) {
    this(configDir, dbName, 50000, 8, 5000000, false);
  }

  /**
   * @param configDir
   * @param dbName
   */
  private H2Connection(String configDir, String dbName, int cacheSizeKB, int queryCacheSize, int maxMemoryRows, boolean createDB) {
    //  this.cacheSizeKB = cacheSizeKB;
    this.queryCacheSize = queryCacheSize;
    this.maxMemoryRows = maxMemoryRows;
    connect(configDir, dbName, createDB);
  }

  private void connect(String configDir, String database, boolean createDB) {
    String h2FilePath = configDir + File.separator + database;
    connect(h2FilePath, createDB);
  }

  /**
   * //jdbc:h2:file:/Users/go22670/DLITest/clean/netPron2/war/config/urdu/vlr-parle;IFEXISTS=TRUE;CACHE_SIZE=30000
   *
   * @param h2FilePath
   */
  private void connect(String h2FilePath, boolean create) {
    String url = "jdbc:h2:file:" + h2FilePath + ";" +
        (create ? "" : "IFEXISTS=TRUE;") +
        "LOG=0;CACHE_SIZE=" +
        CACHE_SIZE +
        ";LOCK_MODE=0;" +
        "MVCC=FALSE;"+
        "MV_STORE=FALSE;"+
        "QUERY_CACHE_SIZE=" + queryCacheSize + ";" +
        //    "CACHE_SIZE="       + cacheSizeKB + ";" +
        "DATABASE_TO_UPPER=false" + ";" +
        "MAX_MEMORY_ROWS=" + maxMemoryRows;

    logger.debug("---->>>> connect : connecting to " + url);
    org.h2.Driver.load();
    logger.debug("---->>>> connect : loaded driver " + url);

    try {
      conn = DriverManager.getConnection(url, "", "");
      conn.setAutoCommit(true);

    } catch (SQLException e) {
      conn = null;
      logger.error("got error trying to create h2 connection with URL '" + url + "', exception = " + e, e);
    }
  }

  public void contextDestroyed() {
    logger.debug("---->>>> disconnect : from " + conn);

    if (conn == null) {
      logger.info("not never successfully created h2 connection ");
    } else {
      sendShutdown();
      closeConnection();
      org.h2.Driver.unload();
    }
  }

  private void sendShutdown() {
    logger.info("send shutdown on connection " + conn);

    try {
      Statement stat = conn.createStatement();
      stat.execute("SHUTDOWN");
      stat.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void closeConnection() {
    try {
      //   logger.info("closing connection " + conn);
      if (conn != null) {
        conn.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Connection getConnection() {
    return conn;
  }

  public boolean isValid() {
    return conn != null;
  }

  @Override
  public String getType() {
    return "h2";  //To change body of implemented methods use File | Settings | File Templates.
  }
}
