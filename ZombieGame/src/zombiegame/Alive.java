/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

/**
 *
 * @author Xavier
 */
public abstract class Alive extends VisibleObject implements Runnable {
    private double velocityX;
    private double velocityY;

    public Alive(double x, double y, boolean state, ZombieGame zombieGameObjects, String id, int width, int height,double velocityX,double velocityY) {
        super(x, y, state, zombieGameObjects, id, width, height);
        this.velocityX=velocityX;
        this.velocityY=velocityY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
   

    
    
    
    
}
