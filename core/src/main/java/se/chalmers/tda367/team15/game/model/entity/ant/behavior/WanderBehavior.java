package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;

/**
 * This behaviour is used when ants are moving on their own, searching for a pheromone trail.
 */
public class WanderBehavior extends AntBehavior implements GeneralizedBehaviour{
    private final Home home;
    private int accumulator = 0;

    // TODO: we should not need to pass along everything to all behaviors
    public WanderBehavior(Ant ant, Home home, EntityQuery entityQuery) {
        super(ant, entityQuery);
        this.home = home;
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
        Vector2 colonyPos = home.getPosition();
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
    public void update(PheromoneManager system) {

        if (enemiesInSight()) {
           ant.setAttackBehaviour();
           return;
        }

        accumulator += 1;
        if (accumulator >= 20) {
            changeTrajectory();
            accumulator = 0;
        }

        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);

        if (!neighbors.isEmpty()) {
            ant.setFollowTrailBehaviour();
        }
    }

    @Override
    public void handleCollision() {
        ant.pickRandomDirection();
    }
}
