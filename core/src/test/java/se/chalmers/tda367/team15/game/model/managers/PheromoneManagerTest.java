package se.chalmers.tda367.team15.game.model.managers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

/**
 * Tests for the {@link PheromoneManager} class.
 *
 * Organized using nested test classes for better structure:
 * - AddPheromoneTests: Tests for adding pheromones
 * - RemovePheromoneTests: Tests for removing pheromones and cascade deletion
 * - DistancePropagationTests: Tests for distance calculation and propagation
 * - QueryTests: Tests for querying pheromones
 */
class PheromoneManagerTest {

    private PheromoneManager pheromoneManager;
    private PheromoneGridConverter converter;
    private GridPoint2 colonyPosition;

    // Colony at (10, 10) with size 4 tiles, 2 pheromones per tile
    private static final int PHEROMONES_PER_TILE = 2;
    private static final int COLONY_SIZE_TILES = 4;

    @BeforeEach
    void setUp() {
        converter = new PheromoneGridConverter(PHEROMONES_PER_TILE);
        colonyPosition = new GridPoint2(10, 10);
        pheromoneManager = new PheromoneManager(colonyPosition, converter, COLONY_SIZE_TILES);
    }

    /**
     * Helper to get the colony position in pheromone grid coordinates.
     */
    private GridPoint2 getColonyGridPos() {
        return converter.worldToPheromoneGrid(
                new com.badlogic.gdx.math.Vector2(colonyPosition.x, colonyPosition.y));
    }

    /**
     * Helper to get a position adjacent to the colony (just outside).
     * The position must be adjacent to a point inside the colony.
     * Colony uses Euclidean distance: pos.dst(colonyCenter) < colonyGridSize / 2
     */
    private GridPoint2 getAdjacentToColony() {
        GridPoint2 colonyCenter = getColonyGridPos();
        int colonyGridSize = COLONY_SIZE_TILES * PHEROMONES_PER_TILE;
        int radius = colonyGridSize / 2;

        // Position at the edge of the colony (inside, at distance radius - 1)
        // Its neighbor to the right will be just outside but adjacent to this inside
        // point
        GridPoint2 insideEdge = new GridPoint2(colonyCenter.x + radius - 1, colonyCenter.y);

        // Return the position just outside, adjacent to the inside edge
        return new GridPoint2(insideEdge.x + 1, insideEdge.y);
    }

    @Nested
    @DisplayName("Adding Pheromones")
    class AddPheromoneTests {

        @Test
        @DisplayName("should add pheromone adjacent to colony")
        void shouldAddPheromoneAdjacentToColony() {
            GridPoint2 pos = getAdjacentToColony();

            boolean added = pheromoneManager.addPheromone(pos, PheromoneType.GATHER);

            assertTrue(added);
            assertNotNull(pheromoneManager.getPheromoneAt(pos));
            assertEquals(PheromoneType.GATHER, pheromoneManager.getPheromoneAt(pos).getType());
        }

        @Test
        @DisplayName("should not add pheromone at same position twice")
        void shouldNotAddPheromoneAtSamePositionTwice() {
            GridPoint2 pos = getAdjacentToColony();

            boolean firstAdd = pheromoneManager.addPheromone(pos, PheromoneType.GATHER);
            boolean secondAdd = pheromoneManager.addPheromone(pos, PheromoneType.ATTACK);

            assertTrue(firstAdd);
            assertFalse(secondAdd);
            assertEquals(PheromoneType.GATHER, pheromoneManager.getPheromoneAt(pos).getType());
        }

        @Test
        @DisplayName("should add pheromone adjacent to existing pheromone")
        void shouldAddPheromoneAdjacentToExisting() {
            GridPoint2 firstPos = getAdjacentToColony();
            pheromoneManager.addPheromone(firstPos, PheromoneType.GATHER);

            GridPoint2 secondPos = new GridPoint2(firstPos.x + 1, firstPos.y);
            boolean added = pheromoneManager.addPheromone(secondPos, PheromoneType.ATTACK);

            assertTrue(added);
            assertNotNull(pheromoneManager.getPheromoneAt(secondPos));
        }

        @Test
        @DisplayName("should not add pheromone not adjacent to colony or existing pheromone")
        void shouldNotAddIsolatedPheromone() {
            // Position far from colony and no existing pheromones
            GridPoint2 isolatedPos = new GridPoint2(1000, 1000);

            boolean added = pheromoneManager.addPheromone(isolatedPos, PheromoneType.GATHER);

            assertFalse(added);
            assertNull(pheromoneManager.getPheromoneAt(isolatedPos));
        }

        @Test
        @DisplayName("should add multiple pheromones in a chain")
        void shouldAddMultiplePheromonesInChain() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            assertTrue(pheromoneManager.addPheromone(pos1, PheromoneType.GATHER));
            assertTrue(pheromoneManager.addPheromone(pos2, PheromoneType.GATHER));
            assertTrue(pheromoneManager.addPheromone(pos3, PheromoneType.GATHER));

            assertEquals(3, pheromoneManager.getPheromones().size());
        }

        @Test
        @DisplayName("should set correct distance from colony")
        void shouldSetCorrectDistanceFromColony() {
            GridPoint2 pos1 = getAdjacentToColony();
            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);

            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);

            // Second pheromone should have higher distance
            int distance1 = pheromoneManager.getPheromoneAt(pos1).getDistance();
            int distance2 = pheromoneManager.getPheromoneAt(pos2).getDistance();

            assertTrue(distance2 > distance1);
        }
    }

    @Nested
    @DisplayName("Removing Pheromones")
    class RemovePheromoneTests {

        @Test
        @DisplayName("should remove pheromone at position")
        void shouldRemovePheromoneAtPosition() {
            GridPoint2 pos = getAdjacentToColony();
            pheromoneManager.addPheromone(pos, PheromoneType.GATHER);

            pheromoneManager.removePheromone(pos);

            assertNull(pheromoneManager.getPheromoneAt(pos));
            assertTrue(pheromoneManager.getPheromones().isEmpty());
        }

        @Test
        @DisplayName("should handle removing non-existent pheromone gracefully")
        void shouldHandleRemovingNonExistentPheromone() {
            GridPoint2 pos = new GridPoint2(100, 100);

            // Should not throw
            assertDoesNotThrow(() -> pheromoneManager.removePheromone(pos));
        }

        @Test
        @DisplayName("should cascade remove downstream pheromones")
        void shouldCascadeRemoveDownstreamPheromones() {
            // Create a chain of pheromones
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);

            // Remove the first one - should cascade remove pos2 and pos3
            pheromoneManager.removePheromone(pos1);

            assertNull(pheromoneManager.getPheromoneAt(pos1));
            assertNull(pheromoneManager.getPheromoneAt(pos2));
            assertNull(pheromoneManager.getPheromoneAt(pos3));
            assertTrue(pheromoneManager.getPheromones().isEmpty());
        }

        @Test
        @DisplayName("should only remove downstream pheromones, not upstream")
        void shouldOnlyRemoveDownstreamPheromones() {
            // Create a chain of pheromones
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);

            // Remove the middle one - should keep pos1, remove pos3
            pheromoneManager.removePheromone(pos2);

            assertNotNull(pheromoneManager.getPheromoneAt(pos1));
            assertNull(pheromoneManager.getPheromoneAt(pos2));
            assertNull(pheromoneManager.getPheromoneAt(pos3));
            assertEquals(1, pheromoneManager.getPheromones().size());
        }
    }

    @Nested
    @DisplayName("Distance Propagation")
    class DistancePropagationTests {

        @Test
        @DisplayName("should update distances when shorter path becomes available")
        void shouldUpdateDistancesWhenShorterPathAvailable() {
            // Create a longer path first
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);
            GridPoint2 pos4 = new GridPoint2(pos3.x, pos3.y + 1); // Above pos3

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos4, PheromoneType.GATHER);

            int originalDistance4 = pheromoneManager.getPheromoneAt(pos4).getDistance();

            // Now add a shortcut adjacent to pos4
            GridPoint2 shortcut = new GridPoint2(pos4.x - 1, pos4.y); // Left of pos4
            pheromoneManager.addPheromone(shortcut, PheromoneType.GATHER);

            // pos4's distance should remain the same or be updated if shortcut provides
            // shorter path
            int newDistance4 = pheromoneManager.getPheromoneAt(pos4).getDistance();
            assertTrue(newDistance4 <= originalDistance4);
        }

        @Test
        @DisplayName("should propagate distances through connected pheromones")
        void shouldPropagateThroughConnectedPheromones() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);

            int d1 = pheromoneManager.getPheromoneAt(pos1).getDistance();
            int d2 = pheromoneManager.getPheromoneAt(pos2).getDistance();
            int d3 = pheromoneManager.getPheromoneAt(pos3).getDistance();

            // Distances should increase along the chain
            assertTrue(d2 > d1);
            assertTrue(d3 > d2);
            assertEquals(d1 + 1, d2);
            assertEquals(d2 + 1, d3);
        }
    }

    @Nested
    @DisplayName("Query Methods")
    class QueryTests {

        @Test
        @DisplayName("should return empty collection when no pheromones")
        void shouldReturnEmptyCollectionWhenNoPheromones() {
            Collection<Pheromone> pheromones = pheromoneManager.getPheromones();

            assertNotNull(pheromones);
            assertTrue(pheromones.isEmpty());
        }

        @Test
        @DisplayName("should return all pheromones")
        void shouldReturnAllPheromones() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.ATTACK);

            Collection<Pheromone> pheromones = pheromoneManager.getPheromones();

            assertEquals(2, pheromones.size());
        }

        @Test
        @DisplayName("should return null for non-existent position")
        void shouldReturnNullForNonExistentPosition() {
            Pheromone pheromone = pheromoneManager.getPheromoneAt(new GridPoint2(999, 999));

            assertNull(pheromone);
        }

        @Test
        @DisplayName("should return pheromone at specific position")
        void shouldReturnPheromoneAtSpecificPosition() {
            GridPoint2 pos = getAdjacentToColony();
            pheromoneManager.addPheromone(pos, PheromoneType.EXPLORE);

            Pheromone pheromone = pheromoneManager.getPheromoneAt(pos);

            assertNotNull(pheromone);
            assertEquals(pos, pheromone.getPosition());
            assertEquals(PheromoneType.EXPLORE, pheromone.getType());
        }

        @Test
        @DisplayName("should return pheromones in 3x3 area")
        void shouldReturnPheromonesIn3x3Area() {
            GridPoint2 center = getAdjacentToColony();
            GridPoint2 right = new GridPoint2(center.x + 1, center.y);

            pheromoneManager.addPheromone(center, PheromoneType.GATHER);
            pheromoneManager.addPheromone(right, PheromoneType.GATHER);

            List<Pheromone> pheromones = pheromoneManager.getPheromonesIn3x3(center);

            assertEquals(2, pheromones.size());
        }

        @Test
        @DisplayName("should return empty list for 3x3 area with no pheromones")
        void shouldReturnEmptyListFor3x3AreaWithNoPheromones() {
            List<Pheromone> pheromones = pheromoneManager.getPheromonesIn3x3(new GridPoint2(500, 500));

            assertNotNull(pheromones);
            assertTrue(pheromones.isEmpty());
        }
    }

    @Nested
    @DisplayName("Converter Access")
    class ConverterTests {

        @Test
        @DisplayName("should return the converter")
        void shouldReturnConverter() {
            PheromoneGridConverter returnedConverter = pheromoneManager.getConverter();

            assertSame(converter, returnedConverter);
        }
    }
}
