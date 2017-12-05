package service.users;

import java.util.ArrayList;

import service.events.Event;
import service.events.EventSearch;

public class UserInfo {
	
	private String userName;
	private int userID;
	private ArrayList<Event> previousEvents;
	private ArrayList<EventSearch> previousSearches;
	
	public UserInfo(String userName, int userID) {
		this.userName = userName;
		this.userID = userID;
		previousEvents = null;
		previousSearches = null;
	}
	
	public void addEvent(Event e) {
		if(previousEvents == null) {
			previousEvents = new ArrayList<Event>();
		}
		previousEvents.add(e);
	}
	
	public void addSearch(EventSearch es) {
		if(previousSearches == null) {
			previousSearches = new ArrayList<EventSearch>();
		}
		previousSearches.add(es);
	}
	
	public int getID() {
		return userID;
	}
	
	public String getName() {
		return userName;
	}
	
}
