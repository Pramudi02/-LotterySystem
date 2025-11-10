package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import optional.HttpServerModule;

public class LotteryServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private DataManager dataManager;
    private HttpServerModule httpServer;
    private volatile boolean running;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(10);
        dataManager = new DataManager();

        // Start HTTP server on port 8080 for web frontend
        httpServer = new HttpServerModule(dataManager);
        httpServer.start(8080);

        running = true;

        System.out.println("Lottery Server started on port " + port);
        System.out.println("HTTP API Server started on port 8080");

        while (running) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());
            threadPool.execute(new ClientHandler(clientSocket, dataManager));
        }
    }

    public void stop() throws IOException {
        running = false;
        if (httpServer != null) httpServer.stop();
        if (threadPool != null) threadPool.shutdown();
        if (serverSocket != null) serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        LotteryServer server = new LotteryServer();
        server.start(5000);
    }
}
