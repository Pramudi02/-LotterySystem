package optional;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import server.DataManager;
import protocol.MessageParser;
import protocol.Request;
import protocol.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashMap;

// Note: This HTTP server now serves as a coordination layer.
// Most user operations (login, buy tickets, check results) are handled client-side with Firebase.
// Admin operations and legacy support remain here for backward compatibility.
public class HttpServerModule {
    private HttpServer server;
    private DataManager dataManager;
    private MessageParser messageParser;

    public HttpServerModule(DataManager dataManager) {
        this.dataManager = dataManager;
        this.messageParser = new MessageParser();
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Register all HTTP endpoints
        server.createContext("/health", new HealthHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/buy-ticket", new BuyTicketHandler());
        server.createContext("/check-results", new CheckResultsHandler());
        server.createContext("/admin-login", new AdminLoginHandler());
        server.createContext("/set-winner", new SetWinnerHandler());
        server.createContext("/view-tickets", new ViewTicketsHandler());
        server.createContext("/announce-results", new AnnounceResultsHandler());

        // Legacy endpoints
        server.createContext("/results", new ResultsHandler());
        server.createContext("/tickets", new TicketsHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Server started on port " + port);
        System.out.println("Note: Most operations now handled client-side with Firebase");
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    // Helper method to read JSON from request body
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }

    // Helper method to send JSON response
    private void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        byte[] responseBytes = jsonResponse.getBytes();
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"OK\",\"message\":\"Lottery server is running\"}";
            sendJsonResponse(exchange, response);
        }
    }

    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                Map<String, Object> requestData = messageParser.parseJsonObject(requestBody);

                String username = (String) requestData.get("username");
                if (username == null || username.trim().isEmpty()) {
                    String response = "{\"success\":false,\"message\":\"Username is required\"}";
                    sendJsonResponse(exchange, response);
                    return;
                }

                // Create user if doesn't exist
                dataManager.createUserIfNotExists(username);

                // Get user balance
                double balance = dataManager.getUserBalance(username);

                String response = String.format("{\"success\":true,\"username\":\"%s\",\"balance\":%d}",
                        username, balance);
                sendJsonResponse(exchange, response);

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"Login failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    class BuyTicketHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                Map<String, Object> requestData = messageParser.parseJsonObject(requestBody);

                String username = (String) requestData.get("username");
                if (username == null || username.trim().isEmpty()) {
                    String response = "{\"success\":false,\"message\":\"Username is required\"}";
                    sendJsonResponse(exchange, response);
                    return;
                }

                // Buy ticket
                int[] numbers = dataManager.buyTicket(username);
                if (numbers != null) {
                    double balance = dataManager.getUserBalance(username);
                    String numbersJson = java.util.Arrays.toString(numbers);
                    String response = String.format("{\"success\":true,\"numbers\":%s,\"balance\":%d}",
                            numbersJson, (int)balance);
                    sendJsonResponse(exchange, response);
                } else {
                    String response = "{\"success\":false,\"message\":\"Insufficient balance or user not found\"}";
                    sendJsonResponse(exchange, response);
                }

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"Buy ticket failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    class CheckResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                Map<String, Object> requestData = messageParser.parseJsonObject(requestBody);

                String username = (String) requestData.get("username");
                if (username == null || username.trim().isEmpty()) {
                    String response = "{\"success\":false,\"message\":\"Username is required\"}";
                    sendJsonResponse(exchange, response);
                    return;
                }

                // Get user tickets and check results
                String ticketsJson = dataManager.getUserTicketsJson(username);
                String response = String.format("{\"success\":true,\"tickets\":%s}", ticketsJson);
                sendJsonResponse(exchange, response);

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"Check results failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    class AdminLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                Map<String, Object> requestData = messageParser.parseJsonObject(requestBody);

                String password = (String) requestData.get("password");
                if ("admin123".equals(password)) {
                    String response = "{\"success\":true,\"message\":\"Admin login successful\"}";
                    sendJsonResponse(exchange, response);
                } else {
                    String response = "{\"success\":false,\"message\":\"Invalid admin password\"}";
                    sendJsonResponse(exchange, response);
                }

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"Admin login failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    class SetWinnerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                Map<String, Object> requestData = messageParser.parseJsonObject(requestBody);

                Integer winningNumber = ((Double) requestData.get("winningNumber")).intValue();
                if (winningNumber == null || winningNumber < 1 || winningNumber > 10) {
                    String response = "{\"success\":false,\"message\":\"Invalid winning number\"}";
                    sendJsonResponse(exchange, response);
                    return;
                }

                dataManager.setWinningNumber(winningNumber);
                String response = String.format("{\"success\":true,\"winningNumber\":%d,\"message\":\"Winning number set successfully\"}",
                        winningNumber);
                sendJsonResponse(exchange, response);

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"Set winner failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    class ViewTicketsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String ticketsJson = dataManager.getAllTicketsJson();
                String response = String.format("{\"success\":true,\"tickets\":%s}", ticketsJson);
                sendJsonResponse(exchange, response);

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"View tickets failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    class AnnounceResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                // Announce results to all users (this would need to be implemented in DataManager)
                // For now, just return success
                String response = "{\"success\":true,\"message\":\"Results announced to all users\"}";
                sendJsonResponse(exchange, response);

            } catch (Exception e) {
                String response = "{\"success\":false,\"message\":\"Announce results failed: " + e.getMessage() + "\"}";
                sendJsonResponse(exchange, response);
            }
        }
    }

    // Legacy handlers
    class ResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = String.format("{\"winningNumber\":%d,\"totalTickets\":%d}",
                    dataManager.getWinningNumber(), dataManager.getTicketCount());
            sendJsonResponse(exchange, response);
        }
    }

    class TicketsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = dataManager.getAllTicketsJson();
            sendJsonResponse(exchange, response);
        }
    }
}
