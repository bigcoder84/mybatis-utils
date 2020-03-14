package cn.tjd.mybatisutils;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Auther: TJD
 * @Date: 2019-12-29
 * @DESCRIPTION:
 **/
public class TableInfo extends ArrayList<TableInfo.ColumnInfo> {
    private String tableName;

    public static class ColumnInfo {
        private String colname;//字段名称
        private String coltype;//数据类型
        private Integer collength;//数据长度
        private Integer colprec;//小数位数

        public String getJdbcType() {
            if ("varchar".equals(coltype)) {
                return "VARCHAR";
            }
            if ("bigint".equals(coltype)) {
                return "BIGINT";
            }
            if ("char".equals(coltype)) {
                return "CHAR";
            }
            if ("date".equals(coltype)) {
                return "DATE";
            }
            if ("nchar".equals(coltype)) {
                return "NCHAR";
            }
            if ("ntext".equals(coltype)) {
                return "LONGNVARCHAR";
            }
            if ("nvarchar".equals(coltype)) {
                return "NVARCHAR";
            }
            if ("tinyint".equals(coltype)) {
                return "TINYINT";
            }
            if ("int".equals(coltype)) {
                return "INTEGER";
            }
            if ("timestamp".equals(coltype)) {
                return "BINARY";
            }
            if (fullPermit("datetime", "datetime2")) {
                return "TIMESTAMP";
            }
            return null;
        }

        private boolean fullPermit(String... types) {
            for (String type : types) {
                if (type.equals(coltype)) {
                    return true;
                }
            }
            return false;
        }

        public String getColname() {
            return colname;
        }

        public void setColname(String colname) {
            this.colname = colname;
        }

        public String getColtype() {
            return coltype;
        }

        public void setColtype(String coltype) {
            this.coltype = coltype;
        }

        public Integer getCollength() {
            return collength;
        }

        public void setCollength(Integer collength) {
            this.collength = collength;
        }

        public Integer getColprec() {
            return colprec;
        }

        public void setColprec(Integer colprec) {
            this.colprec = colprec;
        }
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 生成Insert语句
     *
     * @return
     */
    public String generateInsertSelective() {
        StringBuilder sql = new StringBuilder(" insert into ");
        sql.append(this.tableName + "\n");
        sql.append("\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (int i = 0; i < this.size(); i++) {
            ColumnInfo columnInfo = this.get(i);
            sql.append("\t\t<if test=\"" + columnInfo.getColname() + "!= null and " + columnInfo.getColname() + "!=''\">\n");
            sql.append("\t\t\t" + columnInfo.getColname() + ",\n");
            sql.append("\t\t</if>\n");
        }
        sql.append("\t</trim>\n");
        sql.append("\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
        for (int i = 0; i < this.size(); i++) {
            ColumnInfo columnInfo = this.get(i);
            sql.append("\t\t<if test=\"" + columnInfo.getColname() + "!= null and " + columnInfo.getColname() + "!=''\">\n");
            if (columnInfo.getJdbcType() != null) {
                sql.append("\t\t\t#{" + columnInfo.getColname() + ",jdbcType=" + columnInfo.getJdbcType() + "},\n");
            } else {
                sql.append("\t\t\t#{" + columnInfo.getColname() + "},\n");
            }

            sql.append("\t\t</if>\n");
        }
        sql.append("\t</trim>\n");
        return sql.toString();
    }

    /**
     * 生成Update语句
     *
     * @return
     */
    public String generateUpdate() {
        StringBuilder sql = new StringBuilder(" update ");
        sql.append(this.tableName + "\n");
        sql.append("\t<set>\n");
        for (int i = 0; i < this.size(); i++) {
            ColumnInfo columnInfo = this.get(i);
            sql.append("\t\t<if test=\"" + columnInfo.getColname() + "!= null and " + columnInfo.getColname() + "!=''\">\n");
            if (columnInfo.getJdbcType() != null) {
                sql.append("\t\t\t" + columnInfo.getColname() + "= #{" + columnInfo.getColname() + ",jdbcType=" + columnInfo.getJdbcType() + "},\n");
            } else {
                sql.append("\t\t\t" + columnInfo.getColname() + "= #{" + columnInfo.getColname() + "},\n");
            }
            sql.append("\t\t</if>\n");
        }
        sql.append("\t</set>\n");
        sql.append("where");
        return sql.toString();
    }

    /**
     * 生成表中所有字段名
     *
     * @return
     */
    public String generateFiled() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            str.append(this.get(i).getColname());
            if (i != this.size() - 1) {
                str.append(",");
            }
        }
        return str.toString();
    }

    public static TableInfo tableInfo(String ip, String port, String username, String passwd, String databaseName, String tableName) throws SQLException {
        String url = "jdbc:sqlserver://" + ip + ":" + port + ";DatabaseName=" + databaseName;
        Connection connection = DriverManager.getConnection(url, username, passwd);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT \n" +
                "        col.name AS colname ,  \n" +
                "        t.name AS coltype ,  \n" +
                "        col.length AS collength ,  \n" +
                "        ISNULL(convert(varchar(100),COLUMNPROPERTY(col.id, col.name, 'Scale')), 0) AS colprec--小数位数\n" +
                "      \n" +
                "FROM    dbo.syscolumns col  \n" +
                "        LEFT  JOIN dbo.systypes t ON col.xtype = t.xusertype  \n" +
                "        inner JOIN dbo.sysobjects obj ON col.id = obj.id  \n" +
                "                                         AND obj.xtype = 'U'  \n" +
                "                                         AND obj.status >= 0  \n" +
                "        LEFT  JOIN dbo.syscomments comm ON col.cdefault = comm.id  \n" +
                "        LEFT  JOIN sys.extended_properties ep ON col.id = ep.major_id  \n" +
                "                                                      AND col.colid = ep.minor_id  \n" +
                "                                                      AND ep.name = 'MS_Description'  \n" +
                "        LEFT  JOIN sys.extended_properties epTwo ON obj.id = epTwo.major_id  \n" +
                "                                                         AND epTwo.minor_id = 0  \n" +
                "                                                         AND epTwo.name = 'MS_Description'  \n" +
                "WHERE   obj.name = ?--表名  \n" +
                "ORDER BY col.colorder ;");
        preparedStatement.setString(1, tableName);
        ResultSet resultSet = preparedStatement.executeQuery();
        return transferTableInfo(resultSet, tableName);
    }

    private static TableInfo transferTableInfo(ResultSet resultSet, String tableName) throws SQLException {
        TableInfo tableInfos = new TableInfo();
        tableInfos.setTableName(tableName);
        while (resultSet.next()) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setCollength(resultSet.getInt("collength"));
            columnInfo.setColname(resultSet.getString("colname"));
            columnInfo.setColprec(resultSet.getInt("colprec"));
            columnInfo.setColtype(resultSet.getString("coltype"));
            tableInfos.add(columnInfo);
        }
        return tableInfos;
    }
}
