package com.potato.burritohunter.places;

public class Location
{
	private double lat = Double.MIN_VALUE;
	private double lng = Double.MIN_VALUE;

	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
}