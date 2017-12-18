package service.events;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import service.events.eventbrite.EventbriteSearch;
import service.events.ticketmaster.TicketMasterSearch;
import service.location.LocationData;
import service.users.UserInfo;


public class EventSystemApplication extends Application {
	
	private static Map<String, EventSearch> results = new HashMap<String, EventSearch>();
	private static Gson gson = new Gson();
	
	public Router createInboundRoot() {
		Router router = new Router(getContext());
		
		router.attach("/search", new Restlet() {
			
			public void handle(Request request, Response response) {
				if (request.getMethod() == Method.POST) {
					try {
						JsonParser parser = new JsonParser();
						JsonObject json = (JsonObject) parser.parse(request.getEntityAsText());
						JsonObject locationObj = json.get("location").getAsJsonObject();
						LocationData location = LocationData.create(locationObj.get("latitude").getAsDouble(),
																		locationObj.get("longitude").getAsDouble());
						String keywords = json.get("keywords").getAsString();
						int radius = json.get("radius").getAsInt();
						UserInfo user = gson.fromJson(json.get("user"), UserInfo.class);
						EventSearch theSearch = new EventSearch(user, radius, location, keywords);
						theSearch = TicketMasterSearch.search(theSearch);
						theSearch = EventbriteSearch.search(theSearch);
						results.put(user.getName(), theSearch);
						String link = request.getResourceRef().getPath() + "/retrieve/" + user.getName();
						response.setEntity("{\"link\" : \"" + link + "\"}" , MediaType.APPLICATION_JSON);
						response.setStatus(Status.SUCCESS_OK);
					} catch (Exception e) {
						e.printStackTrace();
						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					}
				}
				else {
					response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
		});
		
		router.attach("/search/retrieve/{user_name}", new Restlet() {
		
			public void handle(Request request, Response response) {
				if(request.getMethod() == Method.GET) {
					String userName = (String) request.getAttributes().get("user_name");
					EventSearch result = results.get(userName);
					if (result != null) {
						String resJson = "{ \"success\":\"true\", \"results\":[";
						boolean isFirst = true;
						for (Event e : result.getResults()) {
							if(!isFirst) {
								resJson += ',';
							}
							else {
								isFirst = false;
							}
							resJson += gson.toJson(e);
						}
						resJson += "]}";
						System.out.println(resJson);
						response.setEntity(resJson, MediaType.APPLICATION_JSON);
						response.setStatus(Status.SUCCESS_OK);
					}
					else {
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					}
				}
				else {
					response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
		});
		
		return router;
	}
	
	public static void main(String[] args) throws Exception {
		
		//Add cors to allow browsers to accept the response
		CorsService corsService = new CorsService();
		corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("*")));
		corsService.setAllowedCredentials(true);
		EventSystemApplication app = new EventSystemApplication();
		app.getServices().add(corsService);
		
		Component component = new Component();
	    component.getServers().add(Protocol.HTTP, 9001);
	    component.getClients().add(Protocol.HTTP);
	    component.getClients().add(Protocol.HTTPS);
	    component.getDefaultHost().attach("", app);
	    component.start();
	}
}
