package com.qq.dao;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.qq.model.Community;
import com.qq.model.Jkuser;
import com.qq.util.GenericsUtils;
import com.qq.util.JdbcUtil;

public class BaseJdbcDao<T> {

	private Class entityClass;

	public Class getEntityClass() {
		if (entityClass == null) {
			entityClass = GenericsUtils.getGenericClass(this.getClass());
		}
		return entityClass;
	}

	public Object findById(Integer id) {

		return null;
	}

	/**
	 * 插入一个Object
	 * 
	 * @param obj
	 * @return
	 */
	public int save(Object obj) {
		String tableName = getEntityClass().getSimpleName();
		String sql = "insert into " + tableName + " values(";
		String sql1 = "select * from " + tableName;
		Connection conn = JdbcUtil.getConnection();
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int result = 0;
		int columnCount = 0;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql1);
			ResultSetMetaData rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				sql += "?,";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += ")";

			ps = conn.prepareStatement(sql);

			for (int i = 1; i <= columnCount; i++) {
				String colName = rsmd.getColumnName(i);
				String getMethodName = getter(colName);
				Method getMethod = getEntityClass().getDeclaredMethod(
						getMethodName, null);
				ps.setObject(i, getMethod.invoke(obj, null));
			}

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.closeAll(conn, st, rs);
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 更新一个对象的信息 根据id区别不同的对象信息
	 * 
	 * @param obj
	 * @return
	 */
	public int update(Object obj) throws Exception {
		String tableName = getEntityClass().getSimpleName();
		String sql1 = "select * from " + tableName;
		String sql = "update " + tableName + " set ";
		Connection conn = JdbcUtil.getConnection();
		ResultSet rs = null;
		PreparedStatement ps = null;
		Statement st = null;
		int result = 0;
		int columnCount = 0;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql1);
			ResultSetMetaData rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();

			for (int i = 2; i <= columnCount; i++) {
				sql += rsmd.getColumnName(i) + "=?,";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += " where " + rsmd.getColumnName(1) + "=" + "?";

			ps = conn.prepareStatement(sql);

			for (int i = 2; i <= columnCount; i++) {
				String colName = rsmd.getColumnName(i);
				String getMethodName = getter(colName);
				Method getMethod = getEntityClass().getDeclaredMethod(
						getMethodName, null);
				ps.setObject(i - 1, getMethod.invoke(obj, null));
			}
			// 最后反射添加ID
			String colName = rsmd.getColumnName(1);
			String getMethodName = getter(colName);
			Method getMethod = getEntityClass().getDeclaredMethod(
					getMethodName, null);
			ps.setObject(columnCount, getMethod.invoke(obj, null));
			result = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.closeAll(conn, st, rs);
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	
	
	/**
	 * 根据id查找群或者用户
	 * @param classify
	 * @param findId
	 * @return
	 * @throws SQLException 
	 */
	public static Object findById(byte classify, int findId) throws SQLException {
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		if(classify == 1) {
			sql = "select * from jkuser where jknum = ?";
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1, findId);
				rs = ps.executeQuery();
				if(rs.next()) {
					Jkuser jkuser = new Jkuser();
					jkuser.setJknum(rs.getInt(1));
					jkuser.setName(rs.getString(2));
					jkuser.setSignature(rs.getString(4));
					String path = rs.getString(5);
					if(path!=null && !path.equals("")) {
						File file = new File(path);
						jkuser.setIconpath(file);
					}
					jkuser.setSite(rs.getString(6));
					jkuser.setPhone(rs.getString(7));
					jkuser.setEmail(rs.getString(8));
					jkuser.setSex(rs.getInt(11));					
					return jkuser;
				} else {
					return null;
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if(classify == 2) {
			sql = "select * from community where cid = ?";
			ps = conn.prepareStatement(sql);
			ps.setObject(1, findId);
			rs = ps.executeQuery();
			if(rs.next()) {
				Community community = new Community();
				community.setCid(rs.getInt(1));
				community.setName(rs.getString(2));
				community.setOwner(rs.getInt(3));
				community.setDes(rs.getString(4));
				String path = rs.getString(5);
				if(path!=null && !path.equals("")) {
					File file = new File(path);
					community.setIconpath(file);
				}
				return community;
			} else {
				return null;
			}
		}
		return null;
		
	}
	
	
	
	
	
	/**
	 * getter方法
	 * 
	 * @param args
	 */
	private static String getter(String colName) {
		return "get" + colName.substring(0, 1).toUpperCase()
				+ colName.substring(1).toLowerCase();
	}
	
	/**
	 * setter方法
	 * 
	 * @param args
	 */
	private static String setter(String colName) {
		return "set" + colName.substring(0, 1).toUpperCase()
				+ colName.substring(1).toLowerCase();
	}

	
}
