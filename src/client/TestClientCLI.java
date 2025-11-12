package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TestClientCLI
 * - Simple command-line client that exercises the Member 2 actions:
 *   login, buyTicket, checkResult
 * - Useful when GUI cannot be displayed; prints server responses to console.
 */
public class TestClientCLI {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 5555;
        String username = "alice";
        if (args.length > 0) username = args[0];

        try (Socket s = new Socket(host, port);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            System.out.println("Connected to server " + host + ":" + port);

            String login = String.format("{\"action\":\"login\",\"username\":\"%s\",\"initialBalance\":100.0}", username);
            System.out.println("-> " + login);
            out.println(login);
            System.out.println("<- " + in.readLine());

            String buy = String.format("{\"action\":\"buyTicket\",\"username\":\"%s\"}", username);
            System.out.println("-> " + buy);
            out.println(buy);
            System.out.println("<- " + in.readLine());

            String check = String.format("{\"action\":\"checkResult\",\"username\":\"%s\"}", username);
            System.out.println("-> " + check);
            out.println(check);
            System.out.println("<- " + in.readLine());

            System.out.println("Done.");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
