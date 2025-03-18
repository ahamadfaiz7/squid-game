## Rock Paper Scissors Minus One - Game Rules

Rock Paper Scissors Minus One is a **real-time multiplayer** game based on the classic **Rock Paper Scissors** game, but
with a slight twist!
It allows **two players** to compete in a **best-of-three** format using **real-time WebSocket communication**.

### Objective:

- The goal of the game is to **win two rounds** before your opponent.
- Each round, both players **simultaneously choose** one move: **Rock, Paper, or Scissors**.
- The winner of the round is determined based on the classic rules:
- **Rock beats Scissors** (Rock crushes Scissors)
- **Scissors beats Paper** (Scissors cuts Paper)
- **Paper beats Rock** (Paper wraps Rock)
- **Same move results in a draw**, and the round is replayed.

### How to Play:

1. **Register/Login** using your credentials.
2. **Player 1 (creator) starts a new game** in the lobby.
3. **Player 2 joins the game** using the provided game ID.
4. **Once both players are ready**, they can start making moves.
5. Each player **enters their move (Rock, Paper, or Scissors)** and submits it.
6. When both players submit their moves, the game calculates the winner for that round.
7. The game continues until **one player wins 2 rounds**.
8. The **game status is updated in real-time**, and the winner is displayed.

### Winning the Game:

- The game is **best of three** rounds.
- A player **wins the match** when they secure **two wins**.
- The final result is displayed in the **Game Room UI**.

## Example Game Flow:

| **Player One Move** | **Player Two Move** | **Winner**                              |
|---------------------|---------------------|-----------------------------------------|
| **Rock**            | Scissors            | **Player One**  (Rock crushes Scissors) |
| **Scissors**        | Paper               | **Player One**  (Scissors cut Paper)    |
| **Paper**           | Rock                | **Player One**  (Paper covers Rock)     |
| **Scissors**        | Rock                | **Player Two**  (Rock crushes Scissors) |
| **Paper**           | Scissors            | **Player Two**  (Scissors cut Paper)    |
| **Rock**            | Paper               | **Player Two**  (Paper covers Rock)     |
| **Same Move**       | Same Move           | **Tie** (No Winner)                     |


## Technologies Used

### Frontend (React)

The frontend is developed using **React.js**

- **React**: Component-based UI library for building the frontend.
- **React Router**: Handles client-side routing for navigation.
- **Tailwind CSS**: Utility-first CSS framework for styling.
- **Fetch API / Axios**: Handles API calls to communicate with the backend.
- **WebSockets**: Used for real-time updates between players.

### Backend (Spring Boot)

The backend is built using **Spring Boot**

- **Spring Boot**: Provides the framework for RESTful APIs and WebSockets.
- **Spring Security**: Manages authentication and authorization.
- **JWT (JSON Web Token)**: Securely handles user authentication and session management.
- **Spring WebSocket**: Enables real-time WebSocket communication between players.

### Database (H2 In-Memory Database)

- **H2 Database**: Lightweight in-memory database used for storing game data.
- **Spring Data JPA (Hibernate)**: ORM (Object-Relational Mapping) framework for managing database operations.

### Authentication & Security

- **JWT (JSON Web Token)**: Secure user authentication and stateless session management.
- **Spring Security**: Enforces authentication for API endpoints.
- **CORS (Cross-Origin Resource Sharing)**: Secure communication between frontend and backend running on
  different ports.

### Real-Time Communication

- **WebSockets**: Used to enable real-time communication between players.
- **Spring WebSocket + React WebSocket Client**: Enables bidirectional updates.

### Steps to Run the Application

1. **Clone the Repository** from git@github.com:ahamadfaiz7/squid-game.git or https://github.com/ahamadfaiz7/squid-game.git
2. The project is under the folder cd squid-game/squid-games.
4. **Import the Project** into your IDE and perform a **clean build** (The project follows a **Maven-based structure**).
5. **Start the Backend** by running `RpsMinusOneApplication.java` in your IDE.
6. Then cd to squid-ui which has the react Ui files. Run npm install to install the modules and then npm start to start the UI app.
7. **Launch the React UI** by visiting: [`http://localhost:3000`](http://localhost:3000).
8. **Access the In-Memory Database** (H2) at: [`http://localhost:8787/h2-console`](http://localhost:8787/h2-console) (automatically hosted after backend startup).
9. **Database Connection Details** (URL, username, and password) are available in `application.properties`.
10. **Register a New User** at: [`http://localhost:3000/register`](http://localhost:3000/register).
11. **Log in to the Application** as a registered user at: [`http://localhost:3000/login`](http://localhost:3000/login).
12. **Game Creation & Joining**:
 - The first user **creates a game**, which generates a **Game ID**.
 - The second user **logs in** and **joins the game** using the Game ID.
10. **Check Game History**:
- Log in to **H2 Database** and run: `SELECT * FROM GAMES;` to view past games.
11. **Play the Game**:
- Once both players have joined, they can **start playing** by submitting their moves.
12. One user can play from the UI and another from the API . The steps are below.


### Steps to Run the Application from the Postman as an API
Player 2 Can Join the Game Using a REST Client (All Requests Use POST Method)
1. Register a New Player
   Endpoint: POST http://localhost:8787/auth/register
   Request Body:
   {
   "username": "player2",
   "password": "testpass"
   }
2. Log In
   Endpoint: POST http://localhost:8787/auth/login
   Request Body:{
   "username": "player2",
   "password": "testpass"
   }
   Response:
   Upon successful login, the server will return a JWT token. Use this token in the Authorization header for all subsequent requests.

Example Response:{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwbGF5ZXIyIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3NDIxOTkzMDcsImV4cCI6MTc0MjIwMjkwN30.qJAf8ucvZm848l3bhY22pURUgbmWrVa6lEfxPtLn0VU"
}


3. Join an Existing Game
   Endpoint: POST http://localhost:8787/game/join/4
   Headers:Authorization: Bearer <your_jwt_token>
   Replace 4 with the Game ID you want to join.

   
4. Submit a Move
   Endpoint: POST http://localhost:8787/game/move
   Headers:Authorization: Bearer <your_jwt_token>
   Request Body:{
   "gameId": 4,
   "move": "rock"
   }
   Replace 4 with the actual Game ID and "rock" with your chosen move ("rock", "paper", or "scissors").

