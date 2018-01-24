/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class SqlServer {

    private final JavaPlugin plugin;
    private final String database;
    private Connection conn = null;
    private final String ip;
    private final String id;
    private final String password;
    private final List<AutoCloseable> autoclose = new LinkedList(); //用于自动关闭连接
    private Statement s;
    private final File dir;

//    public static void main(String args[]) throws Exception {
//        String url = "jdbc:sqlserver://" + "127.0.0.1" + ":" + 1433 + ";databaseName=";
//        Connection conn = DriverManager.getConnection(url, "sa", "sqlserver597667");
//        Statement state = conn.createStatement();
//        state.execute("use LogAll");
//        ResultSet rs = state.executeQuery("select * from Teleport");
//        while (rs.next()) {
//            System.out.println(rs.getInt(2));
//        }
//        rs.close();
//        state.close();
//        conn.close();
//    }
    public SqlServer(JavaPlugin plugin, File dir, String database, String address, String user, String pwd) {
        this.plugin = plugin;
        this.dir = dir;
        this.database = database;
        this.ip = address;
        this.id = user;
        this.password = pwd;
    }

    /**
     * 获取指定表的列数
     *
     * @param table
     * @return
     * @throws SQLException
     */
    public int getColumnCountOfTable(String table) throws SQLException {
        ResultSet rs = s.executeQuery("select count(name) from syscolumns where id = object_id('" + table + "')");
        if (rs.next()) {
            int i = rs.getInt(1);
            rs.close();
            return i;
        } else {
            throw new SQLException("表" + table + "不存在");
        }
    }

    public int getRowCountOfTable(String table) throws SQLException {
        ResultSet rs = s.executeQuery("select count(*) from " + table);
        if (rs.next()) {
            int i = rs.getInt(1);
            rs.close();
            return i;
        } else {
            throw new SQLException("表" + table + "不存在");
        }
    }

    /**
     * 设置自动事物
     *
     * @param a
     * @throws SQLException
     */
    public void setAutoCommit(boolean a) throws SQLException {
        conn.setAutoCommit(a);
    }

    /**
     * 开始事物
     *
     * @throws SQLException
     */
    public void startTransaction() throws SQLException {
        s.execute("begin transaction");
    }

    /**
     * 提交事物
     *
     * @throws SQLException
     */
    public void commitTransaction() throws SQLException {
        s.execute("commit transaction");
    }

    /**
     * 回滚事物
     *
     * @throws SQLException
     */
    public void rollbackTransaction() throws SQLException {
        s.execute("rollback transaction");
    }

    public void connection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  //注册驱动
        String url = "jdbc:sqlserver://" + ip + ";databaseName=";
        conn = DriverManager.getConnection(url, id, password);
        autoclose.add(conn);
        s = conn.createStatement();
        autoclose.add(s);
        initDatabase();
        useDatabase(database);
    }

    public void useDatabase(String database) throws SQLException {
        s.execute("USE " + database);
    }

    private void initDatabase() throws SQLException {
        if (!databaseExist(database)) {
            //创建数据库
            if (!dir.exists()) {
                dir.mkdirs();
            }
            plugin.getLogger().log(Level.INFO, "数据库不存在，创建...");
            s.execute("create database " + database + " on primary("
                    + "name='" + database + "_data',"
                    + "filename='" + new File(dir, database + "_data.mdf").getPath() + "',"
                    + "size=5mb,filegrowth=10mb)"
                    + "log on("
                    + "name='" + database + "_log',"
                    + "filename='" + new File(dir, database + "_log.ldf").getPath() + "',"
                    + "size=1mb,maxsize=100mb,filegrowth=10mb)");
            plugin.getLogger().log(Level.INFO, "创建数据库:" + database);
            s.execute("ALTER DATABASE " + database + " SET RECOVERY SIMPLE");//设置为简单恢复模式
            plugin.getLogger().log(Level.INFO, "设置简单恢复模式");
            s.execute("EXEC sp_dboption '" + database + "', 'autoshrink', 'TRUE'");//设置数据库自动收缩
            plugin.getLogger().log(Level.INFO, "设置数据库自动收缩");
        }
    }

    public boolean hasResult(String sql) throws SQLException {
        ResultSet rs = s.executeQuery(sql);
        boolean re = rs.next();
        rs.close();
        return re;
    }

    public boolean tableExist(String table) throws SQLException {
        return hasResult("select * from sysobjects where name = '" + table + "'");
    }

    public boolean databaseExist(String database) throws SQLException {
        return hasResult("select * from master.dbo.sysdatabases where name = '" + database + "'");
    }

    public PreparedStatement getPreparedStatement(String sentence) throws SQLException {
        PreparedStatement p = conn.prepareStatement(sentence);
        autoclose.add(p);
        return p;
    }

    public PreparedStatement getPreparedStatement(String sentence, int resultSetType, int resultSetConcurrency) throws SQLException {
        PreparedStatement p = conn.prepareStatement(sentence, resultSetType, resultSetConcurrency);
        autoclose.add(p);
        return p;
    }

    public Statement getStatement() {
        return s;
    }

    public void close() {
        plugin.getLogger().log(Level.INFO, "关闭连接");
        for (int i = autoclose.size() - 1; i >= 0; i--) {
            close(autoclose.get(i));
        }
        autoclose.clear();
        plugin.getLogger().log(Level.INFO, "关闭完成");
    }

    private void close(AutoCloseable c) {
        try {
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "关闭连接出错" + c.getClass().getName());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
