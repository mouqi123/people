package com.peopleNet.sotp.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class ExhibitionLog implements Serializable {

	private static final long serialVersionUID = 1L;
	private String time;
	private String phoneNumber;
	private String place;
	private int business;
	private String center;
	private int status;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		// this.place = place;
		try {
			this.place = new String(place.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
	}

	public int getBusiness() {
		return business;
	}

	public void setBusiness(int business) {
		this.business = business;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		// this.center = center;
		try {
			this.center = new String(center.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
