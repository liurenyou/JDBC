package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.User;
import util.DBUtil;

/**
 * Dao:数据访问对象 是一个接口
 * @author liurenyou
 *
 */
public class UserDao implements Serializable {
	private static final long serialVersionUID = 1L;

	//按工资排序分页查询员工
	public List<User> findUserByPage(int loc, int size) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String pag = "select * from (select * from user order by money) u limit ?,?";
			PreparedStatement ps = conn.prepareStatement(pag);
			ps.setInt(1, loc);
			ps.setInt(2, size);
			ResultSet rs = ps.executeQuery();
			List<User> list = new ArrayList<User>();
			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setName(rs.getString("name"));
				user.setPwd(rs.getString("pwd"));
				user.setMoney(rs.getDouble("money"));
				list.add(user);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("按工资分页查询员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	//增加员工
	public void addUser(User user) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String add = 
					"insert into user values(null,?,?,?);";
			PreparedStatement ps = conn.prepareStatement(add);
			ps.setString(1, user.getName());
			ps.setString(2, user.getPwd());
			ps.setDouble(3, user.getMoney());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("增加员工失败",e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
}
