package com.thend.friendship.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="solr")
public class SolrProperty {
	
	private String pingUrl;
	private String searchUrl;
	public String getPingUrl() {
		return pingUrl;
	}
	public void setPingUrl(String pingUrl) {
		this.pingUrl = pingUrl;
	}
	public String getSearchUrl() {
		return searchUrl;
	}
	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}
}
