package service.events;
/*
 * Core class for an event returned from one of
 * the APIs
 */
public class Event {
	
	private String name;
	private double[] location;
	private EventType type;
	private int price;
	
	public Event(String name, double[] loc, EventType t, int price) {
		this.name = name;
		this.location = loc;
		this.type = t;
		this.price = price;
	}
	
	public String toString() {
		String retStr = "Name = " + name + "\nLocation = " + location +
				"\nType = " + type.toString() + "\nPrice = €" + price;
		
		return retStr;
	}
	
}
