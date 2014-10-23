package com.thend.friendship.solr.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;

import com.thend.friendship.utils.HttpUtil;
/**
 * solr ping服务客户端
 * @author wangkai
 *
 */
public class SolrPingClient {
	
	private ThreadPoolExecutor exec = new ThreadPoolExecutor(1, 10, 300, 
			TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
	
	private String solrPingUrl;
	
	private boolean commit;
	  
	private boolean optimize;
	  
	private String xmlDoc;
	
	public SolrPingClient(String url) {
		this.solrPingUrl = url;
	    this.commit = true;
	    this.optimize = false;
	}
	  
	public void post(String xmlDoc) {
	    post(xmlDoc,true,false);
	}
	
	public void postAsync(final String xmlDoc) {
		exec.execute(new Runnable() {
			public void run() {
				post(xmlDoc,true,false);
			}
		});
	}

	public void post(String xmlDoc, boolean commit, boolean optimize) {
	    this.commit = commit;
	    this.optimize = optimize;
	    this.xmlDoc = xmlDoc;
	    this.execute();
	}
	
	public void execute() {
		 boolean isRetry = true;
		 int retryCount = 3;
		 while(isRetry && retryCount-- > 0) {
			 String ret = HttpUtil.postXML(solrPingUrl, xmlDoc);
			 if(!ret.equalsIgnoreCase(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR))) {
				 isRetry = false;
				 if (this.commit) commit();
				 if (this.optimize) optimize();
			 } else {
				 System.out.println("retrying...");
			 }
		 }
	}

	  private void commit() {
		  doGet(appendParam(solrPingUrl, "commit=true"));
	  }

	  private void optimize() {
		  doGet(appendParam(solrPingUrl, "optimize=true"));
	  }

	  private String appendParam(String url, String param) {
		  String[] pa = param.split("&");
		  for (String p : pa) {
			  if(p.trim().length() != 0) {
				  String[] kv = p.split("=");
				  if (kv.length == 2) {
					  url = new StringBuilder().append(url).append((url.indexOf(63) > 0) ? "&" : "?").append(kv[0]).append("=").append(kv[1]).toString();
				  }
			  }
		  }
		  return url;
	  }

	  private void doGet(String url) {
		  HttpUtil.get(url);
	  }
}
