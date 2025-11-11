/*
 Ticket.java
 - Data model for a purchased lottery ticket
 - Typical fields:
   - ticketId, userId, numbers (list/array), purchaseTime, drawId, status
   - methods: validation, getters/setters
 - Placeholder comment only.
*/

package model;

public class Ticket {
    private int ticketId;
    private String username;
    private int[] numbers;
    private long purchaseTime;

    public Ticket(int ticketId, String username, int[] numbers) {
        this.ticketId = ticketId;
        this.username = username;
        this.numbers = numbers.clone(); // Defensive copy
        this.purchaseTime = System.currentTimeMillis();
    }

    // Legacy constructor for backward compatibility
    public Ticket(int ticketId, String username, int ticketNumber) {
        this.ticketId = ticketId;
        this.username = username;
        this.numbers = new int[]{ticketNumber};
        this.purchaseTime = System.currentTimeMillis();
    }

    public int getTicketId() { return ticketId; }
    public String getUsername() { return username; }
    public int[] getNumbers() { return numbers.clone(); } // Defensive copy

    // For backward compatibility
    public int getTicketNumber() { return numbers.length > 0 ? numbers[0] : 0; }

    public long getPurchaseTime() { return purchaseTime; }
}
