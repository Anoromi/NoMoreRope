package Objects.Entities;

import java.awt.image.BufferedImage;

import Helpers.ImageHelper;
import Objects.GameSprite;

/**
 * Defines a goal you need to reach.
 * File: Goal.java
 * @author Andrii Zahorulko
 */
public class Goal extends GameSprite {

  public static BufferedImage GOAL_IMAGE = ImageHelper.imageOrNull("icons/Goal.png");

  public Goal() {
    super(GOAL_IMAGE, 4);
  }

}
