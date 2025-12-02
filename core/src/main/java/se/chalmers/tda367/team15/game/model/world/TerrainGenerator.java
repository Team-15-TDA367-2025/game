package se.chalmers.tda367.team15.game.model.world;

public interface TerrainGenerator {
    Tile[][] generate(int width, int height);
}
