# Crypto Trading Simulator

This is a web application that simulates a cryptocurrency trading platform, allowing users to trade with a virtual account balance using real-time data from the Kraken exchange.

## Features

- **Real-time Price Ticker**: View live prices for the top 20 cryptocurrencies from the Kraken WebSocket API.
- **User Authentication**: Secure user registration and login system using JWT.
- **Virtual Trading Account**: Each user gets a virtual account with an initial balance of $10,000.
- **Buy & Sell**: Execute buy and sell orders for various cryptocurrencies.
- **Portfolio Management**: View your current cryptocurrency holdings and cash balance.
- **Transaction History**: A complete log of all your past trades.
- **Account Reset**: Reset your account balance and holdings back to the initial state.

## Technologies Used

**Backend:**
- Java 17
- Spring Boot 3.3.1
- Spring Security
- Spring Data JDBC
- PostgreSQL
- Maven
- Liquibase
- JWT

**Frontend:**
- React 18
- React Router
- WebSocket API
- CSS

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- Node.js 14+
- npm 6+
- A running PostgreSQL instance

### Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/VKalushev/crypto-trading-sim.git
    cd crypto-trading-sim
    ```

2.  **Configure the Backend:**
    - Navigate to `backend/src/main/resources/`.
    - Open `application.yml` and update the `spring.datasource` properties with your PostgreSQL connection details (URL, username, password).
    - *Disregard the fact that jwtSecret is left there, it is left on purpose so you don't have to generate your own secret*

3.  **Configure the Frontend:**
    - Navigate to `frontend/crypto-trading-sim/`.
    - Create a `.env` file from the example: `cp .env-example .env`.
    - The `REACT_APP_KRAKEN_BASE_API_URL` is used for fetching the initial list of assets. The WebSocket is used for live prices. No key is required for this public endpoint.

### Running the Application

You will need to run two processes in separate terminals: the backend server and the frontend development server.

1.  **Run the Backend:**
    ```bash
    cd backend
    mvn spring-boot:run
    ```
    The backend server will start on `http://localhost:8080`.

2.  **Run the Frontend:**
    ```bash
    cd frontend/crypto-trading-sim
    npm install
    npm start
    ```
    The frontend development server will start and open the application in your browser at `http://localhost:3000`.

You can now register a new user and start trading.