package se.chalmers.tda367.team15.game.view.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import se.chalmers.tda367.team15.game.controller.CoordinateConverter;
import se.chalmers.tda367.team15.game.model.camera.CameraModel;

// Implements the CoordinateConverter interface, so we can use it in the CameraController without depending on the view code.
public class CameraView implements CoordinateConverter, ViewportObserver {
  private OrthographicCamera camera;
  private CameraModel model;
  private Vector2 viewportSize;
  private final float worldViewportWidth;

  public CameraView(CameraModel model, float viewportWidth, float viewportHeight) {
    this.model = model;
    this.worldViewportWidth = viewportWidth;

    this.camera = new OrthographicCamera(0, 0);
    this.setViewport(viewportWidth, viewportHeight);
  }

  public void updateCamera() {
    camera.position.set(new Vector3(model.getPosition(), 0));
    camera.zoom = 1f / model.getZoom();
    camera.update();
  }

  public void setViewport(float width, float height) {
    this.viewportSize = new Vector2(width, height);
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    updateCamera();
  }
  
  public Matrix4 getCombinedMatrix() {
    return camera.combined;
  }

  @Override
  public Vector2 getViewportSize() {
    return viewportSize.cpy();
  }

  /**
   * Gets the effective viewport size (what we actually see in world coordinates after zoom).
   * @return Effective viewport size in world units
   */
  public Vector2 getEffectiveViewportSize() {
    return viewportSize.cpy().scl(1f / model.getZoom());
  }

  public Vector2 getPosition() {
    return model.getPosition();
  }

  public float getZoom() {
    return model.getZoom();
  }

  @Override
  public Vector2 screenToWorld(Vector2 screenPos) {
    Vector3 worldPos = camera.unproject(new Vector3(screenPos, 0));
    return new Vector2(worldPos.x, worldPos.y);
  }

  /**
   * Converts world coordinates to screen coordinates.
   * @param worldPos World position
   * @return Screen coordinates in pixels (Y=0 at top, matching LibGDX screen input)
   */
  public Vector2 worldToScreen(Vector2 worldPos) {
    // Use the actual screen viewport size for projection
    float screenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
    float screenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();
    
    // Project to screen coordinates (camera.project returns Y=0 at bottom, OpenGL convention)
    Vector3 screenPos = camera.project(new Vector3(worldPos, 0), 0, 0, screenWidth, screenHeight);
    
    // Convert from OpenGL coordinates (Y=0 at bottom) to LibGDX screen coordinates (Y=0 at top)
    return new Vector2(screenPos.x, screenHeight - screenPos.y);
  }

  @Override
  public Vector2 screenDeltaToWorldDelta(Vector2 screenDelta, Vector2 screenSize) {
    Vector2 effectiveViewportSize = getEffectiveViewportSize();
    Vector2 worldDelta = screenDelta.cpy();
    worldDelta.scl(effectiveViewportSize.x / screenSize.x,
            effectiveViewportSize.y / screenSize.y);
    return worldDelta;
  }

  @Override
  public void onViewportResize(int width, int height) {
    float aspectRatio = (float) height / (float) width;
    setViewport(worldViewportWidth, worldViewportWidth * aspectRatio);
  }
}
