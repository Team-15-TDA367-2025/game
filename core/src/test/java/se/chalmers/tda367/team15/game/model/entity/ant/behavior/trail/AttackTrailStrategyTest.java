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
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

@ExtendWith(MockitoExtension.class)
class AttackTrailStrategyTest {

    private AttackTrailStrategy strategy;

    @Mock
    private EntityQuery entityQuery;

    @Mock
    private PheromoneGridConverter converter;

    @Mock
    private Ant ant;

    @Mock
    private AntType antType;

    @BeforeEach
    void setUp() {
        strategy = new AttackTrailStrategy(entityQuery, converter);
        lenient().when(ant.getType()).thenReturn(antType);
        lenient().when(antType.id()).thenReturn("soldier");
    }

    // ========== Core Behavior: Spread Out on Trail ==========

    @Test
    @DisplayName("should move away from visible soldiers to maximize distance")
    void shouldMoveAwayFromVisibleSoldiers() {
        Ant otherSoldier = mock(Ant.class);
        AntType otherType = mock(AntType.class);
        when(otherSoldier.getType()).thenReturn(otherType);
        when(otherType.id()).thenReturn("soldier");

        // Other soldier is at (10, 0) - to the right
        when(otherSoldier.getPosition()).thenReturn(new Vector2(10, 0));
        when(ant.getPosition()).thenReturn(new Vector2(0, 0));
        when(ant.getVisionRadius()).thenReturn(20);

        when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Arrays.asList(ant, otherSoldier));

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        Pheromone towardOther = new Pheromone(new GridPoint2(1, 0), PheromoneType.ATTACK, 2);
        Pheromone awayFromOther = new Pheromone(new GridPoint2(-1, 0), PheromoneType.ATTACK, 0);

        // Converter returns world positions
        when(converter.pheromoneGridToWorld(towardOther.getPosition())).thenReturn(new Vector2(5, 0));
        when(converter.pheromoneGridToWorld(awayFromOther.getPosition())).thenReturn(new Vector2(-5, 0));

        List<Pheromone> neighbors = Arrays.asList(towardOther, awayFromOther);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        // awayFromOther is further from otherSoldier (distance 15 vs 5)
        assertEquals(awayFromOther, result, "Should move away from other soldier");
    }

    @Test
    @DisplayName("should patrol along trail when no other soldiers visible")
    void shouldPatrolWhenNoSoldiersVisible() {
        when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.emptyList());

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        Pheromone outward = new Pheromone(new GridPoint2(1, 0), PheromoneType.ATTACK, 2);
        Pheromone backward = new Pheromone(new GridPoint2(-1, 0), PheromoneType.ATTACK, 0);

        List<Pheromone> neighbors = Arrays.asList(outward, backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        // Should pick something (patrol), not null
        assertNotNull(result, "Should patrol on trail when alone");
        assertTrue(result == outward || result == backward, "Should pick a valid neighbor");
    }

    @Test
    @DisplayName("should have jitter - not always pick the same direction when alone")
    void shouldHaveJitterWhenAlone() {
        when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.emptyList());

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        Pheromone outward = new Pheromone(new GridPoint2(1, 0), PheromoneType.ATTACK, 2);
        Pheromone backward = new Pheromone(new GridPoint2(-1, 0), PheromoneType.ATTACK, 0);

        List<Pheromone> neighbors = Arrays.asList(outward, backward);

        Set<Pheromone> results = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);
            assertNotNull(result);
            results.add(result);
        }

        // With jitter, should sometimes pick backward too
        assertEquals(2, results.size(), "Should have jitter - pick both directions sometimes");
    }

    // ========== Spacing with Multiple Soldiers ==========

    @Test
    @DisplayName("should space out evenly between two soldiers")
    void shouldSpaceOutBetweenTwoSoldiers() {
        Ant soldier1 = mock(Ant.class);
        Ant soldier2 = mock(Ant.class);
        AntType soldierType = mock(AntType.class);

        when(soldier1.getType()).thenReturn(soldierType);
        when(soldier2.getType()).thenReturn(soldierType);
        when(soldierType.id()).thenReturn("soldier");

        // soldier1 at (-10, 0), soldier2 at (10, 0), current ant at (0, 0)
        when(soldier1.getPosition()).thenReturn(new Vector2(-10, 0));
        when(soldier2.getPosition()).thenReturn(new Vector2(10, 0));
        when(ant.getPosition()).thenReturn(new Vector2(0, 0));
        when(ant.getVisionRadius()).thenReturn(20);

        when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Arrays.asList(ant, soldier1, soldier2));

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        // Move perpendicular to spread out
        Pheromone perpendicular = new Pheromone(new GridPoint2(0, 1), PheromoneType.ATTACK, 2);
        Pheromone towardSoldier1 = new Pheromone(new GridPoint2(-1, 0), PheromoneType.ATTACK, 0);

        when(converter.pheromoneGridToWorld(perpendicular.getPosition())).thenReturn(new Vector2(0, 5));
        when(converter.pheromoneGridToWorld(towardSoldier1.getPosition())).thenReturn(new Vector2(-5, 0));

        List<Pheromone> neighbors = Arrays.asList(perpendicular, towardSoldier1);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        // perpendicular has min distance ~10, towardSoldier1 has min distance ~5
        assertEquals(perpendicular, result, "Should move to maximize minimum distance");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("should return null when no neighbors available")
    void shouldReturnNullWhenNoNeighbors() {
        lenient().when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.emptyList());

        Pheromone current = new Pheromone(new GridPoint2(0, 0), PheromoneType.ATTACK, 1);
        List<Pheromone> neighbors = Collections.emptyList();

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        assertNull(result);
    }

    @Test
    @DisplayName("speed multiplier should be 1.0 for soldiers")
    void speedMultiplierShouldBeOne() {
        assertEquals(1.0f, strategy.getSpeedMultiplier(), 0.01f, "Soldiers should move at normal speed");
    }
}
