/*
File: BulletTurret.java
Author: Danylo Nechyporchuk
Task: make a class which describe bullet turret. An enemy which shoots high speed bullets, when ball is nearby.
Always look at the ball
 */

package Objects.Entities;

import Base.Game;
import Helpers.ImageHelper;
import Helpers.Vector2;
import Objects.GameSprite;
import Objects.ObjectTag;
import Properties.Mesh;
import Properties.ObjectProperty;
import Properties.PointRigidBody;
import Properties.RigidBody;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class BulletTurret extends GameSprite {
    public static final BufferedImage BULLET_TURRET_IMAGE = ImageHelper.imageOrNull("icons/BulletTurret.png");

    private GameBall ball;
    private Rectangle2D ballBounds;

    private Rectangle2D turretBounds;
    private final Vector2 turretCenter;

    private Bullet bullet;
    private boolean bulletOnScreen = false;
    private boolean tmpBoolean;

    private double prevAngle = 157;

    public BulletTurret(Game game, double x, double y) {
        super(BULLET_TURRET_IMAGE, 3);
        addTags(ObjectTag.Danger);
        addTags(ObjectTag.Touchable);
        setPosition(new Vector2(x, y));
        turretCenter = new Vector2(getMesh().getRelativeRectangleBounds().getBounds2D().getCenterX(),
                getMesh().getRelativeRectangleBounds().getBounds2D().getCenterY());

        rotateTurret(game);
        if (ballInSight(game))
            openFire(game);
    }

    @Override
    public void update(Game game) {
        super.update(game);
        rotateTurret(game);
//        if (ballInSight(game))
//         openFire(game);
    }

    /**
     * Rotate the turret to make a barrel look at the ball
     *
     * @param game object of a class Game to get info about the ball
     */
    private void rotateTurret(Game game) {
        ball = (GameBall) game.DRAWABLES.get(0);

        ballBounds = ball.getMesh().getRelativeRectangleBounds().getBounds2D();
        turretBounds = getMesh().getRelativeRectangleBounds().getBounds2D();

        AffineTransform at = transform.getFullAffine();
        double angle = -(Math.atan2(turretCenter.getY() - ballBounds.getCenterY(), ballBounds.getCenterX() - turretCenter.getX()));
        angle = angle - prevAngle;
        if (angle != 0) {
            at.rotate(angle, BULLET_TURRET_IMAGE.getWidth() / 2.0, BULLET_TURRET_IMAGE.getHeight() / 2.0);
            prevAngle = angle + prevAngle;
        }
    }

    /**
     * Check if there is a ball in sight of turret to open fire
     *
     * @return true if ball is in sight
     */
    private boolean ballInSight(Game game) {
        tmpBoolean = true;

        double distance = new Vector2(ballBounds.getCenterX(), ballBounds.getCenterY()).
                subtract(turretCenter).magnitude();

        Rectangle line = new Rectangle((int) turretBounds.getWidth()/2, (int) turretBounds.getHeight()/2,
                (int) distance, 1);
        Shape checkLine = getTransform().getFullAffine().createTransformedShape(line.getBounds2D());
        Mesh lineMesh = new Mesh(checkLine) {
            @Override
            protected AffineTransform getTransform() {
                return new AffineTransform();
            }
        };

        game.DRAWABLES.forEach(n -> {
            if (n.getProperty(ObjectProperty.Mesh) != null &&
                ((Mesh) n.getProperty(ObjectProperty.Mesh)).intersects(lineMesh) && !n.equals(ball) && !n.equals(this))
                //if (n.intersects(checkLine) && !n.equals(ball) && !n.equals(this))
                tmpBoolean = false;
        });
        return tmpBoolean;
    }

    /**
     * Shoot some bullets which are deadly for the ball
     */
    private void openFire(Game game) {
        if (bulletOnScreen)
            return;

        initBullet();

        game.DRAWABLES.add(bullet);

        bulletOnScreen = !bulletOnScreen;
    }

    /**
     * Create a bullet which will move towards the ball
     */
    private void initBullet() {
        Vector2 ballVector = new Vector2(ballBounds.getCenterX(), ballBounds.getCenterY());
        //Shape tmpRect = getTransform().getFullAffine().createTransformedShape(new Rectangle(0, 0, 1, 1).getBounds2D());

        bullet = new Bullet(this, turretCenter, ballVector);
    }

    public void setBulletOnScreen(boolean bulletOnScreen) {
        this.bulletOnScreen = bulletOnScreen;
    }

    public boolean isBulletOnScreen() {
        return bulletOnScreen;
    }
}

class Bullet extends GameSprite {
    static final BufferedImage BULLET_IMAGE =
            ImageHelper.rescale(ImageHelper.imageOrNull("icons/Bullet.png"), 20, 20);

    private RigidBody rigidBody;
    private BulletTurret turret;
    private boolean wasDestroyed = false;

    public Bullet(BulletTurret turret, Vector2 position, Vector2 direction) {
        super(BULLET_IMAGE, 2);
        this.turret = turret;
        addTags(ObjectTag.Danger);
        addTags(ObjectTag.Touchable);

        setPosition(new Vector2(position.x, position.y));

        rigidBody = new PointRigidBody(1) {
            @Override
            public AffineTransform getTransform() {
                return transform.getFullAffine();
            }

        };
        rigidBody.impulse(direction.subtract(position).normalized().multipliedBy(1));

        addProperty(ObjectProperty.RigidBody, rigidBody);
    }

    @Override
    public void update(Game game) {
        super.update(game);
        processCollisions(game);
    }


    /**
     * Remove the bullet if it collide any Touchable object
     *
     * @param game object of a class Game to get info about collisions
     */
    private void processCollisions(Game game) {
        if (!game.checkForCollision(mesh))
            return;
        //game.DRAWABLES.remove(game.DRAWABLES.size() - 1);
        turret.setBulletOnScreen(false);
        wasDestroyed = true;
    }
}
