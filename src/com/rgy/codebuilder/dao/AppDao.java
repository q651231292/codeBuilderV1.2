package com.rgy.codebuilder.dao;

import com.rgy.codebuilder.util.Jdbc;

/**
 * Created by Administrator on 2017/8/4.
 */
public class AppDao {

    private Jdbc jdbc;

    public boolean tableIsNotExists(String tableName){
        boolean isNotExists = false;
        jdbc = new Jdbc();
        isNotExists = jdbc.tableIsNotExists(tableName);
        return isNotExists;
    }

    public void initTable() {
        jdbc = new Jdbc();
        String temp_sql = "" +
                "CREATE TABLE TEMP "+
                "("+
                "   TEMP_ID              VARCHAR(36)          NOT NULL,"+
                "   TEMP_NAME            VARCHAR(100),"+
                "   CONSTRAINT PK_TEMP PRIMARY KEY (TEMP_ID)"+
                ")";


        jdbc.add(temp_sql);

        String temp_data_sql = "" +
                "CREATE TABLE TEMP_DATA "+
                "("+
                "   TEMP_DATA_ID         VARCHAR(36)          NOT NULL,"+
                "   TEMP_ID              VARCHAR(36),"+
                "   LABEL                  VARCHAR(100),"+
                "   VALUE                VARCHAR(32672),"+
                "   CONSTRAINT PK_TEMP_DATA PRIMARY KEY (TEMP_DATA_ID)"+
                ")";
        jdbc.add(temp_data_sql);
    }
}
