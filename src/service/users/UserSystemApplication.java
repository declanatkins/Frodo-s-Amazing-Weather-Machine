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
						String input = request.getEntityAsText();
						input = input.replace("username=", "#");
						input = input.replace("&password=", "#");
						input = input.replace("}", "");
						String[] substr = input.split("#");
						UserInfo profile = database.getUserProfile(substr[1], substr[2]);
						users.put(profile.getName(), profile);
						assert(profile != null);
						String link = request.getResourceRef().getPath() + "/users/" + profile.getName();
						response.setEntity("{\"user\" : \"" + profile.getName()
										+ "\",\"link\" : \"" + link + "\"}" , MediaType.APPLICATION_JSON);
						response.setStatus(Status.SUCCESS_OK);
					}catch (AssertionError e) {
						response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
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
					try {
						assert(profile != null);
						response.setEntity(gson.toJson(profile), MediaType.APPLICATION_JSON);
						response.setStatus(Status.SUCCESS_OK);
					} catch (AssertionError e) {
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
