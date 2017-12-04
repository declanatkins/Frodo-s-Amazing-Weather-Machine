package service.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	
	private Connection conn;
	private static String DB_URL = "jdbc:sqlite:Database/users.db";
	
	public DatabaseHandler() {
		try {
			conn = DriverManager.getConnection(DB_URL);
		} catch (SQLException e) {
			System.err.println("Could not connect to the database");
		}
		
		try {
			Statement s = conn.createStatement();
			String query = "CREATE TABLE IF NOT EXISTS users (\n" +
						"id INTEGER PRIMARY KEY,\n" +
						"user_name TEXT NOT NULL,\n" +
						"password TEXT NOT NULL\n);";
			s.execute(query);
		} catch (SQLException e) {
			System.err.println("Error verifying tables");
		}
	}
	
	
	public static void main(String[] args) {
		DatabaseHandler dbh = new DatabaseHandler();
	}
}
