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

package mitll.xdata.dataset.bitcoin.ingest;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by go22670 on 1/25/16.
 */
public class StatementResult {
  private static final Logger logger = Logger.getLogger(StatementResult.class);

  public ResultSet rs;
  public Statement statement;

  public StatementResult(ResultSet rs, Statement statement) {
    this.rs = rs;
    this.statement = statement;
  }

  public void close() {
    try {
      rs.close();
      statement.close();

      rs = null;
      statement = null;


    } catch (SQLException e) {
      logger.error("got " + e, e);
    }
  }
}