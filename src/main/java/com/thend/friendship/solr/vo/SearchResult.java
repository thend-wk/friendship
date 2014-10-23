package com.thend.friendship.solr.vo;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	
	private int total;
	
	private List<Long> hits = new ArrayList<Long>();

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Long> getHits() {
		return hits;
	}

	public void setHits(List<Long> hits) {
		this.hits = hits;
	}
}
