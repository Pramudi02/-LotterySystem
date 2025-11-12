# PowerShell script to open the web frontend
# This script opens the lottery system web interface in your default browser
# Note: Frontend now uses Firebase Authentication and Firestore

Write-Host "🎰 Opening Lottery System Web Frontend (Firebase Edition)..." -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Features:" -ForegroundColor Yellow
Write-Host "  ✅ Firebase Authentication (Email/Password)" -ForegroundColor Green
Write-Host "  ✅ Firestore Database (Real-time)" -ForegroundColor Green
Write-Host "  ✅ Modern Web UI" -ForegroundColor Green
Write-Host "  ✅ Admin Panel" -ForegroundColor Green
Write-Host ""
Write-Host "Requirements:" -ForegroundColor Yellow
Write-Host "  - Firebase project configured" -ForegroundColor Cyan
Write-Host "  - Firestore security rules deployed" -ForegroundColor Cyan
Write-Host "  - At least one admin user set up in Firestore" -ForegroundColor Cyan
Write-Host ""

# Check if frontend server is running
$frontendServerRunning = $false
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -TimeoutSec 5 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        $frontendServerRunning = $true
    }
} catch {
    # Server not running
}

if ($frontendServerRunning) {
    Write-Host "🌐 Frontend server detected on port 3000" -ForegroundColor Green
    $url = "http://localhost:3000"
} else {
    Write-Host "⚠️  Frontend server not detected on port 3000" -ForegroundColor Yellow
    Write-Host "   Start it with: cd frontend; python -m http.server 3000" -ForegroundColor Cyan

    # Get the absolute path to the frontend index.html
    $frontendPath = Join-Path $PSScriptRoot "frontend\index.html"
    $absolutePath = Resolve-Path $frontendPath
    $url = $absolutePath
}

Write-Host ""
Write-Host "Opening: $url" -ForegroundColor Cyan

# Open in default browser
Start-Process $url

Write-Host ""
Write-Host "🎯 Quick Start:" -ForegroundColor Green
Write-Host "1. Sign up for a new account or sign in" -ForegroundColor White
Write-Host "2. Buy lottery tickets ($10 each)" -ForegroundColor White
Write-Host "3. Check your results" -ForegroundColor White
Write-Host "4. Admin users can set winning numbers" -ForegroundColor White
Write-Host ""
Write-Host "📖 See FIREBASE_MIGRATION_README.md for detailed setup instructions" -ForegroundColor Cyan