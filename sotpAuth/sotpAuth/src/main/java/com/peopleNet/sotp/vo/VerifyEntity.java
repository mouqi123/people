package com.peopleNet.sotp.vo;

import java.io.Serializable;

public class VerifyEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private int otpType;
	private int windowTime;
	private int nseconds;
	private String pin;
	private String challenge;
	private String dst;

	public VerifyEntity() {
	}

	public VerifyEntity(int otpType, int windowTime, int nseconds, String pin, String challenge, String dst) {
		this.otpType = otpType;
		this.windowTime = windowTime;
		this.nseconds = nseconds;
		this.pin = pin;
		this.challenge = challenge;
		this.dst = dst;
	}

	public int getOtpType() {
		return otpType;
	}

	public void setOtpType(int otpType) {
		this.otpType = otpType;
	}

	public int getWindowTime() {
		return windowTime;
	}

	public void setWindowTime(int windowTime) {
		this.windowTime = windowTime;
	}

	public int getNseconds() {
		return nseconds;
	}

	public void setNseconds(int nseconds) {
		this.nseconds = nseconds;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	@Override
	public String toString() {
		return "VerifyEntity{" + "otpType=" + otpType + ", windowTime=" + windowTime + ", nseconds=" + nseconds
		        + ", pin='" + pin + '\'' + ", challenge='" + challenge + '\'' + ", dst='" + dst + '\'' + '}';
	}
}
