package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import se.chalmers.tda367.team15.game.controller.PheromoneController;
import se.chalmers.tda367.team15.game.model.Pheromone;
import se.chalmers.tda367.team15.game.model.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.PheromoneType;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;

public class HUDView implements ViewportObserver {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final CameraModel cameraModel;
    private final CameraView cameraView;
    private final OrthographicCamera hudCamera;
    private final Stage stage;
    private final Skin skin;
    private PheromoneController pheromoneController;
    private PheromoneSystem pheromoneSystem;
    private static final float MIN_ZOOM_FOR_TEXT = 1.5f;

    public HUDView(CameraModel cameraModel, CameraView cameraView, OrthographicCamera hudCamera) {
        this.cameraModel = cameraModel;
        this.cameraView = cameraView;
        this.hudCamera = hudCamera;
        this.batch = new SpriteBatch();
        this.batch.setProjectionMatrix(hudCamera.combined);
        this.font = new BitmapFont();
        
        // Create stage with viewport matching screen coordinates
        // Stage uses ScreenViewport by default which handles screen coordinates
        this.stage = new Stage();
        // Update viewport to match current screen size
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        this.skin = new Skin();
        this.skin.add("default", font);
        
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        this.skin.add("default", buttonStyle);
    }

    public void setPheromoneController(PheromoneController controller) {
        this.pheromoneController = controller;
        setupButtons();
    }

    public void setPheromoneSystem(PheromoneSystem pheromoneSystem) {
        this.pheromoneSystem = pheromoneSystem;
    }

    private void setupButtons() {
        stage.clear();
        
        float buttonWidth = 120f;
        float buttonHeight = 40f;
        float spacing = 10f;
        float startX = 10f;
        float startY = Gdx.graphics.getHeight() - buttonHeight - 10f;

        // Gather button
        TextButton gatherButton = new TextButton("Gather", skin);
        gatherButton.setPosition(startX, startY);
        gatherButton.setSize(buttonWidth, buttonHeight);
        gatherButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pheromoneController != null) {
                    pheromoneController.setCurrentType(PheromoneType.GATHER);
                }
            }
        });
        stage.addActor(gatherButton);

        // Attack button
        TextButton attackButton = new TextButton("Attack", skin);
        attackButton.setPosition(startX + buttonWidth + spacing, startY);
        attackButton.setSize(buttonWidth, buttonHeight);
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pheromoneController != null) {
                    pheromoneController.setCurrentType(PheromoneType.ATTACK);
                }
            }
        });
        stage.addActor(attackButton);

        // Explore button
        TextButton exploreButton = new TextButton("Explore", skin);
        exploreButton.setPosition(startX + 2 * (buttonWidth + spacing), startY);
        exploreButton.setSize(buttonWidth, buttonHeight);
        exploreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pheromoneController != null) {
                    pheromoneController.setCurrentType(PheromoneType.EXPLORE);
                }
            }
        });
        stage.addActor(exploreButton);

        // Delete button
        TextButton deleteButton = new TextButton("Delete", skin);
        deleteButton.setPosition(startX + 3 * (buttonWidth + spacing), startY);
        deleteButton.setSize(buttonWidth, buttonHeight);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pheromoneController != null) {
                    pheromoneController.setDeleteMode(true);
                }
            }
        });
        stage.addActor(deleteButton);
    }

    public void render() {
        batch.begin();
        
        font.setColor(Color.WHITE);
        float y = Gdx.graphics.getHeight();
        float lineHeight = 25f;

        // camera debug info, just for testing, we will remove this later! (Debug)
        y -= 10f;
        font.draw(batch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 10, y);
        y -= lineHeight;
        font.draw(batch, String.format("Camera xy: (%.1f, %.1f)",
                cameraModel.getPosition().x, cameraModel.getPosition().y), 10, y);
        y -= lineHeight;

        font.draw(batch, String.format("Zoom: %.2f", cameraModel.getZoom()), 10, y);
        y -= lineHeight;

        font.draw(batch, String.format("Viewport: %.1f x %.1f",
                cameraView.getViewportSize().x, cameraView.getViewportSize().y), 10, y);
        y -= lineHeight;
        // end of debug info
        
        batch.end();
        
        // Render pheromone distance labels if zoom is high enough
        if (pheromoneSystem != null && cameraModel.getZoom() > MIN_ZOOM_FOR_TEXT) {
            renderPheromoneLabels();
        }
        
        // Render buttons
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void renderPheromoneLabels() {
        batch.begin();
        font.setColor(Color.WHITE);
        
        float screenHeight = Gdx.graphics.getHeight();
        GlyphLayout layout = new GlyphLayout();
        for (Pheromone pheromone : pheromoneSystem.getPheromones()) {
            // Get world position (center of 1x1 square)
            com.badlogic.gdx.math.GridPoint2 gridPos = pheromone.getPosition();
            com.badlogic.gdx.math.Vector2 worldPos = new com.badlogic.gdx.math.Vector2(gridPos.x + 0.5f, gridPos.y + 0.5f);
            
            // Convert world position to screen coordinates (already in screen space with Y=0 at top)
            com.badlogic.gdx.math.Vector2 screenPos = cameraView.worldToScreen(worldPos);
            
            // Convert from screen coordinates (Y=0 at top) to HUD camera coordinates (Y=0 at bottom)
            float hudY = screenHeight - screenPos.y;
            
            // Draw text at screen position
            String distanceText = String.valueOf(pheromone.getDistance());
            layout.setText(font, distanceText);
            // Center the text
            float textX = screenPos.x - layout.width / 2f;
            float textY = hudY + layout.height / 2f;
            font.draw(batch, distanceText, textX, textY);
        }
        
        batch.end();
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void onViewportResize(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        stage.getViewport().update(width, height, true);
        if (pheromoneController != null) {
            setupButtons();
        }
    }
}

