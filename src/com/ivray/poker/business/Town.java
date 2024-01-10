package com.ivray.poker.business;

import java.util.Objects;

public class Town {

	private String name;
	private String postalCode;
	private double longitude;
	private double latitude;
	
	public Town(String name, String postalCode, double longitude, double latitude) {
		super();
		this.name = name;
		this.postalCode = postalCode;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public int hashCode() {
		return Objects.hash(latitude, longitude, name, postalCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Town other = (Town) obj;
		return Double.doubleToLongBits(latitude) == Double.doubleToLongBits(other.latitude)
				&& Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude)
				&& Objects.equals(name, other.name) && Objects.equals(postalCode, other.postalCode);
	}

	@Override
	public String toString() {
		return "Town [name=" + name + ", postalCode=" + postalCode + ", longitude=" + longitude + ", latitude="
				+ latitude + "]";
	}
	
}
