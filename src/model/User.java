package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private double balance;
    private List<Integer> ticketIds;

    public User(String username, double balance) {
        this.username = username;
        this.balance = balance;
        this.ticketIds = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public List<Integer> getTicketIds() { return ticketIds; }
    public void addTicketId(int id) { ticketIds.add(id); }
}
