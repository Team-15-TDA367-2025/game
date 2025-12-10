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
    
    private final int nucleationCount;
    private final int nucleationRadius;
    private final int nucleationMinDistance;
    private final int colonyNucleationRadius;
    private final int sandBorderWidth;

    public ResourcePlacementFeature(int nucleationCount, int nucleationRadius, int nucleationMinDistance, int colonyNucleationRadius, int sandBorderWidth) {
        this.nucleationCount = nucleationCount;
        this.nucleationRadius = nucleationRadius;
        this.nucleationMinDistance = nucleationMinDistance;
        this.colonyNucleationRadius = colonyNucleationRadius;
        this.sandBorderWidth = sandBorderWidth;
    }

    @Override
    public void apply(TerrainGenerationContext context) {
        List<GridPoint2> points = new ArrayList<>();
        Random nucRandom = new Random(context.getSeed() + 2000);
        int width = context.getWidth();
        int height = context.getHeight();

        // 1. Colony Nucleation (Center area)
        GridPoint2 colonyPoint = findValidPointNear(width/2, height/2, colonyNucleationRadius, context, nucRandom);
        if (colonyPoint != null) points.add(colonyPoint);
        else points.add(new GridPoint2(width/2, height/2)); // Fallback

        // 2. Random Nucleation
        int attempts = 0;
        while (points.size() < nucleationCount && attempts < nucleationCount * 50) {
            attempts++;
            int x = nucRandom.nextInt(width);
            int y = nucRandom.nextInt(height);

            if (!isValidNucleationPoint(x, y, context)) continue;
            if (isTooClose(x, y, points, nucleationMinDistance)) continue;

            points.add(new GridPoint2(x, y));
        }

        // 3. Register Spawns and Apply Zone Textures
        for (GridPoint2 point : points) {
            context.addStructureSpawn(new StructureSpawn(point, "resource_node"));
            applyNucleationZone(context, point);
        }
    }

    private GridPoint2 findValidPointNear(int cx, int cy, int radius, TerrainGenerationContext context, Random rng) {
        for (int i = 0; i < 50; i++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double dist = rng.nextDouble() * radius;
            int x = cx + (int)(Math.cos(angle) * dist);
            int y = cy + (int)(Math.sin(angle) * dist);
            if (isValidNucleationPoint(x, y, context)) return new GridPoint2(x, y);
        }
        return null;
    }

    private boolean isTooClose(int x, int y, List<GridPoint2> points, int minDst) {
        long minDst2 = minDst * minDst;
        for (GridPoint2 p : points) {
            if (p.dst2(new GridPoint2(x, y)) < minDst2) return true;
        }
        return false;
    }

    private void applyNucleationZone(TerrainGenerationContext context, GridPoint2 point) {
        int radius = nucleationRadius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx*dx + dy*dy <= radius*radius) {
                    int nx = point.x + dx;
                    int ny = point.y + dy;
                    if (!context.isWater(nx, ny)) {
                        context.setTile(nx, ny, new Tile("grass1", TileType.GRASS));
                    }
                }
            }
        }
    }

    private boolean isValidNucleationPoint(int x, int y, TerrainGenerationContext context) {
        int checkRadius = nucleationRadius + sandBorderWidth + 2;
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
