# It ain't much, but it's honest work

## 🌐 Link to the application

You can try the application here: https://guesswho.fun

## About the project

This repository contains the backend logic for the **Guess Who?** game.
It provides a RESTful API and STOMP over WebSocket connection to manage rooms, players, game state, and custom image uploads.
The backend ensures a seamless multiplayer experience, handling real-time interactions and enforcing game rules.
It serves as the backbone for the [Guess Who Frontend](https://github.com/Dantomaz/guess-who-fe).

## 🖥️ Features

- 📡 **Game Management**: Handles in-memory creation, updates, and state synchronization of the game, without an additional database.
- 👫 **Team-Based Gameplay**: Supports TEAM vs TEAM mode.
- 🖼️ **Custom Image Support**: Stores and manages user-uploaded images to personalize gameplay.
- ⚡ **Optimized Performance**: Efficiently handles concurrent players and game sessions.

## 🚀 Technologies Used

- **Java Spring Boot**: Framework for building RESTful APIs and managing backend logic.
- **STOMP over WebSockets**: For real-time communication and multiplayer interactions.
- **Spring Session & Redis database**: For managing user sessions.
- **Docker**: For containerizing the application and simplifying local environment management.
- **Gradle**: For build automation and dependency management.

## 📄 License

This project is licensed under the terms of MIT License. See the LICENSE.txt file for more details.