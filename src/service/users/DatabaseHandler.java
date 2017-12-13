package service.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	
	private Connection conn;
	private static final String DB_URL = "jdbc:sqlite:Database/users.db";
	
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
						"eventID TEXT NOT NULL,\n" +
						"eventCats TEXT NOT NULL,\n" + 
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
			System.err.println("Error inserting search");
			return false;
		}
	}
	
	public UserInfo getUserProfile(String userName, String password) {
		try {
			String query = 
						"SELECT id FROM users\n" +
						"WHERE user_name = ? AND password = ?";
			PreparedStatement prepState = conn.prepareStatement(query);
			prepState.setString(1, userName);
			prepState.setString(2, password);
			ResultSet res = prepState.executeQuery();
			int userID = res.getInt("id");
			UserInfo profile = new UserInfo(userName, userID);
			return profile;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public boolean insertNewUser(String userName, String password) {
		try {
			//prelim test to see if user exists already
			UserInfo user = getUserProfile(userName,password);
			if (user != null) {
				return false;
			}
			String query = 
					"INSERT INTO users(user_name, password) \n" +
					"VALUES(?,?);";
			PreparedStatement prepState = conn.prepareStatement(query);
			prepState.setString(1, userName);
			prepState.setString(2, password);
			prepState.execute();
			return true;
		} catch (SQLException e ) {
			System.err.println("Failed inserting new user");
			return false;
		}
	}
	
	public boolean insertEvent(int userID, String eventID, String source) {
		try {
			String query = 
					"INSERT INTO events(userID,eventID,source)\n" +
					"VALUES(?,?,?)";
			PreparedStatement prepState = conn.prepareStatement(query);
			prepState.setInt(1, userID);
			prepState.setString(2, eventID);
			prepState.setString(3, source);
			prepState.execute();
			return true;
		} catch (SQLException e) {
			System.err.println("Failed inserting event");
			return false;
		}
	}
}
