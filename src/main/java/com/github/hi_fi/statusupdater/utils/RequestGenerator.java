package com.github.hi_fi.statusupdater.utils;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

public class RequestGenerator {
	
	public static StringEntity createStringEntityFromString(String requestPayload) {
		try {
			return new StringEntity(requestPayload);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("Payload contained unknown characters. Original error: %s", e.getMessage()));
		}
	}

}
