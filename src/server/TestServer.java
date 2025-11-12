package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final Map<String, Double> users = new ConcurrentHashMap<>();
    private final Map<Integer, Ticket> tickets = new ConcurrentHashMap<>();
    private int nextTicketId = 1001;
    private final Random rnd = new Random();

    public TestServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TestServer started on port " + port);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());
                pool.execute(() -> handleClient(client));
            }
        }
    }

    private void handleClient(Socket socket) {
        try (Socket s = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("<received> " + line);
                String response = process(line);
                out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
    }

    // Very small and forgiving JSON parsing for test purposes only
    private String getField(String json, String key) {
        String quotedKey = "\"" + key + "\"";
        int idx = json.indexOf(quotedKey);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + quotedKey.length());
        if (colon == -1) return null;
        int pos = colon + 1;
        // skip whitespace
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        if (pos >= json.length()) return null;
        char c = json.charAt(pos);
        if (c == '"') {
            int end = json.indexOf('"', pos + 1);
            if (end == -1) return null;
            return json.substring(pos + 1, end);
        } else {
            int end = pos;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(pos, end).trim();
        }
    }

    private String process(String json) {
        String action = getField(json, "action");
        if (action == null) {
            return "{\"status\":\"error\",\"message\":\"Missing action\"}";
        }

        switch (action) {
            case "login": {
                String username = getField(json, "username");
                if (username == null) return "{\"status\":\"error\",\"message\":\"Missing username\"}";
                users.putIfAbsent(username, 100.0);
                double bal = users.get(username);
                return String.format("{\"status\":\"success\",\"message\":\"Login successful\",\"balance\":%.2f}", bal);
            }
            case "buyTicket": {
                String username = getField(json, "username");
                if (username == null) return "{\"status\":\"error\",\"message\":\"Missing username\"}";
                Double bal = users.get(username);
                if (bal == null) return "{\"status\":\"error\",\"message\":\"User not logged in\"}";
                if (bal < 10.0) return "{\"status\":\"error\",\"message\":\"Insufficient balance\"}";

                int ticketNumber = 1000 + rnd.nextInt(9000);
                int ticketId = nextTicketId++;
                tickets.put(ticketId, new Ticket(ticketId, username, ticketNumber));
                users.put(username, bal - 10.0);

                return String.format("{\"status\":\"success\",\"ticketNumber\":%d,\"ticketId\":%d,\"newBalance\":%.2f,\"message\":\"Ticket purchased successfully\"}",
                        ticketNumber, ticketId, users.get(username));
            }
            case "checkResult": {
                // minimal behavior: always return no results
                return "{\"status\":\"success\",\"message\":\"No results available\"}";
            }
            default:
                return "{\"status\":\"error\",\"message\":\"Unknown action\"}";
        }
    }

    private static class Ticket {
        final int ticketId;
        final String username;
        final int ticketNumber;

        Ticket(int id, String user, int number) {
            this.ticketId = id;
            this.username = user;
            this.ticketNumber = number;
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 5555;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        TestServer server = new TestServer(port);
        server.start();
    }
}
