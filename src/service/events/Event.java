package service.events;

import service.location.LocationData;
import service.weather.ForecastInfo;

/*
 * Core class for an event returned from one of
 * the APIs
 */
public class Event {
	
	private String name;
	private String locationName;
	private LocationData location;
	private ForecastInfo forecast;
	private String dateTimeStamp;
	private String categories;
	private String source;
	private String imageHref;
	private String url;
	
	public Event(String name, String locationName,
				LocationData location, ForecastInfo forecast,
				String dateTimeStamp, String source,
				String categories, String imageHref,
				String url) {
		this.name = name;
		this.locationName = locationName;
		this.location = location;
		this.forecast = forecast;
		this.dateTimeStamp = dateTimeStamp;
		this.source = source;
		this.categories = categories;
		this.imageHref = imageHref;
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLocationName() {
		return locationName;
	}
	
	public LocationData getLocation() {
		return location;
	}
	
	public ForecastInfo getForecast() {
		return forecast;
	}
	
	public String getDateTime() {
		return dateTimeStamp;
	}
	
	public String getCategories(){
		return categories;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getImage() {
		return imageHref;
	}
	
	public String getUrl() {
		return url;
	}
}
