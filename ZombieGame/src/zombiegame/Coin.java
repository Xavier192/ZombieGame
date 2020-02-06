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
public class Coin extends Static {
    private int frameCounter;
    private int actualFrame;
    
    
    public Coin(double x, double y, boolean state, ZombieGame zombieGameObjects, String id, int selectObject, int width, int height) {
        super(x, y, state, zombieGameObjects, id, selectObject, width, height);
        this.frameCounter=0;
        this.actualFrame=0;
        loadCoin();
    }
    public void loadCoin(){
        Image [][] fr= new Image[2][8];
        for(int images=0 ; images<8 ; images++){
          File img=new File("img/coin/coin"+images+".png");
          try {
             fr[0][images]=ImageIO.read(img);
            } catch (IOException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        for(int images=0 ; images<6 ; images++){
          File img=new File("img/coin/coinCollected/collected"+images+".png");
          try {
             fr[1][images]=ImageIO.read(img);
            } catch (IOException ex) {
                Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        
          this.setFrames(fr);
    }
    
    
    
     @Override
    public void render(Graphics2D g){
        if(isState()){
            Image frame=getFrames()[getSelectObject()][this.actualFrame];
        
        if(this.frameCounter%10==0){
            if(getSelectObject()==0){
                this.actualFrame=this.frameCounter%8;
            }
            else{
                this.actualFrame=this.frameCounter%6;
            }
            
        }   
        g.drawImage(getFrames()[getSelectObject()][this.actualFrame], (int)getX(), (int)getY(),frame.getWidth(null),frame.getHeight(null),null);
        this.frameCounter++;
        }
    }

    public int getFrameCounter() {
        return frameCounter;
    }

    public void setFrameCounter(int frameCounter) {
        this.frameCounter = frameCounter;
    }

    public int getActualFrame() {
        return actualFrame;
    }

    public void setActualFrame(int actualFrame) {
        this.actualFrame = actualFrame;
    }
    
    
}
