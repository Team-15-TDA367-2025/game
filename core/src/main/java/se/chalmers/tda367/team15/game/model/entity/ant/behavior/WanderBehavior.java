package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public class WanderBehavior extends AntBehavior {

    public WanderBehavior(Ant ant) {
        super(ant);
    }

    @Override
    public void update(PheromoneSystem system, float deltaTime) {
        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);

        if (!neighbors.isEmpty()) {
            ant.setBehavior(new FollowTrailBehavior(ant));
        }
    }
}
