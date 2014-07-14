package application;

import java.util.Observable;

public class LonLat extends Observable  {
	private String lon, lat;
	
	public LonLat() {
		super();
	}
	
	public String getLon() {
		return lon;
	}
	
	public void setLon(String lon) {
		this.lon = lon;
	}
	
	public String getLat() {
			return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}
			
	public void setLonLat(String lon, String lat) {
		this.lon = lon;
		this.lat = lat;

		setChanged();
		notifyObservers();
		
		System.out.println("setLonLat : " + lon + "," + lat);
	}		
	
	
}
