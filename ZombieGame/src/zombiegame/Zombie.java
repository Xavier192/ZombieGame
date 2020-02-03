/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Xavier
 */
public class Zombie extends Controlled {

    private int RADIUS = 20;

    public Zombie(double x, double y, boolean state, ZombieGame zombieGameObjects, String id, int width, int height, double velocityX, double velocityY, double v, int animation, String name) {
        super(x, y, state, zombieGameObjects, id, width, height, velocityX, velocityY, v, animation, name);
        loadZombieFrames();
    }

    @Override
    public void run() {
        while (isState()) {
            move();
            unBlockAbility();
            try {
                Thread.sleep(15);
            } catch (InterruptedException ex) {
                Logger.getLogger(Zombie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void ability() {

        switch ((int) getVelocityX()) {
            case 1:
                if (!this.getZombieGameObjects().colisionTeleport(getX() + 60, getY(), this)) {
                    setX(getX() + 60);
                }
                break;
            case -1:
                if (!this.getZombieGameObjects().colisionTeleport(getX() - 60, getY(), this)) {
                    setX(getX() - 60);
                }
                break;
        }

        switch ((int) getVelocityY()) {
            case 1:
                if (!this.getZombieGameObjects().colisionTeleport(getX(), getY() + 73, this)) {
                    setY(getY() + 73);
                }
                break;
            case -1:
                if (!this.getZombieGameObjects().colisionTeleport(getX(), getY() - 73, this)) {
                    setY(getY() - 73);
                }
                break;
        }

    }

    public boolean checkColisionTeleport(double x, double y, VisibleObject obstacle) {

        Rectangle me = (new Rectangle((int) x, (int) y, getWidth(), getHeight()));
        Rectangle obs = obstacle.getBounds();

        return me.intersects(obs);
    }

    public void loadZombieFrames() {
        int contador = 0;

        Image[][] fr = new Image[4][3];
        for (int perspective = 0; perspective < 4; perspective++) {
            for (int image = 0; image < 3; image++) {
                File img = new File("img/zombie2/zombie" + contador + ".png");

                try {
                    fr[perspective][image] = ImageIO.read(img);
                } catch (IOException ex) {
                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
                }
                contador++;
            }
        }
        this.setFrames(fr);
    }

    @Override
    public void render(Graphics2D g) {
        int a = (int) this.getX();
        int b = (int) this.getY();
        if (isState()) {
            Image frame = getFrames()[getAnimation()][getActualFrame()];
            setWidth(frame.getWidth(null));
            setHeight(frame.getHeight(null));
            g.drawImage(frame, (int) getX(), (int) getY(), getWidth(), getHeight(), null);
            g.drawString(this.getName(), a - (this.getName().length() / 10) * 5, b - (RADIUS - 3));
            setFrameCounter(getFrameCounter() + 1);
        }

    }
}
