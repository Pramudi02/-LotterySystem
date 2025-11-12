# Lottery System with Firebase Integration

This lottery system has been upgraded to use Firebase Authentication and Firestore database, providing a modern, scalable, and secure solution.

## 🚀 Quick Start

### 1. Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing "lotterysystem-18963"
3. Enable Authentication and Firestore Database
4. Copy your Firebase config to `frontend/firebase-config.js`
5. Deploy the Firestore security rules from `firestore.rules`

### 2. Set Admin Users
In Firestore, set admin privileges for users:
```javascript
// In Firebase Console > Firestore
// Update user document to add isAdmin field
{
  email: "admin@example.com",
  displayName: "Admin User",
  balance: 100.0,
  isAdmin: true,  // This grants admin access
  ticketIds: []
}
```

### 3. Run the System
```bash
# Compile Java backend
mvn clean compile

# Start the server
mvn exec:java

# Serve frontend (in another terminal)
cd frontend
python -m http.server 3000
```

### 4. Access the Application
- Open http://localhost:3000 in your browser
- Sign up for a new account or sign in
- Use the lottery system!

## 📋 System Architecture

### Frontend (Firebase-based)
- **Authentication**: Firebase Auth (Email/Password)
- **Database**: Firestore (users, tickets, system settings)
- **Real-time**: Automatic UI updates via Firebase listeners

### Backend (Java - Coordination Layer)
- **HTTP Server**: Port 8080 (minimal API for admin operations)
- **TCP Server**: Port 5000 (legacy client support)
- **DataManager**: Coordination layer (most operations client-side)

## 🔐 Security Model

### Firebase Rules
- Users can read/write their own data
- Authenticated users can read tickets and system settings
- Admins have full read/write access to all data
- All operations require authentication

### Admin Access
- Set `isAdmin: true` in user document in Firestore
- Admin users can:
  - Set winning numbers
  - View all tickets
  - View all users
  - Announce results

## 🎯 User Workflow

### Regular Users
1. **Sign Up/Sign In**: Create account with email/password
2. **Dashboard**: View balance and account info
3. **Buy Tickets**: Purchase lottery tickets ($10 each, 5 random numbers)
4. **Check Results**: View purchased tickets and winning status

### Admin Users
1. **Sign In**: Login with admin account
2. **Set Winner**: Choose winning number (1-100)
3. **View Data**: See all tickets and users
4. **Manage Lottery**: Control lottery operations

## 🏗️ Database Schema

### Collections

#### `users` Collection
```javascript
{
  email: "user@example.com",
  displayName: "User Name",
  balance: 100.0,
  isAdmin: false,  // Optional: grants admin privileges
  ticketIds: ["ticketId1", "ticketId2"],
  createdAt: Timestamp
}
```

#### `tickets` Collection
```javascript
{
  userId: "firebaseUserId",
  numbers: [5, 23, 67, 12, 89],  // 5 random numbers 1-100
  purchaseTime: Timestamp,
  ticketId: "simpleId"  // For backward compatibility
}
```

#### `system` Collection
```javascript
// Document: "lottery"
{
  winningNumber: 42,
  setTime: Timestamp
}
```

## 🔧 API Endpoints (Legacy)

The Java backend provides minimal HTTP endpoints for coordination:

- `GET /health` - Server health check
- `POST /admin-login` - Admin authentication (legacy)
- `POST /set-winner` - Set winning number (admin)
- `GET /view-tickets` - Get all tickets (admin)
- `POST /announce-results` - Announce results (admin)

Most user operations are handled directly through Firebase/Firestore.

## 🚀 Deployment

### Firebase Deployment
1. Deploy Firestore rules: `firebase deploy --only firestore:rules`
2. Enable Authentication providers in Firebase Console
3. Configure CORS for your domain if needed

### Backend Deployment
1. Compile: `mvn clean compile`
2. Package: `mvn package`
3. Run: `java -jar target/lottery-system-1.0-SNAPSHOT.jar`

### Frontend Deployment
1. Host static files on any web server
2. Ensure Firebase config is correct
3. Configure domain in Firebase Console

## 🔍 Troubleshooting

### Common Issues

1. **Authentication Errors**
   - Check Firebase config in `firebase-config.js`
   - Ensure Authentication is enabled in Firebase Console
   - Verify user documents exist in Firestore

2. **Permission Denied**
   - Check Firestore security rules
   - Ensure user is authenticated
   - Verify admin status for admin operations

3. **Data Not Loading**
   - Check Firestore rules allow read access
   - Verify collection names match schema
   - Check browser console for errors

4. **Backend Connection Issues**
   - Ensure Java server is running on port 8080
   - Check CORS headers for cross-origin requests

### Debug Mode
Enable Firebase debug logging:
```javascript
import { getAuth, connectAuthEmulator } from "firebase/auth";
import { getFirestore, connectFirestoreEmulator } from "firebase/firestore";

// In development
connectAuthEmulator(auth, "http://localhost:9099");
connectFirestoreEmulator(db, 'localhost', 8080);
```

## 📈 Performance Considerations

- **Firestore**: Scales automatically, pay-per-use
- **Real-time**: Firebase provides real-time updates
- **Caching**: Implement client-side caching for better UX
- **Pagination**: Use Firestore pagination for large datasets

## 🔒 Security Best Practices

1. **Environment Variables**: Store Firebase config securely
2. **Firestore Rules**: Regularly audit security rules
3. **Admin Access**: Limit admin accounts
4. **Input Validation**: Validate all user inputs
5. **HTTPS**: Always use HTTPS in production

## 🎉 Migration Complete!

The system has been successfully migrated from:
- ❌ Java in-memory database
- ❌ Username/password authentication
- ❌ Manual session management

To:
- ✅ Firebase Authentication
- ✅ Firestore database
- ✅ Real-time updates
- ✅ Scalable architecture
- ✅ Modern security

Enjoy your Firebase-powered lottery system! 🎰</content>
<parameter name="filePath">c:\Users\pramu\OneDrive\Desktop\git_projects\-LotterySystem\FIREBASE_MIGRATION_README.md