package server;

import model.User;
import model.Ticket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Note: This DataManager now serves as a coordination layer.
// Most data operations are handled client-side with Firebase/Firestore.
// This class maintains minimal state for server-side coordination.
public class DataManager {
    private ConcurrentHashMap<String, User> users;
    private ConcurrentHashMap<Integer, Ticket> tickets;
    private AtomicInteger ticketCounter;
    private volatile Integer winningNumber;

    public DataManager() {
        users = new ConcurrentHashMap<>();
        tickets = new ConcurrentHashMap<>();
        ticketCounter = new AtomicInteger(1000);
        winningNumber = null;
    }

    // Legacy methods for backward compatibility with existing TCP clients
    public void loginUser(String username, double initialBalance) {
        users.putIfAbsent(username, new User(username, initialBalance));
    }

    public void createUserIfNotExists(String username) {
        users.putIfAbsent(username, new User(username, 100.0)); // Start with 100 balance
    }

    // Note: buyTicket and other data operations are now handled client-side with Firebase
    // These methods remain for legacy TCP client support
    public int[] buyTicket(String username) {
        User user = users.get(username);
        if (user == null || user.getBalance() < 10.0) return null;

        // Generate 5 random numbers between 1-100
        int[] numbers = new int[5];
        for (int i = 0; i < 5; i++) {
            numbers[i] = 1 + (int)(Math.random() * 100);
        }

        int ticketId = ticketCounter.getAndIncrement();
        Ticket ticket = new Ticket(ticketId, username, numbers);
        tickets.put(ticketId, ticket);
        user.setBalance(user.getBalance() - 10.0);
        user.addTicketId(ticketId);

        return numbers;
    }

    public void setWinningNumber(int number) {
        this.winningNumber = number;
    }

    public boolean checkResult(String username) {
        if (winningNumber == null) return false;
        User user = users.get(username);
        if (user == null) return false;
        for (int ticketId : user.getTicketIds()) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket != null && ticket.getTicketNumber() == winningNumber) {
                return true;
            }
        }
        return false;
    }

    public int getUserTicket(String username) {
        User user = users.get(username);
        if (user != null && !user.getTicketIds().isEmpty()) {
            int lastTicketId = user.getTicketIds().get(user.getTicketIds().size() - 1);
            Ticket ticket = tickets.get(lastTicketId);
            return ticket != null ? ticket.getTicketNumber() : 0;
        }
        return 0;
    }

    public double getUserBalance(String username) {
        User user = users.get(username);
        return user != null ? user.getBalance() : 0.0;
    }

    public int getLastTicketId() {
        return ticketCounter.get() - 1;
    }

    public int getWinningNumber() {
        return winningNumber != null ? winningNumber : 0;
    }

    // Legacy JSON methods for backward compatibility
    public String getAllTicketsJson() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Ticket ticket : tickets.values()) {
            if (!first) sb.append(",");
            sb.append(String.format("{\"id\":%d,\"username\":\"%s\",\"numbers\":%s,\"purchaseTime\":\"%s\"}",
                    ticket.getTicketId(),
                    ticket.getUsername(),
                    java.util.Arrays.toString(ticket.getNumbers()),
                    java.time.Instant.now().toString())); // Simple timestamp
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public void announceResults() {
        // In Firebase implementation, results are automatically visible to all clients
        // This method can be used for server-side notifications if needed
        System.out.println("Results announced to all users via Firebase");
    }

    public String getUserTicketsJson(String username) {
        User user = users.get(username);
        if (user == null) return "[]";

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (int ticketId : user.getTicketIds()) {
            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                if (!first) sb.append(",");
                boolean won = winningNumber != null && ticket.getTicketNumber() == winningNumber;
                int prize = won ? 100 : 0; // Simple prize logic
                sb.append(String.format("{\"id\":%d,\"numbers\":%s,\"won\":%b,\"prize\":%d}",
                        ticket.getTicketId(),
                        java.util.Arrays.toString(ticket.getNumbers()),
                        won,
                        prize));
                first = false;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public int getTicketCount() {
        return tickets.size();
    }
}
