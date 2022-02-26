package pkg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnect {
	
	private static DBConnect db = new DBConnect();
	private Connection conn = null;
	
	private DBConnect(){}
	
	public static DBConnect getInstance(){
		return db;
	}
	
	public Connection getConnection(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/notify_tips", "root", "leon3859025");
				
		} catch(ClassNotFoundException e){
			e.printStackTrace();
			System.out.println("Error: Failed to load the driver");
				
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println("Error: Connection failed");
		}
		
		return conn;
	}
	
	public void exit(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error: Disconnection failed");
		}
	}
}
