package se.chalmers.tda367.team15.game.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;

import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.view.camera.CameraView;

public class FogRenderer {
    private final TextureRegion pixelTexture;
    private final SpriteBatch maskBatch;
    private final SpriteBatch fogBatch;
    private final ShaderProgram fogShader;
    private FrameBuffer fogMaskBuffer;
    private float time = 0f;
    
    public FogRenderer(TextureRegion pixelTexture) {
        this.pixelTexture = pixelTexture;
        this.maskBatch = new SpriteBatch();
        this.fogBatch = new SpriteBatch();
        
        // Load shader
        ShaderProgram.pedantic = false;
        fogShader = new ShaderProgram(
            Gdx.files.internal("shaders/fog.vert"),
            Gdx.files.internal("shaders/fog.frag")
        );
        
        if (!fogShader.isCompiled()) {
            Gdx.app.error("FogRenderer", "Shader compilation failed: " + fogShader.getLog());
        } else {
            Gdx.app.log("FogRenderer", "Shader compiled successfully");
        }
        
        fogBatch.setShader(fogShader);
        
        // Create initial framebuffer
        createFrameBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    private void createFrameBuffer(int width, int height) {
        if (width <= 0 || height <= 0) return;
        
        if (fogMaskBuffer != null) {
            fogMaskBuffer.dispose();
        }
        fogMaskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    }
    
    public void resize(int width, int height) {
        createFrameBuffer(width, height);
    }

    public void render(FogProvider fogProvider, Matrix4 worldProjectionMatrix, CameraView cameraView) {
        time += Gdx.graphics.getDeltaTime();
        
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        
        // Ensure framebuffer is the right size
        if (fogMaskBuffer == null || 
            fogMaskBuffer.getWidth() != screenWidth || 
            fogMaskBuffer.getHeight() != screenHeight) {
            createFrameBuffer(screenWidth, screenHeight);
        }
        
        // Step 1: Render fog mask to framebuffer using WORLD projection
        renderFogMask(fogProvider, worldProjectionMatrix);
        
        // Step 2: Render the fog overlay in SCREEN SPACE with shader
        renderFogOverlay(screenWidth, screenHeight, cameraView);
    }
    
    private void renderFogMask(FogProvider fogProvider, Matrix4 worldProjectionMatrix) {
        fogMaskBuffer.begin();
        
        // Clear to black (revealed = black = 0)
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Use world projection to render fog tiles
        maskBatch.setProjectionMatrix(worldProjectionMatrix);
        maskBatch.begin();
        
        // Draw white where fog should be (unrevealed areas)
        maskBatch.setColor(1f, 1f, 1f, 1f);
        
        GridPoint2 size = fogProvider.getSize();
        float offsetX = -size.x / 2f;
        float offsetY = -size.y / 2f;

        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                if (!fogProvider.isDiscovered(new GridPoint2(x, y))) {
                    float worldX = x + offsetX;
                    float worldY = y + offsetY;
                    maskBatch.draw(pixelTexture, worldX, worldY, 1, 1);
                }
            }
        }
        
        maskBatch.end();
        fogMaskBuffer.end();
    }
    
    private void renderFogOverlay(int screenWidth, int screenHeight, CameraView cameraView) {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Set screen-space projection matrix to avoid z-fighting
        Matrix4 screenProjection = new Matrix4();
        screenProjection.setToOrtho2D(0, 0, screenWidth, screenHeight);
        fogBatch.setProjectionMatrix(screenProjection);
        
        fogBatch.begin();
        
        // Set shader uniforms AFTER begin() so they don't get overwritten
        fogShader.setUniformf("u_time", time);
        fogShader.setUniformf("u_resolution", (float) screenWidth, (float) screenHeight);
        
        // Pass camera/world info so shader can calculate world coordinates
        fogShader.setUniformf("u_cameraPos", cameraView.getPosition().x, cameraView.getPosition().y);
        fogShader.setUniformf("u_cameraZoom", cameraView.getZoom());
        fogShader.setUniformf("u_viewportSize", cameraView.getViewportSize().x, cameraView.getViewportSize().y);
        
        fogBatch.setColor(1f, 1f, 1f, 1f);
        
        // Get the framebuffer texture
        Texture maskTexture = fogMaskBuffer.getColorBufferTexture();
        
        // Draw fullscreen quad in screen space
        // Note: framebuffer texture is flipped, so we flip the V coordinates
        fogBatch.draw(maskTexture, 
            0, 0,                           // position
            screenWidth, screenHeight,      // size
            0, 0,                           // srcX, srcY
            screenWidth, screenHeight,      // srcWidth, srcHeight
            false, true);                   // flipX, flipY (flip Y for framebuffer)
        
        fogBatch.end();
    }
    
    public void dispose() {
        maskBatch.dispose();
        fogBatch.dispose();
        fogShader.dispose();
        if (fogMaskBuffer != null) {
            fogMaskBuffer.dispose();
        }
    }
}
