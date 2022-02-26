package pkg;

import java.sql.*;

public class DBOperation{
	
	private DBConnect dbconn;
	private Connection c;
	
	private PreparedStatement p;
	
	public DBOperation(){
		createInstance();
	}
	
	public void createInstance() {
		dbconn = DBConnect.getInstance();
	}
	
	public void Insert(String table, String values, Success isSuccess){
		
		c = dbconn.getConnection();
		
		String query = "INSERT INTO " + table + " VALUES " + values;
		try {
			p = c.prepareStatement(query);
			p.executeUpdate();
			isSuccess.successful = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Update(String table, String sets, String conditions, Success isSuccess){
		
		c = dbconn.getConnection();
		
		String query = "UPDATE " + table + " SET " + sets + " WHERE " + conditions;
		try {
			p = c.prepareStatement(query);
			p.executeUpdate();
			isSuccess.successful = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Delete(String table, String conditions, Success isSuccess){
		
		c = dbconn.getConnection();
		
		String query = "DELETE FROM " + table + " WHERE " + conditions;
		try {
			p = c.prepareStatement(query);
			p.executeUpdate();
			isSuccess.successful = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet Select(String attributes,
							String tables,
							String conditions,
							String group,
							String having,
							String order,
							Success isSuccess){
		
		c = dbconn.getConnection();
		ResultSet result = null;
		
		String query = "SELECT " + attributes + " FROM " + tables;
		
		if(!conditions.equals(""))
			query += (" WHERE " + conditions);
		if(!group.equals(""))
			query += (" GROUP BY " + group);
		if(!having.equals(""))
			query += (" HAVING " + having);
		if(!order.equals(""))
			query += (" ORDER BY " + order);
		
		try {
			p = c.prepareStatement(query);
			result = p.executeQuery();
			isSuccess.successful = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ResultSet Select(String query, Success isSuccess){
		
		c = dbconn.getConnection();
		ResultSet result = null;
		
		try {
			p = c.prepareStatement(query);
			result = p.executeQuery();
			isSuccess.successful = true;
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void Exit(){
		try {
			dbconn.exit();
			c.close();
			p.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Reset() {
		Exit();
		createInstance();
	}
}