package com.people.sotp.interfaces.controller;

public class text {

	
	
	public static void main(String[] args) {
		
		String nonce_str = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		System.out.println(nonce_str);
	}
}
