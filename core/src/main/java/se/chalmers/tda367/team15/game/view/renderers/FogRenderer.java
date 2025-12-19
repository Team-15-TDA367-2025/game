package se.chalmers.tda367.team15.game.view.renderers;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;

import se.chalmers.tda367.team15.game.model.fog.FogProvider;
import se.chalmers.tda367.team15.game.model.interfaces.observers.FogObserver;
import se.chalmers.tda367.team15.game.view.camera.CameraView;
import se.chalmers.tda367.team15.game.view.camera.ViewportObserver;

public class FogRenderer implements FogObserver, ViewportObserver {
    private final SpriteBatch maskBatch;
    private final SpriteBatch fogBatch;
    private final ShaderProgram fogShader;
    private FrameBuffer fogMaskBuffer;
    private float time = 0f;
    private final FogProvider fogProvider;

    private Pixmap fogPixmap;
    private Texture fogMaskTexture;

    public FogRenderer(FogProvider fogProvider) {
        this.fogProvider = fogProvider;
        this.maskBatch = new SpriteBatch();
        this.fogBatch = new SpriteBatch();

        ShaderProgram.pedantic = false;
        fogShader = new ShaderProgram(
                Gdx.files.internal("shaders/fog.vert"),
                Gdx.files.internal("shaders/fog.frag"));

        fogBatch.setShader(fogShader);

        createFrameBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        updateFogMaskTexture(fogProvider);
    }

    private void createFrameBuffer(int width, int height) {
        if (width <= 0 || height <= 0)
            return;

        if (fogMaskBuffer != null) {
            fogMaskBuffer.dispose();
        }
        fogMaskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    }

    private void ensureFogTextureSize(int width, int height) {
        if (fogPixmap != null && fogPixmap.getWidth() == width && fogPixmap.getHeight() == height) {
            return;
        }

        if (fogPixmap != null) {
            fogPixmap.dispose();
        }

        if (fogMaskTexture != null) {
            fogMaskTexture.dispose();
        }

        fogPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        fogMaskTexture = new Texture(fogPixmap);
        fogMaskTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void resize(int width, int height) {
        createFrameBuffer(width, height);
    }

    public void render(Matrix4 worldProjectionMatrix, CameraView cameraView) {
        time += Gdx.graphics.getDeltaTime();

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Ensure framebuffer is the right size
        if (fogMaskBuffer == null ||
                fogMaskBuffer.getWidth() != screenWidth ||
                fogMaskBuffer.getHeight() != screenHeight) {
            createFrameBuffer(screenWidth, screenHeight);
        }

        renderFogMaskToFramebuffer(fogProvider, worldProjectionMatrix);
        renderFogOverlay(screenWidth, screenHeight, cameraView);
    }

    @Override
    public void onFogDirty() {
        updateFogMaskTexture(fogProvider);
    }

    @Override
    public void onViewportResize(int width, int height) {
        createFrameBuffer(width, height);
    }

    private void updateFogMaskTexture(FogProvider fogProvider) {
        GridPoint2 size = fogProvider.getSize();
        ensureFogTextureSize(size.x, size.y);

        boolean[][] discovered = fogProvider.getDiscoveredArray();

        // Direct pixel buffer manipulation - much faster than setColor/drawPixel
        ByteBuffer pixels = fogPixmap.getPixels();
        pixels.clear();

        // Fill pixmap with RGBA: white (255,255,255,255) = fog, transparent (0,0,0,0) =
        // revealed
        // Shader samples .r channel, so we need white for fog
        // Pixmap is Y-down (top-left origin), world is Y-up, so flip Y
        for (int y = 0; y < size.y; y++) {
            int flippedY = size.y - 1 - y;
            for (int x = 0; x < size.x; x++) {
                if (discovered[x][flippedY]) {
                    // Revealed - transparent black
                    pixels.put((byte) 0); // R
                    pixels.put((byte) 0); // G
                    pixels.put((byte) 0); // B
                    pixels.put((byte) 0); // A
                } else {
                    // Fog - opaque white
                    pixels.put((byte) 255); // R
                    pixels.put((byte) 255); // G
                    pixels.put((byte) 255); // B
                    pixels.put((byte) 255); // A
                }
            }
        }
        pixels.flip();

        // Upload to GPU
        fogMaskTexture.draw(fogPixmap, 0, 0);
    }

    private void renderFogMaskToFramebuffer(FogProvider fogProvider, Matrix4 worldProjectionMatrix) {
        fogMaskBuffer.begin();

        // Clear to transparent
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        maskBatch.setProjectionMatrix(worldProjectionMatrix);
        maskBatch.begin();

        GridPoint2 size = fogProvider.getSize();
        float offsetX = -size.x / 2f;
        float offsetY = -size.y / 2f;

        // Draw the entire fog mask as a single textured quad
        maskBatch.draw(fogMaskTexture, offsetX, offsetY, size.x, size.y);

        maskBatch.end();

        fogMaskBuffer.end();
    }

    private void renderFogOverlay(int screenWidth, int screenHeight, CameraView cameraView) {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Matrix4 screenProjection = new Matrix4();
        screenProjection.setToOrtho2D(0, 0, screenWidth, screenHeight);
        fogBatch.setProjectionMatrix(screenProjection);

        fogBatch.begin();

        fogShader.setUniformf("u_time", time);

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
                0, 0, // position
                screenWidth, screenHeight, // size
                0, 0, // srcX, srcY
                screenWidth, screenHeight, // srcWidth, srcHeight
                false, true); // flipX, flipY (flip Y for framebuffer)

        fogBatch.end();
    }

    public void dispose() {
        maskBatch.dispose();
        fogBatch.dispose();
        fogShader.dispose();
        if (fogMaskBuffer != null) {
            fogMaskBuffer.dispose();
        }
        if (fogPixmap != null) {
            fogPixmap.dispose();
        }
        if (fogMaskTexture != null) {
            fogMaskTexture.dispose();
        }
    }
}
