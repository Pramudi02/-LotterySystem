package protocol;

public class Request {
    private String action;
    private String username;
    private String password;
    private int winningNumber;

    public Request(String action) {
        this.action = action;
    }

    public String getAction() { return action; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getWinningNumber() { return winningNumber; }
    public void setWinningNumber(int winningNumber) { this.winningNumber = winningNumber; }
}
