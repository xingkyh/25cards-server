package impl;

import java.sql.*;

import dao.UserDAO;
import model.User;
import util.JDBCUtil;

public class UserDAOimpl implements UserDAO{
	private String result = "";
	private PreparedStatement ps=null;
	private Statement st = null;
	private ResultSet rs=null;
	@Override
	public String check(String username, String password) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT password FROM user WHERE username='"+username+"'";
		try {
			ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();	
			if(!rs.next()) {
				result = "No this Name, Please register!";
			}else {
				String realpass = rs.getString("password");
				if(!realpass.equals(password)) {
					result = "password wrong! ";
				}else {
					result = "Login Successful";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.realeaseAll(rs,null,conn,ps);
		}
		return result;
	}

	@Override
	public String register(User user) {
		Connection conn = null;
		String sql1 = "select * from user where username='"+user.getUsername()+"'";
		String sql = "Insert into user values(?,?,?)";
		try {
			conn = JDBCUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql1);
			if(rs.next()) {
				result = "this name is occupied, please think another one!";
			}else {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, user.getUsername());
				ps.setString(2, user.getPassword());
				ps.setString(3, user.getNickname());
				int rs = ps.executeUpdate();
				if(rs>0) {
					result = "Register Successful";		
				}else {
					result = "Register false";
				}
			}
		}catch(SQLException e1) {
			e1.printStackTrace();
		}finally {
			JDBCUtil.realeaseAll(rs,st,conn,ps);
		}
		return result;
	}
	
}
