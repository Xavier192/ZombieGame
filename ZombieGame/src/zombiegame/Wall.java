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
public class Wall extends Static {

    public Wall(double x, double y, boolean state, ZombieGame zombieGameObjects, String id, int selectObject, int width, int height) {
        super(x, y, state, zombieGameObjects, id, selectObject, width, height);
        loadObstacles();
    }
    
    public void loadObstacles(){
        Image [][] fr= new Image[1][11];
        for(int images=0 ; images<11 ; images++){
          File img=new File("img/walls/wall"+images+".png");
          try {
             fr[0][images]=ImageIO.read(img);
            } catch (IOException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          this.setFrames(fr);
    }
   

 
    
    
    @Override
    public void render(Graphics2D g){
      g.drawImage(getFrames()[0][getSelectObject()], (int)getX(), (int)getY(),getWidth(),getHeight(),null);
    }
}
