package se.chalmers.tda367.team15.game.model;

import java.util.List;
import java.util.ArrayList;
import se.chalmers.tda367.team15.game.model.entities.*;

public class GameWorld {
    private String type;
    private int quantity;
    private List<Entity> entities;
    private Map map;
    private TimeCycle timeCycle;

    public GameWorld(String type, Map map, TimeCycle timeCycle) {
        this.type = type;
        this.quantity = 0;
        this.entities = new ArrayList<>();
        this.map = map;
        this.timeCycle = new TimeCycle();
    }
}