// Lottery System Admin Client JavaScript

class LotteryAdminClient {
    constructor() {
        this.serverHost = '127.0.0.1';
        this.serverPort = 8080;
        this.isConnected = false;
        this.isAuthenticated = false;

        this.initElements();
        this.initEventListeners();
    }

    initElements() {
        this.connectBtn = document.getElementById('connectBtn');
        this.connectionStatus = document.getElementById('connectionStatus');
        this.authSection = document.getElementById('authSection');
        this.adminSection = document.getElementById('adminSection');
        this.loginBtn = document.getElementById('loginBtn');
        this.setWinnerBtn = document.getElementById('setWinnerBtn');
        this.viewTicketsBtn = document.getElementById('viewTicketsBtn');
        this.announceResultsBtn = document.getElementById('announceResultsBtn');
        this.messages = document.getElementById('messages');
        this.setWinnerStatus = document.getElementById('setWinnerStatus');
        this.ticketsStatus = document.getElementById('ticketsStatus');
        this.announceStatus = document.getElementById('announceStatus');
        this.ticketsDisplay = document.getElementById('ticketsDisplay');
        this.ticketsBody = document.getElementById('ticketsBody');
        this.currentWinner = document.getElementById('currentWinner');
        this.winnerNumber = document.getElementById('winnerNumber');
    }

    initEventListeners() {
        this.connectBtn.addEventListener('click', () => this.connect());
        this.loginBtn.addEventListener('click', () => this.login());
        this.setWinnerBtn.addEventListener('click', () => this.setWinner());
        this.viewTicketsBtn.addEventListener('click', () => this.viewTickets());
        this.announceResultsBtn.addEventListener('click', () => this.announceResults());

        // Enter key support
        document.getElementById('adminPassword').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.login();
        });
        document.getElementById('winningNumber').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.setWinner();
        });
    }

    async connect() {
        this.serverHost = document.getElementById('serverHost').value;
        this.serverPort = document.getElementById('serverPort').value;

        try {
            // Test connection by making a simple HTTP request to the server
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/health`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                this.isConnected = true;
                this.connectionStatus.textContent = 'Connected';
                this.connectionStatus.className = 'status success';
                this.authSection.style.display = 'block';
                this.addMessage('Connected to server successfully', 'received');
            } else {
                throw new Error('Server responded with error');
            }
        } catch (error) {
            this.connectionStatus.textContent = 'Connection failed: ' + error.message;
            this.connectionStatus.className = 'status error';
            this.addMessage('Failed to connect to server: ' + error.message, 'received');
        }
    }

    async login() {
        if (!this.isConnected) {
            this.addMessage('Please connect to server first', 'received');
            return;
        }

        const password = document.getElementById('adminPassword').value;
        if (!password) {
            document.getElementById('authStatus').textContent = 'Please enter admin password';
            document.getElementById('authStatus').className = 'status error';
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/admin-login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'adminLogin',
                    password: password
                })
            });

            const data = await response.json();

            if (data.success) {
                this.isAuthenticated = true;
                document.getElementById('authStatus').textContent = 'Admin login successful!';
                document.getElementById('authStatus').className = 'status success';
                this.adminSection.style.display = 'block';
                this.addMessage('Logged in as admin', 'sent');
            } else {
                document.getElementById('authStatus').textContent = data.message || 'Admin login failed';
                document.getElementById('authStatus').className = 'status error';
            }
        } catch (error) {
            document.getElementById('authStatus').textContent = 'Login error: ' + error.message;
            document.getElementById('authStatus').className = 'status error';
        }
    }

    async setWinner() {
        if (!this.isAuthenticated) {
            this.addMessage('Please login as admin first', 'received');
            return;
        }

        const winningNumber = parseInt(document.getElementById('winningNumber').value);
        if (!winningNumber || winningNumber < 1 || winningNumber > 100) {
            this.setWinnerStatus.textContent = 'Please enter a valid winning number (1-100)';
            this.setWinnerStatus.className = 'status error';
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/set-winner`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'setWinner',
                    winningNumber: winningNumber
                })
            });

            const data = await response.json();

            if (data.success) {
                this.winnerNumber.textContent = winningNumber;
                this.currentWinner.style.display = 'block';
                this.setWinnerStatus.textContent = `Winning number set to ${winningNumber}`;
                this.setWinnerStatus.className = 'status success';
                this.addMessage(`Set winning number to ${winningNumber}`, 'sent');
            } else {
                this.setWinnerStatus.textContent = data.message || 'Failed to set winning number';
                this.setWinnerStatus.className = 'status error';
            }
        } catch (error) {
            this.setWinnerStatus.textContent = 'Error setting winner: ' + error.message;
            this.setWinnerStatus.className = 'status error';
        }
    }

    async viewTickets() {
        if (!this.isAuthenticated) {
            this.addMessage('Please login as admin first', 'received');
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/view-tickets`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (data.success) {
                this.ticketsBody.innerHTML = '';
                this.ticketsDisplay.style.display = 'block';

                if (data.tickets && data.tickets.length > 0) {
                    data.tickets.forEach(ticket => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${ticket.id}</td>
                            <td>${ticket.username}</td>
                            <td>${ticket.numbers.join(', ')}</td>
                            <td>${new Date(ticket.purchaseTime).toLocaleString()}</td>
                        `;
                        this.ticketsBody.appendChild(row);
                    });
                } else {
                    const row = document.createElement('tr');
                    row.innerHTML = '<td colspan="4">No tickets found</td>';
                    this.ticketsBody.appendChild(row);
                }

                this.ticketsStatus.textContent = `Found ${data.tickets ? data.tickets.length : 0} tickets`;
                this.ticketsStatus.className = 'status success';
                this.addMessage('Viewed all tickets', 'sent');
            } else {
                this.ticketsStatus.textContent = data.message || 'Failed to view tickets';
                this.ticketsStatus.className = 'status error';
            }
        } catch (error) {
            this.ticketsStatus.textContent = 'Error viewing tickets: ' + error.message;
            this.ticketsStatus.className = 'status error';
        }
    }

    async announceResults() {
        if (!this.isAuthenticated) {
            this.addMessage('Please login as admin first', 'received');
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/announce-results`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'announceResults'
                })
            });

            const data = await response.json();

            if (data.success) {
                this.announceStatus.textContent = 'Results announced successfully!';
                this.announceStatus.className = 'status success';
                this.addMessage('Announced lottery results to all users', 'sent');
            } else {
                this.announceStatus.textContent = data.message || 'Failed to announce results';
                this.announceStatus.className = 'status error';
            }
        } catch (error) {
            this.announceStatus.textContent = 'Error announcing results: ' + error.message;
            this.announceStatus.className = 'status error';
        }
    }

    addMessage(message, type) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}`;
        messageDiv.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
        this.messages.appendChild(messageDiv);
        this.messages.scrollTop = this.messages.scrollHeight;
    }
}

// Initialize the admin client when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new LotteryAdminClient();
});