package Base;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import Helpers.Vector2;
import Objects.GameBall;

public class Camera {
  private AffineTransform pos;
  private double targetScale;
  private Vector2 target;

  public Camera(Vector2 pos) {
    super();
    this.pos = new AffineTransform();
    targetScale = 1;
    this.target = pos;
  }

  public void adjustCamera(Graphics2D graphics) {
    Vector2 posChange = target.subtracted(Vector2.v(pos.getTranslateX(), pos.getTranslateY())).dividedBy(20);
    // posChange.invert();
    pos.translate(posChange.x, posChange.y);
    double scaleChange = (targetScale - pos.getScaleX()) / 10;
    pos.scale(scaleChange + pos.getScaleX(), scaleChange + pos.getScaleY());
    graphics.setTransform(pos);
  }

  public void updateTarget(Game game, GameBall ball) {
    var ballBounds = ball.getRelativeShape().getBounds2D();
    setCenter(game, new Vector2(ballBounds.getCenterX(), ballBounds.getCenterY()).invert());

  }

  private void setCenter(Game game, Vector2 newTarget) {
    newTarget.x +=game.getCanvas().getWidth()/2;
    newTarget.y += game.getCanvas().getHeight() / 2;
    target = newTarget;
  }

  public void setTarget(Vector2 target) {
    this.target = target;
  }

  public Vector2 getTarget() {
    return target;
  }

  public double getTargetScale() {
    return targetScale;
  }

  public void setTargetFromWindow(Vector2 target) {
    pos.transform(target, this.target);
  }

  public void setTargetScale(double targetScale) {
    this.targetScale = targetScale;
  }

  public AffineTransform getPos() {
    return pos;
  }
}
