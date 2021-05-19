import java.awt.geom.AffineTransform;

public abstract class RigidGameObj extends GameObject implements RigidBody {
  private RigidBody rigidBody;

  protected RigidGameObj(double loss, int layer) {
    super(layer);
    rigidBody = new PointRigidBody(loss) {

      @Override
      public AffineTransform getTransform() {
        return RigidGameObj.this.getTransform();
      }

    };
  }

  @Override
  public void update(Game game) {
    updateForces();
  }

  @Override
  public void impulse(Vector2 vector) {
    rigidBody.impulse(vector);
  }

  @Override
  public void updateForces() {
    rigidBody.updateForces();
  }

  @Override
  public Vector2 getAcceleration() {
    return rigidBody.getAcceleration();
  }

  @Override
  public void setAcceleration(Vector2 acceleration) {
    rigidBody.setAcceleration(acceleration);
  }

  public abstract AffineTransform getTransform();
}
