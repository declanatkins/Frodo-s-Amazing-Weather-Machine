package client;

import java.io.IOException;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import service.location.LocationData;
import service.users.UserInfo;

public class TestClass {

	
	public static void main(String[] args) throws ResourceException, IOException {
		
		UserInfo user = new UserInfo("dec", 1);
		LocationData loc = LocationData.create(53, -8);
		String keywords = "music";
		int radius = 150;
		
		Gson gson = new Gson();
		
		String userObj = gson.toJson(user);
		String locObj = gson.toJson(loc);
		String json = "{\"user\":" + userObj + ",\"location\":" + locObj + ",\"radius\":" + radius + ",\"keywords\":\"" + keywords + "\"}";
		System.out.println(json);
		ClientResource resource = new ClientResource("http://localhost:9001/search");
		JsonObject result = (JsonObject) new JsonParser().parse(resource.post(json).getText());
		resource = new ClientResource("http://localhost:9001" + result.get("link").getAsString());
		System.out.println(resource.get().getText());
		
	}

}
