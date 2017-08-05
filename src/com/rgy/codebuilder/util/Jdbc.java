package com.rgy.codebuilder.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jdbc {

    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:";
    String dbName = "db";
    Connection conn = null;
    Statement s = null;
    ResultSet rs = null;
    private ResultSetMetaData md;
    private PreparedStatement ps;
    private List<Map<String, String>> list;
    private Map<String, String> map;
    private boolean flg;

    static void loadDriver() {
        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection open() {
        try {
            conn = DriverManager.getConnection(protocol + dbName + ";create=true");
        } catch (SQLException e) {
            e.printStackTrace();


        }
        return conn;
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Map<String,String>> query(String sql, Object... objs) {
        loadDriver();
        conn = open();
        list = new ArrayList<>();
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ps.setObject(i + 1, objs[i]);
            }
            rs = ps.executeQuery();
            md = rs.getMetaData();
            while (rs.next()) {
                map = new HashMap<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String key = md.getColumnName(i);
                    String value = rs.getObject(i).toString();
                    map.put(key, value);
                }
                list.add(map);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            close(conn, ps, rs);

        }
        return list;
    }

    public boolean dml(String sql, Object... objs) {
        open();
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ps.setObject(i + 1, objs[i]);
            }
            int exeResult = ps.executeUpdate();
            if(exeResult==1){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }
        return false;
    }

    /**
     * 添加数据
     *
     * @param sql 添加语句
     * @param objs 参数
     * @return 0失败 1成功
     */
    public boolean add(String sql, Object... objs) {
        flg = dml(sql, objs);
        return flg;
    }

    /**
     * 修改数据
     *
     * @param sql 修改语句
     * @param objs 参数
     * @return 0失败 1成功
     */
    public boolean mod(String sql, Object... objs) {
        flg = dml(sql, objs);
        return flg;
    }

    /**
     * 删除数据
     *
     * @param sql 删除语句
     * @param objs 参数
     * @return 0失败 1成功
     */
    public boolean del(String sql, Object... objs) {
        flg = dml(sql, objs);
        return flg;
    }


    public boolean tableIsNotExists(String table) {
        conn = open();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rsTables = meta.getTables(dbName, null, table, new String[]{"TABLE"});
            while (rsTables.next()) {
                String tableName = rsTables.getString("TABLE_NAME");
                if (table.toUpperCase().equals(tableName)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, rs);
        }
        return true;
    }

	public Map<String, String> queryOne(String sql, Object... objs) {
		List<Map<String, String>> list = query(sql,objs);
		if(list!=null){
			return list.get(0);
		}
		return null;
	}

}
