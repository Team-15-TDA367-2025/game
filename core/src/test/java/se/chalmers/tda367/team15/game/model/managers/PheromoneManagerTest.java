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
 */
class PheromoneManagerTest {

    private PheromoneManager pheromoneManager;
    private PheromoneGridConverter converter;
    private GridPoint2 colonyPosition;

    private static final int PHEROMONES_PER_TILE = 2;
    private static final int COLONY_SIZE_TILES = 4;

    @BeforeEach
    void setUp() {
        converter = new PheromoneGridConverter(PHEROMONES_PER_TILE);
        colonyPosition = new GridPoint2(10, 10);
        pheromoneManager = new PheromoneManager(colonyPosition, converter, COLONY_SIZE_TILES);
    }

    private GridPoint2 getColonyGridPos() {
        return converter.worldToPheromoneGrid(
                new com.badlogic.gdx.math.Vector2(colonyPosition.x, colonyPosition.y));
    }

    private GridPoint2 getAdjacentToColony() {
        GridPoint2 colonyCenter = getColonyGridPos();
        int colonyGridSize = COLONY_SIZE_TILES * PHEROMONES_PER_TILE;
        int radius = colonyGridSize / 2;
        GridPoint2 insideEdge = new GridPoint2(colonyCenter.x + radius - 1, colonyCenter.y);
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
            assertNotNull(pheromoneManager.getPheromoneAt(pos, PheromoneType.GATHER));
            assertEquals(PheromoneType.GATHER, pheromoneManager.getPheromoneAt(pos, PheromoneType.GATHER).getType());
        }

        @Test
        @DisplayName("should allow multiple pheromone types at same position")
        void shouldAllowMultiplePheromoneTypesAtSamePosition() {
            GridPoint2 pos = getAdjacentToColony();

            boolean firstAdd = pheromoneManager.addPheromone(pos, PheromoneType.GATHER);
            boolean secondAdd = pheromoneManager.addPheromone(pos, PheromoneType.ATTACK);

            assertTrue(firstAdd);
            assertTrue(secondAdd);
            assertNotNull(pheromoneManager.getPheromoneAt(pos, PheromoneType.GATHER));
            assertNotNull(pheromoneManager.getPheromoneAt(pos, PheromoneType.ATTACK));
            assertEquals(2, pheromoneManager.getPheromonesAt(pos).size());
        }

        @Test
        @DisplayName("should not add same pheromone type at same position twice")
        void shouldNotAddSameTypeAtSamePositionTwice() {
            GridPoint2 pos = getAdjacentToColony();

            boolean firstAdd = pheromoneManager.addPheromone(pos, PheromoneType.GATHER);
            boolean secondAdd = pheromoneManager.addPheromone(pos, PheromoneType.GATHER);

            assertTrue(firstAdd);
            assertFalse(secondAdd);
        }

        @Test
        @DisplayName("should add pheromone adjacent to existing pheromone of same type")
        void shouldAddPheromoneAdjacentToExistingSameType() {
            GridPoint2 firstPos = getAdjacentToColony();
            pheromoneManager.addPheromone(firstPos, PheromoneType.GATHER);

            GridPoint2 secondPos = new GridPoint2(firstPos.x + 1, firstPos.y);
            boolean added = pheromoneManager.addPheromone(secondPos, PheromoneType.GATHER);

            assertTrue(added);
            assertNotNull(pheromoneManager.getPheromoneAt(secondPos, PheromoneType.GATHER));
        }

        @Test
        @DisplayName("should not add pheromone of different type adjacent only to other type")
        void shouldNotAddDifferentTypeAdjacentOnlyToOtherType() {
            GridPoint2 firstPos = getAdjacentToColony();
            pheromoneManager.addPheromone(firstPos, PheromoneType.GATHER);

            // Position that's only adjacent to GATHER, not adjacent to colony
            GridPoint2 secondPos = new GridPoint2(firstPos.x + 1, firstPos.y);
            boolean added = pheromoneManager.addPheromone(secondPos, PheromoneType.ATTACK);

            // Should fail because ATTACK has no adjacent ATTACK pheromone or colony
            assertFalse(added);
        }

        @Test
        @DisplayName("should set independent distances per type")
        void shouldSetIndependentDistancesPerType() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            // Create GATHER chain
            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);

            // Add ATTACK at pos1 and pos3 (skip pos2)
            pheromoneManager.addPheromone(pos1, PheromoneType.ATTACK);

            int gatherDistance3 = pheromoneManager.getPheromoneAt(pos3, PheromoneType.GATHER).getDistance();
            int gatherDistance1 = pheromoneManager.getPheromoneAt(pos1, PheromoneType.GATHER).getDistance();

            // GATHER at pos3 should have higher distance than pos1
            assertTrue(gatherDistance3 > gatherDistance1);
        }
    }

    @Nested
    @DisplayName("Removing Pheromones")
    class RemovePheromoneTests {

        @Test
        @DisplayName("should remove only specific pheromone type")
        void shouldRemoveOnlySpecificType() {
            GridPoint2 pos = getAdjacentToColony();
            pheromoneManager.addPheromone(pos, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos, PheromoneType.ATTACK);

            pheromoneManager.removePheromone(pos, PheromoneType.GATHER);

            assertNull(pheromoneManager.getPheromoneAt(pos, PheromoneType.GATHER));
            assertNotNull(pheromoneManager.getPheromoneAt(pos, PheromoneType.ATTACK));
        }

        @Test
        @DisplayName("should cascade remove only same type downstream")
        void shouldCascadeRemoveOnlySameType() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            // Create GATHER chain
            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);

            // Also add ATTACK at pos1
            pheromoneManager.addPheromone(pos1, PheromoneType.ATTACK);

            // Remove GATHER at pos1: should cascade remove GATHER at pos2, pos3
            pheromoneManager.removePheromone(pos1, PheromoneType.GATHER);

            assertNull(pheromoneManager.getPheromoneAt(pos1, PheromoneType.GATHER));
            assertNull(pheromoneManager.getPheromoneAt(pos2, PheromoneType.GATHER));
            assertNull(pheromoneManager.getPheromoneAt(pos3, PheromoneType.GATHER));
            // ATTACK should still exist
            assertNotNull(pheromoneManager.getPheromoneAt(pos1, PheromoneType.ATTACK));
        }

        @Test
        @DisplayName("should remove all types with removeAllPheromones")
        void shouldRemoveAllTypes() {
            GridPoint2 pos = getAdjacentToColony();
            pheromoneManager.addPheromone(pos, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos, PheromoneType.ATTACK);

            pheromoneManager.removeAllPheromones(pos);

            assertTrue(pheromoneManager.getPheromonesAt(pos).isEmpty());
        }
    }

    @Nested
    @DisplayName("Distance Propagation")
    class DistancePropagationTests {

        @Test
        @DisplayName("should propagate distances only within same type")
        void shouldPropagateThroughSameType() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);
            GridPoint2 pos3 = new GridPoint2(pos2.x + 1, pos2.y);

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos3, PheromoneType.GATHER);

            int d1 = pheromoneManager.getPheromoneAt(pos1, PheromoneType.GATHER).getDistance();
            int d2 = pheromoneManager.getPheromoneAt(pos2, PheromoneType.GATHER).getDistance();
            int d3 = pheromoneManager.getPheromoneAt(pos3, PheromoneType.GATHER).getDistance();

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
        @DisplayName("should return all pheromones")
        void shouldReturnAllPheromones() {
            GridPoint2 pos1 = getAdjacentToColony();
            GridPoint2 pos2 = new GridPoint2(pos1.x + 1, pos1.y);

            pheromoneManager.addPheromone(pos1, PheromoneType.GATHER);
            pheromoneManager.addPheromone(pos1, PheromoneType.ATTACK);
            pheromoneManager.addPheromone(pos2, PheromoneType.GATHER);

            Collection<Pheromone> pheromones = pheromoneManager.getPheromones();

            assertEquals(3, pheromones.size());
        }

        @Test
        @DisplayName("should return pheromones in 3x3 area")
        void shouldReturnPheromonesIn3x3Area() {
            GridPoint2 center = getAdjacentToColony();
            GridPoint2 right = new GridPoint2(center.x + 1, center.y);

            pheromoneManager.addPheromone(center, PheromoneType.GATHER);
            pheromoneManager.addPheromone(center, PheromoneType.ATTACK);
            pheromoneManager.addPheromone(right, PheromoneType.GATHER);

            List<Pheromone> pheromones = pheromoneManager.getPheromonesIn3x3(center);

            assertEquals(3, pheromones.size());
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
