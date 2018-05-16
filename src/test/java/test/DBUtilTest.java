package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import dao.UserDao;
import entity.User;
import util.DBUtil;

/**
 * 测试DBUtill
 * 
 * @author liurenyou
 *
 */
public class DBUtilTest {
	/*
	 * PreparedStatement相比Statement更更适合执行动态(有条件)SQL 
	 * 使用PS执行DML语句
	 */
	@Test
	public void test() {
		int id = 8;
		String name = "辛";
		String gender = "男";
		int age = 23;
		String birthday = "8-8";
		int sal = 9000;
		String job = "manage";
		int deptno = 2;

		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = "insert into emp values (?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setString(2, name);
			ps.setString(3, gender);
			ps.setInt(4, age);
			ps.setString(5, birthday);
			ps.setInt(6, sal);
			ps.setString(7, job);
			ps.setInt(8, deptno);
			// ps发送DML参数(executeUpdate),执行SQL
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("增加员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}

	/*
	 * 使用PS执行DQL语句
	 */
	@Test
	public void test2() {
		int id = (int) (Math.random() * 7 + 1);
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			System.out.println(conn);
			String sql = "select * from emp where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			// 前参数是?的序列，后参数是对应?的值
			ps.setInt(1, id);
			/*
			 * 结果集 
			 * 1.结果集中存在指针(变量),它指向某一行数据 
			 * 2.调用get()就是从这一行获取数据的 
			 * 3.默认情况下指针指向第一行之上
			 * 4.每次调用next()指针向下移动一行 
			 * 5.最终指针会指向最后一行之下
			 * 6.当指针指向空行时,next()返回false,否则返回true
			 */
			// ps发送DQL参数(executeQuery)，执行SQL
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				System.out.println(rs.getInt("id"));
				System.out.println(rs.getString("name"));
				System.out.println(rs.getInt("sal"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}

	/*
	 * 使用PS执行查询，避免注入攻击
	 */
	@Test
	public void test3() {
		// 假设登录账号与密码
		String name = "liurenyou";
		String pwd = "123";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection();
			String sql = "select * from user where name=? and pwd=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, pwd);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				System.out.println("登陆成功");
			} else {
				System.out.println("登录失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询用户失败",e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/*
	 * 获取结果集元数据，并从该对象中获取相关信息
	 */
	@Test
	public void test4() {
		Connection conn = null;
		int id = 1;
		try {
			conn = DBUtil.getConnection();
			String sql =
					"select * from emp where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			//通过结果集获取其元数据，包含了对结果集的描述信息,即多少列、列名、列类型等
			ResultSetMetaData md = rs.getMetaData();
			System.out.println(md.getColumnCount());
			System.out.println(md.getColumnName(1));
			System.out.println(md.getColumnType(1));
			System.out.println(md.getColumnTypeName(1));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询用户失败",e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/*
	 * 模拟转账业务
	 * 假设用户已经登录了网银，要给别人转账N元，目前已经输入了收款账号和金额
	 * 
	 * 实现流程
	 * 1.查询付款方余额，检查余额是否够转账数目
	 * 2.检查收款方账号是否正确
	 * 3.修改付款方余额，减N元
	 * 4.修改收款方余额，加N元
	 */
	@Test
	public void transfer() {
		String payName = "shierrenyou";
		String recName = "liurenyou";
		double money = 2000.0;
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			/*
			 * JDBC会默认自动提交事务，在调用executeUpdate()时，
			 * 要想保证当前业务在同一个事务内，需要取消自动提交事
			 * 务，改为手动提交
			 */
			conn.setAutoCommit(false);
			//1.查询付款方余额
			String checkPayMoney = 
					"select money from user where name=?";
			PreparedStatement ps = conn.prepareStatement(checkPayMoney);
			ps.setString(1, payName);
			ResultSet rs = ps.executeQuery();
			double payMoney = 0.0;
			if(rs.next()) {
				payMoney = rs.getDouble("money");
				if(payMoney<money) {
					throw new SQLException("余额不足");
				}
			}
			//2.检验收款方账号是否正确
			String checkRecName = 
					"select money from user where name=?";
			PreparedStatement ps2 = conn.prepareStatement(checkRecName);
			ps2.setString(1, recName);
			ResultSet rs2 = ps2.executeQuery();
			double recMoney = 0.0;
			if(!rs2.next()) {
				throw new SQLException("收款方账号错误");
			} else {
				recMoney = rs2.getDouble("money");
			}
			//3.付款方付款
			String pay = 
					"update user set money=? where name=?";
			PreparedStatement ps3 = conn.prepareStatement(pay);
			ps3.setDouble(1, payMoney-money);
			ps3.setString(2, payName);
			ps3.executeUpdate();
			//4.收款方收款
			String rec = 
					"update user set money=? where name=?";
			PreparedStatement ps4 = conn.prepareStatement(rec);
			ps4.setDouble(1, recMoney+money);
			ps4.setString(2, recName);
			ps4.executeUpdate();
			//手动提交事务
			conn.commit();
		} catch (Exception e) {
			//发生异常时将数据回滚
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("回滚失败",e);
			}
			e.printStackTrace();
			throw new RuntimeException("转账失败",e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/*
	 * 利用ps批量插入数据
	 * 重点:addBatch() executeBatch() clearBatch() 
	 */
	@Test
	public void test5() {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false);
			String sql = 
					"insert into liangshan values(?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			for(int i=1;i<=108;i++) {
				ps.setInt(1, i);
				ps.setString(2, "好汉"+i);
				ps.setString(3, "打劫");
				ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
				ps.setDouble(5, (int)(Math.random()*1000));
				ps.addBatch();
				//每50人发送一次
				if(i%50==0) {
					ps.executeBatch();
					ps.clearBatch();
				}
				//为最后的8人再发送一次，因为是最后一次，就不用清空了
				ps.executeBatch();
				conn.commit();
			}
			
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("回滚失败",e);
			}
			e.printStackTrace();
			throw new RuntimeException("批量插入数据失败",e);
		} finally {
			DBUtil.close(conn);
		}
	} 
	
	/*
	 * 使用ps记录刚刚生成的主键(getGeneratedKeys())
	 */
	@Test
	public void test6() {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
					"insert into emp values(null,'壬','女',22,'9-9',9000,'director',1)";
			//第二个参数也可以直接用Statement.RETURN_GENERATED_KEYS获取主键
			PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"}); 
			ps.executeUpdate();
			//getGeneratedKeys()方法可以将本次添加数据的主键放进结果集中
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			System.out.println(rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("插入新数据失败",e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/*
	 * 测试UserDao.findUserByPage()
	 */
	@Test
	public void test7() {
		int loc = 0;
		int size = 4;
		UserDao dao = new UserDao();
		List<User> list = dao.findUserByPage(loc, size);
		for(User user : list) {
			System.out.print(user.getId()+" ");
			System.out.print(user.getName()+" ");
			System.out.print(user.getPwd()+" ");
			System.out.println(user.getMoney());
		}
	}
	
	/*
	 * 测试UserDao.addUser()
	 */
	@Test
	public void test8() {
		User user = new User();
		user.setName("ershisirenyou");;
		user.setPwd("789789789");
		user.setMoney(333.333);
		UserDao dao = new UserDao();
		dao.addUser(user);
	}
}
