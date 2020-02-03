/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Xavier
 */
public class Human extends Controlled {

    private int RADIUS = 20;

    public Human(double x, double y, boolean state, ZombieGame zombieGameObjects, String id, int width, int height, double velocityX, double velocityY, double v, int animation, String name) {
        super(x, y, state, zombieGameObjects, id, width, height, velocityX, velocityY, v, animation, name);
        loadHumanFrames();
    }

    @Override
    public void run() {
        int contador = 0;
        while (isState()) {
            move();
            unBlockAbility();
            contador = checkAbility(contador);
            try {
                Thread.sleep(15);
            } catch (InterruptedException ex) {
                Logger.getLogger(Zombie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int checkAbility(int contador) {
        if (getV() == 2) {
            contador++;
            if (contador > 100) {
                setV(1);
                contador = 0;
            }
        }
        return contador;
    }

    @Override
    public void ability() {
        setV(2);
        setDirection();
        this.setBlockedAbility(true);
    }

    @Override
    public void unBlockAbility() {

        if (this.isBlockedAbility()) {
            if (System.nanoTime() / 1000000000 - this.getBlockedTimeOut() > 6) {
                this.setBlockedAbility(false);
            }
        } else {
            this.setBlockedTimeOut(System.nanoTime() / 1000000000);
        }

    }

    public void loadHumanFrames() {
        int contador = 0;

        Image[][] fr = new Image[4][8];
        for (int perspective = 0; perspective < 4; perspective++) {
            for (int image = 0; image < 8; image++) {
                File img = new File("img/humans2/human" + contador + ".png");
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
    public void updateFrames() {
        if (isMoving()) {
            int variacion = 10;
            if (this.getFrameCounter() % variacion / this.getV() == 0) {
                this.setActualFrame(this.getFrameCounter() % 8);
            }
        }

    }

    @Override
    public void render(Graphics2D g) {
        int a = (int) this.getX();
        int b = (int) this.getY();
        if (isState()) {
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

}
