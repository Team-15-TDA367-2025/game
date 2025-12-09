package se.chalmers.tda367.team15.game.model.world;

/**
 * Configuration for terrain generation.
 * Uses the Builder pattern to allow easy configuration of various parameters.
 */
public class TerrainGenerationConfig {
    // Noise parameters
    private final double scale;
    private final int octaves;
    private final double persistence;
    private final double lacunarity;
    private final double redistribution;

    // Lake parameters
    private final int lakeCount;
    private final int lakeMinSteps;
    private final int lakeMaxSteps;
    private final int lakeSmoothingPasses;

    // Nucleation parameters
    private final int nucleationCount;
    private final int nucleationRadius;
    private final int nucleationMinDistance;
    private final int colonyNucleationRadius;

    // Border parameters
    private final int sandBorderWidth;

    private TerrainGenerationConfig(Builder builder) {
        this.scale = builder.scale;
        this.octaves = builder.octaves;
        this.persistence = builder.persistence;
        this.lacunarity = builder.lacunarity;
        this.redistribution = builder.redistribution;
        
        this.lakeCount = builder.lakeCount;
        this.lakeMinSteps = builder.lakeMinSteps;
        this.lakeMaxSteps = builder.lakeMaxSteps;
        this.lakeSmoothingPasses = builder.lakeSmoothingPasses;
        
        this.nucleationCount = builder.nucleationCount;
        this.nucleationRadius = builder.nucleationRadius;
        this.nucleationMinDistance = builder.nucleationMinDistance;
        this.colonyNucleationRadius = builder.colonyNucleationRadius;
        
        this.sandBorderWidth = builder.sandBorderWidth;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public double getScale() { return scale; }
    public int getOctaves() { return octaves; }
    public double getPersistence() { return persistence; }
    public double getLacunarity() { return lacunarity; }
    public double getRedistribution() { return redistribution; }

    public int getLakeCount() { return lakeCount; }
    public int getLakeMinSteps() { return lakeMinSteps; }
    public int getLakeMaxSteps() { return lakeMaxSteps; }
    public int getLakeSmoothingPasses() { return lakeSmoothingPasses; }

    public int getNucleationCount() { return nucleationCount; }
    public int getNucleationRadius() { return nucleationRadius; }
    public int getNucleationMinDistance() { return nucleationMinDistance; }
    public int getColonyNucleationRadius() { return colonyNucleationRadius; }

    public int getSandBorderWidth() { return sandBorderWidth; }

    public static class Builder {
        // Defaults
        double scale = 0.07;
        int octaves = 4;
        double persistence = 0.4;
        double lacunarity = 2.0;
        double redistribution = 1.2;

        int lakeCount = 15;
        int lakeMinSteps = 80;
        int lakeMaxSteps = 100;
        int lakeSmoothingPasses = 3;

        int nucleationCount = 100;
        int nucleationRadius = 2;
        int nucleationMinDistance = 20;
        int colonyNucleationRadius = 10;

        int sandBorderWidth = 1;

        public Builder scale(double scale) { this.scale = scale; return this; }
        public Builder octaves(int octaves) { this.octaves = octaves; return this; }
        public Builder persistence(double persistence) { this.persistence = persistence; return this; }
        public Builder lacunarity(double lacunarity) { this.lacunarity = lacunarity; return this; }
        public Builder redistribution(double redistribution) { this.redistribution = redistribution; return this; }

        public Builder lakeCount(int count) { this.lakeCount = count; return this; }
        public Builder lakeMinSteps(int steps) { this.lakeMinSteps = steps; return this; }
        public Builder lakeMaxSteps(int steps) { this.lakeMaxSteps = steps; return this; }
        public Builder lakeSmoothingPasses(int passes) { this.lakeSmoothingPasses = passes; return this; }

        public Builder nucleationCount(int count) { this.nucleationCount = count; return this; }
        public Builder nucleationRadius(int radius) { this.nucleationRadius = radius; return this; }
        public Builder nucleationMinDistance(int distance) { this.nucleationMinDistance = distance; return this; }
        public Builder colonyNucleationRadius(int radius) { this.colonyNucleationRadius = radius; return this; }

        public Builder sandBorderWidth(int width) { this.sandBorderWidth = width; return this; }

        public TerrainGenerationConfig build() {
            return new TerrainGenerationConfig(this);
        }
    }
}

