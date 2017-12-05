package service.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			//create the tables if they do not exist
			Statement s = conn.createStatement();
			String queryUsers =//user table 
						"CREATE TABLE IF NOT EXISTS users (\n" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
						"user_name TEXT NOT NULL,\n" +
						"password TEXT NOT NULL\n);";
			String queryEvents = //events table
						"CREATE TABLE IF NOT EXISTS events (\n" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
						"userID INTEGER NOT NULL,\n" +
						"eventID INTEGER NOT NULL,\n" +
						"source TEXT NOT NULL\n);";
			String querySearches =//events searches
						"CREATE TABLE IF NOT EXISTS event_searches (\n" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
						"userID INTEGER NOT NULL,\n" +
						"params TEXT NOT NULL\n);";
			s.execute(queryUsers);
			s.execute(queryEvents);
			s.execute(querySearches);
		} catch (SQLException e) {
			System.err.println("Error verifying tables");
		}
	}
	
	public boolean insertSearch(String searchParams, int userID) {
		try {
			String query = 
						"INSERT INTO event_searches "+
						"(userID, params)\n" +
						"VALUES (?,?);";
			PreparedStatement prepState = conn.prepareStatement(query);
			prepState.setString(2, searchParams);
			prepState.setInt(1, userID);
			prepState.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error inserting search");
			return false;
		}
	}
	
	
	public static void main(String[] args) {
		DatabaseHandler dbh = new DatabaseHandler();
		dbh.insertSearch("test", 1);
	}
}
