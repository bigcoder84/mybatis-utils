package cn.tjd.mybatisutils;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * @Auther: TJD
 * @Date: 2019-12-29
 * @DESCRIPTION:
 **/
public class Main {

    private static final String GENERATE_FILED = "-f";
    private static final String GENERATE_UPDATE = "-u";
    private static final String GENERATE_INSERT = "-i";


    private static String IP = "";
    private static String USERNAME = "";
    private static String PASSWD = "";
    private static String DATABASE_NAME = "";
    private static String TABLE_NAME = "";

    public static void main(String[] args) throws SQLException {
        try {
            loadPorperties();
        } catch (IOException e) {
            System.out.println("配置文件加载错误");
        }
        TableInfo tableInfo = TableInfo.tableInfo(IP, USERNAME, PASSWD, DATABASE_NAME, TABLE_NAME);
        String sql = "";
        if (containsKey(args, GENERATE_UPDATE)) {
            sql += tableInfo.generateUpdate();
        }
        if (containsKey(args, GENERATE_FILED)) {
            sql += tableInfo.generateFiled();
        }
        if (containsKey(args, GENERATE_INSERT)) {
            sql += tableInfo.generateInsertSelective();
        }
        System.out.println(sql);
    }

    private static void loadPorperties() throws IOException {
        File directory = new File("");
        String filePath = directory.getAbsolutePath();//设定为上级文件夹 获取绝对路径
        InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(filePath + "//config.properties"))); //方法1
        Properties prop = new Properties();
        prop.load(new InputStreamReader(inputStream)); //加载格式化后的流
        IP = prop.getProperty("IP");
        USERNAME = prop.getProperty("USERNAME");
        PASSWD = prop.getProperty("PASSWD");
        DATABASE_NAME = prop.getProperty("DATABASE_NAME");
        TABLE_NAME = prop.getProperty("TABLE_NAME");
    }

    private static boolean containsKey(String[] args, String key) {
        for (String arg : args) {
            if (arg.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
