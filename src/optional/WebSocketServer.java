package optional;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket Server for Real-Time Lottery Updates
 * Handles real-time communication for winning numbers, ticket counts, 
 * countdown timers, and winner notifications
 */
@ServerEndpoint("/lottery-updates")
public class WebSocketServer {
    // Thread-safe set of connected clients
    private static Set<Session> clients = new CopyOnWriteArraySet<>();
    
    /**
     * Called when a new client connects
     */
    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("✅ WebSocket client connected: " + session.getId());
        System.out.println("📊 Total connected clients: " + clients.size());
        
        // Send welcome message with current stats
        sendToSession(session, createMessage("CONNECTED", 
            String.format("{\"clientId\":\"%s\",\"totalClients\":%d}", 
            session.getId(), clients.size())));
        
        // Broadcast updated client count to all
        broadcastLiveStats();
    }
    
    /**
     * Called when a client disconnects
     */
    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("❌ WebSocket client disconnected: " + session.getId());
        System.out.println("📊 Remaining clients: " + clients.size());
        
        // Broadcast updated client count to all
        broadcastLiveStats();
    }
    
    /**
     * Called when an error occurs
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("⚠️ WebSocket error for session " + session.getId() + ": " + error.getMessage());
        error.printStackTrace();
    }
    
    /**
     * Called when a message is received from client
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("📨 Received from " + session.getId() + ": " + message);
        
        // Handle client messages (e.g., marking user as admin, subscribing to updates)
        if (message.contains("\"type\":\"IDENTIFY\"")) {
            // Extract user info and store in session properties
            // Example: {"type":"IDENTIFY","userId":"user123","isAdmin":true}
            if (message.contains("\"isAdmin\":true")) {
                session.getUserProperties().put("isAdmin", true);
                System.out.println("👑 Admin identified: " + session.getId());
            }
        }
    }
    
    /**
     * Broadcast winning number to all connected clients
     */
    public static void broadcastWinningNumber(int winningNumber) {
        String message = createMessage("WINNING_NUMBER", 
            String.format("{\"number\":%d,\"timestamp\":%d}", 
            winningNumber, System.currentTimeMillis()));
        
        broadcast(message);
        System.out.println("🎉 Broadcasted winning number: " + winningNumber + " to " + clients.size() + " clients");
    }
    
    /**
     * Broadcast ticket count update
     */
    public static void broadcastTicketCount(int totalTickets) {
        String message = createMessage("TICKET_COUNT", 
            String.format("{\"total\":%d,\"timestamp\":%d}", 
            totalTickets, System.currentTimeMillis()));
        
        broadcast(message);
    }
    
    /**
     * Broadcast countdown timer
     */
    public static void broadcastCountdown(int secondsRemaining) {
        String message = createMessage("COUNTDOWN", 
            String.format("{\"seconds\":%d,\"timestamp\":%d}", 
            secondsRemaining, System.currentTimeMillis()));
        
        broadcast(message);
    }
    
    /**
     * Broadcast live statistics (active users, tickets, jackpot)
     */
    public static void broadcastLiveStats() {
        broadcastLiveStats(0, 0.0);
    }
    
    public static void broadcastLiveStats(int totalTickets, double jackpot) {
        int activeUsers = clients.size();
        
        String message = createMessage("LIVE_STATS", 
            String.format("{\"activeUsers\":%d,\"totalTickets\":%d,\"jackpot\":%.2f,\"timestamp\":%d}", 
            activeUsers, totalTickets, jackpot, System.currentTimeMillis()));
        
        broadcast(message);
    }
    
    /**
     * Notify specific user that they won
     */
    public static void notifyWinner(String userId, int winningNumber, double prize) {
        String message = createMessage("YOU_WON", 
            String.format("{\"userId\":\"%s\",\"number\":%d,\"prize\":%.2f,\"timestamp\":%d}", 
            userId, winningNumber, prize, System.currentTimeMillis()));
        
        // Send to all clients (frontend will filter by userId)
        broadcast(message);
        System.out.println("🏆 Notified winner " + userId + " - Prize: $" + prize);
    }
    
    /**
     * Broadcast admin event (visible only to admins)
     */
    public static void broadcastToAdmins(String event, String details) {
        String message = createMessage("ADMIN_EVENT", 
            String.format("{\"event\":\"%s\",\"details\":\"%s\",\"timestamp\":%d}", 
            event, details, System.currentTimeMillis()));
        
        int adminCount = 0;
        for (Session session : clients) {
            if (Boolean.TRUE.equals(session.getUserProperties().get("isAdmin"))) {
                sendToSession(session, message);
                adminCount++;
            }
        }
        
        System.out.println("👑 Sent admin event to " + adminCount + " admins: " + event);
    }
    
    /**
     * Broadcast new ticket purchase event
     */
    public static void broadcastTicketPurchase(String username, int[] numbers) {
        String numbersJson = "[";
        for (int i = 0; i < numbers.length; i++) {
            numbersJson += numbers[i];
            if (i < numbers.length - 1) numbersJson += ",";
        }
        numbersJson += "]";
        
        String message = createMessage("TICKET_PURCHASED", 
            String.format("{\"username\":\"%s\",\"numbers\":%s,\"timestamp\":%d}", 
            username, numbersJson, System.currentTimeMillis()));
        
        broadcast(message);
    }
    
    /**
     * Broadcast system announcement
     */
    public static void broadcastAnnouncement(String title, String content, String type) {
        String message = createMessage("ANNOUNCEMENT", 
            String.format("{\"title\":\"%s\",\"content\":\"%s\",\"type\":\"%s\",\"timestamp\":%d}", 
            title, content, type, System.currentTimeMillis()));
        
        broadcast(message);
        System.out.println("📢 Broadcasted announcement: " + title);
    }
    
    /**
     * Helper: Create JSON message with type and data
     */
    private static String createMessage(String type, String data) {
        return String.format("{\"type\":\"%s\",\"data\":%s}", type, data);
    }
    
    /**
     * Helper: Broadcast message to all connected clients
     */
    private static void broadcast(String message) {
        int successCount = 0;
        int failCount = 0;
        
        for (Session session : clients) {
            if (sendToSession(session, message)) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        if (failCount > 0) {
            System.out.println("⚠️ Broadcast result: " + successCount + " success, " + failCount + " failed");
        }
    }
    
    /**
     * Helper: Send message to specific session
     */
    private static boolean sendToSession(Session session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                return true;
            } catch (IOException e) {
                System.err.println("❌ Failed to send to session " + session.getId() + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    /**
     * Get count of connected clients
     */
    public static int getConnectedClientsCount() {
        return clients.size();
    }
    
    /**
     * Close all connections
     */
    public static void closeAll() {
        System.out.println("🔌 Closing all WebSocket connections...");
        for (Session session : clients) {
            try {
                session.close();
            } catch (IOException e) {
                System.err.println("Error closing session: " + e.getMessage());
            }
        }
        clients.clear();
    }
}
