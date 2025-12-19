package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.enemy.Termite;
import se.chalmers.tda367.team15.game.model.faction.Faction;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WanderBehaviorTest {

    @Mock
    private Ant ant;

    @Mock
    private Home home;

    @Mock
    private EntityQuery entityQuery;

    @Mock
    private PheromoneManager pheromoneManager;

    @Mock
    private AntType antType;

    // ========== Trail Re-entry Cooldown Tests ==========

    @Test
    @DisplayName("should immediately follow trail when created without cooldown")
    void shouldImmediatelyFollowTrailWithoutCooldown() {
        // Given: wander behavior without cooldown
        WanderBehavior behavior = new WanderBehavior(ant, home, entityQuery, false);

        // Setup pheromones in range
        GridPoint2 gridPos = new GridPoint2(0, 0);
        when(ant.getGridPosition()).thenReturn(gridPos);
        when(ant.getType()).thenReturn(antType);
        when(ant.getVisionRadius()).thenReturn(8);
        when(ant.getVelocity()).thenReturn(new Vector2(1, 0));
        when(ant.getPosition()).thenReturn(new Vector2(0, 0));
        when(ant.getSpeed()).thenReturn(1.0f);
        when(home.getPosition()).thenReturn(new Vector2(0, 0));
        when(antType.allowedPheromones()).thenReturn(Set.of(PheromoneType.EXPLORE));
        when(antType.homeBias()).thenReturn(0.0f);

        Pheromone nearbyPheromone = new Pheromone(gridPos, PheromoneType.EXPLORE, 1);
        when(pheromoneManager.getPheromonesIn3x3(gridPos, Set.of(PheromoneType.EXPLORE)))
                .thenReturn(List.of(nearbyPheromone));
        // No enemies in sight
        when(entityQuery.getEntitiesOfType(Termite.class)).thenReturn(Collections.emptyList());

        // When: update is called
        behavior.update(pheromoneManager);

        // Then: should switch to follow trail immediately
        verify(ant).setFollowTrailBehaviour();
    }

    @Test
    @DisplayName("should NOT follow trail during cooldown period after leaving trail")
    void shouldNotFollowTrailDuringCooldown() {
        // Given: wander behavior WITH cooldown (ant just left a trail)
        WanderBehavior behavior = new WanderBehavior(ant, home, entityQuery, true);

        // Setup: no enemies in sight
        when(ant.getVisionRadius()).thenReturn(8);
        when(entityQuery.getEntitiesOfType(Termite.class)).thenReturn(Collections.emptyList());

        // When: update is called once
        behavior.update(pheromoneManager);

        // Then: should NOT switch to follow trail (still cooling down)
        verify(ant, never()).setFollowTrailBehaviour();
    }

    @Test
    @DisplayName("should follow trail after cooldown period expires")
    void shouldFollowTrailAfterCooldownExpires() {
        // Given: wander behavior with cooldown
        WanderBehavior behavior = new WanderBehavior(ant, home, entityQuery, true);

        // Setup pheromones in range
        GridPoint2 gridPos = new GridPoint2(0, 0);
        when(ant.getGridPosition()).thenReturn(gridPos);
        when(ant.getType()).thenReturn(antType);
        when(ant.getVisionRadius()).thenReturn(8);
        when(ant.getPosition()).thenReturn(new Vector2(0, 0));
        when(ant.getSpeed()).thenReturn(1.0f);
        when(ant.getVelocity()).thenReturn(new Vector2(1, 0));
        when(ant.getRotation()).thenReturn(0.0f);
        when(home.getPosition()).thenReturn(new Vector2(0, 0));
        when(antType.allowedPheromones()).thenReturn(Set.of(PheromoneType.EXPLORE));
        when(antType.homeBias()).thenReturn(0.0f);
        // No enemies in sight
        when(entityQuery.getEntitiesOfType(Termite.class)).thenReturn(Collections.emptyList());

        Pheromone nearbyPheromone = new Pheromone(gridPos, PheromoneType.EXPLORE, 1);
        when(pheromoneManager.getPheromonesIn3x3(gridPos, Set.of(PheromoneType.EXPLORE)))
                .thenReturn(List.of(nearbyPheromone));

        // When: update is called many times to exhaust cooldown (30 frames)
        for (int i = 0; i < 31; i++) {
            behavior.update(pheromoneManager);
        }

        // Then: should switch to follow trail after cooldown expires
        verify(ant).setFollowTrailBehaviour();
    }

    @Test
    @DisplayName("cooldown should still allow attacking enemies")
    void cooldownShouldStillAllowAttacking() {
        // Given: wander behavior with cooldown
        WanderBehavior behavior = new WanderBehavior(ant, home, entityQuery, true);

        // Setup enemy in sight
        Termite termite = mock(Termite.class);
        when(termite.getPosition()).thenReturn(new Vector2(1, 1)); // nearby
        when(termite.getFaction()).thenReturn(Faction.TERMITE_PROTECTORATE);
        when(ant.getPosition()).thenReturn(new Vector2(0, 0));
        when(ant.getFaction()).thenReturn(Faction.DEMOCRATIC_REPUBLIC_OF_ANTS);
        when(ant.getVisionRadius()).thenReturn(8);
        when(entityQuery.getEntitiesOfType(Termite.class)).thenReturn(List.of(termite));

        // When: update is called
        behavior.update(pheromoneManager);

        // Then: should switch to attack (cooldown shouldn't prevent attack)
        verify(ant).setAttackBehaviour();
    }
}
