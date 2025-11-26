package se.chalmers.tda367.team15.game.model.objects;

public abstract class WorldObject {
    // GridPoint2
    protected int x;
    protected int y;

    public WorldObject(int x, int y) {
        this.x = x;
        this.x = y;
    }

    public int getPositionX() {
        return x;
    }

    public int getPositionY() {
        return y;
    }

    public void setPositionX(int x) {
        this.x = x;
    }

    public void setPositionY(int y) {
        this.y = y;
    }

}
