package se.chalmers.tda367.team15.game.model.world;

public class Tile {
    private final int variant;
    private final TileType type;

    public Tile(int variant, TileType type) {
        this.variant = variant;
        this.type = type;
    }

    public int getVariant() {
        return variant;
    }

    public TileType getType() {
        return type;
    }
}
