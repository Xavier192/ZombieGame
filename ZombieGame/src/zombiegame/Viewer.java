/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.w3c.dom.css.Rect;

/**
 *
 * @author Xavier
 */
public class Viewer extends Canvas implements Runnable {
    private ZombieGame zombieGameViewer;
    private BufferedImage renderImage;
    private BufferedImage backgroundImage;
    private ArrayList <VisibleObject> visibleObjects;
    private double fps;
    private double time;
    private double target;
    private boolean state;
    private boolean mostrarCartelito;
    private int reloj;
    
    public Viewer(ZombieGame zombieGameViewer){
        this.zombieGameViewer=zombieGameViewer;
        this.visibleObjects = this.zombieGameViewer.getVisibleObjects();
        this.fps=60;
        this.target = 1000000000 / fps;
        this.time = System.nanoTime();
        this.state=true;
        this.mostrarCartelito = true;
        this.reloj=3;
        readBackgroundImage();
    }
    
    public void readBackgroundImage(){
       File image= new File("img/fondo.png");
        try {
            this.backgroundImage = ImageIO.read(image);
        } catch (IOException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        double diferencia = System.nanoTime() - this.time;
            while (this.state) {
                if (diferencia > this.target) {
                bufferStrategy();
                this.time = System.nanoTime();
                }
                diferencia = System.nanoTime() - this.time;
            }
            this.zombieGameViewer.endGame();     
    }
    
    public void paintBackground(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(this.backgroundImage, 0, 0, 992, 672, this);
    }
    public void paintObjects(Graphics2D g){
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            this.visibleObjects.get(i).render(g);
        }
    }
    
    
    
    public void bufferStrategy() {
        BufferStrategy bs = getBufferStrategy();
        Graphics2D g;
        Graphics2D gBufer;
        
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        
        gBufer = (Graphics2D) bs.getDrawGraphics();
        this.renderImage = new BufferedImage(960,640, TYPE_3BYTE_BGR);
        g = (Graphics2D) this.renderImage.getGraphics();
        
        paintBackground(g);
        
        printScoreBoard(g);
        paintObjects(g);
        cartelito(g);
        paintFrame(gBufer,bs);
        
    }
    public void paintFrame(Graphics2D gBufer, BufferStrategy bs){
        gBufer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBufer.drawImage(this.renderImage, 0, 0,992,672, this);
        gBufer.dispose();
        bs.show();
    }
    
    public void printScoreBoard(Graphics2D g){

        g.setFont(new Font("Arial", Font.BOLD, 15));
        Color grey = new Color(   62, 62, 62 );
        g.setColor(grey);
        g.drawString("IP: "+this.zombieGameViewer.getLocalIp(), 50, 38);
        g.drawString("Port: "+this.zombieGameViewer.getServer().getPort(), 185, 38);
        g.drawString("FPS: "+this.fps, 285, 38);
        g.drawRoundRect(470, 13, 53, 53, 15, 45);
        
        if(this.zombieGameViewer.getScore().getReloj()>=10){
            g.drawString(Integer.toString(this.zombieGameViewer.getScore().getReloj()), 487, 42);
        }
        else{
           g.drawString(Integer.toString(this.zombieGameViewer.getScore().getReloj()), 493, 42); 
        }
        
        g.drawString("Round: "+this.zombieGameViewer.getScore().getRonda(),600 ,38 );
        g.drawString("Points A: "+this.zombieGameViewer.getScore().getPuntosEquipoA(),710 ,38);
        g.drawString("Points B: "+this.zombieGameViewer.getScore().getPuntosEquipoB(),830 ,38);
        
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    
    public void cartelito(Graphics2D g){
        Font arial = new Font("Arial", Font.BOLD, 46);
        g.setFont(arial);
        if(this.mostrarCartelito){
            Color c = new Color(232,232,232,200);
            g.setColor(c);
            g.fillRect(0, 0, 997, 701);  
            g.setColor(new Color(255,255,255));
            g.drawString( "Round "+this.zombieGameViewer.getScore().getRonda(),385 ,300 );
            g.drawString(""+this.reloj,465,350);
        }      
    }

    public boolean isMostrarCartelito() {
        return mostrarCartelito;
    }

    public void setMostrarCartelito(boolean mostrarCartelito) {
        this.mostrarCartelito = mostrarCartelito;
    }

    public int getReloj() {
        return reloj;
    }

    public void setReloj(int reloj) {
        this.reloj = reloj;
    }
    
    
    
}
