# Quest: A Multiplayer Card Game

## Overview

Quest is an engaging multiplayer card game where players embark on exciting quests, battle foes, and strategically manage their resources to emerge victorious. This project provides a comprehensive implementation of the Quest game, featuring a robust backend and two interactive frontends: a web-based GUI and a command-line interface (CLI).

## Features

* **Multiplayer Gameplay:** Supports multiple players engaging in simultaneous quests.
* **Dynamic Quests:** Quests are generated with varying difficulties, ensuring unique challenges in each game.
* **Strategic Card Management:** Players must carefully select and play cards to optimize their attack and defense.
* **Intuitive Frontends:** User-friendly interfaces for an immersive gaming experience, both through a web GUI and a CLI.
* **Robust Backend:** Efficiently handles game logic, card management, and player interactions.

## Implementation Details

### Backend

The backend is developed in Java using the Spring Boot framework, providing a RESTful API for game management and player interactions. Key components include:

* **Game Logic:** The `GameLogic` class handles core game mechanics, card distribution, and rule enforcement.
* **Game State:** The `GameState` class maintains the current state of the game, including player hands, quest progress, and game status.
* **Decks:** The `Deck` class represents the adventure and quest decks, handling card shuffling and drawing.
* **Cards:** The `Card` class defines the structure of individual cards, including their type, value, and behavior.
* **Players:** The `Player` class represents a player in the game, managing their hand, shields, and actions.

### Frontend

The project offers two frontends for diverse user preferences:

* **Web GUI:** Implemented using HTML, CSS, and JavaScript, providing an intuitive graphical interface for players to interact with the game. Key features include:
    * **Real-time Updates:** Displays game state updates, player actions, and quest progress dynamically.
    * **Card Selection:** Allows players to easily select and play cards from their hand.
    * **Quest Management:** Facilitates quest creation, stage setup, and player participation.
    * **Attack Resolution:** Handles player attacks, calculates results, and updates game state accordingly.

* **Command-Line Interface (CLI):** The `Main` class provides a text-based interface for interacting with the game, offering an alternative way to play for users who prefer a CLI environment.

## Testing

The project includes comprehensive testing using JUnit, Selenium WebDriver, and Cucumber.

* **JUnit:** Unit tests for backend classes ensure the integrity of game logic and card management.
* **Selenium WebDriver:** Integration tests automate user interactions through the web GUI frontend, verifying gameplay functionality.
* **Cucumber:**  Utilizes Cucumber to define and execute test scenarios for the game, ensuring that the game behaves as expected in various situations.

## How to Run

1. **Clone the repository:** `git clone https://github.com/your-username/quest-game.git`
2. **Build the backend:** Navigate to the backend directory and run `mvn clean install`.
3. **Run the backend:** Execute the `QuestApplication` class to start the Spring Boot server.
4. **Choose a frontend:**
    * **Web GUI:** Open the `index.html` file in your web browser.
    * **CLI:** Run the `Main` class in your terminal.

## Future Enhancements

* **Enhanced UI:** Improve the visual appeal and user experience of the web GUI frontend.
* **Advanced Game Features:** Implement additional card types, events, and game modes.
* **AI Players:** Introduce AI-controlled players for single-player mode.
* **Network Optimization:** Optimize network communication for smoother gameplay with more players.
