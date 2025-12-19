package se.chalmers.tda367.team15.game.model.entity.ant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

/**
 * Tests for the {@link Inventory} class.
 * 
 * Organized using nested test classes for better structure:
 * - AddResourceTests: Tests for adding resources
 * - CapacityTests: Tests for capacity-related behavior
 * - StateTests: Tests for inventory state queries
 */
class InventoryTest {

    private static final int DEFAULT_CAPACITY = 10;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(DEFAULT_CAPACITY);
    }

    @Nested
    @DisplayName("Adding Resources")
    class AddResourceTests {

        @Test
        @DisplayName("should add resource when capacity allows")
        void shouldAddResourceWhenCapacityAllows() {
            boolean result = inventory.addResource(ResourceType.FOOD, 5);

            assertTrue(result);
            assertEquals(5, inventory.getAmount(ResourceType.FOOD));
        }

        @Test
        @DisplayName("should accumulate same resource type")
        void shouldAccumulateSameResourceType() {
            inventory.addResource(ResourceType.FOOD, 3);
            inventory.addResource(ResourceType.FOOD, 2);

            assertEquals(5, inventory.getAmount(ResourceType.FOOD));
        }

        @Test
        @DisplayName("should reject resource when exceeding capacity")
        void shouldRejectResourceWhenExceedingCapacity() {
            boolean result = inventory.addResource(ResourceType.FOOD, 15);

            assertFalse(result);
            assertEquals(0, inventory.getAmount(ResourceType.FOOD));
        }

        @Test
        @DisplayName("should reject resource when going below zero")
        void shouldRejectResourceWhenGoingBelowZero() {
            boolean result = inventory.addResource(ResourceType.FOOD, -1);

            assertFalse(result);
            assertEquals(0, inventory.getAmount(ResourceType.FOOD));
        }

        @Test
        @DisplayName("should reject partial overflow")
        void shouldRejectPartialOverflow() {
            inventory.addResource(ResourceType.FOOD, 8);

            boolean result = inventory.addResource(ResourceType.FOOD, 5);

            assertFalse(result);
            assertEquals(8, inventory.getAmount(ResourceType.FOOD));
        }
    }

    @Nested
    @DisplayName("Capacity Checks")
    class CapacityTests {

        @Test
        @DisplayName("should report full when at capacity")
        void shouldReportFullWhenAtCapacity() {
            inventory.addResource(ResourceType.FOOD, DEFAULT_CAPACITY);

            assertTrue(inventory.isFull());
        }

        @Test
        @DisplayName("should report not full when below capacity")
        void shouldReportNotFullWhenBelowCapacity() {
            inventory.addResource(ResourceType.FOOD, 5);

            assertFalse(inventory.isFull());
        }

        @Test
        @DisplayName("should calculate remaining capacity correctly")
        void shouldCalculateRemainingCapacityCorrectly() {
            inventory.addResource(ResourceType.FOOD, 3);

            assertEquals(7, inventory.getRemainingCapacity());
        }

        @Test
        @DisplayName("should have zero remaining when full")
        void shouldHaveZeroRemainingWhenFull() {
            inventory.addResource(ResourceType.FOOD, DEFAULT_CAPACITY);

            assertEquals(0, inventory.getRemainingCapacity());
        }
    }

    @Nested
    @DisplayName("Inventory State")
    class StateTests {

        @Test
        @DisplayName("should be empty when new")
        void shouldBeEmptyWhenNew() {
            assertTrue(inventory.isEmpty());
        }

        @Test
        @DisplayName("should not be empty after adding resource")
        void shouldNotBeEmptyAfterAddingResource() {
            inventory.addResource(ResourceType.FOOD, 1);

            assertFalse(inventory.isEmpty());
        }

        @Test
        @DisplayName("should be empty after clear")
        void shouldBeEmptyAfterClear() {
            inventory.addResource(ResourceType.FOOD, 5);
            inventory.clear();

            assertTrue(inventory.isEmpty());
            assertEquals(0, inventory.getTotalAmount());
        }

        @Test
        @DisplayName("should return zero for missing resource types")
        void shouldReturnZeroForMissingResourceTypes() {
            assertEquals(0, inventory.getAmount(ResourceType.FOOD));
        }

        @Test
        @DisplayName("should track total amount across types")
        void shouldTrackTotalAmountAcrossTypes() {
            inventory.addResource(ResourceType.FOOD, 5);
            // When you add more ResourceTypes, you can test multiple types here:
            // inventory.addResource(ResourceType.WOOD, 3);
            // assertEquals(8, inventory.getTotalAmount());

            assertEquals(5, inventory.getTotalAmount());
        }
    }
}
