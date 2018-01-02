package service.events.eventbrite;

import java.util.ArrayList;
import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import service.events.Event;
import service.events.EventSearch;
import service.location.LocationData;
import service.weather.ForecastInfo;
import service.weather.WeatherData;

public class EventbriteSearch {
	
	private static final String TOKEN = "UDMN4MNC6ZUKF3CBU2HC";
	private static final String SEARCH_ENDPOINT = "https://www.eventbriteapi.com/v3/events/search/?";
	private static final String CATEGORY_ENDPOINT = "https://www.eventbriteapi.com/v3/categories/";
	private static final String VENUE_ENDPOINT = "https://www.eventbriteapi.com/v3/venues/";
	
	
	public static EventSearch search(EventSearch search) {
		
		String response = executeQuery(search);
		if(search != null) {
			search.addResults(parsePage(response));
		}
		return search;
	}
	
	private static String executeQuery(EventSearch search) {
		try {

			String response = requestURL(SEARCH_ENDPOINT + 
										"token=" + TOKEN +
										"&location.latitude=" +search.getLocation().getLat() +
										"&location.longitude=" + search.getLocation().getLon() +
										"&location.within=" + search.getRadius() + EventSearch.units +
										"&q=" + search.getKeywords() +
										"&sort_by=date");
			
			return response;
		} catch(Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Eventbrite keeps the results in pages but to avoid being rate limited, and
	 * to avoid long response times, we're only going to retrieve the first page
	 *
	 */
	private static String requestURL(String url) throws Exception {
		ClientResource res = new ClientResource(url);
		Representation rep = res.get();
		return rep.getText();
	}
	
	private static List<Event> parsePage(String page){
		List<Event> events = new ArrayList<Event>();
		JsonParser parser = new JsonParser();
		JsonObject base = (JsonObject) parser.parse(page);
		JsonArray eventList = base.get("events").getAsJsonArray();
		for(JsonElement thisElem : eventList) {
			JsonObject thisEvent = thisElem.getAsJsonObject();
			String name = thisEvent.get("name").getAsJsonObject()
											.get("text").getAsString();
			String url = thisEvent.get("url").getAsString();
			String source = "Eventbrite";
			//get date and time
			JsonObject dateObj = thisEvent.get("start").getAsJsonObject();
			String dateTimestamp = dateObj.get("local").getAsString();
			dateTimestamp = dateTimestamp.replace("T", " ");
			dateTimestamp = dateTimestamp.replace("Z", "");
			//get image
			String imageURL = "";
			try{
				imageURL = thisEvent.get("logo").getAsJsonObject().
							get("url").getAsString();
			} catch (Exception e) {
				continue;
			}
			//get category
			String category = "";
			try {
				String categoryID = thisEvent.get("category_id").getAsString();
				String catResp = requestURL(CATEGORY_ENDPOINT + categoryID + "/?" +
												"token=" + TOKEN);
				JsonObject catObj = (JsonObject) parser.parse(catResp);
				category = catObj.get("name").getAsString();
			} catch (Exception e) {
				continue;
			}
			//get location
			String venueID = thisEvent.get("venue_id").getAsString();
			LocationData location = null;
			String locationName = "";
			try {
				String venResp = requestURL(VENUE_ENDPOINT + venueID + "/?" +
													"token=" + TOKEN);
				JsonObject venObj = (JsonObject) parser.parse(venResp);
				locationName += venObj.get("name").getAsString();
				location = LocationData.create(venObj.get("latitude").getAsDouble(),
												venObj.get("longitude").getAsDouble());
				JsonObject addressObj = venObj.get("address").getAsJsonObject();
				locationName += addressObj.get("city").getAsString();
			} catch (Exception e) {
				continue;
			}
			//get weather data
			ForecastInfo theForecast = null;
			if(location != null) {
				WeatherData wd = WeatherData.retrieveWeatherData(location);
				for(ForecastInfo fi : wd.getForecast()) {
					String timeStampDate = dateTimestamp.split(" ")[0];
					String timeStampTime = dateTimestamp.split(" ")[1];
					String fiTimeStampDate = fi.getTimestamp().split(" ")[0];
					String fiTimeStampTime = fi.getTimestamp().split(" ")[1];
					if(timeStampDate.equalsIgnoreCase(fiTimeStampDate)) {
						int hour = Integer.parseInt(timeStampTime.split(":")[0]);
						int fiHour = Integer.parseInt(fiTimeStampTime.split(":")[0]);
						if (hour == fiHour || hour < fiHour+3) {
							theForecast = fi;
							break;
						}
					}
				}
			}
			
			events.add(new Event(name, locationName, location, theForecast, dateTimestamp, source,
					category, imageURL, url));
			
			
		}
		return events;
	}	
}
