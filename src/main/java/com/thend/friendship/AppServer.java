package com.thend.friendship;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;


public class AppServer
{
	private static final String DEFAULT_PORT = "8090";
	private static final String DEFAULT_ACCESS_LOG_PATH = "../logs/access-log.yyyy_mm_dd";
	private static final String WEB_XML = "webapp/WEB-INF/web.xml";
	private static final String CLASS_ONLY_AVAILABLE_IN_IDE = "com.sjl.IDE";
	private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/resources/webapp";

	private Server server;
	private int port;
	private String logpath;

	public AppServer(int port) {
		this.port = port;
	}

	public AppServer(int port, String logpath) {
		this.port = port;
		this.logpath = logpath;
	}

	public static interface WebContext {
		public File getWarPath();
		public String getContextPath();
	}

	public void start() throws Exception {
		server = new Server(new QueuedThreadPool(500));
		ServerConnector connector=new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
		server.setHandler(createHandlers());
		server.setStopAtShutdown(true);
		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	private HandlerCollection createHandlers() {
		WebAppContext ctx = new WebAppContext();
		ctx.setContextPath("/");
		if (isRunningInShadedJar()) {
			ctx.setWar(getShadedWarUrl());
		} else {
			ctx.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
		}
		List<Handler> handlers = new ArrayList<Handler>();
		handlers.add(ctx);

		HandlerList contexts = new HandlerList();
		contexts.setHandlers(handlers.toArray(new Handler[0]));

		RequestLogHandler logHandler = new RequestLogHandler();
		logHandler.setRequestLog(createRequestLog());

		HandlerCollection result = new HandlerCollection();
		result.setHandlers(new Handler[] { contexts, logHandler });

		return result;
	}

	private RequestLog createRequestLog() {
		NCSARequestLog log = new NCSARequestLog();
		File logPath = new File(logpath);
		logPath.getParentFile().mkdirs();
		log.setFilename(logPath.getPath());
		log.setRetainDays(90);
		log.setExtended(false);
		log.setAppend(true);
		log.setLogTimeZone("GMT+8");
		log.setLogLatency(true);
		return log;
	}

	private boolean isRunningInShadedJar() {
		try {
			Class.forName(CLASS_ONLY_AVAILABLE_IN_IDE);
			return false;
		} catch (ClassNotFoundException anExc) {
			return true;
		}
	}

	private URL getResource(String aResource) {
		return Thread.currentThread().getContextClassLoader()
				.getResource(aResource);
	}

	private String getShadedWarUrl() {
		String url = getResource(WEB_XML).toString();
		return url.substring(0, url.length() - 15);
	}

	public static void main(String[] args) throws Exception {
		// create the command line parser
		CommandLineParser parser = new BasicParser();

		// create the Options
		Options options = new Options();
		options.addOption("p", "port", true, "host port for connection");
		options.addOption("a", "accesslog", true, "the access log file");

		// parse the command line arguments
		CommandLine cmd = parser.parse(options, args);

		int port = Integer.parseInt(cmd.getOptionValue("port", DEFAULT_PORT));
		String logpath = cmd.getOptionValue("accesslog",
				DEFAULT_ACCESS_LOG_PATH);
		new AppServer(port, logpath).start();
	}
}
