package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.entity.ant.behavior.FollowTrailBehavior;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

@ExtendWith(MockitoExtension.class)
class GatherTrailStrategyTest {

    private GatherTrailStrategy strategy;

    @Mock
    private Ant ant;

    @Mock
    private Inventory inventory;

    @Mock
    private FollowTrailBehavior behavior;

    @BeforeEach
    void setUp() {
        strategy = new GatherTrailStrategy();
        lenient().when(ant.getInventory()).thenReturn(inventory);
        lenient().when(behavior.isOutwards()).thenReturn(true);
    }

    // ========== Core Behavior: Wander on Trail ==========

    @Test
    @DisplayName("should pick randomly at forks (from all forward options)")
    void shouldPickRandomlyAtForks() {
        when(inventory.isFull()).thenReturn(false);

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.GATHER, 1);
        // Two forward options with DIFFERENT distances (like a real fork)
        Pheromone option1 = new Pheromone(new GridPoint2(1, 0), PheromoneType.GATHER, 2);
        Pheromone option2 = new Pheromone(new GridPoint2(0, 1), PheromoneType.GATHER, 3);
        List<Pheromone> neighbors = Arrays.asList(option1, option2);

        Set<Pheromone> results = new HashSet<>();
        // Run multiple times - random should give different results
        for (int i = 0; i < 50; i++) {
            Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);
            assertNotNull(result);
            results.add(result);
        }

        // Should have picked both options at least once (probabilistically)
        assertEquals(2, results.size(), "Expected random selection to pick both options over 50 iterations");
    }

    @Test
    @DisplayName("should turn around at dead ends instead of leaving trail")
    void shouldTurnAroundAtDeadEnds() {
        when(inventory.isFull()).thenReturn(false);
        when(behavior.isOutwards()).thenReturn(true);

        // At the end of trail (dist 3), only neighbor goes backward (dist 2)
        Pheromone current = new Pheromone(new GridPoint2(3, 0), PheromoneType.GATHER, 3);
        Pheromone backward = new Pheromone(new GridPoint2(2, 0), PheromoneType.GATHER, 2);
        List<Pheromone> neighbors = Collections.singletonList(backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        // Should NOT return null (leave trail), should turn around
        assertNotNull(result, "Should turn around at dead end, not leave trail");
        assertEquals(backward, result);
        verify(behavior).flipDirection(); // Should have flipped to go backward
    }

    @Test
    @DisplayName("should continue wandering after turning around at dead end")
    void shouldContinueWanderingAfterTurnAround() {
        when(inventory.isFull()).thenReturn(false);
        when(behavior.isOutwards()).thenReturn(false); // Already turned around

        // Now at mid (dist 2), should be able to continue backward
        Pheromone end = new Pheromone(new GridPoint2(3, 0), PheromoneType.GATHER, 3);
        Pheromone mid = new Pheromone(new GridPoint2(2, 0), PheromoneType.GATHER, 2);
        Pheromone start = new Pheromone(new GridPoint2(1, 0), PheromoneType.GATHER, 1);
        List<Pheromone> neighbors = Arrays.asList(end, start);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, mid, behavior);

        // Should pick something (continue wandering), not leave trail
        assertNotNull(result, "Should continue wandering on trail");
    }

    // ========== Return Home Only When Full ==========

    @Test
    @DisplayName("should return home (prefer lower distance) when inventory is full")
    void shouldReturnHomeWhenFull() {
        when(inventory.isFull()).thenReturn(true);

        Pheromone current = new Pheromone(new GridPoint2(2, 0), PheromoneType.GATHER, 2);
        Pheromone outward = new Pheromone(new GridPoint2(3, 0), PheromoneType.GATHER, 3);
        Pheromone homeward = new Pheromone(new GridPoint2(1, 0), PheromoneType.GATHER, 1);
        List<Pheromone> neighbors = Arrays.asList(outward, homeward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        assertEquals(homeward, result, "Should go toward lower distance (home) when full");
    }

    @Test
    @DisplayName("should return null when reaching colony with full inventory")
    void shouldReturnNullAtColonyWhenFull() {
        when(inventory.isFull()).thenReturn(true);

        // At dist 1, no lower distance available (colony is dist 0 but not a pheromone)
        Pheromone current = new Pheromone(new GridPoint2(1, 0), PheromoneType.GATHER, 1);
        Pheromone outward = new Pheromone(new GridPoint2(2, 0), PheromoneType.GATHER, 2);
        List<Pheromone> neighbors = Collections.singletonList(outward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        assertNull(result, "Should leave trail (return null) when at colony and full");
    }

    @Test
    @DisplayName("should move forward (higher distance) when inventory is not full")
    void shouldMoveForwardWhenNotFull() {
        when(inventory.isFull()).thenReturn(false);
        when(behavior.isOutwards()).thenReturn(true);

        Pheromone current = new Pheromone(new GridPoint2(2, 0), PheromoneType.GATHER, 2);
        Pheromone outward = new Pheromone(new GridPoint2(3, 0), PheromoneType.GATHER, 3);
        Pheromone homeward = new Pheromone(new GridPoint2(1, 0), PheromoneType.GATHER, 1);
        List<Pheromone> neighbors = Arrays.asList(outward, homeward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        // Should pick outward (higher distance), not homeward
        assertEquals(outward, result, "Should go forward (outward) when not full");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("should return null when no neighbors available")
    void shouldReturnNullWhenNoNeighbors() {
        lenient().when(inventory.isFull()).thenReturn(false);

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.GATHER, 1);
        List<Pheromone> neighbors = Collections.emptyList();

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current, behavior);

        assertNull(result);
    }

    @Test
    @DisplayName("speed multiplier should be greater than 1")
    void speedMultiplierShouldBePositive() {
        assertTrue(strategy.getSpeedMultiplier() > 1.0f, "Gather trail should have speed bonus");
    }
}
