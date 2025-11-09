package server;

import optional.HttpServerModule;
import optional.FileLogger;

public class LotteryServer {
    private HttpServerModule httpServer;
    private DataManager dataManager;

    public void start(int port) throws IOException {
        // Initialize data manager
        dataManager = new DataManager();

        // Start HTTP server on port 8080
        try {
            httpServer = new HttpServerModule(8080, dataManager);
            httpServer.start();
        } catch (IOException e) {
            System.err.println("Failed to start HTTP server: " + e.getMessage());
        }

        // Start main TCP server
        ServerSocket serverSocket = new ServerSocket(port);
        // ... rest of your server code ...
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop();
        }
    }
}