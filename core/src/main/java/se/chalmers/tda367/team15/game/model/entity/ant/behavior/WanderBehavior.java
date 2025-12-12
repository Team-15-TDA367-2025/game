package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.sql.Time;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.GameWorld;
import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class WanderBehavior extends AntBehavior implements TimeObserver {
    private GameWorld gameWorld;

    public WanderBehavior(Ant ant, GameWorld world) {
        super(ant);
        this.gameWorld = world;
        gameWorld.addTimeObserver(this);
    }

    private void changeTrajectory() {
        float angle = ant.getRotation(); // in radians
        double sigma = Math.toRadians(10);    // deviation in degrees
        double maxTurn = Math.toRadians(20);  // maximum allowed turn in degrees

        double randomTurn = MathUtils.random.nextGaussian() * sigma;
        randomTurn = MathUtils.clamp(randomTurn, -maxTurn, maxTurn);

        float homeTurn = getHomeTurn(angle);

        angle += (float) randomTurn + homeTurn;

        Vector2 newVelocity = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle)).nor().scl(ant.getSpeed());

        ant.setVelocity(newVelocity);
    }

    private float getHomeTurn(float angle) {
        Vector2 colonyPos = gameWorld.getColony().getPosition();
        Vector2 antPos = ant.getPosition();

        // Direction from ant toward colony
        float desiredAngle = MathUtils.atan2(
            colonyPos.y - antPos.y,
            colonyPos.x - antPos.x
        );

        float angleDiff = MathUtils.atan2(
            MathUtils.sin(desiredAngle - angle),
            MathUtils.cos(desiredAngle - angle)
        );

        // Weight: how strong the home attraction is
        float homeBias = 0.05f;

        float homeTurn = angleDiff * homeBias;
        return homeTurn;
    }

    @Override
    public void onTimeUpdate() {
        changeTrajectory();
    }

    @Override
    public void update(PheromoneSystem system, float deltaTime) {
        if (enemiesInSight()) {
            ant.setBehavior(new AttackBehavior(ant, ant.getPosition(), gameWorld));
            return;
        }

        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);

        if (!neighbors.isEmpty()) {
            ant.setBehavior(new FollowTrailBehavior(ant, gameWorld));
        }

    }

    @Override
    public void handleCollision() {
        ant.pickRandomDirection();
    }
}
