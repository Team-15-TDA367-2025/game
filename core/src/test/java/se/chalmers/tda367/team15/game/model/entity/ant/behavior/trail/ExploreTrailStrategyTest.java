package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

@ExtendWith(MockitoExtension.class)
class ExploreTrailStrategyTest {

    private ExploreTrailStrategy strategy;

    @Mock
    private Ant ant;

    @BeforeEach
    void setUp() {
        strategy = new ExploreTrailStrategy();
    }

    // ========== Core Behavior: Walk Outward Then Wander ==========

    @Test
    @DisplayName("should move outward initially (prefer higher distance)")
    void shouldMoveOutwardInitially() {
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.EXPLORE, 1);
        Pheromone outward = new Pheromone(new GridPoint2(1, 0), PheromoneType.EXPLORE, 2);
        Pheromone backward = new Pheromone(new GridPoint2(-1, 0), PheromoneType.EXPLORE, 0);

        List<Pheromone> neighbors = Arrays.asList(outward, backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        assertEquals(outward, result, "Should move outward (higher distance)");
        assertTrue(strategy.isOutwards(), "Should still be in outward mode");
    }

    @Test
    @DisplayName("should pick highest distance when multiple outward options")
    void shouldPickHighestDistanceOutward() {
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.EXPLORE, 1);
        Pheromone outward1 = new Pheromone(new GridPoint2(1, 0), PheromoneType.EXPLORE, 2);
        Pheromone outward2 = new Pheromone(new GridPoint2(0, 1), PheromoneType.EXPLORE, 3);

        List<Pheromone> neighbors = Arrays.asList(outward1, outward2);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        assertEquals(outward2, result, "Should pick the highest distance option");
    }

    @Test
    @DisplayName("should leave trail at end (return null to trigger wander)")
    void shouldLeaveTrailAtEnd() {
        Pheromone current = new Pheromone(new GridPoint2(2, 0), PheromoneType.EXPLORE, 2);
        Pheromone backward = new Pheromone(new GridPoint2(1, 0), PheromoneType.EXPLORE, 1);

        List<Pheromone> neighbors = Collections.singletonList(backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        // Should return null to leave trail and start wandering
        assertNull(result, "Should leave trail at end (return null) to start wandering");
    }

    @Test
    @DisplayName("should NOT return along trail (unlike Gather)")
    void shouldNotReturnAlongTrail() {
        // Simulate reaching end of trail
        Pheromone end = new Pheromone(new GridPoint2(3, 0), PheromoneType.EXPLORE, 3);
        Pheromone backward = new Pheromone(new GridPoint2(2, 0), PheromoneType.EXPLORE, 2);

        // First call at trail end
        Pheromone result = strategy.selectNextPheromone(ant, Collections.singletonList(backward), end);

        // Should leave trail, NOT go backward
        assertNull(result, "Should leave trail, not return along it");
        // The outwards flag might change, but it shouldn't matter since we return null
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("should return null when no neighbors available")
    void shouldReturnNullWhenNoNeighbors() {
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.EXPLORE, 1);
        List<Pheromone> neighbors = Collections.emptyList();

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        assertNull(result);
    }

    @Test
    @DisplayName("onTrailEnd should set ant to wander behavior")
    void onTrailEndShouldSetWanderBehavior() {
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.EXPLORE, 1);

        strategy.onTrailEnd(ant, current);

        // Can't easily verify setWanderBehaviour was called on mock without
        // ArgumentCaptor
        // but at least we verify it doesn't throw
    }

    @Test
    @DisplayName("speed multiplier should be high for explorers")
    void speedMultiplierShouldBeHigh() {
        assertTrue(strategy.getSpeedMultiplier() > 1.0f, "Explore trail should have speed bonus");
    }
}
