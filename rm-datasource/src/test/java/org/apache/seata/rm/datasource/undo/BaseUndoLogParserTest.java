/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.rm.datasource.undo;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.seata.rm.datasource.DataCompareUtils;
import org.apache.seata.rm.datasource.sql.struct.Field;
import org.apache.seata.rm.datasource.sql.struct.KeyType;
import org.apache.seata.rm.datasource.sql.struct.Row;
import org.apache.seata.rm.datasource.undo.SQLUndoLog;
import org.apache.seata.rm.datasource.undo.UndoLogParser;
import org.apache.seata.sqlparser.struct.TableMeta;
import org.apache.seata.rm.datasource.sql.struct.TableRecords;
import org.apache.seata.sqlparser.SQLType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public abstract class BaseUndoLogParserTest extends BaseH2Test {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public abstract UndoLogParser getParser();

    @Test
    void testEncodeAndDecode() throws SQLException {

        execSQL("INSERT INTO table_name(id, name) VALUES (12345,'aaa');");
        execSQL("INSERT INTO table_name(id, name) VALUES (12346,'aaa');");

        TableRecords beforeImage = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");
        execSQL("update table_name set name = 'xxx' where id in (12345, 12346);");
        TableRecords afterImage = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");

        TableRecords beforeImage2 = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");
        execSQL("INSERT INTO table_name(id, name) VALUES (12347,'aaa');");
        execSQL("INSERT INTO table_name(id, name) VALUES (12348,'aaa');");
        TableRecords afterImage2 = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");

        SQLUndoLog sqlUndoLog00 = new SQLUndoLog();
        sqlUndoLog00.setSqlType(SQLType.UPDATE);
        sqlUndoLog00.setTableMeta(tableMeta);
        sqlUndoLog00.setTableName("table_name");
        sqlUndoLog00.setBeforeImage(beforeImage);
        sqlUndoLog00.setAfterImage(afterImage);

        SQLUndoLog sqlUndoLog01 = new SQLUndoLog();
        sqlUndoLog01.setSqlType(SQLType.UPDATE);
        sqlUndoLog01.setTableMeta(tableMeta);
        sqlUndoLog01.setTableName("table_name");
        sqlUndoLog01.setBeforeImage(beforeImage2);
        sqlUndoLog01.setAfterImage(afterImage2);

        BranchUndoLog originLog = new BranchUndoLog();
        originLog.setBranchId(123456L);
        originLog.setXid("xiddddddddddd");
        List<SQLUndoLog> logList = new ArrayList<>();
        logList.add(sqlUndoLog00);
        logList.add(sqlUndoLog01);
        originLog.setSqlUndoLogs(logList);

        // start test
        byte[] bs = getParser().encode(originLog);

        Assertions.assertNotNull(bs);

        LOGGER.info("data size:{}", bs.length);

        BranchUndoLog dstLog = getParser().decode(bs);

        Assertions.assertEquals(originLog.getBranchId(), dstLog.getBranchId());
        Assertions.assertEquals(originLog.getXid(), dstLog.getXid());
        Assertions.assertEquals(originLog.getSqlUndoLogs().size(), dstLog.getSqlUndoLogs().size());
        List<SQLUndoLog> logList2 = dstLog.getSqlUndoLogs();
        SQLUndoLog sqlUndoLog10 = logList2.get(0);
        SQLUndoLog sqlUndoLog11 = logList2.get(1);
        Assertions.assertEquals(sqlUndoLog00.getSqlType(), sqlUndoLog10.getSqlType());
        Assertions.assertEquals(sqlUndoLog00.getTableName(), sqlUndoLog10.getTableName());
        Assertions.assertTrue(
            DataCompareUtils.isRecordsEquals(sqlUndoLog00.getBeforeImage(), sqlUndoLog10.getBeforeImage()).getResult());
        Assertions.assertTrue(
            DataCompareUtils.isRecordsEquals(sqlUndoLog00.getAfterImage(), sqlUndoLog10.getAfterImage()).getResult());
        Assertions.assertEquals(sqlUndoLog01.getSqlType(), sqlUndoLog11.getSqlType());
        Assertions.assertEquals(sqlUndoLog01.getTableName(), sqlUndoLog11.getTableName());
        Assertions.assertTrue(
            DataCompareUtils.isRecordsEquals(sqlUndoLog01.getBeforeImage(), sqlUndoLog11.getBeforeImage()).getResult());
        Assertions.assertTrue(
            DataCompareUtils.isRecordsEquals(sqlUndoLog01.getAfterImage(), sqlUndoLog11.getAfterImage()).getResult());
    }

    @Test
    void testPerformance() throws SQLException {

        execSQL("INSERT INTO table_name(id, name) VALUES (12345,'aaa');");
        execSQL("INSERT INTO table_name(id, name) VALUES (12346,'aaa');");

        TableRecords beforeImage = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");
        execSQL("update table_name set name = 'xxx' where id in (12345, 12346);");
        TableRecords afterImage = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");

        TableRecords beforeImage2 = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");
        execSQL("INSERT INTO table_name(id, name) VALUES (12347,'aaa');");
        execSQL("INSERT INTO table_name(id, name) VALUES (12348,'aaa');");
        TableRecords afterImage2 = execQuery(tableMeta, "SELECT * FROM table_name WHERE id IN (12345, 12346);");

        SQLUndoLog sqlUndoLog00 = new SQLUndoLog();
        sqlUndoLog00.setSqlType(SQLType.UPDATE);
        sqlUndoLog00.setTableMeta(tableMeta);
        sqlUndoLog00.setTableName("table_name");
        sqlUndoLog00.setBeforeImage(beforeImage);
        sqlUndoLog00.setAfterImage(afterImage);

        SQLUndoLog sqlUndoLog01 = new SQLUndoLog();
        sqlUndoLog01.setSqlType(SQLType.UPDATE);
        sqlUndoLog01.setTableMeta(tableMeta);
        sqlUndoLog01.setTableName("table_name");
        sqlUndoLog01.setBeforeImage(beforeImage2);
        sqlUndoLog01.setAfterImage(afterImage2);

        BranchUndoLog originLog = new BranchUndoLog();
        originLog.setBranchId(123456L);
        originLog.setXid("xiddddddddddd");
        List<SQLUndoLog> logList = new ArrayList<>();
        logList.add(sqlUndoLog00);
        logList.add(sqlUndoLog01);
        originLog.setSqlUndoLogs(logList);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            byte[] bs = getParser().encode(originLog);
            Assertions.assertNotNull(bs);
            BranchUndoLog dstLog = getParser().decode(bs);
            Assertions.assertEquals(originLog.getBranchId(), dstLog.getBranchId());
        }
        long end = System.currentTimeMillis();
        LOGGER.info("elapsed time {} ms.", (end - start));
    }

    @Test
    void testDecodeDefaultContent() {
        byte[] defaultContent = getParser().getDefaultContent();

        BranchUndoLog branchUndoLog = getParser().decode(defaultContent);
        Assertions.assertNotNull(branchUndoLog);
        Assertions.assertNull(branchUndoLog.getXid());
        Assertions.assertEquals(0L, branchUndoLog.getBranchId());
        Assertions.assertNull(branchUndoLog.getSqlUndoLogs());
    }

    /**
     * will check kryo、jackson、fastjson、protostuff timestamp encode and decode
     */
    @Test
    public void testTimestampEncodeAndDecode() {

        BranchUndoLog branchUndoLog = new BranchUndoLog();
        branchUndoLog.setXid("192.168.0.1:8091:123456");
        branchUndoLog.setBranchId(123457);
        List<SQLUndoLog> sqlUndoLogs = new ArrayList<>();
        SQLUndoLog sqlUndoLog = new SQLUndoLog();
        sqlUndoLog.setBeforeImage(TableRecords.empty(new TableMeta()));
        Row row = new Row();
        Field field = new Field();
        field.setName("gmt_create");
        field.setKeyType(KeyType.PRIMARY_KEY);
        field.setType(JDBCType.TIMESTAMP.getVendorTypeNumber());
        Timestamp timestampEncode = new Timestamp(Integer.MAX_VALUE + 1L);
        timestampEncode.setNanos(999999);
        field.setValue(timestampEncode);
        row.add(field);
        TableRecords afterRecords = new TableRecords();
        List<Row> rows = new ArrayList<>();
        rows.add(row);
        afterRecords.setRows(rows);
        afterRecords.setTableMeta(new TableMeta());
        afterRecords.setTableName("test");
        sqlUndoLog.setAfterImage(afterRecords);
        sqlUndoLogs.add(sqlUndoLog);
        branchUndoLog.setSqlUndoLogs(sqlUndoLogs);
        byte[] encode = getParser().encode(branchUndoLog);
        BranchUndoLog decodeBranchLog = getParser().decode(encode);
        Timestamp timestampDecode = (Timestamp)(decodeBranchLog.getSqlUndoLogs().get(0).getAfterImage().getRows().get(0)
            .getFields().get(0).getValue());
        Assertions.assertEquals(timestampEncode, timestampDecode);

    }
}
