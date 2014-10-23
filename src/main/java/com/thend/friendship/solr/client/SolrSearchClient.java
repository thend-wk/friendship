package com.thend.friendship.solr.client;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thend.friendship.solr.FieldConstant;
import com.thend.friendship.solr.FieldConstant.SearchOrder;
import com.thend.friendship.solr.vo.SearchResult;
import com.thend.friendship.utils.HttpUtil;
import com.thend.friendship.utils.JsonSerializer;
/**
 * solr搜索服务客户端
 * @author wangkai
 *
 */
public class SolrSearchClient {
	
	private static final Log logger = LogFactory.getLog(SolrSearchClient.class);
	
	private String solrSearchUrl;
	
	public SolrSearchClient(String solrSearchUrl) {
		this.solrSearchUrl = solrSearchUrl;
	}
	
	public SearchResult searchByFieldAnd(String[] fields, String[] values, String[] filterFields, String[] filterValues,
			int start, int limit, SearchOrder order, 
			boolean desc) {
		if(fields == null || values == null) {
			fields = new String[0];
			values = new String[0];
		}
		if(filterFields == null || filterValues == null) {
			filterFields = new String[0];
			filterValues = new String[0];
		}
		if(fields.length != values.length || filterFields.length != filterValues.length) {
			return new SearchResult();
		}
		String reqUrl = buildAndUrl(fields, values, filterFields, filterValues, start, limit, order, desc);
		return searchByUrl(reqUrl);
	}
	
	private String buildAndUrl(String[] fields, String[] values, String[] filterFields, String[] filterValues, 
			int start, int limit, SearchOrder order, boolean desc) {
		StringBuilder sb = new StringBuilder();
		int len = fields.length;
		if(len == 0) {
			sb.append("q=*:*");
		} else {
			sb.append("q=");
		}
		StringBuilder qFields = new StringBuilder();
		for(int i=0;i<len;i++) {
			qFields.append(fields[i]).append(":").append("\"" + values[i] + "\"").append(i==len-1?"":" AND ");
		}
		String encodedArgs = "";
		try {
			encodedArgs = URLEncoder.encode(qFields.toString(),"UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		sb.append(encodedArgs);
		sb.append("&start=" + start);
		sb.append("&rows=" + limit);
		sb.append("&fl=" + FieldConstant.USER_USERID);
		sb.append("&sort=" + order.getField() + "+" + (desc == true? "desc":"asc"));
		sb.append("&wt=json");
		len = filterFields.length;
		for(int i=0;i<len;i++) {
			try {
				sb.append("&fq=" + filterFields[i] + ":" + URLEncoder.encode(filterValues[i],"UTF-8"));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return solrSearchUrl + "?" + sb.toString();
	}
	
	public SearchResult searchByFieldOr(String[] fields, String[] values, String[] filterFields, String[] filterValues,
			int start, int limit, SearchOrder order, 
			boolean desc) {
		if(fields == null || values == null) {
			fields = new String[0];
			values = new String[0];
		}
		if(filterFields == null || filterValues == null) {
			filterFields = new String[0];
			filterValues = new String[0];
		}
		if(fields.length != values.length || filterFields.length != filterValues.length) {
			return new SearchResult();
		}
		String reqUrl = buildOrUrl(fields, values, filterFields, filterValues, start, limit, order, desc);
		return searchByUrl(reqUrl);
	}
	
	private String buildOrUrl(String[] fields, String[] values, String[] filterFields, String[] filterValues, 
			int start, int limit, SearchOrder order, boolean desc) {
		StringBuilder sb = new StringBuilder();
		int len = fields.length;
		if(len == 0) {
			sb.append("q=*:*");
		} else {
			sb.append("q=");
		}
		StringBuilder qFields = new StringBuilder();
		for(int i=0;i<len;i++) {
			qFields.append(fields[i]).append(":").append("\"" + values[i] + "\"").append(i==len-1?"":" OR ");
		}
		String encodedArgs = "";
		try {
			encodedArgs = URLEncoder.encode(qFields.toString(),"UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		sb.append(encodedArgs);
		sb.append("&start=" + start);
		sb.append("&rows=" + limit);
		sb.append("&fl=" + FieldConstant.USER_USERID);
		sb.append("&sort=" + order.getField() + "+" + (desc == true? "desc":"asc"));
		sb.append("&wt=json");
		len = filterFields.length;
		for(int i=0;i<len;i++) {
			try {
				sb.append("&fq=" + filterFields[i] + ":" + URLEncoder.encode("\"" + filterValues[i] +"\"","UTF-8"));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return solrSearchUrl + "?" + sb.toString();
	}
	
	private SearchResult searchByUrl(String url) {
		List<Long> hits = Collections.emptyList();
		SearchResult searchResult = new SearchResult();
		String json = HttpUtil.get(url);
		if(json != null) {
			try {
				Map<String,Object> jsonMap = JsonSerializer.fromJson(json, Map.class);
				Map<String,Object> docHitMap = (Map<String,Object>) jsonMap.get("response");
				int total = (Integer) docHitMap.get("numFound");
				List<Map<String,Object>> hitDocs = (List<Map<String,Object>>) docHitMap.get("docs");
				if(hitDocs != null && hitDocs.size() > 0) {
					hits = new ArrayList<Long>();
					for(Map<String,Object> hit : hitDocs) {
						Object userIdObj = hit.get(FieldConstant.USER_USERID);
						if(userIdObj instanceof Long) {
							hits.add((Long) userIdObj);
						} else if(userIdObj instanceof BigInteger) {
							hits.add(((BigInteger) userIdObj).longValue());
						}
					}
				}
				searchResult.setTotal(total);
				searchResult.setHits(hits);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return searchResult;
	}
}
