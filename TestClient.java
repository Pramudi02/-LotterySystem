import protocol.MessageParser;
import protocol.Request;
import protocol.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Test login
            Request loginReq = new Request("login");
            loginReq.setUsername("testuser");
            String json = MessageParser.toJson(loginReq);
            System.out.println("Sending: " + json);
            out.println(json);

            String response = in.readLine();
            System.out.println("Received: " + response);
            Response resp = MessageParser.parseResponse(response);
            System.out.println("Parsed: Status=" + resp.getStatus() + ", Message=" + resp.getMessage());

            // Test buy ticket
            Request buyReq = new Request("buyTicket");
            buyReq.setUsername("testuser");
            json = MessageParser.toJson(buyReq);
            System.out.println("Sending: " + json);
            out.println(json);

            response = in.readLine();
            System.out.println("Received: " + response);
            resp = MessageParser.parseResponse(response);
            System.out.println("Parsed: Status=" + resp.getStatus() + ", Message=" + resp.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}