# PowerShell script to run the Lottery Server
# This script starts the lottery server using Maven
# Note: Most operations are now handled client-side with Firebase

Write-Host "🎰 Starting Lottery Server (Firebase Edition)..." -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Server will listen on:" -ForegroundColor Yellow
Write-Host "  - TCP Socket: port 5000 (legacy clients)" -ForegroundColor Yellow
Write-Host "  - HTTP API: port 8080 (admin operations)" -ForegroundColor Yellow
Write-Host "" -ForegroundColor Yellow
Write-Host "Most user operations now use Firebase/Firestore directly!" -ForegroundColor Green
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Run the server
mvn exec:java

Write-Host "Server stopped." -ForegroundColor Red