package server;

import protocol.MessageParser;
import protocol.Request;
import protocol.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataManager dataManager;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, DataManager dataManager) {
        this.socket = socket;
        this.dataManager = dataManager;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Request request = MessageParser.parseRequest(inputLine);
                Response response = processRequest(request);
                String jsonResponse = MessageParser.toJson(response);
                out.println(jsonResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private Response processRequest(Request request) {
        String action = request.getAction();
        switch (action) {
            case "login":
                dataManager.loginUser(request.getUsername(), 100.0);
                Response loginResp = new Response("success", "Login successful");
                loginResp.setBalance(100.0);
                return loginResp;
            case "buyTicket":
                int[] ticketNumbers = dataManager.buyTicket(request.getUsername());
                if (ticketNumbers != null) {
                    Response buyResp = new Response("success", "Ticket purchased successfully");
                    buyResp.setTicketNumbers(ticketNumbers);
                    buyResp.setTicketId(dataManager.getLastTicketId());
                    buyResp.setBalance(dataManager.getUserBalance(request.getUsername()));
                    return buyResp;
                } else {
                    return new Response("error", "Insufficient balance");
                }
            case "checkResult":
                boolean won = dataManager.checkResult(request.getUsername());
                if (won) {
                    return new Response("success", "Congratulations! You won!");
                } else {
                    return new Response("success", "Better luck next time");
                }
            case "adminLogin":
                return new Response("success", "Admin authenticated");
            case "setWinningNumber":
                dataManager.setWinningNumber(request.getWinningNumber());
                return new Response("success", "Winning number set");
            case "viewTickets":
                // For simplicity, return a basic response; full list via JSON
                return new Response("success", "Tickets retrieved");
            case "announceResults":
                dataManager.announceResults();
                return new Response("success", "Results announced");
            default:
                return new Response("error", "Unknown action");
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
