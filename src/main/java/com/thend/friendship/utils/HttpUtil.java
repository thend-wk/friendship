package com.thend.friendship.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	private static final int MAX_TOTAL_CONNECTIONS = 500;
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int SOCKET_TIMEOUT = 5000;
	private static final int CONNECTION_MANAGER_TIMEOUT = 2000;

	private static CloseableHttpClient client;
	private static RequestConfig requestConfig;
	private static Log log = LogFactory.getLog(HttpUtil.class);
	private static ExecutorService executorService;

	static {
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setCharset(Consts.UTF_8).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultConnectionConfig(connectionConfig);
		connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		requestConfig = RequestConfig.custom()
				.setConnectTimeout(CONNECTION_TIMEOUT)
				.setSocketTimeout(SOCKET_TIMEOUT)
				.setConnectionRequestTimeout(CONNECTION_MANAGER_TIMEOUT)
				.build();
		client = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).build();
		executorService = new ThreadPoolExecutor(0,2,10,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1000),new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static String get(String url) {
		HttpGet method = new HttpGet(url);
		try {
			CloseableHttpResponse response = client.execute(method);
			try {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return EntityUtils.toString(response.getEntity());
				}
				log.warn("http get:" + response.getStatusLine());
			} finally {
				response.close();
			}
		} catch (IOException e) {
			log.warn("HTTP POST请求发生异常:" + e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
		return String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}

	public static String post(String url, NameValuePair... params) {
		HttpPost method = new HttpPost(url);
		try {
			if (params != null) {
				method.setEntity(new UrlEncodedFormEntity(
						Arrays.asList(params), Consts.UTF_8));
			}
			CloseableHttpResponse response = client.execute(method);
			try {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return EntityUtils.toString(response.getEntity());
				}
			} finally {
				response.close();
			}
			log.warn("http post:" + response.getStatusLine());
		} catch (IOException e) {
			log.warn("HTTP POST请求发生异常:" + e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
		return String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}
	
	public static String postXML(String url, String body) {
		HttpPost method = new HttpPost(url);
		try {
			method.addHeader("Content-Type", "text/xml");
			method.setEntity(new StringEntity(body, Consts.UTF_8));
			CloseableHttpResponse response = client.execute(method);
			try {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return EntityUtils.toString(response.getEntity());
				}
			} finally {
				response.close();
			}
			log.warn("http post:" + response.getStatusLine());
		} catch (IOException e) {
			log.warn("HTTP POST请求发生异常:" + e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
		return String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}
	
	public static Future<String> postAsyn(final String url, final NameValuePair... params){
		return executorService.submit(new Callable<String>() {

			public String call() throws Exception {
				return post(url,params);
			}});
	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		String res = post(
				"http://localhost:8280/broadcast/cCurrency.do",
				new BasicNameValuePair("userId","1"));
		Future<String> future = postAsyn("http://localhost:8280/broadcast/cCurrency.do",
				new BasicNameValuePair("userId","4"));
		System.out.println("post asyn");
		System.out.println(res);
		System.out.println(future.get());
	}

}
