package service.users;

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

import service.events.Event;

public class UserSystemApplication extends Application{
	
	private static DatabaseHandler database = new DatabaseHandler();
	private static Map<String, UserInfo> users = new HashMap<String, UserInfo>();
	private static Gson gson = new Gson();
	
	public Router createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/login", new Restlet() {
			
			public void handle(Request request, Response response) {
				if(request.getMethod() == Method.POST) {
					try {
						JsonParser parser = new JsonParser();
						JsonObject input = (JsonObject) parser.parse(request.getEntityAsText());
						UserInfo profile = database.getUserProfile(
														input.get("username").getAsString(),
														input.get("password").getAsString());
						if(profile != null) {
							users.put(profile.getName(), profile);
							String link = request.getResourceRef().getPath() + "/users/" + profile.getName();
							response.setEntity("{\"user\" : \"" + profile.getName()
											+ "\",\"link\" : \"" + link + "\"}" , MediaType.APPLICATION_JSON);
							response.setStatus(Status.SUCCESS_OK);
						}
						else {
							response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
						}
					}
					catch(Exception e) {
						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					}
				}
				else {
					response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
		});
		
		router.attach("/login/users/{user_name}", new Restlet() {
			
			public void handle(Request request, Response response) {
				if(request.getMethod() == Method.GET) {
					String userName = (String) request.getAttributes().get("user_name");
					UserInfo profile = users.get(userName);
					if (profile != null) {
						response.setEntity(gson.toJson(profile), MediaType.APPLICATION_JSON);
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
		
		router.attach("/register", new Restlet() {
			
			public void handle(Request request, Response response) {
				if(request.getMethod() == Method.POST) {
					JsonParser parser = new JsonParser();
					JsonObject input = (JsonObject) parser.parse(request.getEntityAsText());
					boolean success = database.insertNewUser(input.get("username").getAsString(),
															input.get("password").getAsString());
					if(success) {
						response.setStatus(Status.SUCCESS_OK);
					}
					else {
						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					}
				}
				else {
					response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
		});
		
		router.attach("/lastevent/{user_name}", new Restlet() {
			
			public void handle(Request request, Response response) {
				
				if (request.getMethod() == Method.GET) {
					try {
						String userName = (String) request.getAttributes().get("user_name");
						UserInfo profile = users.get(userName);
						Event last = database.getLastEvent(profile);
						if (last == null) {
							throw new Exception();
						}
						else {
							response.setStatus(Status.SUCCESS_OK);
							response.setEntity("{\"user\":\"" + userName + "\"," + 
											"\"keywords\":\"" + last.getCategories() + "\"}",
											MediaType.APPLICATION_JSON);
						}
					}
					catch(Exception e) {
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					}
						
				} else {
					response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				}
			}
		});
		
		router.attach("/lastevent/add/{user_name}", new Restlet() {
			
			public void handle(Request request, Response response) {
				if (request.getMethod() == Method.POST) {
					try {
						//this line is just to ensure that a valid event was 
						//posted it performs no assignments as the string will
						//be stored in the database
						gson.fromJson(request.getEntityAsText(), Event.class);
						String userName = (String) request.getAttributes().get("user_name");
						UserInfo profile = users.get(userName);
						if(database.insertEvent(profile.getID(), request.getEntityAsText())) {
							response.setStatus(Status.SUCCESS_OK);
						}
						else {
							throw new Exception();
						}
					}
					catch(Exception e) {
						response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
		UserSystemApplication app = new UserSystemApplication();
		app.getServices().add(corsService);
		
		Component component = new Component();
	    component.getServers().add(Protocol.HTTP, 9000);
	    component.getClients().add(Protocol.HTTP);
	    component.getDefaultHost().attach("", app);
	    component.start();
	}

}
