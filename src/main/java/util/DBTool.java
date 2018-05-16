package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBTool {
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;

	static {
		// 只读取一次(加载驱动)
		Properties p = new Properties();
		try {
			p.load(DBTool.class.getClassLoader().getResourceAsStream("db.properties"));
			driver = p.getProperty("driver");
			url = p.getProperty("url");
			user = p.getProperty("user");
			pwd = p.getProperty("pwd");
			// 驱动只需要加载一次
			Class.forName(driver);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("找不到这个文件", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("找不到驱动类", e);
		}
	}

	// 创建一个新连接
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, pwd);
	}

	// 关闭连接
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭连接失败", e);
			}
		}
	}

}
