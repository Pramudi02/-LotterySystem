# Complete Lottery System Runner
# This script sets up and runs the entire Firebase-based lottery system

Write-Host "🎰 Complete Lottery System Setup & Runner" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

# Check prerequisites
Write-Host "🔍 Checking Prerequisites..." -ForegroundColor Yellow

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
    } else {
        throw "Java not found"
    }
} catch {
    Write-Host "❌ Java 11+ required. Please install Java from https://adoptium.net/" -ForegroundColor Red
    exit 1
}

# Check Maven
try {
    mvn -version | Out-Null
    Write-Host "✅ Maven found" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven required. Please install Maven from https://maven.apache.org/" -ForegroundColor Red
    exit 1
}

# Check Python
try {
    python --version | Out-Null
    Write-Host "✅ Python found" -ForegroundColor Green
} catch {
    Write-Host "❌ Python required. Please install Python from https://python.org/" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔥 Step 1: Firebase Setup" -ForegroundColor Cyan
Write-Host "------------------------" -ForegroundColor Cyan

$firebaseConfigured = Read-Host "Have you set up Firebase project with Authentication and Firestore? (y/n)"
if ($firebaseConfigured -ne "y") {
    Write-Host ""
    Write-Host "📋 Firebase Setup Instructions:" -ForegroundColor Yellow
    Write-Host "1. Go to https://console.firebase.google.com/" -ForegroundColor White
    Write-Host "2. Create new project: 'lotterysystem'" -ForegroundColor White
    Write-Host "3. Enable Authentication > Email/Password" -ForegroundColor White
    Write-Host "4. Enable Firestore Database" -ForegroundColor White
    Write-Host "5. Run: .\setup-firebase.ps1" -ForegroundColor White
    Write-Host ""
    Write-Host "Press Enter when Firebase is configured..." -ForegroundColor Cyan
    Read-Host
}

# Step 2: Build Java Backend
Write-Host ""
Write-Host "🔧 Step 2: Building Java Backend" -ForegroundColor Cyan
Write-Host "--------------------------------" -ForegroundColor Cyan

Write-Host "Compiling Java project..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Java compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Java backend compiled successfully" -ForegroundColor Green

# Step 3: Start Backend Server
Write-Host ""
Write-Host "🚀 Step 3: Starting Backend Server" -ForegroundColor Cyan
Write-Host "----------------------------------" -ForegroundColor Cyan

Write-Host "Starting Java server on port 8080..." -ForegroundColor Yellow

# Start server in background
$serverJob = Start-Job -ScriptBlock {
    Set-Location "C:\Users\pramu\OneDrive\Desktop\git_projects\-LotterySystem"
    mvn exec:java
}

# Wait a moment for server to start
Start-Sleep -Seconds 5

# Check if server is running
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -TimeoutSec 5 -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Backend server started successfully on port 8080" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  Backend server may not be responding, but continuing..." -ForegroundColor Yellow
}

# Step 4: Start Frontend
Write-Host ""
Write-Host "🌐 Step 4: Starting Frontend" -ForegroundColor Cyan
Write-Host "---------------------------" -ForegroundColor Cyan

Write-Host "Starting frontend server on port 3000..." -ForegroundColor Yellow

# Change to frontend directory and start server
Set-Location frontend
$frontendJob = Start-Job -ScriptBlock {
    Set-Location "C:\Users\pramu\OneDrive\Desktop\git_projects\-LotterySystem\frontend"
    python -m http.server 3000
}

# Wait for frontend to start
Start-Sleep -Seconds 3

# Check if frontend is running
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -TimeoutSec 5 -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Frontend server started successfully on port 3000" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  Frontend server may not be responding, but continuing..." -ForegroundColor Yellow
}

# Return to root directory
Set-Location ..

Write-Host ""
Write-Host "🎉 System Started Successfully!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "🔧 Backend:  http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Next Steps:" -ForegroundColor Yellow
Write-Host "1. Open http://localhost:3000 in your browser" -ForegroundColor White
Write-Host "2. Sign up for a new account or sign in" -ForegroundColor White
Write-Host "3. Buy lottery tickets and check results" -ForegroundColor White
Write-Host "4. Admin users can access the admin panel" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  Press Ctrl+C to stop all servers" -ForegroundColor Yellow
Write-Host ""

# Keep script running to maintain servers
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
} finally {
    # Cleanup when script is terminated
    Write-Host ""
    Write-Host "🛑 Stopping servers..." -ForegroundColor Yellow

    # Stop background jobs
    if ($serverJob) {
        Stop-Job $serverJob -ErrorAction SilentlyContinue
        Remove-Job $serverJob -ErrorAction SilentlyContinue
    }

    if ($frontendJob) {
        Stop-Job $frontendJob -ErrorAction SilentlyContinue
        Remove-Job $frontendJob -ErrorAction SilentlyContinue
    }

    Write-Host "✅ Servers stopped. Goodbye!" -ForegroundColor Green
}