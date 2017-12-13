package service.events;

import service.location.LocationData;
import service.weather.ForecastInfo;

/*
 * Core class for an event returned from one of
 * the APIs
 */
public class Event {
	
	public String name;
	public int price;
	public String locationName;
	public LocationData location;
	public ForecastInfo forecast;
	public String dateTimeStamp;
}
