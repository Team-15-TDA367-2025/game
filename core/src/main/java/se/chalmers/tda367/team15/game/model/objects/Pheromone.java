package se.chalmers.tda367.team15.game.model.objects;

public class Pheromone extends Object {
    private String type;

    public Pheromone(int x, int y, String type) {
        super(x, y);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
