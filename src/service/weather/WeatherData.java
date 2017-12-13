package service.weather;

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

	public static WeatherData retrieveWeatherData(LocationData location) {
		URL url;
		WeatherData wd = null;
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
			e.printStackTrace();
		} 
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
			JsonObject rain = curr.get("rain").getAsJsonObject();
			double precip = 0;
			for(String key : rain.keySet()) {
				//only one key in the key set
				//normally 3h but can change
				//hence the loop
				precip = rain.get(key).getAsDouble();
			}
			JsonArray weatherArr = curr.get("weather").getAsJsonArray();
			JsonObject weatherObj = weatherArr.get(0).getAsJsonObject();
			String desc = weatherObj.get("description").getAsString();
			ForecastInfo fi = new ForecastInfo(
											temp,
											humidity,
											speed,
											precip,
											desc,
											timeStamp);
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
