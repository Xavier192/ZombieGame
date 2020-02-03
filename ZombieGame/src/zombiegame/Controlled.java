/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Rectangle;

/**
 *
 * @author Xavier
 */
public class Controlled extends Alive {

    private int actualFrame;
    private int frameCounter;
    private int[] moviments;
    private int animation;
    private double v;
    private boolean blockedAbility;
    private double blockedTimeOut;
    private String name;

    public Controlled(double x, double y, boolean state, ZombieGame zombieGameObjects, String id, int width, int height, double velocityX, double velocityY, double v, int animation, String name) {
        super(x, y, state, zombieGameObjects, id, width, height, velocityX, velocityY);
        this.actualFrame = 0;
        this.frameCounter = 0;
        this.moviments = new int[8];
        this.animation = animation;
        this.v = v;
        this.name = name;
        this.blockedAbility = false;
        this.blockedTimeOut = System.currentTimeMillis() / 1000000000;
    }

    public Rectangle getOffsetBoundsX() {
        return new Rectangle(new Rectangle((int) getX() + (int) getVelocityX(), (int) this.getY(), getWidth(), getHeight()));
    }

    public Rectangle getOffsetBoundsY() {
        return new Rectangle(new Rectangle((int) getX(), (int) this.getY() + (int) getVelocityY(), getWidth(), getHeight()));
    }

    public void ability() {

    }

    public void unBlockAbility() {

        if (this.blockedAbility) {
            if (System.nanoTime() / 1000000000 - this.blockedTimeOut > 4) {
                this.blockedAbility = false;
            }
        } else {
            this.blockedTimeOut = System.nanoTime() / 1000000000;
        }

    }

    @Override
    public boolean colisionX(VisibleObject obs) {
        Rectangle rectangleObstacle = obs.getBounds();
        Rectangle character = this.getOffsetBoundsX();

        return rectangleObstacle.intersects(character);
    }

    @Override
    public boolean colisionY(VisibleObject obs) {
        Rectangle rectangleObstacle = obs.getBounds();
        Rectangle character = this.getOffsetBoundsY();

        return rectangleObstacle.intersects(character);
    }

    public int getActualFrame() {
        return actualFrame;
    }

    public void setActualFrame(int actualFrame) {
        this.actualFrame = actualFrame;
    }

    public int getFrameCounter() {
        return frameCounter;
    }

    public void setFrameCounter(int frameCounter) {
        this.frameCounter = frameCounter;
    }

    public int[] getMoviments() {
        return moviments;
    }

    public void setMoviment(int pos) {
        this.moviments[pos] = 1;
    }

    public void cleanLastMoviment() {
        for (int position = 0; position < this.moviments.length; position++) {
            this.moviments[position] = 0;
        }
        setVelocityX(0);
        setVelocityY(0);
    }

    @Override
    public void run() {

    }

    public void setDirection() {

        if (getMoviments()[0] == 1) {
            setVelocityX(this.v);
        }
        if (getMoviments()[1] == 1) {
            setVelocityX(-this.v);
        }
        if (getMoviments()[2] == 1) {
            setVelocityY(-this.v);
        }
        if (getMoviments()[3] == 1) {
            setVelocityY(+this.v);
            setVelocityX(0);
        }
        if (getMoviments()[4] == 1) {
            setVelocityY(-this.v);
            setVelocityX(+this.v);
        }
        if (getMoviments()[5] == 1) {
            setVelocityX(-this.v);
            setVelocityY(-this.v);
        }
        if (getMoviments()[6] == 1) {
            setVelocityX(-this.v);
            setVelocityY(+this.v);
        }
        if (getMoviments()[7] == 1) {
            setVelocityX(+this.v);
            setVelocityY(+this.v);
        }
    }

    public void move() {
        boolean colisionX = this.getZombieGameObjects().colisionTestX(this);
        boolean colisionY = this.getZombieGameObjects().colisionTestY(this);
        this.getZombieGameObjects().colisionTestBorders(this);

        if (!colisionX) {
            setX(getX() + getVelocityX());
        }
        if (!colisionY) {
            setY(getY() + getVelocityY());
        }

        updateFrames();
    }

    public void updateFrames() {
        if (isMoving()) {
            if (this.frameCounter % 10 == 0) {
                this.actualFrame = this.frameCounter % 3;
            }
        }
    }

    public boolean isMoving() {
        boolean moved = false;
        for (int position = 0; position < this.moviments.length; position++) {
            if (this.moviments[position] == 1) {
                moved = true;
            }
        }

        return moved;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    public boolean isBlockedAbility() {
        return blockedAbility;
    }

    public void setBlockedAbility(boolean blockedAbility) {
        this.blockedAbility = blockedAbility;
    }

    public double getBlockedTimeOut() {
        return blockedTimeOut;
    }

    public void setBlockedTimeOut(double blockedTimeOut) {
        this.blockedTimeOut = blockedTimeOut;
    }

    public void setMoviments(int[] moviments) {
        this.moviments = moviments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
