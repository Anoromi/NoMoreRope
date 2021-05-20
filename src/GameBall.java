import java.awt.geom.AffineTransform;
import java.util.List;

import Helpers.ImageHelper;

public class GameBall extends GameSprite {
  protected RigidBody rigidBody;

  public GameBall(String path, int layer) {
    super(path, layer);

    rigidBody = new PointRigidBody(GameSettings.LOSS) {
      @Override
      public AffineTransform getTransform() {
        return transform.getTransform();
      }

    };
    addProperty(ObjectProperty.RigidBody, rigidBody);

    addTags(ObjectTag.Touchable);
  }

  @Override
  public void update(Game game) {
    super.update(game);
    processCollisions(game);
  }

  private void processCollisions(Game game) {
    if (!game.checkForCollision(this))
      return;
    double ballRadius = image.getWidth() / 2;
    double distanceToRadius = (ballRadius + 1) / Math.cos(3.14 / ballRadius / 2);
    double pointSumX = 0, pointSumY = 0;
    int colliderEdges = GameSettings.COLLIDER_EDGES;
    int counter = 0;
    boolean coll = false;
    for (double theta = -90; theta < 270; theta += 360.0 / colliderEdges) {
      Vector2 collision = Vector2.v(ballRadius + distanceToRadius * Math.cos(theta * 3.14 / 180),
          ballRadius + distanceToRadius * Math.sin(theta * 3.14 / 180));
      Vector2 transformed = new Vector2();
      transform.getTransform().transform(collision, transformed);
      List<GameObject> collided = game.getElementsAt(transformed);
      if (collided.stream().parallel().anyMatch(x -> x.hasTags(ObjectTag.Touchable))) {
        pointSumX += collision.x;
        pointSumY += collision.y;
        counter++;
      }
    }
    if (counter == 0)
      return;
    switchDirection(new Vector2(pointSumX / counter, pointSumY / counter), ballRadius);
  }

  private void switchDirection(Vector2 collisionPoint, double ballRadius) {
    Vector2 realPos = new Vector2();
    transform.getTransform().transform(new Vector2(0, 0), realPos);
    Vector2 nCollVect = new Vector2(collisionPoint.x - ballRadius, collisionPoint.y - ballRadius).normalized();
    Vector2 nMoveDir = rigidBody.getAcceleration().normalized();
    double cosine = Math.abs(nCollVect.x * nMoveDir.x + nCollVect.y * nMoveDir.y);
    nCollVect.multiplyBy(cosine * 2);
    Vector2 move = new Vector2(nMoveDir.x - nCollVect.x, nMoveDir.y - nCollVect.y).normalized();
    move.multiplyBy(rigidBody.getAcceleration().magnitude());
    rigidBody.setAcceleration(move);
  }
}
