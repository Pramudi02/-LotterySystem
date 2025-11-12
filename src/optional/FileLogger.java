package optional;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger {
    private PrintWriter writer;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileLogger(String filename) throws IOException {
        writer = new PrintWriter(new FileWriter(filename, true));
    }

    public void log(String event) {
        String timestamp = LocalDateTime.now().format(formatter);
        writer.println("[" + timestamp + "] " + event);
        writer.flush();
    }

    public void close() {
        if (writer != null) writer.close();
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        FileLogger logger = new FileLogger("lottery.log");
        logger.log("Server started");
        logger.log("Client connected");
        logger.close();
    }
}
