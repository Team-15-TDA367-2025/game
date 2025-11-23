package se.chalmers.tda367.team15.game.model.objects;

public class Object {
    protected int x;
    protected int y;

    public Object(int x, int y) {
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
