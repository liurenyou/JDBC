package util;
/**
 * 引入连接池来管理连接
 * 连接池代替了DriverManager,是DBTool的升级版
 * @author liurenyou
 *
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

public class DBUtil {
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;
	private static String initSize;
	private static String maxSize;
	private static BasicDataSource ds;	//连接池

	static {
		// 1.读取一次连接参数
		Properties p = new Properties();
		try {
			p.load(DBUtil.class.getClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("未找到文件", e);
		}
		driver = p.getProperty("driver");
		url = p.getProperty("url");
		user = p.getProperty("user");
		pwd = p.getProperty("pwd");
		initSize = p.getProperty("initSize");
		maxSize = p.getProperty("maxSize");
		//2.创建一个连接池
		ds = new BasicDataSource();
		//3.将连接参数设置给连接池
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pwd);
		ds.setInitialSize(new Integer(initSize));
		ds.setMaxActive(new Integer(maxSize));
	}
	
	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	
	/*
	 * 由连接池创建的连接，其close()被连接池改为归还的作用，
	 * 而不是真正的关闭连接，并且归还时，该内连接的数据会被
	 * 清空，状态重置为空闲态
	 */
	public static void close(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("归还连接失败",e);
			}
		}
	}
}
