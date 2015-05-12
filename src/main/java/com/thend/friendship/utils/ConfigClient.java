package com.thend.friendship.utils;

import java.io.File;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;

public class ConfigClient {
	
	private static CompositeConfiguration config;
	
	static {
		try {
			String resPath = ConfigClient.class.getResource("/").getPath();
			config = ConfigUtils.parsePropertyConfig(new File(resPath), new String[] {"config.property"});
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static String getString(String key) {
		return config.getString(key);
	}
	
	public static int getInteger(String key) {
		return config.getInt(key);
	}
	
	public static void main(String[] args) {
		System.out.println(config.getString("aa.bb"));
	}

}
