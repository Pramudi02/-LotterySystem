# PowerShell script to run the Lottery Admin Client GUI
# This script starts the admin client GUI application

Write-Host "Starting Lottery Admin Client..." -ForegroundColor Green
Write-Host "Make sure the server is running on port 5000" -ForegroundColor Yellow
Write-Host "Use this client to set winning numbers and manage lottery results" -ForegroundColor Yellow
Write-Host ""

# Set the classpath to include compiled classes and Gson library
$classpath = ".;target/classes;lib/gson-2.10.1.jar"

# Run the Admin Client GUI
java -cp $classpath client.AdminClientGUI

Write-Host "Admin client closed." -ForegroundColor Red