package org.example.sockjs;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

/**
 * This is a simple Java verticle which acts as an EventBusBridge to SockJS/WebSocket clients. 
 */
public class SockJSServerVerticle extends Verticle {
	
	private static final int SOCKJS_SERVER_LISTEN_PORT = 8081;

	/**
	 * Initial execution point of verticle.
	 */
	public void start() {
		HttpServer server = vertx.createHttpServer();

		// serve up static resources if they are requested via HTTP
		server.requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
				if (request.path().equals("/")) {
					request.response().sendFile("index.html");
				}
				if (request.path().endsWith("vertxbus.js")) {
					request.response().sendFile("vertxbus.js");
				}
			}
		});

		// set up SockJS server
		SockJSServer sockJSServer = vertx.createSockJSServer(server);
		// allow clients to subscribe to any address they wish 
		// (usually this would be configured and locked down via a configuration file)
		JsonArray permitted = new JsonArray();
		JsonObject allAddress = new JsonObject();
		allAddress.putString("address_re", ".*");
		permitted.add(allAddress);
		sockJSServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);
		server.listen(SOCKJS_SERVER_LISTEN_PORT);
	}	
}
