# PowerShell script to run the Lottery User Client GUI
# This script starts the user client GUI application

Write-Host "Starting Lottery User Client..." -ForegroundColor Green
Write-Host "Make sure the server is running on port 5000" -ForegroundColor Yellow
Write-Host ""

# Set the classpath to include compiled classes and Gson library
$classpath = ".;target/classes;lib/gson-2.10.1.jar"

# Run the User Client GUI
java -cp $classpath client.UserClientGUI

Write-Host "Client closed." -ForegroundColor Red