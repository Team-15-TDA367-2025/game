package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.FollowTrailBehavior;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

@ExtendWith(MockitoExtension.class)
class PatrolTrailStrategyTest {

    private PatrolTrailStrategy strategy;

    @Mock
    private Ant ant;

    @Mock
    private AntType antType;

    @Mock
    private FollowTrailBehavior behavior;

    @BeforeEach
    void setUp() {
        strategy = new PatrolTrailStrategy();
        lenient().when(ant.getType()).thenReturn(antType);
        lenient().when(antType.id()).thenReturn("soldier");
        lenient().when(behavior.isOutwards()).thenReturn(true);
    }

    // ========== Core Behavior: Patrol Along Trail ==========

    @Test
    @DisplayName("should patrol along trail when alone on pheromone")
    void shouldPatrolWhenAloneOnPheromone() {
        // When soldier count is 1 (just this ant), it should patrol normally
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        current.incrementAnts(); // This ant

        Pheromone outward = new Pheromone(new GridPoint2(1, 0), PheromoneType.ATTACK, 2);
        Pheromone backward = new Pheromone(new GridPoint2(-1, 0), PheromoneType.ATTACK, 0);

        List<Pheromone> neighbors = Arrays.asList(outward, backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        // Should pick something (patrol), not null
        assertNotNull(result, "Should patrol on trail when alone");
        assertTrue(result == outward || result == backward, "Should pick a valid neighbor");
    }

    @Test
    @DisplayName("should turn around at trail end")
    void shouldTurnAroundAtTrailEnd() {
        when(behavior.isOutwards()).thenReturn(true);

        // At end of trail, only backward option available
        Pheromone current = new Pheromone(new GridPoint2(5, 0), PheromoneType.ATTACK, 5);
        Pheromone backward = new Pheromone(new GridPoint2(4, 0), PheromoneType.ATTACK, 4);

        List<Pheromone> neighbors = Collections.singletonList(backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        // Should turn around and pick backward
        assertEquals(backward, result, "Should turn around at trail end");
        verify(behavior).flipDirection();
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("should return null when no neighbors available")
    void shouldReturnNullWhenNoNeighbors() {
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        List<Pheromone> neighbors = Collections.emptyList();

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        assertNull(result);
    }

    @Test
    @DisplayName("speed multiplier should be 1.0 for soldiers")
    void speedMultiplierShouldBeOne() {
        assertEquals(1.0f, strategy.getSpeedMultiplier(), 0.01f, "Soldiers should move at normal speed");
    }

    @Test
    @DisplayName("should consider turning when other soldiers on same pheromone")
    void shouldConsiderTurningWithOtherSoldiers() {
        when(behavior.isOutwards()).thenReturn(true);

        // When multiple soldiers are on the same pheromone, there's a chance to turn
        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        current.incrementAnts(); // This ant
        current.incrementAnts(); // Another soldier
        current.incrementAnts(); // Another soldier

        assertEquals(3, current.getAntCount(), "Should have 3 soldiers on pheromone");

        Pheromone outward = new Pheromone(new GridPoint2(1, 0), PheromoneType.ATTACK, 2);
        Pheromone backward = new Pheromone(new GridPoint2(-1, 0), PheromoneType.ATTACK, 0);

        List<Pheromone> neighbors = Arrays.asList(outward, backward);

        // The result should still be valid even with other soldiers
        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);
        assertNotNull(result, "Should still patrol even with other soldiers");
    }
}
