# Balanced Bites (INF1009 OOP Team Project)

## Team
- Thaw Zin Htun
- Ee Chew Fong Olivia
- Gan Wei Yang
- Gerome Wong Jun Hong
- Mohamad Danish Bin Mohammad
- Sreekantom Sai Saketh

## Project Overview
Balanced Bites is a real-time educational nutrition game built with Java + libGDX on top of a reusable OOP engine.

Goal:
- Help children and families learn balanced eating through active play.
- Turn static nutrition learning into interactive decision-making.

Target audience:
- Children aged 3-13 and families.

## Gameplay Summary
Players control an avatar and collect moving food-group objects to form a healthy plate.

Core loop:
1. Move and collect food objects.
2. Build a plate using Vegetables, Protein, Carbohydrates, and Oil.
3. Submit the plate for scoring and time bonus/penalty.
4. Repeat until timer ends.

Included game flows:
- Main Menu
- Settings (difficulty + music volume)
- Tutorial / How To Play
- Avatar Setup
- Gameplay
- Leaderboard Entry / Leaderboard View
- Credits

## Architecture
Layered structure:
- `app`: entry point, state flow, orchestration, app-level UI rendering
- `game`: domain logic, entities, services, persistence, game screens
- `engine`: reusable runtime infrastructure (IO, entity/scene/movement/collision managers)
- `framework`: libGDX runtime (rendering, input polling, asset loading, game loop)

Runtime style:
- Component-based modules
- Event-driven IO routing via `InputOutputManager` and `IOEvent`

## Key Features
- 3 difficulty presets (Easy / Normal / Hard)
- Fullscreen-aware UI and gameplay scaling
- HUD with food-group tracking
- Persistent leaderboard storage
- Avatar presets and image upload flow
- Music/SFX with runtime volume control
- In-game credits screen

## Design Patterns (Implemented)
- **Factory:** `FoodFactory` centralizes creation of food entities.
- **Observer / Listener:** `InputOutputManager` dispatches `IOEvent` to subscribed `IOListener`s.
- **Strategy:** `MovementComponent` enables interchangeable movement behavior (`PlayerMovement`, `AIMovement`).
- **Template Method:** `AbstractInputHandler` and `AbstractOutputHandler` define shared lifecycle with specialized subclasses.
- **Facade / Orchestrator:** `app.flow.*Orchestrator` classes coordinate complex flow steps behind simple calls.

## Project Structure (Current)
```text
SIT_Tri2_ObjectOrientedProgramming/
|-- Asgn_briefs/
|-- assets/
|-- core/
|   |-- src/
|   |   |-- main/java/com/sit/inf1009/project/
|   |   |   |-- app/
|   |   |   |   |-- controllers/
|   |   |   |   |-- flow/
|   |   |   |   |-- runtime/
|   |   |   |   `-- ui/
|   |   |   |-- engine/
|   |   |   |   |-- components/
|   |   |   |   |-- core/handlers/
|   |   |   |   |-- entities/
|   |   |   |   |-- interfaces/
|   |   |   |   `-- managers/
|   |   |   `-- game/
|   |   |       |-- components/
|   |   |       |-- domain/
|   |   |       |-- entities/
|   |   |       |-- factory/
|   |   |       |-- interfaces/
|   |   |       |-- persistence/
|   |   |       |-- services/
|   |   |       `-- ui/screens/
|   |   `-- test/java/com/sit/inf1009/project/
|   |       |-- app/flow/
|   |       `-- engine/
|-- lwjgl3/
|   `-- src/main/resources/Sounds/
`-- gradle + wrapper/build files
```

## Technologies Used
- Java 17
- libGDX 1.14.0
- Gradle
- Git/GitHub

## Testing

### Current Test Areas
- Engine manager tests:
  - `InputOutputManagerTest`
  - `IOEventTest`
  - `IOLoggerTest`
- Engine handler tests:
  - `PlayerImageInputServiceTest`
  - `DisplayOutputHandlerTest`
  - `SoundOutputHandlerTest`
- App flow tests:
  - `StateFlowOrchestratorTest`
  - `LeaderboardFlowOrchestratorTest`
  - `AvatarFlowOrchestratorTest`

### Run Tests
- Windows: `.\gradlew.bat test`
- macOS/Linux: `./gradlew test`

## Build and Run
- Windows build: `.\gradlew.bat build`
- Windows run desktop: `.\gradlew.bat :lwjgl3:run`
- macOS/Linux build: `./gradlew build`
- macOS/Linux run desktop: `./gradlew :lwjgl3:run`

## Controls (Gameplay)
- Move: `WASD` or Arrow keys
- Submit plate: `Enter`
- Reset plate: `R`
- Pause: `Esc`

## Attribution / Credits
Some music and background effects are inspired by Plants vs. Zombies (PopCap Games), used for educational and non-commercial project context.

Special thanks:
- Team members
- Professors and lab instructors

## License
Developed for INF1009 Object-Oriented Programming (Singapore Institute of Technology), AY 2025/2026.
