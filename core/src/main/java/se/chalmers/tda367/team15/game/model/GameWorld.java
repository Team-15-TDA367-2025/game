package se.chalmers.tda367.team15.game.model;

import java.util.List;
import java.util.ArrayList;
import se.chalmers.tda367.team15.game.model.entities.*;

public class GameWorld {
    private String type;
    private List<Entity> entities;
    private Map map;
    private TimeCycle timeCycle;

    public GameWorld(String type, Map map, TimeCycle timeCycle) {
        this.type = type;
        this.entities = new ArrayList<>();
        this.map = map;
        this.timeCycle = timeCycle;
    }
}