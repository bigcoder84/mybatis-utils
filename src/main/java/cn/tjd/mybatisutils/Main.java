package cn.tjd.mybatisutils;

import org.apache.commons.cli.*;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * @Auther: TJD
 * @Date: 2019-12-29
 * @DESCRIPTION:
 **/
public class Main {

    private static Options OPTIONS = new Options();
    private static CommandLine commandLine;
    private static String HELP_STRING = null;

    private static int databaseOptionNum = 0;

    private static String IP = "";
    private static String USERNAME = "";
    private static String PASSWD = "";
    private static String DATABASE_NAME = "";
    private static String TABLE_NAME = "";
    private static String PORT = "";

    public static void main(String[] args) {
        try {
            initCliArgs(args);
        } catch (ParseException e) {
            System.out.println("Command resolution exception");
            return;
        }
        loadPorperties();
        try {
            analyzeArgs();
        } catch (SQLException e) {
            System.out.println("Database connection exception");
        }
    }

    /**
     * init args
     *
     * @param args args
     */
    private static void initCliArgs(String[] args) throws ParseException {
        CommandLineParser commandLineParser = new DefaultParser();
        OPTIONS.addOption(Option.builder().hasArg(true).longOpt("ip").type(String.class).desc("Database IP address" +
                ".You can also specify the 'IP' property in the config.properties configuration file under the " +
                "current path.").build());
        OPTIONS.addOption(Option.builder().hasArg(true).longOpt("port").type(String.class).desc("Database port.You " +
                "can also specify the 'PORT' property in the config.properties configuration file under the current " +
                "path.").build());
        OPTIONS.addOption(Option.builder().hasArg(true).longOpt("database").type(String.class).desc("Database port" +
                ".You can also specify the 'DATABASE' property in the config.properties configuration file under" +
                " the current path.").build());
        OPTIONS.addOption(Option.builder().hasArg(true).longOpt("table").type(String.class).desc("Database table name" +
                ".You can also specify the 'TABLE' property in the config.properties configuration file under " +
                "the current path.").build());
        OPTIONS.addOption(Option.builder().hasArg(true).longOpt("username").type(String.class).desc("Database " +
                "username.You can also specify the 'USERNAME' property in the config.properties configuration file " +
                "under the current path.").build());
        OPTIONS.addOption(Option.builder().hasArg(true).longOpt("passwd").type(String.class).desc("Database " +
                "password.You can also specify the 'PASSWD' property in the config.properties configuration file " +
                "under the current path.").build());
        // host
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(Option.builder("f").hasArg(false).longOpt("filed").type(String.class).desc("Gets all " +
                "the fields in the table.").build());
        optionGroup.addOption(Option.builder("i").hasArg(false).longOpt("insert").type(String.class).desc("Gets " +
                "dynamic insert statements for all fields in the table.").build());
        optionGroup.addOption(Option.builder("u").hasArg(false).longOpt("update").type(String.class).desc("Gets " +
                "dynamic update statements for all fields in the table.").build());
        optionGroup.setRequired(true);
        OPTIONS.addOptionGroup(optionGroup);
        try {
            commandLine = commandLineParser.parse(OPTIONS, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage() + "\n" + getHelpString());
            System.exit(0);//退出
        }
    }


    /**
     * get string of help usage
     *
     * @return help string
     */
    private static String getHelpString() {
        if (HELP_STRING == null) {
            HelpFormatter helpFormatter = new HelpFormatter();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "-[option]", null,
                    OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
        return HELP_STRING;
    }

    private static void loadPorperties() {
        File directory = new File("");
        String filePath = directory.getAbsolutePath();//设定为上级文件夹 获取绝对路径
        Properties prop = null;
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(filePath + "//config.properties"))); //方法1
            prop = new Properties();
            prop.load(new InputStreamReader(inputStream)); //加载格式化后的流
            IP = prop.getProperty("IP");
            USERNAME = prop.getProperty("USERNAME");
            PASSWD = prop.getProperty("PASSWD");
            DATABASE_NAME = prop.getProperty("DATABASE");
            TABLE_NAME = prop.getProperty("TABLE");
            PORT = prop.getProperty("PORT");
        } catch (IOException e) {
        }
        //加载参数中的配置，参数由于配置文件
        String port = commandLine.getOptionValue("port");
        String ip = commandLine.getOptionValue("ip");
        String table = commandLine.getOptionValue("table");
        String database = commandLine.getOptionValue("database");
        String username = commandLine.getOptionValue("username");
        String passwd = commandLine.getOptionValue("passwd");

        if (isNotEmpty(port)) {
            PORT = port;
        }
        if (isNotEmpty(ip)) {
            IP = ip;
        }
        if (isNotEmpty(table)) {
            TABLE_NAME = table;
        }
        if (isNotEmpty(table)) {
            DATABASE_NAME = database;
        }
        if (isNotEmpty(username)) {
            USERNAME = username;
        }
        if (isNotEmpty(passwd)) {
            PASSWD = passwd;
        }
    }

    public static boolean isNotEmpty(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return true;
    }

    private static void analyzeArgs() throws SQLException {
        TableInfo tableInfo = null;
        try {
            tableInfo = TableInfo.tableInfo(IP, PORT, USERNAME, PASSWD, DATABASE_NAME, TABLE_NAME);
        } catch (SQLException e) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Database connection failed, parameters are as follows:\n");
            errorMsg.append("IP:" + IP);
            errorMsg.append("\n");
            errorMsg.append("PORT:" + PORT);
            errorMsg.append("\n");
            errorMsg.append("USERNAME:" + USERNAME);
            errorMsg.append("\n");
            errorMsg.append("PASSWD:" + PASSWD);
            errorMsg.append("\n");
            errorMsg.append("DATABASE:" + DATABASE_NAME);
            errorMsg.append("\n");
            errorMsg.append("TABLE:" + TABLE_NAME);
            errorMsg.append("\n");
            System.out.println(errorMsg.toString());
            return;
        }
        for (Option option : commandLine.getOptions()) {
            if (option.getOpt() != null) {
                switch (option.getOpt()) {
                    case "i":
                        System.out.println(tableInfo.generateInsertSelective());
                        return;
                    case "u":
                        System.out.println(tableInfo.generateUpdate());
                        return;
                    case "f":
                        System.out.println(tableInfo.generateFiled());
                        return;
                    default:
                }
            }
        }
    }
}
