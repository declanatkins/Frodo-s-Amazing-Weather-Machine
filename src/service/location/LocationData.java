package service.location;

public class LocationData {
	
	private int latitude;
	private int longitude;
	
	private LocationData(int lat, int lon) {
		latitude = lat;
		longitude = lon;
	}
	
	public static LocationData create(int lat, int lon) {
		return new LocationData(lat,lon);
	}
	
	public int getLat() {
		return latitude;
	}
	
	public int getLon() {
		return longitude;
	}
}
