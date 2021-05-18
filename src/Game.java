import static java.lang.System.out;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Game extends JPanel implements ActionListener {
  private Timer t;

  private static final double DELAY = 1000 / 30;
  public final List<GameObject> DRAWABLES;
  public final List<Runnable> CALL;

  public Game() {

    t = new Timer((int) DELAY, this);
    DRAWABLES = new ArrayList<>();
    CALL = new ArrayList<>();
    //DRAWABLES.add(GameSprite.createFrom("icons\\Reload.png", 1))
  }

  public void start() {
    t.start();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D graphics = (Graphics2D) g;
    int min = 0;
    int max = 0;
    for (int i = 0; i < DRAWABLES.size(); i++) {
      var current = DRAWABLES.get(i);
      if (current.getLayer() > max)
        max = current.getLayer();
      if (current.getLayer() < min)
        min = current.getLayer();
    }
    for (int layer = min; layer <= max; layer = nextSmallest(layer, max)) {
      for (int j = 0; j < DRAWABLES.size(); j++) {
        var next = DRAWABLES.get(j);
        if (next.getLayer() == layer) {
          next.draw(graphics);
        }
      }
    }
  }

  private int nextSmallest(int min, int max) {
    int curMin = max + 1;
    for (int i = 0; i < DRAWABLES.size(); i++) {
      GameObject next = DRAWABLES.get(i);
      if (next.getLayer() > min && next.getLayer() < curMin) {
        curMin = next.getLayer();
      }
    }
    return curMin;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateAll();
    processCalls();
    repaint();
  }

  private void processCalls() {
    for (Runnable runnable : CALL) {
      runnable.run();
    }
    CALL.clear();
  }

  private void updateAll() {
    for (GameObject gameObject : DRAWABLES) {
      gameObject.update();
    }
  }

  public void addCall(Runnable r) {
    CALL.add(r);
  }

}
