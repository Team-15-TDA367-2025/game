package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;
import se.chalmers.tda367.team15.game.model.pheromones.Pheromone;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneGridConverter;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;
import se.chalmers.tda367.team15.game.view.camera.CameraView;

public class PheromoneRenderer {
    private final SpriteBatch batch;
    private final ShaderProgram shader;
    private final CameraView cameraView;
    private final PheromoneManager pheromoneManager;
    private final PheromoneGridConverter converter;
    private final Texture pheromoneTexture;
    private final TextureRegion pheromoneRegion;
    private float time = 0f;
    
    // Reusable color to avoid allocations
    private final Color tempColor = new Color();

    public PheromoneRenderer(CameraView cameraView, PheromoneManager pheromoneManager) {
        this.cameraView = cameraView;
        this.pheromoneManager = pheromoneManager;
        this.converter = pheromoneManager.getConverter();
        this.batch = new SpriteBatch();
        
        // Create a simple white texture with proper UV coordinates (not from atlas)
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        pheromoneTexture = new Texture(pixmap);
        pheromoneRegion = new TextureRegion(pheromoneTexture, 0, 0, 2, 2);
        pixmap.dispose();
        
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
            Gdx.files.internal("shaders/pheromone.vert"),
            Gdx.files.internal("shaders/pheromone.frag")
        );
        
        if (!shader.isCompiled()) {
            Gdx.app.error("PheromoneRenderer", "Shader compilation failed: " + shader.getLog());
        }
        
        batch.setShader(shader);
    }

    public void render() {
        time += Gdx.graphics.getDeltaTime();
        
        Matrix4 projectionMatrix = cameraView.getCombinedMatrix();
        batch.setProjectionMatrix(projectionMatrix);
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        batch.begin();
        
        // Set uniforms once before drawing
        shader.setUniformf("u_time", time);

        // Find max distance for normalization
        int maxDistance = 1;
        for (Pheromone pheromone : pheromoneManager.getPheromones()) {
            maxDistance = Math.max(maxDistance, pheromone.getDistance());
        }

        float cellSize = converter.getPheromoneCellSize();
        // Draw at 4x size so balls can overflow into neighboring cells
        float drawSize = cellSize * 4f;
        
        for (Pheromone pheromone : pheromoneManager.getPheromones()) {
            float distanceRatio = (float) pheromone.getDistance() / maxDistance;
            
            // Trail strength: 1.0 at start, 0.0 at end
            float trailStrength = 1.0f - distanceRatio;
            
            // Pack: R = type index, G = trail strength, B = random seed, A = 1.0
            int typeIndex = getTypeIndex(pheromone.getType());
            
            // Generate deterministic random seed from grid position
            GridPoint2 gridPos = pheromone.getPosition();
            float randomSeed = ((gridPos.x * 73856093) ^ (gridPos.y * 19349663)) / (float)Integer.MAX_VALUE;
            randomSeed = Math.abs(randomSeed);
            
            tempColor.set(typeIndex / 3.0f, trailStrength, randomSeed, 1.0f);
            
            batch.setColor(tempColor);
            Vector2 worldPos = converter.pheromoneGridToWorld(gridPos);

            float thisDrawSize = ((float) Math.log(trailStrength + 1) + 0.5f) * drawSize;
            
            batch.draw(pheromoneRegion, 
                worldPos.x - thisDrawSize / 2f, 
                worldPos.y - thisDrawSize / 2f, 
                thisDrawSize, 
                thisDrawSize);
        }

        batch.end();
    }

    private int getTypeIndex(PheromoneType type) {
        switch (type) {
            case GATHER: return 0;
            case ATTACK: return 1;
            case EXPLORE: return 2;
            default: return 0;
        }
    }

    public void dispose() {
        batch.dispose();
        shader.dispose();
        pheromoneTexture.dispose();
    }
}
