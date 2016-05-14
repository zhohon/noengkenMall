package com.jfinalshop.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据源工具类
 *
 * @author Jieven
 * @date 2015-6-27
 */
public class MetaInfoUtil {

    public static final String TABLE = "Table";
    public static final String VIEW = "View";

    /**
     * 获得元数据对象
     *
     * @param ds    数据源
     * @param props 连接配置
     * @return
     */
    public static DatabaseMetaData getDatabaseMetaData(String ds, Properties props) {
        Connection conn = null;
        try {
            Config config = DbKit.getConfig(ds);
            if (config == null) {
                throw new SQLException(ds + " datasrouce can not get config");
            }
            conn = config.getDataSource().getConnection();
            // TODO Mysql Test is OK!
            if (props != null) {
                conn.setClientInfo(props);
            }
            DatabaseMetaData md = conn.getMetaData();
            return md;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConn(conn);
        }
    }

    public static DatabaseMetaData getDatabaseMetaData(String ds) {
        return getDatabaseMetaData(ds, null);
    }

    /**
     * 获取数据源的数据库名
     *
     * @param ds 数据源
     * @return
     */
    public static String getDbNameByConfigName(String ds) {
        String dbName = null;
        Connection conn = null;
        try {
            Config config = DbKit.getConfig(ds);
            if (config == null) {
                throw new SQLException(ds + " datasrouce can not get config");
            }
            conn = config.getConnection();
            dbName = conn.getCatalog();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConn(conn);
        }
        return dbName;
    }

    /**
     * 获取数据源的用户名
     *
     * @param ds 数据源
     * @return
     */
    public static String getUserNameByConfigName(String ds) {
        try {
            DatabaseMetaData databaseMetaData = getDatabaseMetaData(ds);
            return databaseMetaData.getUserName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据源中表/视图的名字列表
     *
     * @param ds               数据源
     * @param type             DsUtil.TABLE/VIEW
     * @param schemaPattern    TODO
     * @param tableNamePattern TODO
     * @return
     */
    public static List<String> getTableNamesByConfigName(String ds, String type, String schemaPattern, String tableNamePattern) {

        if (tableNamePattern == null) {
            tableNamePattern = "%";
        }

        List<String> tables = new ArrayList<String>();
        ResultSet rs = null;
        try {
            DatabaseMetaData md = getDatabaseMetaData(ds);
            rs = md.getTables(null, schemaPattern, tableNamePattern, new String[]{type.toUpperCase()});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
        }
        return tables;
    }

    public static String getPkName(String ds, String table) {
        ResultSet rs = null;
        try {
            String schemaPattern = null;
            DatabaseMetaData md = getDatabaseMetaData(ds);
            rs = md.getPrimaryKeys(null, schemaPattern, table);
            while (rs.next()) {
                return rs.getString("COLUMN_NAME");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
        }
        return null;
    }

    /**
     * 获取数据源中元数据Column信息
     *
     * @param ds               数据源
     * @param tableNamePattern 表名
     * @return
     */
    public static JSONArray getColumnInfoByConfigName(String ds, String tableNamePattern) {
        JSONArray array = new JSONArray();
        ResultSet rs = null;
        try {
            Properties props = null;
            props = new Properties();
            props.setProperty("REMARKS", "true");// 获取注释
            props.setProperty("COLUMN_DEF", "true");// 获取默认值
            DatabaseMetaData md = getDatabaseMetaData(ds, props);
            String schemaPattern = null;
            rs = md.getColumns(null, schemaPattern, tableNamePattern, null);
            // 获取列数
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 遍历ResultSet中的每条数据
            while (rs.next()) {
//				System.out.println("Remarks: "+ rs.getObject(12));
                JSONObject json = new JSONObject();
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    json.put(columnName, value);
                }
                array.add(json);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
        }
        return array;
    }

    private static void closeConn(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignore) {
            }
        }
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignore) {
            }
        }
    }

    public static String refreshDictAndDisplay(String ds, String tableName) {
        StringBuilder sb = new StringBuilder("更新成功{");
        List<String> tables = getTableNamesByConfigName(ds, TABLE, null, tableName);
        List<Record> dicts = new ArrayList<Record>();
        List<Record> displays = new ArrayList<Record>();
        for (String table : tables) {
            JSONArray list = getColumnInfoByConfigName(ds, table);
            for (int i = 0; i < list.size(); i++) {
                JSONObject o = list.getJSONObject(i);
                String remarks = o.getString("REMARKS");
                List<Record> dict = buildDictByRemarks(remarks, table, o.getString("COLUMN_NAME"));
                if (dict != null) {
                    dicts.addAll(dict);
                    remarks = remarks.split(":|：")[0];
                }
                Record display = buildDisplayMapping(remarks, table, o.getString("COLUMN_NAME"));
                displays.add(display);
            }
        }
        //不输入表名，则全部清除
        if (tableName == null || tableName.trim().isEmpty() || tableName.trim().equals("*")) {
            Db.update("truncate table dicts");
            Db.update("truncate table displaymapping");
        }
        Db.batchSave("dicts", dicts, 500);
        sb.append("数据字典:" + dicts.size()).append("行,");
        Db.batchSave("displaymapping", displays, 500);
        sb.append("字段名称:" + displays.size()).append("行");
        sb.append("}");
        return sb.toString();
    }

    private static Record buildDisplayMapping(String remarks, String table, String field) {
        try {
            // 保存字典
            Record result = new Record();
            result.set("display", remarks);
            result.set("model", table);
            result.set("field", field);
            result.set("ext", null);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("字段mapping字段备注预处理异常:" + remarks, e);
        }
    }

    private static List<Record> buildDictByRemarks(String remarks, String table, String field) {
        List<Record> list = new ArrayList<Record>();
        Record result = null;
        // eg. 状态:1=上架，2=售罄,3=下架 ,4=过期
        String[] temp = null;
        try {
            // 获取第1项注释作为列名
            if (remarks.contains(":") || remarks.contains("：")) {
                temp = remarks.split(":|：");
            }
            if (temp == null) {
                return null;
            }
            remarks = temp[0];

            // 获取第2项注释作为字典
            if (temp.length > 1) {
                String ss1 = temp[1];
                // 如果没有=号 说明是其它描述eg. 原价:大于等于0
                if (ss1.contains("=")) {
                    String[] dicts = ss1.split(",|，");
                    for (String dict : dicts) {
                        String[] sss = dict.split("=");
                        String value = sss[0];
                        String name = sss[1];
                        // 保存字典
                        result = new Record();
                        result.set("dict_value", value);
                        result.set("dict_name", name);
                        result.set("model", table);
                        result.set("field", field);
                        result.set("ext", null);
                        System.out.println(value + '-' + name + '|');
                        list.add(result);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("数据字典字段备注预处理异常:" + remarks, e);
        }
        return list;
    }
}
