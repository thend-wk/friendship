package com.thend.friendship.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="jdbc")
public class JdbcProperty {
	
	private String driver;
	private String url;
	private String username;
	private String password;
	private int maxIdleTime;
	private int minPoolSize;
	private int MaxPoolSize;
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMaxIdleTime() {
		return maxIdleTime;
	}
	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public int getMaxPoolSize() {
		return MaxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		MaxPoolSize = maxPoolSize;
	}
	
	
}
