package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import optional.HttpServerModule;
import optional.WebSocketServer;
import org.glassfish.tyrus.server.Server;

public class LotteryServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private DataManager dataManager;
    private HttpServerModule httpServer;
    private Server webSocketServer;
    private volatile boolean running;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(10);
        dataManager = new DataManager();

        // Start HTTP server on port 8080 for web frontend
        httpServer = new HttpServerModule(dataManager);
        httpServer.start(8080);

        // 🔴 Start WebSocket server on port 9090
        try {
            webSocketServer = new Server("localhost", 9090, "/ws", null, WebSocketServer.class);
            webSocketServer.start();
            System.out.println("🌐 WebSocket Server started on ws://localhost:9090/ws/lottery-updates");
            System.out.println("✅ Real-time updates enabled for winning numbers, ticket counts, and notifications");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to start WebSocket server: " + e.getMessage());
            e.printStackTrace();
        }

        running = true;

        System.out.println("Lottery Server started on port " + port);
        System.out.println("HTTP API Server started on port 8080");
        System.out.println("=====================================");
        System.out.println("  Lottery System Ready!");
        System.out.println("=====================================");

        while (running) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());
            threadPool.execute(new ClientHandler(clientSocket, dataManager));
        }
    }

    public void stop() throws IOException {
        running = false;
        
        // Stop WebSocket server
        if (webSocketServer != null) {
            try {
                WebSocketServer.closeAll();
                webSocketServer.stop();
                System.out.println("🔌 WebSocket server stopped");
            } catch (Exception e) {
                System.err.println("Error stopping WebSocket server: " + e.getMessage());
            }
        }
        
        if (httpServer != null) httpServer.stop();
        if (threadPool != null) threadPool.shutdown();
        if (serverSocket != null) serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        LotteryServer server = new LotteryServer();
        server.start(5000);
    }
}
