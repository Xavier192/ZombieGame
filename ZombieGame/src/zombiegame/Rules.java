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
public class Rules {
    private ZombieGame zombieGameRules;
    
    public Rules(ZombieGame zombieGmaeRules){
        this.zombieGameRules=zombieGmaeRules;
    }
    
    public void proccessCollision(VisibleObject object,VisibleObject collisionSubject){
        if(object instanceof Human && collisionSubject instanceof Zombie && object.isState()){
            this.zombieGameRules.destroyHuman(object);
        }
        if(object instanceof Coin && collisionSubject instanceof Human){
            this.zombieGameRules.collectCoin(object);
        }
    }
    
    public void colisionBorders(int colisionType,VisibleObject subject){
       switch(colisionType){
           case 1:
               this.zombieGameRules.sendObjectToLeft(subject);
               break;
           case 2:
               this.zombieGameRules.sendObjectToRight(subject);
               break;
           case 3:
               this.zombieGameRules.sendObjectToBottom(subject);
               break;
           case 4:
               this.zombieGameRules.sendObjectToTop(subject);
               break;
       }
    }
}
