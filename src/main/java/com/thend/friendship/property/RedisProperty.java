package com.thend.friendship.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="redis")
public class RedisProperty {
	
	private String host;
	private int port;
	private int soloMaxTotal;
	private int soloMaxIdle;
	private int shardedMaxTotal;
	private int shardedMaxIdle;
	private int maxWaitMillis;
	private boolean testOnBorrow;
	private String clusters;
	public int getSoloMaxTotal() {
		return soloMaxTotal;
	}
	public void setSoloMaxTotal(int soloMaxTotal) {
		this.soloMaxTotal = soloMaxTotal;
	}
	public int getSoloMaxIdle() {
		return soloMaxIdle;
	}
	public void setSoloMaxIdle(int soloMaxIdle) {
		this.soloMaxIdle = soloMaxIdle;
	}
	public int getShardedMaxTotal() {
		return shardedMaxTotal;
	}
	public void setShardedMaxTotal(int shardedMaxTotal) {
		this.shardedMaxTotal = shardedMaxTotal;
	}
	public int getShardedMaxIdle() {
		return shardedMaxIdle;
	}
	public void setShardedMaxIdle(int shardedMaxIdle) {
		this.shardedMaxIdle = shardedMaxIdle;
	}
	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}
	public void setMaxWaitMillis(int maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getClusters() {
		return clusters;
	}
	public void setClusters(String clusters) {
		this.clusters = clusters;
	}
	
	

}
