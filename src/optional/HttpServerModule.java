package optional;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServerModule {
    private HttpServer server;
    private int port;

    public HttpServerModule(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create endpoints
        server.createContext("/results", new ResultsHandler());
        server.createContext("/tickets", new TicketsHandler());
        server.createContext("/stats", new StatsHandler());

        server.setExecutor(null); // Use default executor
        server.start();

        FileLogger.getInstance().log("HTTP Server started on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            FileLogger.getInstance().log("HTTP Server stopped");
        }
    }

    // Handler for /results endpoint
    static class ResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "{\"winningNumber\": 1523, \"status\": \"Results will be available here\"}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();

                FileLogger.getInstance().log("HTTP GET /results served");
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Handler for /tickets endpoint
    static class TicketsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Simulate ticket data
                String response = "{\"tickets\": [" +
                        "{\"id\": 1, \"number\": 1234, \"purchased\": \"2024-01-15\"}," +
                        "{\"id\": 2, \"number\": 5678, \"purchased\": \"2024-01-15\"}," +
                        "{\"id\": 3, \"number\": 1523, \"purchased\": \"2024-01-16\"}" +
                        "], \"totalTickets\": 3}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();

                FileLogger.getInstance().log("HTTP GET /tickets served");
            } else if ("POST".equals(exchange.getRequestMethod())) {
                // Handle ticket purchase
                String response = "{\"status\": \"success\", \"message\": \"Ticket purchased successfully\", \"ticketId\": 123}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();

                FileLogger.getInstance().log("HTTP POST /tickets - New ticket purchased");
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Handler for /stats endpoint
    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Simulate statistics data
                String response = "{" +
                        "\"totalTicketsSold\": 1500," +
                        "\"totalRevenue\": 7500.00," +
                        "\"currentDraw\": \"2024-01-16\"," +
                        "\"nextDraw\": \"2024-01-20\"," +
                        "\"topWinningNumber\": 1523" +
                        "}";

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();

                FileLogger.getInstance().log("HTTP GET /stats served");
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Utility method for error responses
    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\", \"statusCode\": " + statusCode + "}";

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();

        FileLogger.getInstance().log("HTTP Error: " + statusCode + " - " + message);
    }

    // Utility method to parse query parameters
    private static Map<String, String> parseQueryParameters(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}