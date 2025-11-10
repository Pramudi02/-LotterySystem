// Lottery System User Client JavaScript

class LotteryClient {
    constructor() {
        this.serverHost = '127.0.0.1';
        this.serverPort = 8080;
        this.isConnected = false;
        this.username = '';
        this.balance = 0;

        this.initElements();
        this.initEventListeners();
    }

    initElements() {
        this.connectBtn = document.getElementById('connectBtn');
        this.connectionStatus = document.getElementById('connectionStatus');
        this.authSection = document.getElementById('authSection');
        this.userSection = document.getElementById('userSection');
        this.loginBtn = document.getElementById('loginBtn');
        this.buyTicketBtn = document.getElementById('buyTicketBtn');
        this.checkResultsBtn = document.getElementById('checkResultsBtn');
        this.messages = document.getElementById('messages');
        this.balanceDisplay = document.getElementById('balance');
        this.ticketStatus = document.getElementById('ticketStatus');
        this.resultsStatus = document.getElementById('resultsStatus');
        this.resultsDisplay = document.getElementById('resultsDisplay');
        this.userTickets = document.getElementById('userTickets');
    }

    initEventListeners() {
        this.connectBtn.addEventListener('click', () => this.connect());
        this.loginBtn.addEventListener('click', () => this.login());
        this.buyTicketBtn.addEventListener('click', () => this.buyTicket());
        this.checkResultsBtn.addEventListener('click', () => this.checkResults());

        // Enter key support
        document.getElementById('username').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.login();
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

        const username = document.getElementById('username').value.trim();
        if (!username) {
            document.getElementById('authStatus').textContent = 'Please enter a username';
            document.getElementById('authStatus').className = 'status error';
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'login',
                    username: username
                })
            });

            const data = await response.json();

            if (data.success) {
                this.username = username;
                this.balance = data.balance || 0;
                this.balanceDisplay.textContent = this.balance;
                document.getElementById('authStatus').textContent = 'Login successful!';
                document.getElementById('authStatus').className = 'status success';
                this.userSection.style.display = 'block';
                this.addMessage(`Logged in as ${username}`, 'sent');
            } else {
                document.getElementById('authStatus').textContent = data.message || 'Login failed';
                document.getElementById('authStatus').className = 'status error';
            }
        } catch (error) {
            document.getElementById('authStatus').textContent = 'Login error: ' + error.message;
            document.getElementById('authStatus').className = 'status error';
        }
    }

    async buyTicket() {
        if (!this.username) {
            this.addMessage('Please login first', 'received');
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/buy-ticket`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'buyTicket',
                    username: this.username
                })
            });

            const data = await response.json();

            if (data.success) {
                this.balance = data.balance;
                this.balanceDisplay.textContent = this.balance;
                this.ticketStatus.textContent = `Ticket purchased! Numbers: ${data.numbers.join(', ')}`;
                this.ticketStatus.className = 'status success';
                this.addMessage(`Bought ticket with numbers: ${data.numbers.join(', ')}`, 'sent');
            } else {
                this.ticketStatus.textContent = data.message || 'Failed to buy ticket';
                this.ticketStatus.className = 'status error';
            }
        } catch (error) {
            this.ticketStatus.textContent = 'Error buying ticket: ' + error.message;
            this.ticketStatus.className = 'status error';
        }
    }

    async checkResults() {
        if (!this.username) {
            this.addMessage('Please login first', 'received');
            return;
        }

        try {
            const response = await fetch(`http://${this.serverHost}:${this.serverPort}/check-results`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'checkResults',
                    username: this.username
                })
            });

            const data = await response.json();

            if (data.success) {
                this.resultsDisplay.style.display = 'block';
                this.userTickets.innerHTML = '';

                if (data.tickets && data.tickets.length > 0) {
                    data.tickets.forEach(ticket => {
                        const ticketDiv = document.createElement('div');
                        ticketDiv.className = 'ticket-item';
                        ticketDiv.innerHTML = `
                            <strong>Ticket #${ticket.id}</strong><br>
                            Numbers: ${ticket.numbers.join(', ')}<br>
                            Status: ${ticket.won ? '<span style="color: green;">WON!</span>' : '<span style="color: red;">Lost</span>'}<br>
                            Prize: ${ticket.prize || 0} units
                        `;
                        this.userTickets.appendChild(ticketDiv);
                    });
                } else {
                    this.userTickets.innerHTML = '<p>No tickets found</p>';
                }

                this.resultsStatus.textContent = 'Results checked successfully';
                this.resultsStatus.className = 'status success';
                this.addMessage('Checked lottery results', 'sent');
            } else {
                this.resultsStatus.textContent = data.message || 'Failed to check results';
                this.resultsStatus.className = 'status error';
            }
        } catch (error) {
            this.resultsStatus.textContent = 'Error checking results: ' + error.message;
            this.resultsStatus.className = 'status error';
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

// Initialize the client when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new LotteryClient();
});