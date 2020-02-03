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
public class ScoreBoard {
    private int puntosEquipoA;
    private int puntosEquipoB;
    private int ronda;
    private int reloj;
    
    public ScoreBoard(){
        this.puntosEquipoA=0;
        this.puntosEquipoB=0;
        this.reloj=60;
        this.ronda=1;
    } 
    public void restarSegundosReloj(int segundos){
        this.reloj-=segundos;
    } 
    public void cogerMonedaA(){
        this.puntosEquipoA += 20;
    }
    public void cogerMonedaB(){
        this.puntosEquipoB+=20;
    }
    public void matarHumanoA(){
        this.puntosEquipoA+=40;
    }
    public void matarHumanoB(){
        this.puntosEquipoB+=40;
    }

    public int getReloj() {
        return reloj;
    }

    public void setReloj(int reloj) {
        this.reloj = reloj;
    }

    public int getPuntosEquipoA() {
        return puntosEquipoA;
    }

    public void setPuntosEquipoA(int puntosEquipoA) {
        this.puntosEquipoA = puntosEquipoA;
    }

    public int getPuntosEquipoB() {
        return puntosEquipoB;
    }

    public void setPuntosEquipoB(int puntosEquipoB) {
        this.puntosEquipoB = puntosEquipoB;
    }
    
    public int getRonda(){
        return ronda;
    }

    public void setRonda(int ronda) {
        this.ronda = ronda;
    }
    
    public void incrementarRonda(){
        this.ronda++;
        this.reloj=60;
    }
    
    
    
}
