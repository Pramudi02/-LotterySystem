package client;

import protocol.MessageParser;
import protocol.Request;
import protocol.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * UserClientGUI
 * - Minimal Swing client for Member 2 responsibilities:
 *   - Establish TCP socket to server
 *   - Send JSON login and buyTicket requests
 *   - Display server responses asynchronously
 *
 * Note: This client is intentionally small and dependency-free so it can run
 * against a simple test server. The server must accept newline-terminated JSON
 * messages and reply with newline-terminated JSON responses.
 */
public class UserClientGUI extends JFrame {
  private JTextField hostField;
  private JTextField portField;
  private JTextField usernameField;
  private JButton connectBtn;
  private JButton loginBtn;
  private JButton buyTicketBtn;
  private JButton checkResultBtn;
  private JTextArea eventArea;

  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;
  private Thread readerThread;

  public UserClientGUI() {
    super("Lottery - User Client (Member 2)");
    initUI();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 400);
    setLocationRelativeTo(null);
  }

  private void initUI() {
    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    hostField = new JTextField("127.0.0.1", 12);
    portField = new JTextField("5555", 6);
    usernameField = new JTextField("alice", 10);
    connectBtn = new JButton("Connect");
    connectBtn.addActionListener(this::onConnect);

    top.add(new JLabel("Host:"));
    top.add(hostField);
    top.add(new JLabel("Port:"));
    top.add(portField);
    top.add(new JLabel("Username:"));
    top.add(usernameField);
    top.add(connectBtn);

    JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
    loginBtn = new JButton("Login");
    buyTicketBtn = new JButton("Buy Ticket");
    checkResultBtn = new JButton("Check Results");
    loginBtn.setEnabled(false);
    buyTicketBtn.setEnabled(false);
    checkResultBtn.setEnabled(false);

    loginBtn.addActionListener(this::onLogin);
    buyTicketBtn.addActionListener(this::onBuyTicket);
    checkResultBtn.addActionListener(this::onCheckResult);

    middle.add(loginBtn);
    middle.add(buyTicketBtn);
    middle.add(checkResultBtn);

    eventArea = new JTextArea();
    eventArea.setEditable(false);
    JScrollPane scroll = new JScrollPane(eventArea);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(top, BorderLayout.NORTH);
    getContentPane().add(middle, BorderLayout.CENTER);
    getContentPane().add(scroll, BorderLayout.SOUTH);
    scroll.setPreferredSize(new Dimension(580, 300));
  }

  private void onConnect(ActionEvent e) {
    if (socket != null && socket.isConnected() && !socket.isClosed()) {
      append("Already connected");
      return;
    }

    String host = hostField.getText().trim();
    int port = Integer.parseInt(portField.getText().trim());

    append("Connecting to " + host + ":" + port + " ...");
    try {
      socket = new Socket(host, port);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Start reader thread
      readerThread = new Thread(this::readLoop);
      readerThread.setDaemon(true);
      readerThread.start();

      append("Connected to server");
      loginBtn.setEnabled(true);
      buyTicketBtn.setEnabled(true);
      checkResultBtn.setEnabled(true);
    } catch (IOException ex) {
      append("Connection failed: " + ex.getMessage());
    }
  }

  private void onLogin(ActionEvent e) {
    String username = usernameField.getText().trim();
    if (username.isEmpty()) {
      append("Username required");
      return;
    }

    Request request = new Request("login");
    request.setUsername(username);
    String json = MessageParser.toJson(request);
    send(json);
    append("Sent login request for '" + username + "'");
  }

  private void onBuyTicket(ActionEvent e) {
    String username = usernameField.getText().trim();
    if (username.isEmpty()) {
      append("Username required");
      return;
    }

    Request request = new Request("buyTicket");
    request.setUsername(username);
    String json = MessageParser.toJson(request);
    send(json);
    append("Sent buyTicket request for '" + username + "'");
  }

  private void onCheckResult(ActionEvent e) {
    String username = usernameField.getText().trim();
    if (username.isEmpty()) {
      append("Username required");
      return;
    }

    Request request = new Request("checkResult");
    request.setUsername(username);
    String json = MessageParser.toJson(request);
    send(json);
    append("Sent checkResult request for '" + username + "'");
  }

  private void send(String line) {
    if (out == null) {
      append("Not connected to server");
      return;
    }
    out.println(line);
  }

  private void readLoop() {
    try {
      String line;
      while ((line = in.readLine()) != null) {
        Response response = MessageParser.parseResponse(line);
        final String msg = formatResponse(response);
        SwingUtilities.invokeLater(() -> append("<Server> " + msg));
      }
    } catch (IOException ex) {
      SwingUtilities.invokeLater(() -> append("Disconnected: " + ex.getMessage()));
    } finally {
      closeResources();
    }
  }

  private String formatResponse(Response response) {
    StringBuilder sb = new StringBuilder();
    sb.append("Status: ").append(response.getStatus());
    sb.append(", Message: ").append(response.getMessage());
    if (response.getBalance() != null) {
      sb.append(", Balance: ").append(response.getBalance());
    }
    if (response.getTicketNumber() != null) {
      sb.append(", Ticket Number: ").append(response.getTicketNumber());
    }
    if (response.getTicketId() != null) {
      sb.append(", Ticket ID: ").append(response.getTicketId());
    }
    return sb.toString();
  }

  private void closeResources() {
    try {
      if (in != null) in.close();
    } catch (IOException ignored) {
    }
    if (out != null) out.close();
    try {
      if (socket != null) socket.close();
    } catch (IOException ignored) {
    }
    SwingUtilities.invokeLater(() -> append("Connection closed"));
  }

  private void append(String s) {
    eventArea.append(s + "\n");
    eventArea.setCaretPosition(eventArea.getDocument().getLength());
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      UserClientGUI gui = new UserClientGUI();
      gui.setVisible(true);
    });
  }
}
