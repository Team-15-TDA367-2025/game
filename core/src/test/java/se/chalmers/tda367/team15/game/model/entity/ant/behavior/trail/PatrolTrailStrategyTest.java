package se.chalmers.tda367.team15.game.model.entity.ant.behavior.trail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

@ExtendWith(MockitoExtension.class)
class PatrolTrailStrategyTest {

    private PatrolTrailStrategy strategy;

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
        strategy = new PatrolTrailStrategy(entityQuery, converter);
        lenient().when(ant.getType()).thenReturn(antType);
        lenient().when(antType.id()).thenReturn("soldier");
    }

    // ========== Core Behavior: Patrol Along Trail ==========

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
    @DisplayName("should turn around at trail end")
    void shouldTurnAroundAtTrailEnd() {
        when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.emptyList());

        // At end of trail, only backward option available
        Pheromone current = new Pheromone(new GridPoint2(5, 0), PheromoneType.ATTACK, 5);
        Pheromone backward = new Pheromone(new GridPoint2(4, 0), PheromoneType.ATTACK, 4);

        List<Pheromone> neighbors = Collections.singletonList(backward);

        Pheromone result = strategy.selectNextPheromone(ant, neighbors, current);

        // Should turn around and pick backward
        assertEquals(backward, result, "Should turn around at trail end");
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
