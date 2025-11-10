# Lottery System - Firebase Edition

A complete online lottery system built with Firebase Authentication, Firestore database, and modern web interface with glassmorphism design.

## 🎯 Features

- **Firebase Authentication**: Secure user registration and login with email/password
- **Firestore Database**: Real-time data storage and synchronization
- **Modern UI Design**: Professional dark theme with glassmorphism effects
- **Split-Screen Login**: Elegant authentication interface with visual branding
- **Single-Page Application**: Smooth client-side routing without page reloads
- **Admin Panel**: Complete lottery management system
- **Real-time Results**: Live lottery results and ticket checking
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Java Backend**: Coordination layer for admin operations

## 🚀 Quick Start Guide

### Prerequisites

Make sure you have these installed on your system:

- **Java 11 or higher** - [Download Java](https://www.oracle.com/java/technologies/downloads/)
- **Maven** - [Download Maven](https://maven.apache.org/download.cgi)
- **Python 3.x** - [Download Python](https://www.python.org/downloads/)
- **Web Browser** - Chrome, Firefox, Safari, or Edge (latest version)
- **Firebase Account** - [Create Firebase Account](https://firebase.google.com/)

### Step 1: Firebase Project Setup

1. **Create a New Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add Project" or "Create a project"
   - Enter project name: `LotterySystem` (or any name you prefer)
   - Disable Google Analytics (optional)
   - Click "Create Project"

2. **Enable Authentication**:
   - In Firebase Console, click "Authentication" from the left sidebar
   - Click "Get Started"
   - Go to "Sign-in method" tab
   - Enable "Email/Password" provider
   - Click "Save"

3. **Create Firestore Database**:
   - In Firebase Console, click "Firestore Database" from the left sidebar
   - Click "Create database"
   - Choose "Start in test mode" (for development)
   - Select your preferred location
   - Click "Enable"

4. **Get Firebase Configuration**:
   - In Firebase Console, click the gear icon (⚙️) next to "Project Overview"
   - Click "Project settings"
   - Scroll down to "Your apps" section
   - Click the web icon (</>) to add a web app
   - Register app with nickname: "Lottery Web App"
   - Copy the Firebase configuration object

5. **Update Frontend Configuration**:
   - Open `frontend/modern-app.html` in a text editor
   - Find the Firebase configuration section (around line 400-420)
   - Replace the `firebaseConfig` object with your copied configuration:
   ```javascript
   const firebaseConfig = {
       apiKey: "YOUR_API_KEY",
       authDomain: "YOUR_PROJECT.firebaseapp.com",
       projectId: "YOUR_PROJECT_ID",
       storageBucket: "YOUR_PROJECT.appspot.com",
       messagingSenderId: "YOUR_SENDER_ID",
       appId: "YOUR_APP_ID"
   };
   ```

### Step 2: Create Admin User

1. **Register a User Account**:
   - Start the frontend (see Step 4)
   - Go to `http://localhost:3000/modern-app.html`
   - Click "Sign Up" tab
   - Create an account with email and password
   - Remember this email - it will be your admin account

2. **Grant Admin Privileges**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Open your project
   - Go to "Firestore Database"
   - You should see a `users` collection created
   - Find your user document (ID matches your Firebase user ID)
   - Click on the document
   - Add a new field:
     - Field name: `isAdmin`
     - Type: `boolean`
     - Value: `true`
   - Click "Save"

   **Alternative method using admin-setup.html**:
   - Open `http://localhost:3000/admin-setup.html`
   - Sign in with your account
   - Click "Grant Admin Access" button

### Step 3: Backend Setup (Java Server)

1. **Navigate to Project Directory**:
   ```powershell
   cd C:\Users\pramu\OneDrive\Desktop\git_projects\-LotterySystem
   ```

2. **Clean and Compile the Project**:
   ```powershell
   mvn clean compile
   ```
   - This will download dependencies and compile the Java code
   - Wait for "BUILD SUCCESS" message

3. **Start the Java Server**:
   ```powershell
   mvn exec:java
   ```
   - Server will start on port 8080
   - You should see: "HTTP Server started on port 8080"
   - Leave this terminal window open

   **Alternative: Using PowerShell script**:
   ```powershell
   .\run-server.ps1
   ```

### Step 4: Frontend Setup (Web Server)

1. **Open a New Terminal/PowerShell Window**

2. **Navigate to Frontend Directory**:
   ```powershell
   cd C:\Users\pramu\OneDrive\Desktop\git_projects\-LotterySystem\frontend
   ```

3. **Start Python HTTP Server**:
   ```powershell
   python -m http.server 3000
   ```
   - If Python 3 is not default, try: `python3 -m http.server 3000`
   - You should see: "Serving HTTP on :: port 3000"
   - Leave this terminal window open

4. **Open the Application**:
   - Open your web browser
   - Navigate to: `http://localhost:3000/modern-app.html`
   - You should see the lottery system login page

### Step 5: Test the Application

1. **Sign In**:
   - Use the email and password you created earlier
   - Click "Log In" button

2. **Explore Features**:
   - **Dashboard**: View your statistics and balance
   - **Play Lottery**: Buy lottery tickets ($10 each)
   - **Results**: Check your tickets and see if you won
   - **Admin Panel** (if admin): Set winning numbers and manage the lottery

3. **Admin Functions** (requires admin privileges):
   - Go to Admin section
   - Set winning number (1-100)
   - View all tickets purchased
   - View all users

## 🎮 How to Use the System

### For Regular Users

1. **Create Account**:
   - Click "Sign Up" tab on login page
   - Enter your name, email, and password (minimum 6 characters)
   - Click "Create Account"

2. **Buy Lottery Tickets**:
   - After login, click "Play Lottery" in the navigation
   - Click "Buy Ticket" button ($10 per ticket)
   - Each ticket generates 5 random numbers (1-100)
   - Your balance will be deducted automatically

3. **Check Results**:
   - Click "Results" in the navigation
   - View all your purchased tickets
   - See the winning number (if set by admin)
   - Winning tickets are highlighted in green
   - Each winning number in your ticket awards $25

4. **View Balance**:
   - Your balance is displayed in the top-right corner
   - Starts at $100 for new accounts
   - Increases when you win, decreases when you buy tickets

### For Admin Users

1. **Access Admin Panel**:
   - Log in with your admin account
   - Click "Admin" in the navigation menu

2. **Set Winning Number**:
   - In the "Set Winning Number" section
   - Enter a number between 1 and 100
   - Click "Set Winner" button
   - All tickets will be checked automatically

3. **View All Tickets**:
   - See all tickets purchased by all users
   - View ticket numbers and purchase times
   - Check which tickets won

4. **View All Users**:
   - See all registered users
   - View their balances and email addresses
   - Monitor admin privileges

## 🏗️ Project Structure

```
LotterySystem/
├── frontend/
│   ├── modern-app.html          # Main application (PRIMARY)
│   ├── app-styles-new.css       # Modern dark theme styles
│   ├── admin-setup.html         # Admin privilege setup tool
│   ├── admin.js                 # Admin client JavaScript (legacy)
│   ├── client.js                # User client JavaScript (legacy)
│   ├── firebase-config.js       # Firebase configuration module
│   └── assets/
│       ├── logo2.png           # Application logo
│       ├── wallpaper.jpeg      # Login page background
│       └── mainwallpaper.jpg   # Main background image
├── src/
│   └── main/
│       └── java/
│           └── lms/
│               ├── HttpServerModule.java    # REST API server
│               ├── DataManager.java         # Firebase integration
│               └── [other Java files]
├── pom.xml                      # Maven configuration
├── README.md                    # This file
├── service.md                   # Network services documentation
└── run-server.ps1              # PowerShell script to start server
```

## 🔧 Technical Details

### Frontend Technologies

- **HTML5**: Modern semantic markup
- **CSS3**: Glassmorphism design, animations, responsive layout
- **JavaScript**: Vanilla JS with Firebase SDK
- **Firebase SDK v10.7.1**: Authentication, Firestore
- **Lucide Icons**: Beautiful SVG icons

### Backend Technologies

- **Java 11+**: Server-side logic
- **Maven**: Dependency management
- **HTTP Server**: REST API on port 8080
- **Firebase Admin SDK**: Server-side Firebase operations

### Database Structure (Firestore)

**users** collection:
```javascript
{
  email: "user@example.com",
  displayName: "John Doe",
  balance: 100,
  isAdmin: false  // true for admin users
}
```

**tickets** collection:
```javascript
{
  userId: "firebase_user_id",
  numbers: [12, 45, 67, 23, 89],
  purchaseTime: Timestamp,
  status: "pending"  // or "won" or "lost"
}
```

**system** collection (lottery document):
```javascript
{
  winningNumber: 42,
  setTime: Timestamp
}
```

## 🔐 Security Features

- **Firebase Authentication**: Industry-standard secure authentication
- **Firestore Security Rules**: Granular access control for database operations
- **Admin Verification**: Server-side admin privilege checking
- **Input Validation**: Client and server-side validation
- **HTTPS Ready**: Supports secure connections in production
- **Password Requirements**: Minimum 6 characters enforced

## 🚀 Deployment to Production

### Firebase Hosting Deployment

1. **Install Firebase CLI**:
   ```powershell
   npm install -g firebase-tools
   ```

2. **Login to Firebase**:
   ```powershell
   firebase login
   ```

3. **Initialize Hosting**:
   ```powershell
   firebase init hosting
   ```
   - Select your Firebase project
   - Set public directory to: `frontend`
   - Configure as single-page app: Yes
   - Don't overwrite files

4. **Deploy to Firebase**:
   ```powershell
   firebase deploy --only hosting
   ```

5. **Update Firebase Config**:
   - Use production Firebase config in `modern-app.html`
   - Enable production security rules in Firestore

### Java Backend Deployment

1. **Cloud Platform Options**:
   - **Heroku**: Easy deployment with Git
   - **AWS Elastic Beanstalk**: Scalable Java hosting
   - **Google Cloud Run**: Containerized deployment
   - **Azure App Service**: Microsoft cloud hosting

2. **Build Production JAR**:
   ```powershell
   mvn clean package
   ```

3. **Configure Environment Variables**:
   - Set Firebase credentials
   - Configure CORS for production domain
   - Set production API endpoints

## 🐛 Troubleshooting Guide

### Common Issues and Solutions

#### 1. Firebase Connection Failed
**Problem**: Can't connect to Firebase, authentication not working

**Solutions**:
- Check Firebase configuration in `modern-app.html`
- Verify API key and project ID are correct
- Ensure Authentication is enabled in Firebase Console
- Check browser console for specific error messages
- Verify Firestore is created and not in locked mode

#### 2. "Permission Denied" Error
**Problem**: Can't read/write to Firestore

**Solutions**:
- Check Firestore security rules
- Ensure you're logged in
- For test mode, rules should allow read/write when authenticated
- Admin operations require `isAdmin: true` in user document

#### 3. Java Server Won't Start
**Problem**: Maven exec:java fails or server doesn't start

**Solutions**:
- Verify Java 11+ is installed: `java -version`
- Check Maven is installed: `mvn -version`
- Run `mvn clean compile` first
- Check port 8080 is not already in use
- Look for error messages in console output

#### 4. Frontend Not Loading
**Problem**: Page shows "Can't reach this page" or blank screen

**Solutions**:
- Verify Python HTTP server is running: `python -m http.server 3000`
- Check you're accessing: `http://localhost:3000/modern-app.html`
- Try a different port if 3000 is busy: `python -m http.server 8000`
- Clear browser cache (Ctrl+Shift+Delete)
- Check browser console for JavaScript errors

#### 5. Admin Features Not Available
**Problem**: Can't access admin panel or admin buttons don't appear

**Solutions**:
- Verify `isAdmin: true` is set in your Firestore user document
- Log out and log back in after setting admin privileges
- Check user document exists in Firestore `users` collection
- Use `admin-setup.html` to easily grant admin access

#### 6. Balance Not Updating
**Problem**: Balance doesn't change after buying tickets or winning

**Solutions**:
- Refresh the page (F5)
- Check Firestore user document for correct balance
- Verify transaction completed in browser console
- Check network tab for failed requests

#### 7. Tickets Not Appearing
**Problem**: Purchased tickets don't show up

**Solutions**:
- Wait a few seconds and refresh the page
- Check Firestore `tickets` collection for your tickets
- Verify balance was deducted (confirms purchase)
- Check browser console for errors

### Debug Commands

```powershell
# Check if Java is installed
java -version

# Check if Maven is installed
mvn -version

# Check if Python is installed
python --version

# Test server health (when Java server is running)
curl http://localhost:8080/health

# Check if port 3000 is available
netstat -ano | findstr :3000

# Check if port 8080 is available
netstat -ano | findstr :8080
```

### Browser Console Debugging

1. Open browser Developer Tools (F12)
2. Go to Console tab
3. Look for error messages in red
4. Check Network tab for failed requests
5. Verify Firebase modules are loading

## 📝 Development Guide

### Adding New Features

#### Frontend Changes

1. **Add New Pages**:
   - Add new section in `modern-app.html`
   - Update navigation menu with new page link
   - Create corresponding styles in `app-styles-new.css`

2. **Add New Firebase Operations**:
   - Use Firebase SDK functions in `modern-app.html` script section
   - Example: Adding new collection or fields
   ```javascript
   await setDoc(doc(db, "newCollection", "documentId"), {
       field1: "value1",
       field2: "value2"
   });
   ```

3. **Update Styles**:
   - Modify `app-styles-new.css` for styling changes
   - Use CSS custom properties (variables) for consistency
   - Follow existing glassmorphism design patterns

#### Backend Changes

1. **Add New API Endpoints**:
   - Edit `src/main/java/lms/HttpServerModule.java`
   - Add new route handlers
   - Example:
   ```java
   if (path.equals("/new-endpoint")) {
       handleNewEndpoint(exchange);
   }
   ```

2. **Add Data Operations**:
   - Edit `src/main/java/lms/DataManager.java`
   - Add methods for new database operations
   - Use Firebase Admin SDK

3. **Test Changes**:
   - Recompile: `mvn clean compile`
   - Restart server: `mvn exec:java`
   - Test with browser or Postman

### Testing Checklist

Before deploying or committing changes:

- [ ] User registration works
- [ ] User login works
- [ ] Ticket purchase deducts balance
- [ ] Tickets appear in results page
- [ ] Admin can set winning number
- [ ] Winning calculation is correct
- [ ] Admin panel shows all tickets
- [ ] Admin panel shows all users
- [ ] Logout works properly
- [ ] UI is responsive on mobile
- [ ] No console errors
- [ ] Backend server responds to health check

## 🎨 UI Customization

### Changing Colors

Edit `app-styles-new.css` CSS variables in the `:root` section:

```css
:root {
    --navy-dark: #121b2b;        /* Dark navy background */
    --accent-orange: #ff6b35;    /* Primary orange accent */
    --accent-coral: #ff5c4d;     /* Secondary coral accent */
    --text-primary: #f1f5f9;     /* Light text */
    --text-secondary: #94a3b8;   /* Muted text */
    /* Modify these to change the entire color scheme */
}
```

### Changing Logo

Replace logo files in `frontend/assets/`:
- `logo2.png` - Main logo (appears in header and login)
- Make sure image is PNG format with transparent background
- Recommended size: 200x100 pixels

### Changing Background Images

Replace image files in `frontend/assets/`:
- `mainwallpaper.jpg` - Main background for all pages
- `wallpaper.jpeg` - Login page right side background

## 📚 Additional Resources

### Firebase Documentation
- [Firebase Authentication Docs](https://firebase.google.com/docs/auth)
- [Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Firebase Security Rules](https://firebase.google.com/docs/rules)

### Development Tools
- [Maven Documentation](https://maven.apache.org/guides/)
- [Java HTTP Server](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html)
- [Lucide Icons](https://lucide.dev/) - Icon library used in UI

### Tutorials
- [Firebase Web Quickstart](https://firebase.google.com/docs/web/setup)
- [Firestore Data Model](https://firebase.google.com/docs/firestore/data-model)
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

## 📄 License

This project is for educational purposes. Feel free to modify and distribute as needed.

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes
4. Test thoroughly using the checklist above
5. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
6. Push to the branch (`git push origin feature/AmazingFeature`)
7. Open a Pull Request

## 💬 Support

If you encounter issues:

1. Check the Troubleshooting Guide above
2. Review browser console for errors
3. Check Firebase Console for data issues
4. Verify all prerequisites are installed
5. Open an issue on GitHub with:
   - Detailed description of the problem
   - Steps to reproduce
   - Error messages or screenshots
   - Your environment (OS, browser, Java version)

## 🎉 Features Roadmap

Potential future enhancements:

- [ ] Multiple lottery games simultaneously
- [ ] Scheduled automatic draws
- [ ] Email notifications for winners
- [ ] Social sharing features
- [ ] Transaction history
- [ ] User profile customization
- [ ] Multi-language support
- [ ] Mobile app version
- [ ] Payment gateway integration
- [ ] Statistics and analytics dashboard

---

**Happy Lottery Gaming! 🎰✨**

For questions or support, please check the Troubleshooting Guide or open an issue on GitHub.
