package se.chalmers.tda367.team15.game.model.egg;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.Egg;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.providers.EntityModificationProvider;
import se.chalmers.tda367.team15.game.model.managers.EggManager;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

/**
 * Tests for the {@link EggManager} class.
 *
 * Organized using nested test classes for better structure:
 * - EggAdditionTests: Tests for adding eggs
 * - EggHatchingTests: Tests for egg hatching lifecycle
 * - ObserverTests: Tests for observer pattern behavior
 * - EggListTests: Tests for egg list state queries
 */
class EggManagerTest {

    private AntTypeRegistry antTypeRegistry;
    private AntFactory antFactory;
    private Home home;
    private EntityModificationProvider entityManager;
    private EggManager eggManager;
    private AntType workerType;
    private AntType soldierType;

    @BeforeEach
    void setUp() {
        antTypeRegistry = new AntTypeRegistry();
        antFactory = mock(AntFactory.class);
        home = mock(Home.class);
        entityManager = mock(EntityModificationProvider.class);

        // Register test ant types
        workerType = AntType.with()
                .id("worker")
                .displayName("Worker")
                .foodCost(10)
                .developmentTicks(3)
                .maxHealth(100f)
                .moveSpeed(1.0f)
                .carryCapacity(5)
                .build();

        soldierType = AntType.with()
                .id("soldier")
                .displayName("Soldier")
                .foodCost(20)
                .developmentTicks(5)
                .maxHealth(200f)
                .moveSpeed(0.8f)
                .carryCapacity(2)
                .build();

        antTypeRegistry.register(workerType);
        antTypeRegistry.register(soldierType);

        eggManager = new EggManager(antTypeRegistry, antFactory, home, entityManager);
    }

    @Nested
    @DisplayName("Adding Eggs")
    class EggAdditionTests {

        @Test
        @DisplayName("should add egg to the list")
        void shouldAddEggToList() {
            eggManager.addEgg(workerType);

            List<Egg> eggs = eggManager.getEggs();
            assertEquals(1, eggs.size());
            assertEquals("worker", eggs.get(0).getTypeId());
        }

        @Test
        @DisplayName("should add multiple eggs")
        void shouldAddMultipleEggs() {
            eggManager.addEgg(workerType);
            eggManager.addEgg(soldierType);
            eggManager.addEgg(workerType);

            List<Egg> eggs = eggManager.getEggs();
            assertEquals(3, eggs.size());
        }

        @Test
        @DisplayName("should set correct development ticks from ant type")
        void shouldSetCorrectDevelopmentTicks() {
            eggManager.addEgg(workerType);
            eggManager.addEgg(soldierType);

            List<Egg> eggs = eggManager.getEggs();
            assertEquals(3, eggs.get(0).getTicksRemaining()); // worker has 3 ticks
            assertEquals(5, eggs.get(1).getTicksRemaining()); // soldier has 5 ticks
        }
    }

    @Nested
    @DisplayName("Egg Hatching Lifecycle")
    class EggHatchingTests {

        @Test
        @DisplayName("should tick all eggs on onMinute")
        void shouldTickAllEggsOnMinute() {
            eggManager.addEgg(workerType);
            eggManager.addEgg(soldierType);

            eggManager.onMinute();

            List<Egg> eggs = eggManager.getEggs();
            assertEquals(2, eggs.get(0).getTicksRemaining()); // 3 - 1
            assertEquals(4, eggs.get(1).getTicksRemaining()); // 5 - 1
        }

        @Test
        @DisplayName("should remove hatched eggs and add to entity manager after onMinute")
        void shouldRemoveHatchedEggsAfterMinute() {
            eggManager.addEgg(workerType); // 3 ticks
            Ant ant = mock(Ant.class);
            when(antFactory.createAnt(home, workerType)).thenReturn(ant);

            // Tick 3 times to hatch worker
            eggManager.onMinute();
            eggManager.onMinute();
            eggManager.onMinute();

            List<Egg> eggs = eggManager.getEggs();
            assertEquals(0, eggs.size());
            verify(entityManager).addEntity(ant);
        }

        @Test
        @DisplayName("should eventually remove all eggs after enough ticks")
        void shouldEventuallyRemoveAllEggs() {
            eggManager.addEgg(workerType); // 3 ticks
            eggManager.addEgg(soldierType); // 5 ticks

            when(antFactory.createAnt(eq(home), any(AntType.class))).thenReturn(mock(Ant.class));

            // Tick 5 times to hatch all
            for (int i = 0; i < 5; i++) {
                eggManager.onMinute();
            }

            assertTrue(eggManager.getEggs().isEmpty());
            verify(entityManager, times(2)).addEntity(any(Ant.class));
        }

        @Test
        @DisplayName("should handle empty egg list gracefully")
        void shouldHandleEmptyEggListGracefully() {
            // Should not throw exception
            assertDoesNotThrow(() -> eggManager.onMinute());
            assertTrue(eggManager.getEggs().isEmpty());
        }
    }

    @Nested
    @DisplayName("Purchasing Eggs")
    class EggPurchaseTests {

        @Test
        @DisplayName("should add egg when purchase is successful")
        void shouldAddEggWhenPurchaseSuccessful() {
            when(home.spendResources(ResourceType.FOOD, workerType.foodCost())).thenReturn(true);

            boolean result = eggManager.purchaseEgg(workerType);

            assertTrue(result);
            assertEquals(1, eggManager.getEggs().size());
            assertEquals("worker", eggManager.getEggs().get(0).getTypeId());
            verify(home).spendResources(ResourceType.FOOD, workerType.foodCost());
        }

        @Test
        @DisplayName("should not add egg when resources are insufficient")
        void shouldNotAddEggWhenResourcesInsufficient() {
            when(home.spendResources(ResourceType.FOOD, workerType.foodCost())).thenReturn(false);

            boolean result = eggManager.purchaseEgg(workerType);

            assertFalse(result);
            assertTrue(eggManager.getEggs().isEmpty());
            verify(home).spendResources(ResourceType.FOOD, workerType.foodCost());
        }

        @Test
        @DisplayName("should return false for null type")
        void shouldReturnFalseForNullType() {
            boolean result = eggManager.purchaseEgg(null);
            assertFalse(result);
            assertTrue(eggManager.getEggs().isEmpty());
        }
    }

    @Nested
    @DisplayName("Egg List State")
    class EggListTests {

        @Test
        @DisplayName("should return empty list when no eggs")
        void shouldReturnEmptyListWhenNoEggs() {
            assertTrue(eggManager.getEggs().isEmpty());
        }

        @Test
        @DisplayName("should return unmodifiable list")
        void shouldReturnUnmodifiableList() {
            eggManager.addEgg(workerType);

            List<Egg> eggs = eggManager.getEggs();

            assertThrows(UnsupportedOperationException.class, () -> eggs.add(null));
            assertThrows(UnsupportedOperationException.class, () -> eggs.remove(0));
            assertThrows(UnsupportedOperationException.class, () -> eggs.clear());
        }

        @Test
        @DisplayName("should reflect current state after modifications")
        void shouldReflectCurrentStateAfterModifications() {
            eggManager.addEgg(workerType);
            assertEquals(1, eggManager.getEggs().size());

            // Hatch the egg
            for (int i = 0; i < 3; i++) {
                eggManager.onMinute();
            }

            assertEquals(0, eggManager.getEggs().size());

            // Add new egg
            eggManager.addEgg(soldierType);
            assertEquals(1, eggManager.getEggs().size());
            assertEquals("soldier", eggManager.getEggs().get(0).getTypeId());
        }
    }
}
