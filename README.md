# Lottery System - Firebase Edition

A complete online lottery system built with Firebase Authentication, Firestore database, and Java backend coordination layer.

## 🎯 Features

- **Firebase Authentication**: Secure user registration and login
- **Firestore Database**: Real-time data storage and synchronization
- **Single-Page Application**: Modern web interface with client-side routing
- **Admin Panel**: Complete lottery management system
- **Real-time Results**: Live lottery results and ticket checking
- **Java Backend**: Coordination layer for admin operations

## 🚀 Quick Start

### Prerequisites

- **Java 11+** (for backend server)
- **Maven** (for building Java project)
- **Firebase Project** (with Authentication and Firestore enabled)
- **Web Browser** (Chrome, Firefox, Safari, Edge)

### Step 1: Firebase Setup

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project called "lotterysystem"
   - Enable **Authentication** with Email/Password provider
   - Enable **Firestore Database**

2. **Configure Firebase**:
   - Run the setup script: `.\setup-firebase.ps1`
   - This will deploy Firestore security rules and guide you through manual setup

3. **Create Admin User**:
   - In Firestore, create a user document with admin privileges:
     ```
     Collection: users
     Document ID: [your-firebase-user-id]
     Fields:
       - email: "admin@example.com"
       - displayName: "Admin"
       - balance: 100
       - isAdmin: true
     ```

### Step 2: Backend Setup

1. **Build the Java Server**:
   ```powershell
   mvn clean compile
   ```

2. **Start the Server**:
   ```powershell
   .\run-server.ps1
   ```
   - Server runs on port 8080 (HTTP API)
   - Legacy TCP server on port 5000

### Step 3: Frontend Setup

1. **Serve the Frontend**:
   ```powershell
   cd frontend
   python -m http.server 3000
   ```

2. **Open in Browser**:
   - Navigate to: `http://localhost:3000`
   - Or run: `.\open-web.ps1`

## 🎮 How to Use

### For Players

1. **Sign Up**: Create an account with email, password, and display name
2. **Sign In**: Log in with your credentials
3. **Buy Tickets**: Purchase lottery tickets ($10 each, 5 random numbers)
4. **Check Results**: View your tickets and see if you won
5. **Balance Management**: Track your account balance

### For Admins

1. **Admin Login**: Sign in with an admin account (isAdmin: true in Firestore)
2. **Set Winning Number**: Choose the winning number (1-100)
3. **View All Tickets**: See all purchased tickets
4. **View All Users**: Manage user accounts
5. **Monitor Results**: Track lottery outcomes

## 🏗️ Architecture

```
Frontend (SPA)
├── index.html          # Single-page application with routing
├── firebase-config.js  # Firebase integration
└── styles.css          # Modern UI styles

Backend (Java)
├── HttpServerModule.java  # REST API server (port 8080)
├── DataManager.java       # Data coordination layer
└── Legacy TCP server      # Port 5000 (backward compatibility)

Database (Firebase)
├── Authentication         # User management
├── Firestore              # Real-time data storage
└── Security Rules         # Access control
```

## 🔧 API Endpoints

### Authentication (Firebase)
- User registration and login handled client-side
- Real-time auth state management

### Database Operations (Firestore)
- User profiles and balances
- Ticket purchases and storage
- Lottery results and winning numbers

### Admin Operations (Java Backend)
- `GET /health` - Server health check
- `POST /admin-login` - Admin authentication
- `POST /set-winner` - Set winning number
- `GET /view-tickets` - Get all tickets
- `GET /announce-results` - Refresh results

## 🎨 Frontend Pages

1. **Authentication Page**: Login/Signup forms
2. **Dashboard**: Main navigation hub
3. **Play Lottery**: Buy tickets interface
4. **My Results**: View tickets and winnings
5. **Admin Panel**: Complete management interface

## 🔒 Security

- **Firebase Authentication**: Secure user authentication
- **Firestore Rules**: Granular access control
- **Admin Verification**: Server-side admin privilege checking
- **Input Validation**: Client and server-side validation

## 🚀 Deployment

### Production Deployment

1. **Firebase Hosting**:
   ```bash
   firebase init hosting
   firebase deploy --only hosting
   ```

2. **Backend Deployment**:
   - Deploy Java application to cloud service (Heroku, AWS, etc.)
   - Update Firebase config with production URLs

3. **Environment Variables**:
   - Set production Firebase config
   - Configure CORS for production domain

## 🐛 Troubleshooting

### Common Issues

1. **Firebase Connection Issues**:
   - Check Firebase project configuration
   - Verify API keys and project IDs
   - Ensure Firestore rules are deployed

2. **Authentication Problems**:
   - Enable Email/Password in Firebase Console
   - Check user document structure in Firestore

3. **Server Connection Issues**:
   - Verify Java server is running on port 8080
   - Check Maven compilation succeeded
   - Ensure firewall allows connections

4. **Frontend Loading Issues**:
   - Verify Python HTTP server is running on port 3000
   - Check browser console for JavaScript errors
   - Ensure all Firebase modules are loading

### Debug Commands

```powershell
# Check server health
curl http://localhost:8080/health

# Test Firebase connection
# Open browser dev tools and check network tab

# View server logs
# Server output visible in terminal when running
```

## 📝 Development

### Adding New Features

1. **Frontend Changes**:
   - Modify `index.html` for new pages/routes
   - Update `firebase-config.js` for new Firebase operations
   - Add styles to `styles.css`

2. **Backend Changes**:
   - Add new endpoints in `HttpServerModule.java`
   - Update `DataManager.java` for data operations
   - Test with Postman or curl

3. **Database Changes**:
   - Update Firestore security rules
   - Modify data structures in Firebase console

### Testing

```powershell
# Run Java tests
mvn test

# Manual testing
# 1. Start server: .\run-server.ps1
# 2. Serve frontend: cd frontend; python -m http.server 3000
# 3. Open http://localhost:3000
# 4. Test all user flows
```

## 📄 License

This project is for educational purposes. Modify and distribute as needed.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

**Happy Lottering! 🎰**
