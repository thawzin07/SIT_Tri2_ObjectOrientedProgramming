# SIT Tri2 Object-Oriented Programming - Team Project

**Developed by:**

- Thaw Zin Htun
- Ee Chew Fong Olivia
- Gan Wei Yang
- Gerome Wong Jun Hong
- Mohamad Danish Bin Mohammad
- Sreekantom Sai Saketh

## Project Overview
This project applies object-oriented programming principles in Java with libGDX to build a reusable engine (Part 1) and a game implementation (Part 2).

**Part 1 (Week 1-7):** Abstract Engine - generic, reusable foundation  
**Part 2 (Week 8-12):** Game implementation - gameplay, UI flows, persistence, and polish built on top of the engine

## Current Architecture
The codebase is organized into layered packages:

- `app` - application orchestration and runtime flow (`Main`, controllers, runtime, app UI renderer, flow orchestrators)
- `engine` - reusable abstract systems (input/output manager, scene/entity/movement/collision managers, handlers, interfaces)
- `game` - game-specific domain logic (food domain, collidables, UI screens, leaderboard and persistence logic)

### Flow Orchestrators
Recent refactoring decomposed large app flow into dedicated units:

- `StateFlowOrchestrator`
- `GameplayLoopOrchestrator`
- `AvatarFlowOrchestrator`
- `LeaderboardFlowOrchestrator`

### Design Patterns Used
- **Observer / Event Bus:** `IOEvent`, `IOListener`, `InputOutputManager`
- **Factory:** `FoodFactory`
- **Strategy / Polymorphism:** `MovementComponent`, `PlayerMovement`, `AIMovement`
- **Orchestrator / Facade-style flow modules:** `app.flow.*`

## Key Features
- Start menu, settings, how-to-play, avatar setup, gameplay, leaderboard flows
- Difficulty presets and configurable gameplay parameters
- Food spawning coordination and collision-based collection
- Leaderboard persistence abstraction (`LeaderboardStore`)
- Audio system with MP3 support:
  - button click SFX (`btn_click`)
  - collision SFX (`collisionmusic`)
  - state-based BGM (`foodmenumusic`, `settingmusic`, `howtoplaymusic`, `playersetupmusic`, `leaderboardmusic`)

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

- **Language:** Java 17
- **Framework:** libGDX 1.14.0
- **Build Tool:** Gradle
- **Version Control:** Git/GitHub
- **IDE:** Eclipse

## OOP Principles Applied

- **Encapsulation** - Data hiding and access control
- **Inheritance** - Deriving new classes from existing ones
- **Polymorphism** - Method overriding and interface implementation
- **Abstraction** - Generic engine components separate from specific logic
- **SOLID Principles** - Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion

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
- Windows:
  - `.\gradlew.bat test`
- macOS/Linux:
  - `./gradlew test`

### If output looks too quiet
This project sets Gradle logging to quiet (`org.gradle.logging.level=quiet`), so successful runs may show minimal logs.

Use:
- `.\gradlew.bat test --info`
- `.\gradlew.bat test --console=plain`
- `.\gradlew.bat cleanTest test --rerun-tasks --info`

## Setup Instructions

1. Build from terminal:
   - Windows: `.\gradlew.bat build`
   - macOS/Linux: `./gradlew build`

2. Run desktop launcher from terminal:
   - Windows: `.\gradlew.bat :lwjgl3:run`
   - macOS/Linux: `./gradlew :lwjgl3:run`

3. Or run from IDE:
   - `lwjgl3/src/main/java/com/sit/inf1009/project/lwjgl3/Lwjgl3Launcher.java`

### Eclipse Import (optional detailed steps)
1. Clone the repository in Eclipse:
   - File -> Import -> Git -> Projects from Git -> Clone URI
   - Enter repository URL: `https://github.com/thawzin07/SIT_Tri2_ObjectOrientedProgramming.git`
   - Enter your GitHub credentials (Use your Personal Access Token as password)
   - Select the `main` branch
   - Import project files into an empty folder
   - Choose "Import as general project"

2. Refresh/build:
   - Right-click project -> Gradle -> Refresh Gradle Project

3. Run:
   - Navigate to `lwjgl3` module and run `Lwjgl3Launcher.java`

## GitHub Personal Access Token Setup

To push/pull code, you need a GitHub personal access token:

1. Go to GitHub.com -> Settings -> Developer settings
2. Click **Personal access tokens** -> **Tokens (classic)**
3. Click **Generate new token (classic)**
4. Give it a name (e.g., "Eclipse Git Access")
5. Select scope: **repo** (full control of private repositories)
6. Click **Generate token**
7. **Copy the token immediately** (you won't see it again)
8. Use this token as your password in Eclipse when pushing/pulling

## Development Workflow

### Initial Setup (One-time)
1. Clone the repository in Eclipse (see Setup Instructions)
2. Create your personal branch:
   - Right-click project -> **Team** -> **Switch To** -> **New Branch**
   - Name it: `yourname-dev` (e.g., `john-dev`, `mary-dev`)
   - Check **Checkout new branch** and **Configure Upstream for push and pull**
   - Click **Finish**

### Daily Workflow

#### Step 1: Pull Latest Changes from Main
Before starting work each day:
1. Right-click project -> **Team** -> **Switch To** -> **main**
2. Right-click project -> **Team** -> **Pull**
3. Switch back to your branch: **Team** -> **Switch To** -> **yourname-dev**
4. Merge main into your branch: **Team** -> **Merge** -> Select **main**

#### Step 2: Make Your Changes
1. Create/modify files in the appropriate package
2. Test your changes locally
3. Ensure code follows coding conventions

#### Step 3: Commit to Your Branch
1. Right-click project -> **Team** -> **Add to Index** (stages your changes)
2. Right-click project -> **Team** -> **Commit**
3. Write a descriptive commit message (e.g., "Add collision detection to EntityManager")
4. Click **Commit**

#### Step 4: Push to Your Branch
1. Right-click project -> **Team** -> **Push Branch 'yourname-dev'**
2. Enter GitHub credentials when prompted:
   - **Username:** Your GitHub username
   - **Password:** Your GitHub personal access token
3. Click **Finish**

#### Step 5: Create Pull Request (When Feature is Complete)
1. Go to GitHub repository: `https://github.com/thawzin07/SIT_Tri2_ObjectOrientedProgramming`
2. Click **Pull requests** tab
3. Click **New pull request**
4. Set:
   - **Base:** `main`
   - **Compare:** `yourname-dev`
5. Click **Create pull request**
6. Add title and description:
   - Title: "Add [Feature Name]"
   - Description: Explain what you added/changed and why
7. Click **Create pull request**
8. Notify team members to review

#### Step 6: Code Review Process
- **For Pull Request Creator:**
  - Wait for at least 1-2 team members to review
  - Address any feedback or requested changes
  - Make additional commits if needed

- **For Reviewers:**
  - Review the code changes on GitHub
  - Add comments or suggestions
  - Click **Approve** if code looks good
  - Click **Request changes** if issues are found

#### Step 7: Merge Pull Request
Once approved by team members:
1. Click **Merge pull request** on GitHub
2. Click **Confirm merge**
3. Click **Delete branch** (optional, keeps repo clean)
4. In Eclipse, switch to `main` and pull latest changes

### Branch Naming Convention
- **main** - Main production branch (only merge after approval)
- **yourname-dev** - Personal development branch (e.g., `john-dev`, `mary-dev`)

### Important Rules
- NEVER push directly to main
- Always work on your personal branch
- Pull from main regularly to avoid conflicts
- Test thoroughly before creating pull request
- Get approval before merging to main

## Team Collaboration

### Git Workflow Best Practices
- Each member works on their own branch (`yourname-dev`)
- Pull from main daily to stay updated
- Create pull requests for all changes to main
- Require at least 1-2 approvals before merging
- Use descriptive commit messages

### Code Standards
- Follow established coding conventions
- Document classes and methods where necessary
- Test code before pushing
- Communicate breaking changes to the team
- Review teammates' pull requests promptly

## Contributing

All team members should:
1. Pull latest changes before starting work
2. Work on assigned components
3. Test thoroughly before committing
4. Push regularly to keep repository updated
5. Communicate blockers or dependencies with team

## License

This project is developed as part of INF1009 Object-Oriented Programming course at Singapore Institute of Technology.

---

**Course:** INF1009 Object-Oriented Programming  
**Institution:** Singapore Institute of Technology  
**Academic Year:** 2025/2026
