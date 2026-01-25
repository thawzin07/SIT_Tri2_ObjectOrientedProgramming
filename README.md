# SIT Tri2 Object-Oriented Programming - Team Project

Developed by 
Thaw Zin Htun
Ee Chew Fong, Olivia
Gan Wei Yang
Team member 4 
Team member 5 
Team member 6 

## Project Overview
This project develops a simulation environment using object-oriented programming principles in Java with libGDX framework. The project is divided into two main components:

**Part 1 (Week 1-7):** Abstract Engine - Generic, reusable simulation foundation  
**Part 2 (Week 8-12):** Logic Engine - Simulation-specific implementation

## Abstract Engine Components

The Abstract Engine provides core functionality for any simulation without containing game-specific logic:

- **Scene Management** - Load, unload, and transition between scenes
- **Entity Management** - Create, manage, and update all entities
- **Collision Management** - Detect and resolve collisions between entities
- **Movement Management** - Control movement of non-playing entities
- **Input/Output Management** - Handle user input and display output

## Project Structure

```
OOP_projectLibGDX-parent/
├── assets/                      # Game resources (images, sounds, fonts)
├── gradle/                      # Build automation files
├── OOP_projectLibGDX-core/      # Core logic
│   └── src/main/java/com/sit/inf1009/project/
│       ├── Main.java
│       ├── engine/              # Abstract Engine (Week 1-7)
│       │   ├── managers/
│       │   │   ├── InputOutputManager.java
│       │   │   ├── EntityManager.java
│       │   │   ├── CollisionManager.java
│       │   │   ├── MovementManager.java
│       │   │   └── SceneManager.java
│       │   ├── entities/
│       │   │   ├── Entity.java
│       │   │   ├── Collidable.java
│       │   │   └── NonCollidable.java
│       │   └── core/
│       │       └── AbstractEngine.java
│       └── logic/               # Logic Engine (Week 8-12)
└── OOP_projectLibGDX-lwjgl3/    # Desktop launcher
```

## Technologies Used

- **Language:** Java (JavaSE-1.8)
- **Framework:** libGDX 1.14.0
- **Build Tool:** Gradle
- **Version Control:** Git/GitHub
- **IDE:** Eclipse

## Coding Conventions

1. **Naming:** camelCase for methods and variables
2. **Strings:** Use double quotation marks
3. **Documentation:** 
   - Class-level comments explaining purpose and functionality
   - Method-level comments explaining what each function does
4. **Data Types:** Use appropriate types (int, double, float) as specified

## OOP Principles Applied

- **Encapsulation** - Data hiding and access control
- **Inheritance** - Deriving new classes from existing ones
- **Polymorphism** - Method overriding and interface implementation
- **Abstraction** - Generic engine components separate from specific logic
- **SOLID Principles** - Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion

## Setup Instructions

1. Clone the repository in Eclipse:
   - File → Import → Git → Projects from Git → Clone URI
   - Enter repository URL: `https://github.com/thawzin07/SIT_Tri2_ObjectOrientedProgramming.git`
   - Enter your GitHub credentials
   - Select the `main` branch
   - Choose import as "Existing Gradle Project"

2. Build the project:
   - Right-click on project → Gradle → Refresh Gradle Project

3. Run the application:
   - Navigate to `OOP_projectLibGDX-lwjgl3` module
   - Run the launcher class

## GitHub Personal Access Token Setup

To push/pull code, you need a GitHub personal access token:

1. Go to GitHub.com → Settings → Developer settings
2. Click **Personal access tokens** → **Tokens (classic)**
3. Click **Generate new token (classic)**
4. Give it a name (e.g., "Eclipse Git Access")
5. Select scope: **repo** (full control of private repositories)
6. Click **Generate token**
7. **Copy the token immediately** (you won't see it again!)
8. Use this token as your password in Eclipse when pushing/pulling


## Development Workflow

### Initial Setup (One-time)
1. Clone the repository in Eclipse (see Setup Instructions)
2. Create your personal branch:
   - Right-click on project → **Team** → **Switch To** → **New Branch**
   - Name it: `yourname-dev` (e.g., `john-dev`, `mary-dev`)
   - Check **"Checkout new branch"**
   - Click **Finish**

### Daily Workflow

#### Step 1: Pull Latest Changes from Main
Before starting work each day:
1. Right-click on project → **Team** → **Switch To** → **main**
2. Right-click on project → **Team** → **Pull**
3. Switch back to your branch: **Team** → **Switch To** → **yourname-dev**
4. Merge main into your branch: **Team** → **Merge** → Select **main**

#### Step 2: Make Your Changes
1. Create/modify files in the appropriate package
2. Test your changes locally
3. Ensure code follows coding conventions

#### Step 3: Commit to Your Branch
1. Right-click on project → **Team** → **Add to Index** (stages your changes)
2. Right-click on project → **Team** → **Commit**
3. Write a descriptive commit message (e.g., "Add collision detection to EntityManager")
4. Click **Commit**

#### Step 4: Push to Your Branch
1. Right-click on project → **Team** → **Push Branch 'yourname-dev'**
2. Enter GitHub credentials when prompted:
   - **Username:** Your GitHub username
   - **Password:** Your GitHub personal access token
3. Click **Finish**

#### Step 5: Create Pull Request (When Feature is Complete)
1. Go to GitHub repository: `https://github.com/thawzin07/SIT_Tri2_ObjectOrientedProgramming`
2. Click **"Pull requests"** tab
3. Click **"New pull request"**
4. Set:
   - **Base:** `main`
   - **Compare:** `yourname-dev`
5. Click **"Create pull request"**
6. Add title and description:
   - Title: "Add [Feature Name]"
   - Description: Explain what you added/changed and why
7. Click **"Create pull request"**
8. Notify team members to review

#### Step 6: Code Review Process
- **For Pull Request Creator:**
  - Wait for at least 1-2 team members to review
  - Address any feedback or requested changes
  - Make additional commits if needed
  
- **For Reviewers:**
  - Review the code changes on GitHub
  - Add comments or suggestions
  - Click **"Approve"** if code looks good
  - Click **"Request changes"** if issues found

#### Step 7: Merge Pull Request
Once approved by team members:
1. Click **"Merge pull request"** on GitHub
2. Click **"Confirm merge"**
3. Click **"Delete branch"** (optional, keeps repo clean)
4. In Eclipse, switch to main and pull the latest changes

### Branch Naming Convention
- **main** - Main production branch (only merge after approval)
- **yourname-dev** - Personal development branch (e.g., `john-dev`, `mary-dev`)

### Important Rules
- ⚠️ **NEVER push directly to main**
- ⚠️ Always work on your personal branch
- ⚠️ Pull from main regularly to avoid conflicts
- ⚠️ Test thoroughly before creating pull request
- ⚠️ Get approval before merging to main

## Team Collaboration

### Git Workflow Best Practices
- Each member works on their own branch (`yourname-dev`)
- Pull from main daily to stay updated
- Create pull requests for all changes to main
- Require at least 1-2 approvals before merging (standard practice but for this course work , please discuss with the team) 
- Use descriptive commit messages

### Code Standards
- Follow the established coding conventions
- Document all classes and methods
- Test your code before pushing
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
