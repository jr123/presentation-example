package org.example.httpgateway;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

/**
 * This is a simple Java verticle which HTTP requests and forwards them onto
 * the event bus.
 */
public class HTTPGatewayVerticle extends Verticle {

	private static int HTTP_LISTEN_PORT = 8080;

	/**
	 * Initial execution point of verticle.
	 */
	public void start() {
		// create new HTTP server, and assign a request handler
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(new HttpServerRequestHandler());
		httpServer.listen(HTTP_LISTEN_PORT);
	}
	
	/**
	 * Handles HTTP requests on server.
	 */
	private class HttpServerRequestHandler implements Handler<HttpServerRequest> {
	
		@Override
		public void handle(HttpServerRequest request) {
			RequestBodyHandler bodyHandler = new RequestBodyHandler(request);
			request.bodyHandler(bodyHandler);
			request.response().setChunked(true);
		}		
	}
	
	/**
	 * Method handles the body of post requests.
	 */
	private class RequestBodyHandler implements Handler<Buffer> {

		private HttpServerRequest request;
		
		public RequestBodyHandler(HttpServerRequest request) {
			this.request = request;
		}

		@Override
		public void handle(Buffer buffer) {
			if ("POST".equalsIgnoreCase(request.method())) {
				forwardMessage(request, buffer.getBytes());
			} else {
				request.response().setStatusCode(405).setStatusMessage("Method Not Allowed").end();
			}			
		}
		
		private void forwardMessage(HttpServerRequest request, byte[] bytes) {
			MultiMap params = request.params();
			String address = params.get("address");
			boolean send = shouldSend(params.get("sendOrPublish"));
			String message = new String(bytes);
			if (send) {
				vertx.eventBus().send(address, message);
			} else {
				vertx.eventBus().publish(address, message);
			}
			request.response().setStatusCode(200).write("Message Forwarded Successfully").end();
		}
		
		private boolean shouldSend(String sendOrPublish) {
			boolean send = true;
			if ("publish".equals(sendOrPublish)) {
				send = false;
			}
			return send;
		}
	}
}
