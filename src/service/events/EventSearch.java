package service.events;

import java.util.ArrayList;
import java.util.List;

import service.location.LocationData;
import service.users.UserInfo;

public class EventSearch {
	
	public static final String units = "km";//always use kilometres
	private UserInfo user;
	private List<Event> results;
	private int radius;
	private LocationData location;
	private String keywords;
	
	public EventSearch(UserInfo user, int radius,
					LocationData location, String keywords) {
		this.user = user;
		this.radius = radius;
		this.location = location;
		this.keywords = keywords;
		results = new ArrayList<Event>();
	}
	
	public UserInfo getUser(){
		return user;
	}
	
	public List<Event> getResults(){
		return results;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	public LocationData getLocation() {
		return location;
	}
	
	public void addResults(List<Event> newResults) {
		results.addAll(newResults);
	}
}
