# PowerShell script to run the Lottery Server
# This script starts the lottery server using Maven

Write-Host "Starting Lottery Server..." -ForegroundColor Green
Write-Host "Server will listen on port 5000" -ForegroundColor Yellow
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Run the server
mvn exec:java

Write-Host "Server stopped." -ForegroundColor Red