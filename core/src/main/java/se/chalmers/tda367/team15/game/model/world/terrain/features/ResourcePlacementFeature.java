package se.chalmers.tda367.team15.game.model.world.terrain.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.world.Tile;
import se.chalmers.tda367.team15.game.model.world.TileType;
import se.chalmers.tda367.team15.game.model.world.terrain.StructureSpawn;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainFeature;
import se.chalmers.tda367.team15.game.model.world.terrain.TerrainGenerationContext;

/**
 * Places resource nucleation points and ensures they are on valid ground.
 */
public class ResourcePlacementFeature implements TerrainFeature {
    
    public record Config(
        int nucleationCount,
        int nucleationRadius,
        int nucleationMinDistance,
        int colonyNucleationRadius,
        int sandBorderWidth // Needed for validation check
    ) {}

    private final Config config;

    public ResourcePlacementFeature(Config config) {
        this.config = config;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        List<GridPoint2> points = new ArrayList<>();
        Random nucRandom = new Random(context.getSeed() + 2000);
        int width = context.getWidth();
        int height = context.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        // 1. Colony Nucleation
        GridPoint2 colonyNucleation = generateColonyNucleationPoint(
                centerX, centerY, context, nucRandom);
        if (colonyNucleation != null) {
            points.add(colonyNucleation);
        }

        // 2. Random Nucleation
        int margin = config.nucleationMinDistance();
        int attempts = 0;
        int maxAttempts = config.nucleationCount() * 100;

        while (points.size() < config.nucleationCount() && attempts < maxAttempts) {
            attempts++;
            int x = margin + nucRandom.nextInt(width - 2 * margin);
            int y = margin + nucRandom.nextInt(height - 2 * margin);

            if (!isValidNucleationPoint(x, y, context)) continue;

            boolean tooClose = false;
            for (GridPoint2 existing : points) {
                if (existing.dst2(new GridPoint2(x, y)) < config.nucleationMinDistance() * config.nucleationMinDistance()) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                points.add(new GridPoint2(x, y));
            }
        }

        // 3. Register Spawns and Apply Zone Textures
        for (GridPoint2 point : points) {
            // Add structure spawn
            context.addStructureSpawn(new StructureSpawn(point, "resource_node"));

            // Apply grass zone around nucleation
            applyNucleationZone(context, point, nucRandom);
        }
    }

    private void applyNucleationZone(TerrainGenerationContext context, GridPoint2 point, Random random) {
        int smoothRadius = config.nucleationRadius() + 3;

        for (int dx = -smoothRadius; dx <= smoothRadius; dx++) {
            for (int dy = -smoothRadius; dy <= smoothRadius; dy++) {
                int nx = point.x + dx;
                int ny = point.y + dy;

                if (!context.isInBounds(nx, ny)) continue;
                if (context.isWater(nx, ny)) continue;

                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist <= config.nucleationRadius()) {
                    setGrass1(context, nx, ny);
                } else if (dist <= smoothRadius) {
                    double blendFactor = (dist - config.nucleationRadius()) / (smoothRadius - config.nucleationRadius());
                    if (random.nextDouble() > blendFactor * blendFactor) {
                        setGrass1(context, nx, ny);
                    }
                }
            }
        }
    }

    private void setGrass1(TerrainGenerationContext context, int x, int y) {
        // Force tile to grass1
        context.setTile(x, y, new Tile("grass1", TileType.GRASS));
    }

    private GridPoint2 generateColonyNucleationPoint(int centerX, int centerY, 
            TerrainGenerationContext context, Random rng) {
        
        for (int attempt = 0; attempt < 100; attempt++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double distance = 3 + rng.nextDouble() * (config.colonyNucleationRadius() - 3);
            
            int x = centerX + (int) Math.round(Math.cos(angle) * distance);
            int y = centerY + (int) Math.round(Math.sin(angle) * distance);

            if (context.isInBounds(x, y)) {
                if (isValidNucleationPoint(x, y, context)) {
                    return new GridPoint2(x, y);
                }
            }
        }
        return new GridPoint2(centerX + 5, centerY + 5);
    }

    private boolean isValidNucleationPoint(int x, int y, TerrainGenerationContext context) {
        int checkRadius = config.nucleationRadius() + config.sandBorderWidth() + 2;
        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dy = -checkRadius; dy <= checkRadius; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (context.isInBounds(nx, ny)) {
                    if (context.isWater(nx, ny)) return false;
                }
            }
        }
        return true;
    }
}
