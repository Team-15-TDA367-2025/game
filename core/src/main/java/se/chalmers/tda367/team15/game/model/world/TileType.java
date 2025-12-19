package se.chalmers.tda367.team15.game.model.world;

public enum TileType {
    GRASS(true, false), // Walkable ground
    SAND(true, false), // Walkable beach/shore
    WATER(false, true); // Swimmable water

    private final boolean walkable;
    private final boolean swimmable;

    TileType(boolean walkable, boolean swimmable) {
        this.walkable = walkable;
        this.swimmable = swimmable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isSwimmable() {
        return swimmable;
    }
}
