package protocol;

public class Response {
    private String status;
    private String message;
    private Double balance;
    private Integer ticketNumber;
    private Integer ticketId;
    private int[] ticketNumbers;

    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public Integer getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(Integer ticketNumber) { this.ticketNumber = ticketNumber; }
    public Integer getTicketId() { return ticketId; }
    public void setTicketId(Integer ticketId) { this.ticketId = ticketId; }
    public int[] getTicketNumbers() { return ticketNumbers != null ? ticketNumbers.clone() : null; }
    public void setTicketNumbers(int[] ticketNumbers) { this.ticketNumbers = ticketNumbers != null ? ticketNumbers.clone() : null; }
}
