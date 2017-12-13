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
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;


public class EventSystemApplication extends Application {
	
	private static Map<String, EventSearch> results = new HashMap<String, EventSearch>();
	
	
	public Router createInboundRoot() {
		Router router = new Router(getContext());
		
		router.attach("/search", new Restlet() {
			
			public void handle(Request request, Response response) {
				
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
	    component.getServers().add(Protocol.HTTP, 9000);
	    component.getClients().add(Protocol.HTTP);
	    component.getDefaultHost().attach("", app);
	    component.start();
	}
}
