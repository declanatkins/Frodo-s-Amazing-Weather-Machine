package service.location;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;

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
	
	public double getDistance(LocationData otherLoc) {
		WGS84Point thisPoint = new WGS84Point(this.latitude,this.longitude);
		WGS84Point otherPoint = new WGS84Point(otherLoc.latitude,otherLoc.longitude);
		double distance = VincentyGeodesy.distanceInMeters(thisPoint, otherPoint);
		return distance;
	}
}
