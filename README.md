# Lottery System

A multi-client lottery system implemented in Java with TCP socket communication and JSON protocol.

## Features

- **Server**: Multi-threaded TCP server handling multiple client connections
- **User Client**: GUI application for users to login, buy tickets, and check results
- **Admin Client**: GUI application for administrators to set winning numbers and manage lottery results
- **Thread-safe**: Concurrent data structures for handling multiple users
- **JSON Protocol**: Communication using newline-terminated JSON messages
- **Optional Features**: HTTP API and NIO server implementations

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- PowerShell execution policy allowing script execution (run `Set-ExecutionPolicy RemoteSigned` as administrator if needed)

## Quick Start

### 1. Compile the Project

```powershell
mvn clean compile
```

### 2. Run the Server

Use the provided PowerShell script:

```powershell
.\run-server.ps1
```

Or run manually:
```powershell
mvn exec:java
```

The server will start on port 5000.

### 3. Run the User Client

In a new PowerShell window:

```powershell
.\run-client.ps1
```

Or run manually:
```powershell
java -cp ".;target/classes;lib/gson-2.10.1.jar" client.UserClientGUI
```

### 4. Web Frontend (Alternative to Desktop Clients)

Open the web interface in your browser:

```powershell
# Open the main page
start frontend/index.html
```

Or manually open `frontend/index.html` in your web browser.

**Note**: The server automatically starts an HTTP API on port 8080 for the web frontend.

## Usage

### Desktop Clients

### User Client
1. Enter server host (default: 127.0.0.1) and port (default: 5000)
2. Enter username and click "Connect"
3. Click "Login" to authenticate
4. Click "Buy Ticket" to purchase a lottery ticket (costs 10 units)
5. Click "Check Results" to see if you won

### Admin Client
1. Enter server host (default: 127.0.0.1) and port (default: 5000)
2. Enter admin password (default: admin123) and click "Connect"
3. Enter a winning number and click "Set Winning Number"
4. Click "View Tickets" to see all purchased tickets
### Web Frontend

#### User Client
1. Open `frontend/index.html` in your web browser
2. Click "User Client" to access the user interface
3. Enter server host (default: 127.0.0.1) and port (default: 8080)
4. Click "Connect" to establish connection
5. Enter username and click "Login"
6. Click "Buy Ticket" to purchase lottery tickets (costs 10 units each)
7. Click "Check Results" to view your tickets and see if you won

#### Admin Client
1. Open `frontend/index.html` in your web browser
2. Click "Admin Client" to access the admin interface
3. Enter server host (default: 127.0.0.1) and port (default: 8080)
4. Click "Connect" to establish connection
5. Enter admin password (default: admin123) and click "Login as Admin"
6. Set a winning number using "Set Winning Number"
7. View all tickets using "View All Tickets"
8. Announce results to all users using "Announce Results"

## Architecture

### Server Components
- `LotteryServer`: Main server class with thread pool
- `ClientHandler`: Handles individual client connections
- `DataManager`: Thread-safe data storage and business logic

### Client Components
- `UserClientGUI`: Swing-based user interface
- `AdminClientGUI`: Swing-based admin interface

### Protocol
- `Request`: Client request messages
- `Response`: Server response messages
- `MessageParser`: JSON serialization/deserialization

### Models
- `User`: User account information
- `Ticket`: Lottery ticket data
- `LotteryResult`: Draw results and prizes

## Optional Features

### HTTP API Server
Run the HTTP server for REST API access:

```java
java -cp ".;target/classes;lib/gson-2.10.1.jar" optional.HttpServerModule
```

API Endpoints:
- `GET /results` - Get lottery results
- `GET /tickets` - Get all tickets

### NIO Server
Run the NIO-based server for high-performance:

```java
java -cp ".;target/classes;lib/gson-2.10.1.jar" optional.NIOServer
```

## Building

### Create JAR
```powershell
mvn clean package
```

### Run Tests
```powershell
mvn test
```

## Project Structure

```
src/
├── server/
│   ├── LotteryServer.java
│   ├── ClientHandler.java
│   └── DataManager.java
├── client/
│   ├── UserClientGUI.java
│   └── AdminClientGUI.java
├── protocol/
│   ├── Request.java
│   ├── Response.java
│   └── MessageParser.java
├── model/
│   ├── User.java
│   ├── Ticket.java
│   └── LotteryResult.java
└── optional/
    ├── HttpServerModule.java
    ├── NIOServer.java
    └── FileLogger.java
frontend/
├── index.html
├── user-client.html
├── admin-client.html
├── styles.css
├── client.js
└── admin.js
lib/
└── gson-2.10.1.jar
docs/
├── ProjectReport.pdf
└── UserManual.pdf
```

## Dependencies

- Gson 2.10.1 - JSON processing
- Java Swing - GUI components

## License

This project is for educational purposes.
