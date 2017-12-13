package service.events.ticketmaster;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import service.events.Event;
import service.location.LocationData;

public class TicketMasterSearch {

	private static final String API_KEY = "lLShZiKoxGxMSjEJSNtzCXZFTEGgscGZ";
	private static final String SEARCH_ENDPOINT = "https://app.ticketmaster.com/discovery/v2/events.json?";

	public static String search(String keywords,
							 LocationData location,
							 int radius,
							 String units){
		try{
			URL url = new URL(SEARCH_ENDPOINT +
							"keyword=" + keywords +
							"&radius=" + radius +
							"&unit=" + units +
							"&geoPoint=" + location.getHash() +
							"&apikey=" + API_KEY);
			
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = "";
			String result = "";
			while((line = reader.readLine()) != null){
				result += line;
			}
			reader.close();
			is.close();
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static List<Event> parse(String JSON){
		List<Event> results = new ArrayList<Event>();
		JsonObject json;
		JsonParser parser = new JsonParser();
		json = (JsonObject) parser.parse(JSON);
		JsonObject _embedded = json.get("_embedded").getAsJsonObject();
		JsonArray events = _embedded.get("events").getAsJsonArray();
		for(JsonElement e : events) {
		}
		
		return results;
	}
	
	public static void main(String[] a) {
		String res = search("music", LocationData.create(52, -8), 250, "km");
		parse(res);
	}
}
