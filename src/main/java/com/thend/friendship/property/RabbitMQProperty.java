package com.thend.friendship.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="rabbit")
public class RabbitMQProperty {
	
	private String uri;
	private String exchange;
	private String routingKey;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getRoutingKey() {
		return routingKey;
	}
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
	

}
