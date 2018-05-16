package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Test;

import util.DBTool;

/**
 * 测试DBTool
 * @author liurenyou
 */
public class DBToolTest {
	// 最普通的创建连接，执行DML语句使用数据库
	@Test
	public void test() {
		Connection conn = null;
		try {
			// 1.加载驱动:告诉DriverManager使用哪个驱动(jar)
			Class.forName("com.mysql.jdbc.Driver");
			// 2.创建连接
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8", "root", null);
			System.out.println(conn);
			// 3.创建Statement
			Statement smt = conn.createStatement();
			// 4.执行静态SQL(JDBC的SQL不能以分号结束)
			String sql = "insert into emp values(" + "8,'庚','男',22,'1992-01-02',8500,'sales',2" + ")";
			// 返回该SQL所影响的行数
			int rows = smt.executeUpdate(sql);
			System.out.println(rows);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("找不到驱动类", e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("创建连接失败", e);
		} finally {
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

	/*
	 * 演示如何使用Properties工具类读取db.properties中的参数
	 * 这个类本质上就是Map,Sun设计它是专门用来读properties文件的
	 */
	@Test
	public void test2() {
		Properties p = new Properties();
		try {
			// 1.任何类都可以获取ClassLoder
			// 2.ClassLoder默认从编辑路径(classes)下读取文件
			// 3.load()是将流中的数据读取到对象p里
			p.load(DBToolTest.class.getClassLoader().getResourceAsStream("db.properties"));
			// 从p中获取一个参数
			System.out.println(p.getProperty("driver"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("未找到文件", e);
		}
	}
	
	// 利用测试DBTool使用数据库
	@Test
	public void test3() {
		Connection conn = null;
		try {
			conn = DBTool.getConnection();
			System.out.println(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("创建失败",e);
		} finally {
			DBTool.close(conn);
		}
	}
}
