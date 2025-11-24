package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import se.chalmers.tda367.team15.game.model.CameraModel;

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

  public OrthographicCamera getCamera() {
    return camera;
  }

  public Vector2 getViewportSize() {
    return viewportSize.cpy();
  }

  /**
   * Converts screen coordinates to world coordinates.
   * @param screenX Screen X coordinate (pixels)
   * @param screenY Screen Y coordinate (pixels)
   * @return World coordinates
   */
  public Vector2 screenToWorld(Vector2 screenPos) {
    Vector3 worldPos = camera.unproject(new Vector3(screenPos, 0));
    return new Vector2(worldPos.x, worldPos.y);
  }
}
