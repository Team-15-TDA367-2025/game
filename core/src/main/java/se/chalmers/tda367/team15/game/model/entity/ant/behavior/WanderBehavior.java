package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class WanderBehavior extends AntBehavior {

    public WanderBehavior(Ant ant) {
        super(ant);
    }

    @Override
    public void update(PheromoneSystem system, float deltaTime) {

        if (enemiesInSight()) {
            ant.setBehavior(new AttackBehavior(ant, ant.getPosition()));
            return;
        }

        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);

        if (!neighbors.isEmpty()) {
            ant.setBehavior(new FollowTrailBehavior(ant));
        }

    }

    @Override
    public void handleCollision() {
        ant.pickRandomDirection();
    }
}
