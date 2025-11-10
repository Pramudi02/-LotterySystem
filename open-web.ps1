# PowerShell script to open the web frontend
# This script opens the lottery system web interface in your default browser

Write-Host "Opening Lottery System Web Frontend..." -ForegroundColor Green
Write-Host "Make sure the server is running (both TCP on 5000 and HTTP API on 8080)" -ForegroundColor Yellow
Write-Host ""

# Get the absolute path to the frontend index.html
$frontendPath = Join-Path $PSScriptRoot "frontend\index.html"
$absolutePath = Resolve-Path $frontendPath

Write-Host "Opening: $absolutePath" -ForegroundColor Cyan

# Open in default browser
Start-Process $absolutePath

Write-Host "Web frontend opened in your default browser." -ForegroundColor Green
Write-Host "If it doesn't open automatically, manually open: $absolutePath" -ForegroundColor Yellow