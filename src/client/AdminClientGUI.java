/*
 AdminClientGUI.java
 - Admin-facing GUI client
 - Responsibilities:
   - Authenticate as admin
   - Start/stop draws, view/modify tickets, force-publish results
   - View server statistics and logs
   - Communicate via the same Request/Response protocol used by UserClientGUI
 - Placeholder with descriptive comments only.
*/

package client;

import protocol.MessageParser;
import protocol.Request;
import protocol.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AdminClientGUI extends JFrame {
    private JTextField hostField, portField, passwordField, winningNumField;
    private JButton connectBtn, setWinnerBtn, viewTicketsBtn, announceBtn;
    private JTextArea logArea;
    private JTable ticketTable;
    private DefaultTableModel tableModel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread readerThread;

    public AdminClientGUI() {
        super("Lottery Admin Client");
        initUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hostField = new JTextField("127.0.0.1", 12);
        portField = new JTextField("5000", 6);
        passwordField = new JTextField("admin123", 10);
        connectBtn = new JButton("Connect");

        connectBtn.addActionListener(this::onConnect);

        top.add(new JLabel("Host:"));
        top.add(hostField);
        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(new JLabel("Password:"));
        top.add(passwordField);
        top.add(connectBtn);

        JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        winningNumField = new JTextField("1523", 6);
        setWinnerBtn = new JButton("Set Winning Number");
        viewTicketsBtn = new JButton("View Tickets");
        announceBtn = new JButton("Announce Results");

        setWinnerBtn.setEnabled(false);
        viewTicketsBtn.setEnabled(false);
        announceBtn.setEnabled(false);

        setWinnerBtn.addActionListener(this::onSetWinner);
        viewTicketsBtn.addActionListener(this::onViewTickets);
        announceBtn.addActionListener(this::onAnnounce);

        middle.add(new JLabel("Winning Num:"));
        middle.add(winningNumField);
        middle.add(setWinnerBtn);
        middle.add(viewTicketsBtn);
        middle.add(announceBtn);

        tableModel = new DefaultTableModel(new String[]{"Ticket ID", "Username", "Number"}, 0);
        ticketTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(ticketTable);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(middle, BorderLayout.CENTER);
        getContentPane().add(tableScroll, BorderLayout.EAST);
        getContentPane().add(logScroll, BorderLayout.SOUTH);

        tableScroll.setPreferredSize(new Dimension(300, 400));
        logScroll.setPreferredSize(new Dimension(780, 150));
    }

    private void onConnect(ActionEvent e) {
        if (socket != null && socket.isConnected()) {
            append("Already connected");
            return;
        }

        String host = hostField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        String password = passwordField.getText().trim();

        append("Connecting to " + host + ":" + port);
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            readerThread = new Thread(this::readLoop);
            readerThread.setDaemon(true);
            readerThread.start();

            append("Connected, logging in as admin");
            Request request = new Request("adminLogin");
            request.setPassword(password);
            String json = MessageParser.toJson(request);
            send(json);

            setWinnerBtn.setEnabled(true);
            viewTicketsBtn.setEnabled(true);
            announceBtn.setEnabled(true);
        } catch (IOException ex) {
            append("Connection failed: " + ex.getMessage());
        }
    }

    private void onSetWinner(ActionEvent e) {
        int num = Integer.parseInt(winningNumField.getText().trim());
        Request request = new Request("setWinningNumber");
        request.setWinningNumber(num);
        String json = MessageParser.toJson(request);
        send(json);
        append("Sent setWinningNumber: " + num);
    }

    private void onViewTickets(ActionEvent e) {
        Request request = new Request("viewTickets");
        String json = MessageParser.toJson(request);
        send(json);
        append("Requested ticket list");
    }

    private void onAnnounce(ActionEvent e) {
        Request request = new Request("announceResults");
        String json = MessageParser.toJson(request);
        send(json);
        append("Announced results");
    }

    private void send(String line) {
        if (out == null) {
            append("Not connected");
            return;
        }
        out.println(line);
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                Response response = MessageParser.parseResponse(line);
                SwingUtilities.invokeLater(() -> processResponse(response));
            }
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> append("Disconnected: " + ex.getMessage()));
        } finally {
            closeResources();
        }
    }

    private void processResponse(Response response) {
        append("<Server> Status: " + response.getStatus() + ", Message: " + response.getMessage());
        // For viewTickets, we would need to extend Response to include ticket list
        // For now, just display the basic response
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {}
        if (out != null) out.close();
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        SwingUtilities.invokeLater(() -> append("Connection closed"));
    }

    private void append(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminClientGUI gui = new AdminClientGUI();
            gui.setVisible(true);
        });
    }
}
