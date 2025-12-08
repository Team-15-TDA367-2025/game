package se.chalmers.tda367.team15.game.model.world;

public class Tile {
    private final String textureName;
    private final TileType type;

    public Tile(String textureName, TileType type) {
        this.textureName = textureName;
        this.type = type;
    }

    public String getTextureName() {
        return textureName;
    }

    public TileType getType() {
        return type;
    }
}
