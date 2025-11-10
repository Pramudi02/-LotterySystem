# Network Services - Team Member Contributions

## Project Overview

**Lottery System** - A distributed multi-client lottery application with multiple network communication protocols and services.

This document outlines the **5 distinct network services** implemented by team members, each responsible for a specific aspect of the system's network architecture.

---

## 📋 Team Member Assignment

| Member | Network Service | Protocol | Port | Files |
|--------|----------------|----------|------|-------|
| **Member 1** | TCP Socket Server | Custom JSON Protocol | 5000 | LotteryServer.java, ClientHandler.java |
| **Member 2** | Java Swing Client (User) | TCP Socket Client | 5000 | UserClientGUI.java |
| **Member 3** | HTTP REST API Server | HTTP/1.1 + JSON | 8080 | HttpServerModule.java |
| **Member 4** | Java Swing Client (Admin) | TCP Socket Client | 5000 | AdminClientGUI.java |
| **Member 5** | Firebase Web Application | HTTPS + Firebase SDK | Cloud | modern-app.html, Firebase integration |

---

## 🔷 Member 1: TCP Socket Server

### **Responsibility:** Backend Socket Server Infrastructure

### **Network Service:** Multi-threaded TCP Socket Server with JSON Protocol

### **Description:**
Implemented the core server that accepts TCP socket connections from multiple clients simultaneously. Uses a thread pool to handle concurrent client connections efficiently.

### **Technical Details:**

**Protocol:** Custom JSON-line protocol
- Each message is a single line of JSON
- Newline-terminated messages for framing
- Request-response communication pattern

**Architecture:**
```
Client 1 ──┐
Client 2 ──┼──> ServerSocket (Port 5000) ──> Thread Pool
Client 3 ──┘                                   │
                                               ├──> ClientHandler (Thread 1)
                                               ├──> ClientHandler (Thread 2)
                                               └──> ClientHandler (Thread 3)
```

**Key Features:**
- ✅ Multi-threaded architecture using `ExecutorService`
- ✅ Thread pool with 10 worker threads
- ✅ Handles multiple simultaneous client connections
- ✅ Graceful connection handling and cleanup
- ✅ Integrates with DataManager for business logic

### **Implementation Files:**

#### 1. `LotteryServer.java` (Main Entry Point)
```java
package server;

public class LotteryServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private DataManager dataManager;
    private HttpServerModule httpServer;
    
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(10);
        dataManager = new DataManager();
        
        // Start HTTP server on port 8080
        httpServer = new HttpServerModule(dataManager);
        httpServer.start(8080);
        
        // Accept client connections
        while (running) {
            Socket clientSocket = serverSocket.accept();
            threadPool.execute(new ClientHandler(clientSocket, dataManager));
        }
    }
}
```

**Responsibilities:**
- Initialize ServerSocket on port 5000
- Create thread pool for concurrent handling
- Accept incoming client connections
- Delegate client handling to worker threads
- Coordinate with HTTP server (Member 3's service)

#### 2. `ClientHandler.java` (Connection Handler)
```java
package server;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataManager dataManager;
    
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Request request = MessageParser.parseRequest(inputLine);
                Response response = processRequest(request);
                String jsonResponse = MessageParser.toJson(response);
                out.println(jsonResponse);
            }
        } catch (IOException e) {
            // Handle error
        }
    }
    
    private Response processRequest(Request request) {
        // Process login, buyTicket, checkResult actions
    }
}
```

**Responsibilities:**
- Handle individual client socket connections
- Parse incoming JSON requests
- Process business logic (login, buy ticket, check results)
- Send JSON responses back to clients
- Maintain connection state

### **Network Protocol Example:**

**Client Request (Login):**
```json
{"action":"login","username":"alice"}
```

**Server Response:**
```json
{"status":"success","message":"Login successful","balance":100.0}
```

**Client Request (Buy Ticket):**
```json
{"action":"buyTicket","username":"alice"}
```

**Server Response:**
```json
{"status":"success","ticketNumber":5432,"ticketId":1001,"newBalance":90.0,"message":"Ticket purchased"}
```

### **Network Concepts Demonstrated:**
- ✅ TCP/IP socket programming
- ✅ Multi-threaded server design
- ✅ Thread pooling for scalability
- ✅ Connection-oriented communication
- ✅ Request-response pattern
- ✅ JSON serialization/deserialization
- ✅ Resource management (socket cleanup)

### **Testing:**
- Can be tested with Member 2's UserClientGUI
- Can be tested with Member 4's AdminClientGUI
- Handles multiple concurrent connections

---

## 🔷 Member 2: Java Swing User Client

### **Responsibility:** End-User Desktop Client Application

### **Network Service:** TCP Socket Client with Swing GUI

### **Description:**
Implemented a desktop GUI application using Java Swing that connects to the TCP socket server (Member 1). Provides user interface for lottery players to login, purchase tickets, and check results.

### **Technical Details:**

**Protocol:** TCP Socket Client (Consumer of Member 1's service)
- Establishes TCP connection to server
- Sends JSON-formatted requests
- Receives and displays JSON responses
- Asynchronous message reading using separate thread

**Architecture:**
```
┌─────────────────────────────────┐
│    UserClientGUI (Swing)        │
│  ┌───────────────────────────┐  │
│  │  UI Thread (Main)         │  │
│  │  - Handle button clicks   │  │
│  │  - Update display         │  │
│  └───────────────────────────┘  │
│  ┌───────────────────────────┐  │
│  │  Reader Thread            │  │
│  │  - Listen for responses   │  │
│  │  - Parse JSON             │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
            │
            │ TCP Socket
            ▼
    Server (Port 5000)
```

### **Implementation File:**

#### `UserClientGUI.java`

**Key Components:**

1. **Connection Management:**
```java
private void onConnect(ActionEvent e) {
    String host = hostField.getText().trim();
    int port = Integer.parseInt(portField.getText().trim());
    
    socket = new Socket(host, port);
    out = new PrintWriter(socket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    // Start async reader thread
    readerThread = new Thread(this::readLoop);
    readerThread.setDaemon(true);
    readerThread.start();
}
```

2. **Request Sending:**
```java
private void onLogin(ActionEvent e) {
    Request request = new Request("login");
    request.setUsername(usernameField.getText());
    String json = MessageParser.toJson(request);
    out.println(json);  // Send to server
}

private void onBuyTicket(ActionEvent e) {
    Request request = new Request("buyTicket");
    request.setUsername(usernameField.getText());
    String json = MessageParser.toJson(request);
    out.println(json);
}
```

3. **Asynchronous Response Handling:**
```java
private void readLoop() {
    try {
        String line;
        while ((line = in.readLine()) != null) {
            Response response = MessageParser.parseResponse(line);
            SwingUtilities.invokeLater(() -> 
                append("<Server> " + formatResponse(response))
            );
        }
    } catch (IOException ex) {
        // Connection lost
    }
}
```

### **User Interface Features:**
- **Connection Panel:** Host, Port, Username input fields
- **Action Buttons:** Login, Buy Ticket, Check Results
- **Event Log:** Real-time display of client-server communication
- **Thread-safe UI updates** using `SwingUtilities.invokeLater()`

### **Network Concepts Demonstrated:**
- ✅ TCP client socket programming
- ✅ Asynchronous I/O (separate reader thread)
- ✅ Thread-safe GUI updates
- ✅ Connection state management
- ✅ JSON request formatting
- ✅ JSON response parsing
- ✅ Error handling for network failures

### **User Workflow:**
1. Enter server host (127.0.0.1) and port (5555 or 5000)
2. Enter username
3. Click "Connect" → Establishes TCP connection
4. Click "Login" → Sends login request
5. Click "Buy Ticket" → Purchases lottery ticket
6. Click "Check Results" → Views ticket results
7. See server responses in event log

---

## 🔷 Member 3: HTTP REST API Server

### **Responsibility:** Web-Based API Server

### **Network Service:** HTTP RESTful API Server with CORS Support

### **Description:**
Implemented an HTTP server that exposes lottery system functionality through REST API endpoints. Enables web-based clients to interact with the system using standard HTTP requests and JSON payloads.

### **Technical Details:**

**Protocol:** HTTP/1.1 with JSON
- RESTful endpoint design
- JSON request/response bodies
- CORS headers for cross-origin requests
- Standard HTTP methods (GET, POST, OPTIONS)

**Architecture:**
```
Web Browser ────> HTTP Request (POST /buy-ticket)
    │                         │
    │                         ▼
    │              HttpServerModule (Port 8080)
    │                         │
    │                         ├──> Parse JSON body
    │                         ├──> Call DataManager
    │                         └──> Format JSON response
    │                         
    └────────<── HTTP Response (JSON)
```

### **Implementation File:**

#### `HttpServerModule.java`

**API Endpoints:**

| Endpoint | Method | Purpose | Request | Response |
|----------|--------|---------|---------|----------|
| `/health` | GET | Server health check | None | `{status, message}` |
| `/login` | POST | User authentication | `{username}` | `{success, username, balance}` |
| `/buy-ticket` | POST | Purchase ticket | `{username}` | `{success, numbers, balance}` |
| `/check-results` | POST | View user tickets | `{username}` | `{success, tickets[]}` |
| `/admin-login` | POST | Admin authentication | `{password}` | `{success, message}` |
| `/set-winner` | POST | Set winning number | `{winningNumber}` | `{success, winningNumber}` |
| `/view-tickets` | GET | View all tickets (admin) | None | `{success, tickets[]}` |
| `/announce-results` | POST | Announce results | None | `{success, message}` |

**Key Implementation:**

1. **Server Initialization:**
```java
public void start(int port) throws IOException {
    server = HttpServer.create(new InetSocketAddress(port), 0);
    
    // Register endpoints
    server.createContext("/health", new HealthHandler());
    server.createContext("/login", new LoginHandler());
    server.createContext("/buy-ticket", new BuyTicketHandler());
    server.createContext("/check-results", new CheckResultsHandler());
    server.createContext("/admin-login", new AdminLoginHandler());
    server.createContext("/set-winner", new SetWinnerHandler());
    
    server.setExecutor(null);
    server.start();
}
```

2. **Request Handler Example:**
```java
class BuyTicketHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Handle CORS preflight
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        // Read JSON request body
        String requestBody = readRequestBody(exchange);
        Map<String, Object> requestData = parseJsonObject(requestBody);
        
        String username = (String) requestData.get("username");
        
        // Buy ticket through DataManager
        int[] numbers = dataManager.buyTicket(username);
        double balance = dataManager.getUserBalance(username);
        
        // Send JSON response
        String response = String.format(
            "{\"success\":true,\"numbers\":%s,\"balance\":%d}",
            Arrays.toString(numbers), (int)balance
        );
        sendJsonResponse(exchange, response);
    }
}
```

3. **CORS Configuration:**
```java
private void sendJsonResponse(HttpExchange exchange, String json) {
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    
    exchange.sendResponseHeaders(200, json.getBytes().length);
    exchange.getResponseBody().write(json.getBytes());
}
```

### **Network Concepts Demonstrated:**
- ✅ HTTP server implementation
- ✅ RESTful API design
- ✅ HTTP methods (GET, POST, OPTIONS)
- ✅ CORS (Cross-Origin Resource Sharing)
- ✅ JSON content negotiation
- ✅ Request/response headers
- ✅ Status codes (200, 404, 500)
- ✅ Stateless communication

### **Example API Usage:**

**cURL Request:**
```bash
curl -X POST http://localhost:8080/buy-ticket \
  -H "Content-Type: application/json" \
  -d '{"username":"alice"}'
```

**Response:**
```json
{
  "success": true,
  "numbers": [23, 45, 67, 12, 89],
  "balance": 90
}
```

**JavaScript (Fetch API):**
```javascript
fetch('http://localhost:8080/buy-ticket', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({username: 'alice'})
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## 🔷 Member 4: Java Swing Admin Client

### **Responsibility:** Administrative Desktop Client Application

### **Network Service:** TCP Socket Admin Client with Advanced GUI

### **Description:**
Implemented an administrative desktop application using Java Swing that connects to the TCP socket server. Provides privileged operations for lottery administrators including setting winning numbers, viewing all tickets, and managing results.

### **Technical Details:**

**Protocol:** TCP Socket Client (Consumer of Member 1's service)
- Establishes authenticated TCP connection
- Sends administrative JSON requests
- Displays tabular data (tickets, users)
- Real-time log monitoring

**Architecture:**
```
┌─────────────────────────────────────┐
│   AdminClientGUI (Swing)            │
│  ┌───────────────────────────────┐  │
│  │  Admin Panel                  │  │
│  │  - Set winning number         │  │
│  │  - View all tickets (table)   │  │
│  │  - View all users (table)     │  │
│  │  - Announce results           │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │  Authentication               │  │
│  │  - Admin password required    │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
            │
            │ TCP Socket
            ▼
    Server (Port 5000)
```

### **Implementation File:**

#### `AdminClientGUI.java`

**Key Components:**

1. **Admin Authentication:**
```java
private void onConnect(ActionEvent e) {
    String host = hostField.getText().trim();
    int port = Integer.parseInt(portField.getText().trim());
    String password = passwordField.getText();
    
    // Establish connection
    socket = new Socket(host, port);
    out = new PrintWriter(socket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    // Send admin login request
    Request adminLogin = new Request("adminLogin");
    adminLogin.setPassword(password);
    String json = MessageParser.toJson(adminLogin);
    out.println(json);
}
```

2. **Set Winning Number:**
```java
private void onSetWinner(ActionEvent e) {
    String numStr = winningNumField.getText().trim();
    int winningNumber = Integer.parseInt(numStr);
    
    Request request = new Request("setWinner");
    request.setWinningNumber(winningNumber);
    String json = MessageParser.toJson(request);
    out.println(json);
    
    log("Sent set winner request: " + winningNumber);
}
```

3. **View All Tickets (Table Display):**
```java
private void onViewTickets(ActionEvent e) {
    Request request = new Request("viewTickets");
    String json = MessageParser.toJson(request);
    out.println(json);
    
    // Response handler updates JTable with ticket data
}

private void displayTicketsInTable(List<Ticket> tickets) {
    tableModel.setRowCount(0);
    for (Ticket ticket : tickets) {
        tableModel.addRow(new Object[]{
            ticket.getTicketId(),
            ticket.getUsername(),
            ticket.getTicketNumber(),
            ticket.getPurchaseTime()
        });
    }
}
```

4. **Announce Results:**
```java
private void onAnnounce(ActionEvent e) {
    Request request = new Request("announceResults");
    String json = MessageParser.toJson(request);
    out.println(json);
    
    log("Announcing results to all users...");
}
```

### **Admin Interface Features:**
- **Connection Panel:** Host, Port, Admin Password
- **Admin Actions:**
  - Set Winning Number (1-100)
  - View All Tickets (JTable with columns: ID, User, Number, Time)
  - View All Users (JTable with columns: Username, Balance, Tickets)
  - Announce Results
- **Log Area:** Real-time activity log
- **Data Tables:** Scrollable tables for viewing system data

### **Network Concepts Demonstrated:**
- ✅ Authenticated socket connections
- ✅ Privileged operations over TCP
- ✅ Tabular data transmission
- ✅ Admin-specific protocol commands
- ✅ Real-time data refresh
- ✅ Complex GUI with networking
- ✅ Role-based access control

### **Admin Workflow:**
1. Enter server host and port (5000)
2. Enter admin password ("admin123")
3. Click "Connect" → Authenticate as admin
4. Enter winning number → Click "Set Winner"
5. Click "View Tickets" → See all tickets in table
6. Click "View Users" → See all users and balances
7. Click "Announce" → Broadcast results to users

### **Unique Features:**
- ✅ Table-based data display (JTable)
- ✅ Password-protected admin access
- ✅ Bulk data operations
- ✅ System-wide result announcements

---

## 🔷 Member 5: Firebase Web Application

### **Responsibility:** Modern Web-Based Client with Cloud Backend

### **Network Service:** Firebase Cloud Services Integration (Auth + Firestore + Hosting)

### **Description:**
Implemented a modern single-page web application that uses Firebase services for authentication, real-time database, and hosting. This represents a shift from traditional socket-based architecture to cloud-based serverless architecture.

### **Technical Details:**

**Protocols:** Multiple cloud services
- **HTTPS** - Secure web communication
- **WebSocket** - Real-time database sync
- **REST API** - Firebase SDK communication
- **OAuth 2.0** - Authentication flow

**Architecture:**
```
┌─────────────────────────────────────────┐
│  Web Browser (modern-app.html)          │
│  ┌───────────────────────────────────┐  │
│  │  Frontend (HTML/CSS/JS)           │  │
│  │  - Split-screen login UI          │  │
│  │  - Dashboard with stats           │  │
│  │  - Ticket purchase                │  │
│  │  - Results display                │  │
│  │  - Admin panel                    │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
            │
            │ HTTPS + WebSocket
            ▼
┌─────────────────────────────────────────┐
│        Firebase Cloud Services          │
│  ┌───────────────────────────────────┐  │
│  │  Firebase Authentication          │  │
│  │  - Email/Password provider        │  │
│  │  - User session management        │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  Cloud Firestore (Database)       │  │
│  │  - users collection               │  │
│  │  - tickets collection             │  │
│  │  - system collection              │  │
│  │  - Real-time sync                 │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  Firebase Hosting (CDN)           │  │
│  │  - Static file serving            │  │
│  │  - SSL/TLS certificates           │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### **Implementation File:**

#### `modern-app.html` (with embedded JavaScript)

**Key Components:**

1. **Firebase SDK Integration:**
```javascript
// Import Firebase modules from CDN
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js";
import { getAuth, signInWithEmailAndPassword, createUserWithEmailAndPassword } 
    from "https://www.gstatic.com/firebasejs/10.7.1/firebase-auth.js";
import { getFirestore, doc, setDoc, getDoc, collection, addDoc, query, where, getDocs } 
    from "https://www.gstatic.com/firebasejs/10.7.1/firebase-firestore.js";

// Initialize Firebase
const firebaseConfig = {
    apiKey: "AIzaSyBWEtzjum1mcPN3e61XghXaNC9qum7vhDw",
    authDomain: "lotterysystem-18963.firebaseapp.com",
    projectId: "lotterysystem-18963",
    storageBucket: "lotterysystem-18963.firebasestorage.app",
    messagingSenderId: "41637006495",
    appId: "1:41637006495:web:e7c9e6e3a57792bbc126aa"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);
```

2. **Authentication Service:**
```javascript
// Sign Up
async function handleSignUp(event) {
    event.preventDefault();
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const name = document.getElementById('signupName').value;
    
    try {
        // Create user with Firebase Auth
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        
        // Create user document in Firestore
        await setDoc(doc(db, "users", userCredential.user.uid), {
            email: email,
            displayName: name,
            balance: 100.0,
            isAdmin: false,
            createdAt: Timestamp.now()
        });
        
        showToast("Account created successfully!", "success");
    } catch (error) {
        showToast(error.message, "error");
    }
}

// Sign In
async function handleSignIn(event) {
    event.preventDefault();
    const email = document.getElementById('signinEmail').value;
    const password = document.getElementById('signinPassword').value;
    
    try {
        await signInWithEmailAndPassword(auth, email, password);
        showToast("Signed in successfully!", "success");
    } catch (error) {
        showToast(error.message, "error");
    }
}

// Session Management
onAuthStateChanged(auth, async (user) => {
    if (user) {
        currentUser = user;
        await loadUserData();
        showDashboard();
    } else {
        showAuthPage();
    }
});
```

3. **Firestore Database Operations:**
```javascript
// Buy Ticket
async function buyTicket() {
    if (currentUserData.balance < 10) {
        showToast('Insufficient balance!', 'error');
        return;
    }
    
    // Generate random ticket numbers
    const numbers = Array.from({length: 5}, () => 
        Math.floor(Math.random() * 100) + 1
    );
    
    // Add ticket to Firestore
    await addDoc(collection(db, "tickets"), {
        userId: currentUser.uid,
        numbers: numbers,
        purchaseTime: Timestamp.now(),
        checked: false
    });
    
    // Update user balance
    const newBalance = currentUserData.balance - 10;
    await updateDoc(doc(db, "users", currentUser.uid), {
        balance: newBalance
    });
    
    showToast("Ticket purchased! Good luck!", "success");
}

// Load User Tickets
async function loadResults() {
    const q = query(
        collection(db, "tickets"),
        where("userId", "==", currentUser.uid),
        orderBy("purchaseTime", "desc")
    );
    
    const snapshot = await getDocs(q);
    const tickets = [];
    
    snapshot.forEach(doc => {
        const data = doc.data();
        tickets.push({
            id: doc.id,
            numbers: data.numbers,
            purchaseTime: data.purchaseTime.toDate()
        });
    });
    
    displayTickets(tickets);
}

// Admin: Set Winning Number
async function setWinningNumber() {
    if (!currentUserData?.isAdmin) {
        showToast("Admin access required", "error");
        return;
    }
    
    const number = parseInt(document.getElementById('winningNumberInput').value);
    
    await setDoc(doc(db, "system", "lottery"), {
        winningNumber: number,
        setTime: Timestamp.now()
    }, { merge: true });
    
    showToast(`Winning number set to ${number}`, 'success');
}
```

4. **Real-Time Updates:**
```javascript
// Firestore automatically syncs data changes
// No need for manual polling or WebSocket code

// Example: Listen for balance updates
const userDocRef = doc(db, "users", currentUser.uid);
onSnapshot(userDocRef, (doc) => {
    if (doc.exists()) {
        currentUserData = doc.data();
        updateUIWithUserData();
    }
});
```

### **Firebase Services Used:**

#### **1. Firebase Authentication**
**Endpoint:** `https://identitytoolkit.googleapis.com/v1/accounts`

**Features:**
- Email/password authentication
- Session token management
- Password reset
- User account creation
- Secure authentication flow

**Network Protocol:** REST API over HTTPS with JWT tokens

#### **2. Cloud Firestore (Database)**
**Endpoint:** `https://firestore.googleapis.com/v1/projects/lotterysystem-18963`

**Collections Structure:**
```
firestore/
├── users/
│   └── {userId}/
│       ├── email: string
│       ├── displayName: string
│       ├── balance: number
│       ├── isAdmin: boolean
│       └── createdAt: timestamp
├── tickets/
│   └── {ticketId}/
│       ├── userId: string
│       ├── numbers: array
│       ├── purchaseTime: timestamp
│       └── checked: boolean
└── system/
    └── lottery/
        ├── winningNumber: number
        └── setTime: timestamp
```

**Features:**
- Real-time data synchronization
- Offline persistence
- Query capabilities
- Transaction support
- Security rules

**Network Protocol:** WebSocket for real-time sync, REST API for queries

#### **3. Firebase Hosting** (Optional)
**Endpoint:** `https://lotterysystem-18963.web.app`

**Features:**
- CDN distribution
- SSL/TLS certificates
- Fast global serving
- Automatic cache management

**Network Protocol:** HTTPS with HTTP/2

### **Network Concepts Demonstrated:**
- ✅ Cloud-based architecture
- ✅ RESTful API consumption
- ✅ WebSocket for real-time data
- ✅ OAuth 2.0 authentication
- ✅ JWT token management
- ✅ HTTPS/TLS encryption
- ✅ CDN content delivery
- ✅ Serverless architecture
- ✅ NoSQL database queries
- ✅ Real-time synchronization
- ✅ Offline-first design

### **User Workflow:**
1. Open `http://localhost:3000/modern-app.html`
2. Sign up with email/password → Firebase Auth
3. Login redirects to dashboard → Session token stored
4. Click "Buy Ticket" → Firestore write + balance update
5. View "Results" → Firestore query + real-time sync
6. Admin sets winner → Firestore system document update
7. All changes sync across devices in real-time

### **Advantages Over Socket-Based Architecture:**
- ✅ No server maintenance (serverless)
- ✅ Automatic scaling
- ✅ Real-time sync without polling
- ✅ Built-in authentication
- ✅ Offline support
- ✅ Global CDN distribution
- ✅ Secure by default (HTTPS)

---

## 📊 Network Services Comparison

| Aspect | Member 1<br>TCP Server | Member 2<br>User Client | Member 3<br>HTTP API | Member 4<br>Admin Client | Member 5<br>Firebase Web |
|--------|---------|---------|---------|---------|---------|
| **Protocol** | TCP/JSON | TCP/JSON | HTTP/JSON | TCP/JSON | HTTPS/WebSocket |
| **Port** | 5000 | Client | 8080 | Client | 443 (Cloud) |
| **Architecture** | Server | Client | Server | Client | Cloud/Client |
| **Concurrency** | Multi-threaded | Single connection | Request-based | Single connection | Serverless scale |
| **State** | Connection-oriented | Stateful | Stateless | Stateful | Stateless |
| **Authentication** | Username | Username | Username/Password | Admin Password | Email/Password (OAuth) |
| **Data Format** | JSON lines | JSON lines | JSON HTTP | JSON lines | JSON REST |
| **UI Type** | N/A | Java Swing | N/A | Java Swing | HTML/CSS/JS |
| **Real-time** | Yes (persistent) | Yes (thread) | No (req/res) | Yes (thread) | Yes (WebSocket) |
| **Deployment** | On-premise | Desktop app | On-premise | Desktop app | Cloud (CDN) |

---

## 🎯 Learning Outcomes by Member

### **Member 1 - TCP Socket Server**
- Multi-threaded server programming
- Socket lifecycle management
- Thread pool design patterns
- Concurrent connection handling
- Custom protocol design

### **Member 2 - User Client GUI**
- TCP client socket programming
- Asynchronous I/O with threads
- GUI event handling
- Thread-safe UI updates
- Client-side state management

### **Member 3 - HTTP REST API**
- HTTP server implementation
- RESTful API design principles
- CORS configuration
- JSON content negotiation
- Stateless web services

### **Member 4 - Admin Client GUI**
- Authenticated TCP connections
- Complex Swing GUI components
- Tabular data display (JTable)
- Role-based access control
- Administrative protocols

### **Member 5 - Firebase Integration**
- Cloud service integration
- Authentication services (OAuth)
- NoSQL database operations
- Real-time data synchronization
- Modern web development
- Serverless architecture

---

## 🔧 How They Work Together

### **Integration Flow:**

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Member 2   │────────>│   Member 1   │<────────│   Member 4   │
│ User Client  │  TCP    │ Socket Server│  TCP    │ Admin Client │
│  (Port any)  │ 5000    │  (Port 5000) │ 5000    │  (Port any)  │
└──────────────┘         └──────┬───────┘         └──────────────┘
                                │
                                │ Shared DataManager
                                │
                         ┌──────▼───────┐
                         │   Member 3   │
                         │  HTTP API    │
                         │  (Port 8080) │
                         └──────┬───────┘
                                │
                                │ HTTP
                                │
                         ┌──────▼───────┐
                         │   Member 5   │
                         │  Web Browser │
                         │ (Firebase)   │
                         └──────────────┘
```

### **Communication Patterns:**

1. **Member 2 → Member 1:** TCP socket connection for user operations
2. **Member 4 → Member 1:** TCP socket connection for admin operations
3. **Member 5 → Member 3:** HTTP requests for legacy API (optional)
4. **Member 5 → Firebase:** HTTPS/WebSocket for primary operations
5. **Member 1 ↔ Member 3:** Shared DataManager (in-process)

---

## 📝 Testing Each Service

### **Member 1 (TCP Server):**
```bash
# Start the server
mvn exec:java

# Expected output:
# Lottery Server started on port 5000
# HTTP API Server started on port 8080
```

### **Member 2 (User Client):**
```bash
# Compile and run
javac -cp src src/client/UserClientGUI.java
java -cp src client.UserClientGUI

# 1. Enter: Host=127.0.0.1, Port=5000
# 2. Click "Connect"
# 3. Test login, buy ticket, check results
```

### **Member 3 (HTTP API):**
```bash
# Test health endpoint
curl http://localhost:8080/health

# Test buy ticket
curl -X POST http://localhost:8080/buy-ticket \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser"}'
```

### **Member 4 (Admin Client):**
```bash
# Compile and run
javac -cp src src/client/AdminClientGUI.java
java -cp src client.AdminClientGUI

# 1. Enter: Host=127.0.0.1, Port=5000, Password=admin123
# 2. Click "Connect"
# 3. Test set winner, view tickets
```

### **Member 5 (Firebase Web):**
```bash
# Start Python HTTP server
cd frontend
python -m http.server 3000

# Open browser
http://localhost:3000/modern-app.html

# 1. Sign up with email/password
# 2. Test buy ticket, view results
# 3. Test admin features
```

---

## 🎓 Summary

This lottery system demonstrates a **comprehensive multi-protocol network architecture** where each team member contributed a distinct network service:

1. **Member 1** built the **TCP socket server backbone**
2. **Member 2** created a **user-facing TCP client**
3. **Member 3** developed an **HTTP REST API layer**
4. **Member 4** implemented an **administrative TCP client**
5. **Member 5** modernized with **cloud-based Firebase services**

Together, they showcase:
- **3 different protocols** (TCP, HTTP, HTTPS/WebSocket)
- **2 server implementations** (Socket, HTTP)
- **3 client implementations** (2 desktop, 1 web)
- **4 ports** (5000, 5555, 8080, 3000)
- **Multiple architectural patterns** (client-server, REST, cloud/serverless)

Each member's contribution is **unique, independent, and essential** to the overall system functionality.

---

**Project:** Lottery System - Distributed Network Services  
**Team Size:** 5 Members  
**Technologies:** Java, Firebase, HTTP, TCP/IP, JSON, Swing, HTML/CSS/JavaScript  
**Last Updated:** November 11, 2025
