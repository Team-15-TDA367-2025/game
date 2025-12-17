package se.chalmers.tda367.team15.game.model.egg;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;

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
    private EggManager eggManager;
    private AntType workerType;
    private AntType soldierType;

    @BeforeEach
    void setUp() {
        antTypeRegistry = new AntTypeRegistry();
        // AntFactory is only passed through to observers, not used by EggManager
        // directly
        antFactory = null;

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

        eggManager = new EggManager(antTypeRegistry, antFactory);
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
        @DisplayName("should remove hatched eggs after onMinute")
        void shouldRemoveHatchedEggsAfterMinute() {
            eggManager.addEgg(workerType); // 3 ticks
            eggManager.addEgg(soldierType); // 5 ticks

            // Tick 3 times to hatch worker
            eggManager.onMinute();
            eggManager.onMinute();
            eggManager.onMinute();

            List<Egg> eggs = eggManager.getEggs();
            assertEquals(1, eggs.size());
            assertEquals("soldier", eggs.get(0).getTypeId());
        }

        @Test
        @DisplayName("should eventually remove all eggs after enough ticks")
        void shouldEventuallyRemoveAllEggs() {
            eggManager.addEgg(workerType); // 3 ticks
            eggManager.addEgg(soldierType); // 5 ticks

            // Tick 5 times to hatch all
            for (int i = 0; i < 5; i++) {
                eggManager.onMinute();
            }

            assertTrue(eggManager.getEggs().isEmpty());
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
    @DisplayName("Observer Pattern")
    class ObserverTests {

        @Test
        @DisplayName("should notify observer when egg hatches")
        void shouldNotifyObserverWhenEggHatches() {
            AtomicInteger hatchCount = new AtomicInteger(0);
            AtomicReference<AntType> hatchedType = new AtomicReference<>();

            EggHatchObserver observer = (factory, type) -> {
                hatchCount.incrementAndGet();
                hatchedType.set(type);
            };

            eggManager.addObserver(observer);
            eggManager.addEgg(workerType); // 3 ticks

            // Tick until hatched
            eggManager.onMinute();
            eggManager.onMinute();
            eggManager.onMinute();

            assertEquals(1, hatchCount.get());
            assertEquals(workerType, hatchedType.get());
        }

        @Test
        @DisplayName("should notify multiple observers")
        void shouldNotifyMultipleObservers() {
            AtomicInteger observer1Count = new AtomicInteger(0);
            AtomicInteger observer2Count = new AtomicInteger(0);

            eggManager.addObserver((factory, type) -> observer1Count.incrementAndGet());
            eggManager.addObserver((factory, type) -> observer2Count.incrementAndGet());

            eggManager.addEgg(workerType); // 3 ticks

            for (int i = 0; i < 3; i++) {
                eggManager.onMinute();
            }

            assertEquals(1, observer1Count.get());
            assertEquals(1, observer2Count.get());
        }

        @Test
        @DisplayName("should not notify observer after removal")
        void shouldNotNotifyObserverAfterRemoval() {
            AtomicInteger hatchCount = new AtomicInteger(0);

            EggHatchObserver observer = (factory, type) -> hatchCount.incrementAndGet();

            eggManager.addObserver(observer);
            eggManager.removeObserver(observer);
            eggManager.addEgg(workerType);

            for (int i = 0; i < 3; i++) {
                eggManager.onMinute();
            }

            assertEquals(0, hatchCount.get());
        }

        @Test
        @DisplayName("should pass correct ant factory to observer")
        void shouldPassCorrectAntFactoryToObserver() {
            AtomicReference<AntFactory> receivedFactory = new AtomicReference<>();

            EggHatchObserver observer = (factory, type) -> receivedFactory.set(factory);

            eggManager.addObserver(observer);
            eggManager.addEgg(workerType);

            for (int i = 0; i < 3; i++) {
                eggManager.onMinute();
            }

            assertSame(antFactory, receivedFactory.get());
        }

        @Test
        @DisplayName("should notify once per hatched egg even with multiple eggs")
        void shouldNotifyOncePerHatchedEgg() {
            AtomicInteger hatchCount = new AtomicInteger(0);

            eggManager.addObserver((factory, type) -> hatchCount.incrementAndGet());

            // Add two eggs with same development time
            AntType quickType = AntType.with()
                    .id("quick")
                    .displayName("Quick")
                    .foodCost(5)
                    .developmentTicks(1)
                    .maxHealth(50f)
                    .moveSpeed(2.0f)
                    .carryCapacity(1)
                    .build();
            antTypeRegistry.register(quickType);

            eggManager.addEgg(quickType);
            eggManager.addEgg(quickType);

            eggManager.onMinute();

            assertEquals(2, hatchCount.get());
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
