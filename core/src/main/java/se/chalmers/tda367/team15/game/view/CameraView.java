package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import se.chalmers.tda367.team15.game.model.camera.CameraModel;

public class CameraView {
  private OrthographicCamera camera;
  private CameraModel model;
  private Vector2 viewportSize;

  public CameraView(CameraModel model, float viewportWidth, float viewportHeight) {
    this.model = model;

    // Create camera with aspect ratio consideration
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

  /**
   * Gets the camera position in world coordinates.
   * @return Camera position as Vector2
   */
  public Vector2 getCameraPosition() {
    return new Vector2(camera.position.x, camera.position.y);
  }

  /**
   * Converts screen coordinates to world coordinates using LibGDX's camera unproject.
   * @param screenPos Screen position in pixels
   * @return World coordinates
   */
  public Vector2 screenToWorld(Vector2 screenPos) {
    Vector3 worldPos = camera.unproject(new Vector3(screenPos, 0));
    return new Vector2(worldPos.x, worldPos.y);
  }

  /**
   * Converts a screen delta (pixel movement) to world delta (world unit movement).
   * 
   * @param screenDelta Screen delta in pixels (typically from mouse movement)
   * @param screenSize Screen size in pixels
   * @return World delta in world units
   */
  public Vector2 screenDeltaToWorldDelta(Vector2 screenDelta, Vector2 screenSize) {
    Vector2 effectiveViewportSize = getEffectiveViewportSize();
    Vector2 worldDelta = screenDelta.cpy();
    worldDelta.scl(effectiveViewportSize.x / screenSize.x,
            effectiveViewportSize.y / screenSize.y);
    return worldDelta;
  }

  /**
   * Zooms around a specific screen point, keeping that point fixed in world space.
   * 
   * @param screenPos Screen position in pixels (the point to zoom around)
   * @param scrollAmount Scroll amount (positive = zoom in, negative = zoom out)
   * @param zoomSpeed Zoom speed multiplier
   * @param screenSize Screen size in pixels
   */
  public void zoomAround(Vector2 screenPos, float scrollAmount, float zoomSpeed, Vector2 screenSize) {
    Vector2 worldPosBeforeZoom = screenToWorld(screenPos);

    float zoomMultiplier = 1f + (-scrollAmount * zoomSpeed);
    float newZoom = model.getZoom() * zoomMultiplier;
    model.zoomTo(newZoom);
    updateCamera();

    Vector2 worldPosAfterZoom = screenToWorld(screenPos);

    Vector2 offset = worldPosBeforeZoom.cpy().sub(worldPosAfterZoom);
    model.moveBy(offset);
  }
}
