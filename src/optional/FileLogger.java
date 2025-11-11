package optional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger {
    private static final String LOG_FILE = "lottery_system.log";
    private static FileLogger instance;
    private PrintWriter writer;

    private FileLogger() {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized FileLogger getInstance() {
        if (instance == null) {
            instance = new FileLogger();
        }
        return instance;
    }

    public void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] " + message;

        writer.println(logEntry);
        writer.flush();

        // Also print to console
        System.out.println(logEntry);
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}