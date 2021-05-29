/*
File: LevelReader.java
Author: Danylo Nechyporchuk
Task: will create level if we have file *.lvl; file can have line with tags:

start{x,y} - coordinate of the start to create there a ball
background{file.png} - file of the level background(0 layer)
walls{file.png} - file which have walls(1 layer)
trap{x,y,r} - coordinates of the spikes(x,y) trap and rotation angle(r)
movingTrap{x1,y1,x2,y2,r} - coordinates of the moving spikes trap (from x1,y1 to x2,y2) and rotation angle(r)
jump{x,y,r} - coordinate of the jump pad (x,y) and rotation angle(r);
bullet{x,y} - coordinate of the bullet turret enemy
 */

package Helpers;

import Base.Game;
import Objects.Entities.*;
import Objects.GameSprite;
import Objects.ObjectTag;

import java.io.*;
import java.util.*;

public class LevelReader {
    private static ArrayList<File> LEVELS = new ArrayList<>();
    public static Vector2 startingPoint;

    static {
        File levelDirectory = new File("levels");
        Collections.addAll(LEVELS, levelDirectory.listFiles(pathname -> {
            if (pathname.getName().endsWith(".lvl"))
                return true;
            return false;
        }));
    }

    /**
     * Create level with all objects including background, ball, enemies, etc.
     *
     * @param game        object of class Game to fill DRAWABLES array
     * @param levelNumber number of level to create
     */
    public static void createLevel(Game game, int levelNumber) {
        File file = LEVELS.get(levelNumber - 1);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                processLevelInfo(game, line);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check line to add specific object to a game
     *
     * @param game object of class Game to fill DRAWABLES array
     * @param line which may contains info about the object
     */
    private static void processLevelInfo(Game game, String line) {
        if (line.equals("") || line.charAt(0) == ' ' || line.indexOf('{') == -1 || line.indexOf('}') == -1)
            return;
        int endOfNameChar = line.indexOf('{');
        String name = line.substring(0, endOfNameChar);

        String info = line.substring(endOfNameChar + 1, line.indexOf('}'));
        try {
            if (name.equals("start")) {
                int indexOfComma = info.indexOf(',');
                double x = Double.parseDouble(info.substring(0, indexOfComma));
                double y = Double.parseDouble(info.substring(indexOfComma + 1));

                startingPoint = new Vector2(x, y);

                GameBall ball = (GameBall) game.DRAWABLES.get(0);
                ball.getTransform().setPosition(x, y);

                return;
            }

            if (name.equals("background")) {
                game.DRAWABLES.add(new GameSprite(info, 0));
            }

            if (name.equals("walls")) {
                game.DRAWABLES.add(new GameSprite(info, 1) {
                    {
                        addTags(ObjectTag.Touchable);
                    }
                });
            }

            if (name.equals("trap")) {
                int indexOfComma = info.indexOf(',');
                double x = Double.parseDouble(info.substring(0, indexOfComma));

                info = info.substring(indexOfComma + 1);
                indexOfComma = info.indexOf(',');
                double y = Double.parseDouble(info.substring(0, indexOfComma));
                double angle = Double.parseDouble(info.substring(indexOfComma + 1));

                game.DRAWABLES.add(new Spikes() {
                    {
                        getTransform().setPosition(x, y);
                        getTransform().getFullAffine().rotate(Math.toRadians(angle),
                                SPIKES_IMAGE.getWidth() / 2.0, SPIKES_IMAGE.getHeight() / 2.0);
                    }
                });

                return;
            }

            if (name.equals("movingTrap")) {
                int indexOfComma;
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0, angle = 0;
                for (int i = 0; i <= 4; i++) {
                    indexOfComma = info.indexOf(',');

                    switch (i) {
                        case 0:
                            x1 = Double.parseDouble(info.substring(0, indexOfComma));
                            break;
                        case 1:
                            y1 = Double.parseDouble(info.substring(0, indexOfComma));
                            break;
                        case 2:
                            x2 = Double.parseDouble(info.substring(0, indexOfComma));
                            break;
                        case 3:
                            y2 = Double.parseDouble(info.substring(0, indexOfComma));
                            break;
                        case 4:
                            angle = Double.parseDouble(info.substring(indexOfComma + 1));
                    }
                    info = info.substring(indexOfComma + 1);
                }

                double finalAngle = angle;
                game.DRAWABLES.add(new MovingSpikes(x1, y1, x2, y2) {
                    {
                        getTransform().getFullAffine().rotate(Math.toRadians(finalAngle),
                                MOVING_SPIKES_IMAGE.getWidth() / 2.0, MOVING_SPIKES_IMAGE.getHeight() / 2.0);
                    }
                });

                return;
            }

            if (name.equals("jump")) {
                int indexOfComma = info.indexOf(',');
                double x = Double.parseDouble(info.substring(0, indexOfComma));

                info = info.substring(indexOfComma + 1);
                indexOfComma = info.indexOf(',');
                double y = Double.parseDouble(info.substring(0, indexOfComma));
                double angle = Double.parseDouble(info.substring(indexOfComma + 1));

                game.DRAWABLES.add(new JumpPad() {
                    {
                        getTransform().setPosition(x, y);
                        getTransform().getFullAffine().rotate(Math.toRadians(angle),
                                JUMP_PAD_IMAGE.getWidth() / 2.0, JUMP_PAD_IMAGE.getHeight() / 2.0);
                    }
                });

                return;
            }

            if (name.equals("bullet")) {
                int indexOfComma = info.indexOf(',');
                double x = Double.parseDouble(info.substring(0, indexOfComma));
                double y = Double.parseDouble(info.substring(indexOfComma + 1));

                game.DRAWABLES.add(new BulletTurret(game, x, y));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
