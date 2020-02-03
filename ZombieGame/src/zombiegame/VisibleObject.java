/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Xavier
 */
public abstract class VisibleObject implements Renderizable,Collisionable {
    private double x;
    private double y;
    private boolean state;
    private String id;
    private ZombieGame zombieGameObjects;
    private Image [][] frames;
    private int width;
    private int height;
    
    public VisibleObject(double x, double y, boolean state, ZombieGame zombieGameObjects,String id,int width, int height){
       
        this.zombieGameObjects=zombieGameObjects;
        this.x=x;
        this.y=y;
        this.state=state;
        this.id=id;
        this.width=width;
        this.height=height;
    }
    
    public Rectangle getBounds() {
    return new Rectangle((int)this.x, (int)this.y, this.width, this.height);
    }
    
    @Override
    public boolean colisionX(VisibleObject obs){
        
        
       return false; 
    }
    @Override
    public boolean colisionY(VisibleObject obs){
        return false;
    }
    
    
    
    public void destroyObject(){
        this.state=false;
    }
    
    
    @Override
    public void render(Graphics2D g) {
        
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZombieGame getZombieGameObjects() {
        return zombieGameObjects;
    }

    public void setZombieGameObjects(ZombieGame zombieGameObjects) {
        this.zombieGameObjects = zombieGameObjects;
    }

    public Image[][] getFrames() {
        return frames;
    }

    public void setFrames(Image[][] frames) {
        this.frames = frames;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    
    
    
}
