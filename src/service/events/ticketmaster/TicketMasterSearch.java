package service.events.ticketmaster;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import service.events.Event;
import service.events.EventSearch;
import service.location.LocationData;
import service.weather.ForecastInfo;
import service.weather.WeatherData;

public class TicketMasterSearch {

	private static final String API_KEY = "lLShZiKoxGxMSjEJSNtzCXZFTEGgscGZ";
	private static final String SEARCH_ENDPOINT = "https://app.ticketmaster.com/discovery/v2/events.json?";
	private static final String VENUE_ENDPOINT = "https://app.ticketmaster.com/";
	
	public static EventSearch search(EventSearch search){
	
		String json = executeQuery(search);
		if(json == null) {
			return null;
		}
		List<Event> results = retrieveEvents(json);
		if(results != null) {
			search.addResults(results);
		}
		return search;
	}
	
	private static String executeQuery(EventSearch search) {
			try{
				String result = requestURL(SEARCH_ENDPOINT +
								"keyword=" + search.getKeywords() +
								"&radius=" + search.getRadius() +
								"&unit=" + EventSearch.units +
								"&geoPoint=" + search.getLocation().getHash() +
								"&apikey=" + API_KEY);
				
				return result;
			}
			catch(Exception e) {
				return "";
			}
	}
	
	private static List<Event> retrieveEvents(String JSON){
		List<Event> results = new ArrayList<Event>();
		JsonObject json;
		JsonParser parser = new JsonParser();
		json = (JsonObject) parser.parse(JSON);
		JsonArray events = null;
		try {
			JsonObject _embedded = json.get("_embedded").getAsJsonObject();
			events = _embedded.get("events").getAsJsonArray();
		} catch (Exception e) {
			return null;
		}
		for(JsonElement elem : events) {
			
			JsonObject thisEvent = elem.getAsJsonObject();
			String source = "ticketmaster";
			String name = thisEvent.get("name").getAsString();
			String url = thisEvent.get("url").getAsString();
			String categories = "";
			JsonArray cats = thisEvent.get("classifications").getAsJsonArray();
			for(JsonElement obj : cats) {
				JsonObject curr = obj.getAsJsonObject();
				if (curr.keySet().contains("genre")) {
					JsonObject cat = curr.get("genre").getAsJsonObject();
					categories = cat.get("name").getAsString();
					break;
				}
			}
			String datetime = "";
			try {
				JsonObject dates = thisEvent.get("dates").getAsJsonObject();
				JsonObject startDate = dates.get("start").getAsJsonObject();
				datetime = startDate.get("dateTime").getAsString();
				datetime = datetime.replace('T', ' ');
				datetime = datetime.replace("Z", "");
			}
			catch (Exception e){
				continue;
			}
			String imgURL = "";
			JsonArray images = thisEvent.get("images").getAsJsonArray();
			for(JsonElement imgElem : images) {
				JsonObject img = imgElem.getAsJsonObject();
				if(img.get("height").getAsInt()  == 56) {
					imgURL = img.get("url").getAsString();
					break;
				}
			}
			
			JsonObject _links = thisEvent.get("_links").getAsJsonObject();
			JsonArray venues = _links.get("venues").getAsJsonArray();
			String location = "";
			LocationData loc = null;
			for(JsonElement venElem : venues) {
				JsonObject venue = venElem.getAsJsonObject();
				try {
					String response = requestURL(VENUE_ENDPOINT +venue.get("href").getAsString() + "&apikey=" + API_KEY);
					JsonObject res = (JsonObject) parser.parse(response);
					location += res.get("name").getAsString() + ',';
					JsonObject city = res.get("city").getAsJsonObject();
					location += city.get("name").getAsString();
					JsonObject locObj = res.get("location").getAsJsonObject();
					loc = LocationData.create(Double.parseDouble(locObj.get("latitude").getAsString()),
											Double.parseDouble(locObj.get("longitude").getAsString()));
				} catch (Exception e) {
					continue;
				}
			}
			ForecastInfo theForecast = null;
			if(loc != null) {
				WeatherData wd = WeatherData.retrieveWeatherData(loc);
				for(ForecastInfo fi : wd.getForecast()) {
					String editedForecastStamp = fi.getTimestamp().replaceAll(":00:00", "");
					if(datetime.contains(editedForecastStamp)) {
						theForecast = fi;
					}
				}
			}
			
			results.add(new Event(name, location, loc, theForecast, datetime, source,
							categories, imgURL, url));
		}
		
		return results;
	}
	
	private static String requestURL(String url) throws Exception {
		URL venueURL = new URL(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
											venueURL.openStream()));
		String response = reader.readLine();
		reader.close();
		return response;
	}
}
