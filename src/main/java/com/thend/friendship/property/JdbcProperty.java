package com.thend.friendship.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="jdbc")
public class JdbcProperty {
	
	private String driver;
	private String masterUrl;
	private String masterUsername;
	private String masterPassword;
	private String slaveUrl;
	private String slaveUsername;
	private String slavePassword;
	private int maxIdleTime;
	private int minPoolSize;
	private int MaxPoolSize;
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
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
	public String getMasterUrl() {
		return masterUrl;
	}
	public void setMasterUrl(String masterUrl) {
		this.masterUrl = masterUrl;
	}
	public String getMasterUsername() {
		return masterUsername;
	}
	public void setMasterUsername(String masterUsername) {
		this.masterUsername = masterUsername;
	}
	public String getMasterPassword() {
		return masterPassword;
	}
	public void setMasterPassword(String masterPassword) {
		this.masterPassword = masterPassword;
	}
	public String getSlaveUrl() {
		return slaveUrl;
	}
	public void setSlaveUrl(String slaveUrl) {
		this.slaveUrl = slaveUrl;
	}
	public String getSlaveUsername() {
		return slaveUsername;
	}
	public void setSlaveUsername(String slaveUsername) {
		this.slaveUsername = slaveUsername;
	}
	public String getSlavePassword() {
		return slavePassword;
	}
	public void setSlavePassword(String slavePassword) {
		this.slavePassword = slavePassword;
	}
	
	
}
