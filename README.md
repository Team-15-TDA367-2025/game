# Unite The Ants

This is a project in the course "Objektorienterat programmeringsprojekt" at Chalmers University of Technology.

## Description

Unite The Ants is a strategy game where the player controls an ant colony. The player can buy eggs, place pheromones on the procedurally generated map to guide the ants to their goals.

Each night the colony will be attacked by a wave of termites.

## Technologies

- Java (recommended version 21)
- LibGDX
- Gradle

## Controls

- Left mouse button - Place pheromones
- Right mosue button - Pan camera
- Scroll wheel - Zoom camera
- WASD - Move camera
- Use the UI buttons to select pheromone type, and to buy eggs.

## To run the game

The game is run using the `./gradlew run` command in the root directory of the project.

You can also run the following commands:

- `./gradlew jar` - Creates a JAR file (needs the `assets` directory to be present when running)
- `./gradlew runWithProfiling` - Runs the game with profiling enabled. Creates a `profile-<timestamp>.jfr` file that can be viewed in for example visualvm.

> There's also a few different tasks available from the liftoff template used to create the project. You can run `./gradlew tasks` to see a list of available tasks.

### Flags

- `--no-fog` - Disables fog of war rendering (can be useful when working with terrain generation)
- `--unlimited-fps` - Disables the FPS limit, which allows you to test performance
- `--start-ants=<number>` - Number of ants to start with (default 1)
- `--ant-type=<type>` - Type of ants to start with (default worker)
- `--start-resources=<number>` - Number of resources to start with (default 20)
- `--seed=<number>` - Seed for terrain generation (default System.currentTimeMillis())
- `--map-size=<number>,<number>` - Size of the map (default 400,400)
