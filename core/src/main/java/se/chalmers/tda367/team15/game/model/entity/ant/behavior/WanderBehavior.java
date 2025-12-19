package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.providers.PheromoneUsageProvider;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;

/**
 * This behaviour is used when ants are moving on their own, searching for a
 * pheromone trail.
 */
public class WanderBehavior extends AntBehavior {
    private static final int TRAIL_REENTRY_COOLDOWN = 30; // frames to wait before re-entering a trail

    private final Home home;
    private int accumulator = 0;
    private int trailCooldown = 0;

    public WanderBehavior(Ant ant, Home home, EntityQuery entityQuery) {
        this(ant, home, entityQuery, false);
    }

    /**
     * Creates a wander behavior.
     * 
     * @param ant              The ant
     * @param home             The home colony
     * @param entityQuery      Entity query interface
     * @param leftTrailOnStart If true, applies a cooldown before re-entering any
     *                         trail
     */
    public WanderBehavior(Ant ant, Home home, EntityQuery entityQuery, boolean leftTrailOnStart) {
        super(ant, entityQuery);
        this.home = home;
        this.trailCooldown = leftTrailOnStart ? TRAIL_REENTRY_COOLDOWN : 0;
    }

    private void changeTrajectory() {
        // Use velocity angle directly (radians) - this is the actual movement direction
        float angle = ant.getVelocity().angleRad();

        double sigma = Math.toRadians(10); // deviation in degrees
        double maxTurn = Math.toRadians(20); // maximum allowed turn in degrees

        double randomTurn = MathUtils.random.nextGaussian() * sigma;
        randomTurn = MathUtils.clamp((float) randomTurn, (float) -maxTurn, (float) maxTurn);

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
                colonyPos.x - antPos.x);

        float angleDiff = MathUtils.atan2(
                MathUtils.sin(desiredAngle - angle),
                MathUtils.cos(desiredAngle - angle));

        // Weight: how strong the home attraction is (from AntType config)
        float homeBias = ant.getType().homeBias();

        float homeTurn = angleDiff * homeBias;
        return homeTurn;
    }

    @Override
    public void update(PheromoneUsageProvider system) {

        if (enemiesInSight()) {
            ant.setAttackBehaviour();
            return;
        }

        accumulator += 1;
        if (accumulator >= 20) {
            changeTrajectory();
            accumulator = 0;
        }

        // Decrement trail cooldown
        if (trailCooldown > 0) {
            trailCooldown--;
            return; // Don't check for pheromones while cooling down
        }

        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos, ant.getType().allowedPheromones()).stream()
                .toList();

        if (!neighbors.isEmpty()) {
            ant.setFollowTrailBehaviour();
        }
    }

    @Override
    public void handleCollision() {
        ant.pickRandomDirection();
    }
}
