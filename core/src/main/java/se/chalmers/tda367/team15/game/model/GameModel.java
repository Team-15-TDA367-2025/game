package se.chalmers.tda367.team15.game.model;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.Termite.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.Drawable;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.structure.Colony;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;
import se.chalmers.tda367.team15.game.model.world.TerrainGenerator;
import se.chalmers.tda367.team15.game.model.world.WorldMap;

import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;

public class GameModel {
    private final GameWorld world;
    @SuppressWarnings("unused")
    private final WaveManager waveManager;
    private final SimulationHandler simulationHandler;

    public GameModel(TimeCycle timeCycle, SimulationHandler simulationHandler, GameWorld gameWorld) {

        this.world = gameWorld;
        this.waveManager = new WaveManager(timeCycle,this);

        this.world = new GameWorld(timeCycle, mapWidth, mapHeight, generator);
        this.waveManager = new WaveManager(world, this);

        // Spawn structures based on terrain generation features
        spawnTerrainStructures();
    }

    /**
     * Spawns structures determined by terrain generation features.
     */
    private void spawnTerrainStructures() {
        WorldMap worldMap = world.getWorldMap();
        List<StructureSpawn> spawns = worldMap.getStructureSpawns();

        for (StructureSpawn spawn : spawns) {
            if ("resource_node".equals(spawn.getType())) {
                Vector2 worldPos = worldMap.tileToWorld(spawn.getPosition());
                GridPoint2 worldGridPos = new GridPoint2((int) worldPos.x, (int) worldPos.y);

                world.addResourceNode(new ResourceNode(
                        world,
                        worldGridPos,
                        "node",
                        1,
                        ResourceType.FOOD,
                        10,
                        20));
            }
            // Add other structure types here
        }
    }

    public PheromoneGridConverter getPheromoneGridConverter() {
        return world.getPheromoneSystem().getConverter();
    }

    // --- FACADE METHODS (Actions) ---

    public void spawnTermite(Vector2 position) {
        // Temperory before we have EnemyWaveSystem
        EnemyFactory enemyFactory = new EnemyFactory(world,simulationHandler);
        Termite termite = enemyFactory.createTermite(position);
        world.addEntity(termite);
    }

    public void setTimeFast() {
        simulationHandler.setTimeFast();
    }
    public void setTimeNormal() {
        simulationHandler.setTimeNormal();
    }
    public void setTimePaused(){
        simulationHandler.setTimePaused();
    }

    public void update() {
        simulationHandler.handleSimulation();
    }

    public TimeCycle.GameTime getGameTime() {
        return simulationHandler.getTimeCycle().getGameTime();
    }

    public Iterable<Drawable> getDrawables() {
        return world.getDrawables();
    }

    public FogOfWar getFog() {
        return world.getFog();
    }

    public PheromoneSystem getPheromoneSystem() {
        return world.getPheromoneSystem();
    }

    public Colony getColony() {
        return world.getColony();
    }

    public WorldMap getWorldMap() {
        return world.getWorldMap();
    }

    public int getTotalDays() {
        return simulationHandler.getTimeCycle().getTotalDays();
    }

    public int getTotalAnts() {
        return world.getAnts().size();
    }

}
