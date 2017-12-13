package service.location;

import ch.hsr.geohash.GeoHash;

public class LocationData {
	
	private double latitude;
	private double longitude;
	private String geohash;
	
	private LocationData(double lat, double lon, String geo) {
		latitude = lat;
		longitude = lon;
		geohash = geo;
	}
	
	public static LocationData create(double lat, double lon) {
		GeoHash geo = GeoHash.withCharacterPrecision(lat, lon, 9);
		String hash = geo.toBase32();
		return new LocationData(lat,lon,hash);
	}
	
	public double getLat() {
		return latitude;
	}
	
	public double getLon() {
		return longitude;
	}
	
	public String getHash() {
		return geohash;
	}
}
