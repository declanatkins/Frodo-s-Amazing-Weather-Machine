package service.events;
/*
 * Class for event type,
 * This is used to specify what events
 * to recommend based on the weather
 */
public class EventType {
	
	public static final EventType OPEN_AIR = new EventType("open-air",35,5);
	private String name;
	private int minTemp;
	private int maxTemp;
	
	
	private EventType(String name, int maxT, int minT) {
		this.name = name;
		this.minTemp = minT;
		this.maxTemp = maxT;
	}
}
