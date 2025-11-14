# Firebase Setup and Testing Script
# Run this after setting up Firebase project

Write-Host "🔥 Firebase Lottery System Setup" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

# Check if Firebase CLI is installed
try {
    firebase --version | Out-Null
    Write-Host "✅ Firebase CLI found" -ForegroundColor Green
} catch {
    Write-Host "❌ Firebase CLI not found. Install with: npm install -g firebase-tools" -ForegroundColor Red
    exit 1
}

# Login to Firebase (if not already logged in)
Write-Host "`n🔐 Logging into Firebase..." -ForegroundColor Yellow
firebase login

# Initialize Firebase project (if needed)
$projectId = "lotterysystem-18963"
Write-Host "`n🔧 Setting up Firebase project: $projectId" -ForegroundColor Yellow

# Deploy Firestore rules
Write-Host "`n📋 Deploying Firestore security rules..." -ForegroundColor Yellow
firebase deploy --only firestore:rules --project $projectId

# Enable Authentication
Write-Host "`n🔑 Enabling Authentication..." -ForegroundColor Yellow
Write-Host "Please manually enable Email/Password authentication in Firebase Console:"
Write-Host "https://console.firebase.google.com/project/$projectId/authentication/providers"

# Enable Firestore
Write-Host "`n🗄️  Enabling Firestore Database..." -ForegroundColor Yellow
Write-Host "Please manually create Firestore database in Firebase Console:"
Write-Host "https://console.firebase.google.com/project/$projectId/firestore"

Write-Host "`n🎯 Next Steps:" -ForegroundColor Cyan
Write-Host "1. Enable Email/Password authentication in Firebase Console"
Write-Host "2. Create Firestore database in Firebase Console"
Write-Host "3. Set up your first admin user in Firestore:"
Write-Host "   - Collection: users"
Write-Host "   - Document ID: [your-firebase-user-id]"
Write-Host "   - Fields: {email: 'admin@example.com', displayName: 'Admin', balance: 100, isAdmin: true}"
Write-Host "4. Start the Java server: .\run-server.ps1"
Write-Host "5. Serve frontend: cd frontend; python -m http.server 3000"
Write-Host "6. Open http://localhost:3000 and test!"

Write-Host "`n🎉 Firebase setup complete!" -ForegroundColor Green