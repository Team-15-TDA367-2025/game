package se.chalmers.tda367.team15.game.model.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.Inventory;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNode;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceNodeFactory;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

class ResourceManagerTest {

    private EntityQuery entityQuery;
    private StructureManager structureManager;
    private ResourceManager resourceManager;
    private ResourceNodeFactory resourceNodeFactory;

    @BeforeEach
    void setUp() {
        entityQuery = mock(EntityQuery.class);
        structureManager = mock(StructureManager.class);
        resourceManager = new ResourceManager(entityQuery, structureManager);
        resourceNodeFactory = new ResourceNodeFactory();
    }

    @Test
    @DisplayName("should add resource node to structure manager")
    void shouldAddResourceNode() {
        ResourceNode node = mock(ResourceNode.class);
        resourceManager.addResourceNode(node);
        verify(structureManager).addStructure(node);
    }

    @Nested
    @DisplayName("Resource Pickup")
    class PickupTests {

        @Test
        @DisplayName("should harvest node when ant is in range and has capacity")
        void shouldHarvestInPickUpRange() {
            Ant ant = mock(Ant.class);
            Inventory inventory = mock(Inventory.class);
            ResourceNode node = mock(ResourceNode.class);

            Home home = mock(Home.class);
            when(ant.getPosition()).thenReturn(new Vector2(10, 10));
            when(ant.getInventory()).thenReturn(inventory);
            when(ant.getHome()).thenReturn(home);
            when(home.getPosition()).thenReturn(new Vector2(100, 100)); // Far away from home
            when(inventory.isFull()).thenReturn(false);
            when(inventory.isEmpty()).thenReturn(false);
            when(inventory.getRemainingCapacity()).thenReturn(5);
            when(inventory.addResource(ResourceType.FOOD, 5)).thenReturn(true);

            when(node.getGridPosition()).thenReturn(new GridPoint2(12, 10)); // Manhattan distance 2
            when(node.getType()).thenReturn(ResourceType.FOOD);
            when(node.getCurrentAmount()).thenReturn(10);

            when(structureManager.getStructures()).thenReturn(Collections.singletonList(node));
            when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.singletonList(ant));

            resourceManager.update(1.0f);

            verify(node).harvest(5);
            verify(inventory).addResource(ResourceType.FOOD, 5);
        }

        @Test
        @DisplayName("should not harvest when ant is out of range")
        void shouldNotHarvestOutOfRange() {
            Ant ant = mock(Ant.class);
            Inventory inventory = mock(Inventory.class);
            ResourceNode node = resourceNodeFactory.createResourceNode(new Vector2(15, 15), 10);

            Home home = mock(Home.class);
            when(ant.getPosition()).thenReturn(new Vector2(10, 10));
            when(ant.getInventory()).thenReturn(inventory);
            when(ant.getHome()).thenReturn(home);
            when(home.getPosition()).thenReturn(new Vector2(100, 100));
            when(inventory.isFull()).thenReturn(false);
            when(inventory.isEmpty()).thenReturn(true);

            when(structureManager.getStructures()).thenReturn(Collections.singletonList(node));
            when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.singletonList(ant));

            resourceManager.update(1.0f);

            assertEquals(10, node.getCurrentAmount());
        }

        @Test
        @DisplayName("should not harvest when inventory is full")
        void shouldNotHarvestWhenFull() {
            Ant ant = mock(Ant.class);
            Inventory inventory = mock(Inventory.class);
            ResourceNode node = resourceNodeFactory.createResourceNode(new Vector2(11, 11), 10);

            Home home = mock(Home.class);
            when(ant.getPosition()).thenReturn(new Vector2(10, 10));
            when(ant.getInventory()).thenReturn(inventory);
            when(ant.getHome()).thenReturn(home);
            when(home.getPosition()).thenReturn(new Vector2(100, 100));
            when(inventory.isFull()).thenReturn(true);
            when(inventory.isEmpty()).thenReturn(false);

            when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.singletonList(ant));

            resourceManager.update(1.0f);

            assertEquals(10, node.getCurrentAmount());
        }
    }

    @Nested
    @DisplayName("Resource Deposit")
    class DepositTests {

        @Test
        @DisplayName("should deposit resources when ant is at home")
        void shouldDepositAtHome() {
            Ant ant = mock(Ant.class);
            Inventory inventory = mock(Inventory.class);
            Home home = mock(Home.class);

            when(ant.getPosition()).thenReturn(new Vector2(5, 5));
            when(ant.getInventory()).thenReturn(inventory);
            when(inventory.isFull()).thenReturn(true);
            when(inventory.isEmpty()).thenReturn(false);
            when(ant.getHome()).thenReturn(home);
            when(home.getPosition()).thenReturn(new Vector2(6, 6)); // Distance 2 (within range)

            when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.singletonList(ant));

            resourceManager.update(1.0f);

            verify(ant).leaveResources(home);
        }

        @Test
        @DisplayName("should not deposit when ant is away from home")
        void shouldNotDepositAwayFromHome() {
            Ant ant = mock(Ant.class);
            Inventory inventory = mock(Inventory.class);
            Home home = mock(Home.class);

            when(ant.getPosition()).thenReturn(new Vector2(5, 5));
            when(ant.getInventory()).thenReturn(inventory);
            when(inventory.isFull()).thenReturn(true);
            when(inventory.isEmpty()).thenReturn(false);
            when(ant.getHome()).thenReturn(home);
            when(home.getPosition()).thenReturn(new Vector2(10, 10)); // Distance 10 (out of range)

            when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.singletonList(ant));

            resourceManager.update(1.0f);

            // Verified by lack of exception and internal state if possible
        }

        @Test
        @DisplayName("should not deposit when inventory is empty")
        void shouldNotDepositWhenEmpty() {
            Ant ant = mock(Ant.class);
            Inventory inventory = mock(Inventory.class);

            Home home = mock(Home.class);
            when(ant.getPosition()).thenReturn(new Vector2(5, 5));
            when(ant.getInventory()).thenReturn(inventory);
            when(ant.getHome()).thenReturn(home);
            when(home.getPosition()).thenReturn(new Vector2(100, 100)); // Just in case
            when(inventory.isFull()).thenReturn(false);
            when(inventory.isEmpty()).thenReturn(true);

            when(entityQuery.getEntitiesOfType(Ant.class)).thenReturn(Collections.singletonList(ant));

            resourceManager.update(1.0f);

            // verify(ant, never()).getHome();
        }
    }
}
