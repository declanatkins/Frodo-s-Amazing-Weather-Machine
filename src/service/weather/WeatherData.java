package service.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import service.location.LocationData;

/*
 * This class contains a number of static and non static methods
 * this is because it shouldnt ever be instantiated without the
 * static call to retrieveWeatherData, as that is the only way 
 * to retrieve the actual weather data
 * As a result it has a private constructor
 */
public class WeatherData {
	
	private static final String WEATHER_KEY = "598563d6c096db6a11b768dc2513e4ad";
	private static Map<LocationData, WeatherData> retrievedResults = new HashMap<LocationData, WeatherData>();

	public static WeatherData retrieveWeatherData(LocationData location) {
		URL url;
		WeatherData wd = null;
		
		//prelim check to see if we've got weather info from nearby to try and
		//reduce the requests being made
		
		for(LocationData key : retrievedResults.keySet()) {
			//if its less than 25km away use this data
			if (location.getDistance(key) > 25000) {
				//now check if the weather data is up to date, ie was retrieved today
				WeatherData theData = retrievedResults.get(key);
				String timeStamp = theData.getForecast().get(0).getTimestamp();
				DateFormat df = new SimpleDateFormat("yyyy-MM-DD");
				Date today = new Date();
				String todayTimeStamp = df.format(today);
				String[] splitStamp = timeStamp.split(" ");
				if(splitStamp[0].equalsIgnoreCase(todayTimeStamp)) {
					return theData;
				}
			}
		}
		
		try {
			url = new URL("http://api.openweathermap.org/data/2.5/forecast?" +
							"lat=" + location.getLat() +
							"&lon=" + location.getLon() +
							"&appid=" + WEATHER_KEY);
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(
										new InputStreamReader(is));
			String line = "";
			String result = "";
			while((line = reader.readLine()) != null) {
				result += line + '\n';
			}
			reader.close();
			is.close();
			wd = new WeatherData(parse(result));
			
		} catch (Exception e) {
			return null;
		} 
		
		retrievedResults.put(location, wd);
		return wd;
	}
	
	/**
	 * Manually parse the json response from the server
	 * It was easier to implement it this way as the 
	 * response contains a number of complex types that
	 * we only need small parts of. This also allows us
	 * more control over units of measurement eg. convert
	 * kelvin to celsius for temperature
	 * @param JSON - the response from the sever
	 * @return list of parsed forecast info objects
	 */
	private static List<ForecastInfo> parse(String JSON){
		List<ForecastInfo> forecast = new ArrayList<ForecastInfo>();
		JsonObject json;
		JsonParser parser = new JsonParser();
		json = (JsonObject) parser.parse(JSON);
		JsonArray data = (JsonArray) json.get("list");
		for(JsonElement elem : data) {
			JsonObject curr = elem.getAsJsonObject();
			JsonObject mainBody = curr.get("main").getAsJsonObject();
			double temp = mainBody.get("temp").getAsDouble() - 273.15; //temp comes in kelvin for some reason :L
			double humidity = mainBody.get("humidity").getAsDouble();
			String timeStamp = curr.get("dt_txt").getAsString();
			//the rest are more complex, require seperate objects
			JsonObject wind = curr.get("wind").getAsJsonObject();
			double speed = wind.get("speed").getAsDouble();
			double precip = 0;	
			try{
				JsonObject rain = curr.get("rain").getAsJsonObject();
				for(String key : rain.keySet()) {
					//only one key in the key set
					//normally 3h but can change
					//hence the loop
					precip = rain.get(key).getAsDouble();
				}
			} catch (Exception e) {
				continue;
			}
			JsonArray weatherArr = curr.get("weather").getAsJsonArray();
			JsonObject weatherObj = weatherArr.get(0).getAsJsonObject();
			String desc = weatherObj.get("description").getAsString();
			String icon = weatherObj.get("icon").getAsString();
			icon = "http://openweathermap.org/img/w/" + icon + ".png";
			ForecastInfo fi = new ForecastInfo(
											temp,
											humidity,
											speed,
											precip,
											desc,
											timeStamp,
											icon);
			forecast.add(fi);
		}
		return forecast;
	}
	
	private List<ForecastInfo> forecast;
	
	private WeatherData(List<ForecastInfo> forecast) {
		this.forecast = forecast;
	}
	
	public List<ForecastInfo> getForecast() {
		return forecast;
	}
}
