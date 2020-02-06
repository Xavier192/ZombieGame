/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Xavier
 */
public class ZombieGame extends JFrame implements Runnable {
    private boolean blockedMoviments;
    private ArrayList<Pad> pads;
    private ArrayList<VisibleObject> visibleObjects;
    private Rules rules;
    private Viewer viewer;
    private Server server;
    private ScoreBoard score;
    private Looby looby;
    private boolean inicioPartida;

    public static void main(String[] args) {
        ZombieGame zombieGame = new ZombieGame();
    }

    public ZombieGame() {
        super("ZombieGame");
        this.inicioPartida = false;
        this.blockedMoviments=false;
        this.pads = new ArrayList();
        this.visibleObjects = new ArrayList();
        this.server = new Server(this);
        this.score = new ScoreBoard();
        this.viewer = new Viewer(this);
        this.rules = new Rules(this);
        this.looby = new Looby(this);
        setSize(997, 701);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        prepareGame();

    }

    @Override
    public void run() {
        getReady();
        play();
    }

    public String getLocalIp() {
        String ipVal = null;
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            ipVal = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipVal;
    }

    public void prepareGame() {
        add(this.looby);
        (new Thread(this.server)).start();
    }

    public void getReady() {
        getContentPane().removeAll();
        this.inicioPartida = true;
        this.score.setPuntosEquipoA(0);
        this.score.setPuntosEquipoB(0);
        createWalls();
        createObstacles();
        createCharacters();
        createCoins();
        this.viewer = new Viewer(this);
        (new Thread(this.viewer)).start();
        add(this.viewer);
        setVisible(true);
    }

    public void play() {
        boolean endGame = false;
        this.blockedMoviments=true;
         iniciarRonda();
         
        while (!endGame) {
            while (this.score.getReloj() > 0) {
                sleep(1000);
                if(getNumberHumans() <1 || knowNumberCoins()<1){
                    this.score.setReloj(0);
                }
            }
            this.blockedMoviments=true;
            cambiarPapeles();
            createCoins();
            iniciarRonda();
            endGame = this.score.getRonda() == 3;
        }

        this.viewer.setState(false);
    }
    
    public int getNumberHumans(){
        int numberHumans=0;
        for (int humans = 0; humans < this.visibleObjects.size(); humans++) {
            if(this.visibleObjects.get(humans) instanceof Human && this.visibleObjects.get(humans).isState()){
               numberHumans++;
            }
        }
        return numberHumans;
    }
    
    public void iniciarRonda(){
        if(this.score.getRonda()<3){
        this.viewer.setReloj(3);
        this.viewer.setMostrarCartelito(true);
        
        while(this.viewer.getReloj()>0){
            sleep(1000);
            this.viewer.setReloj(this.viewer.getReloj()-1);
            
        }
        this.viewer.setMostrarCartelito(false);
        this.blockedMoviments=false;
        }
    }

    public void endGame() {
        getContentPane().removeAll();
        eliminateObjects();
        this.viewer = null;
        this.score.setRonda(1);
        this.score.setReloj(60);
        this.looby.configurarVentana();
        add(this.looby);
        this.looby.setBackground("img/fondo2.png");
        this.inicioPartida = false;
        setVisible(true);
    }

    public Viewer getViewer() {
        return viewer;
    }

    public boolean isBlockedMoviments() {
        return blockedMoviments;
    }

    public void setBlockedMoviments(boolean blockedMoviments) {
        this.blockedMoviments = blockedMoviments;
    }
    

    public void eliminateObjects() {
        for (int object = 0; object < this.visibleObjects.size(); object++) {
            this.visibleObjects.get(object).setState(false);

        }
        this.visibleObjects.clear();
    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Logger.getLogger(ZombieGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.score.restarSegundosReloj(1);
    }

    public void cambiarPapeles() {
        this.score.incrementarRonda();
        boolean hasPasse = false;
        if (this.score.getRonda() < 3) {
            for (int characters = 0; characters < this.visibleObjects.size(); characters++) {
                if (this.visibleObjects.get(characters) instanceof Zombie && !hasPasse) {
                    changeZombie(this.visibleObjects.get(characters), characters);
                    hasPasse = true;
                } else if (this.visibleObjects.get(characters) instanceof Human && !hasPasse) {
                    changeHuman(this.visibleObjects.get(characters), characters);
                    hasPasse = true;
                }
                hasPasse = false;
            }
        }
    }

    public void changeHuman(VisibleObject model, int pos) {
        Human m = (Human) model;
        m.cleanLastMoviment();
        Zombie z = new Zombie(217, 441, true, this, m.getId(), m.getWidth(), m.getHeight(), 0, 0, 1, m.getAnimation(), m.getName());
        (new Thread(z)).start();
        m.setState(false);
        this.visibleObjects.remove(m);
        this.visibleObjects.add(pos, z);
        Pad p = searchPad(z.getId());

    }

    public void checkPadDisconnected(String idPad) {
        Pad p = searchPad(idPad);
        Controlled c = null;
        double timeOut = System.nanoTime() / 1000000000;

        if (p != null) {
            while (!p.isConnected()) {
                c = searchControlled(idPad);
                if (System.nanoTime() / 1000000000 - timeOut > 17) {
                    this.looby.removePlayer(idPad);
                    if(c!=null){c.setState(false);
                    this.visibleObjects.remove(c);}
                    this.visibleObjects.remove(p);
                    break;
                }
            }
        }

    }

    public void changeZombie(VisibleObject model, int pos) {
        Zombie z = (Zombie) model;
        z.cleanLastMoviment();
        Human m = new Human(792, 413, true, this, z.getId(), z.getWidth(), z.getHeight(), 0, 0, 1, z.getAnimation(), z.getName());
        (new Thread(m)).start();
        z.setState(false);
        this.visibleObjects.remove(z);
        this.visibleObjects.add(pos, m);
        Pad p = searchPad(m.getId());

    }

    public void createCoins() {
        removeCoins();
        Random r=new Random();
        int[][] pos = {{157, 445}, {331, 357}, {276, 568}, {488, 90},{524,408},{771,167},{784,576},{901,524},{530,238},{22,132},{226,135},{398,139},{747,81},{851,167}};
        
        for (int coins = 0; coins < 10 && knowNumberCoins()<10; ) {
            int choosedCoin=r.nextInt(12);
            if (!checkCoins(pos[choosedCoin][0], pos[choosedCoin][1])) {
                this.visibleObjects.add(new Coin(pos[choosedCoin][0], pos[choosedCoin][1], true, this, "coinGuapa,", 0, 21, 21));
                coins++;
            }
        }
    }
    
    public int knowNumberCoins(){
        int number=0;
        for (int coins = 0; coins < this.visibleObjects.size(); coins++) {
          if(this.visibleObjects.get(coins) instanceof Coin){
              number++;
          }
          }
        return number;
    }
    
    public void removeCoins(){
        for (int coins = 0; coins < this.visibleObjects.size(); coins++) {
          if(this.visibleObjects.get(coins) instanceof Coin){
              Coin c=(Coin)this.visibleObjects.get(coins);
              c.setState(false);
              this.visibleObjects.remove(c);
          }  
        }
    }

    public boolean checkCoins(int x, int y) {
        boolean isCoin = false;

        for (int coin = 0; coin < this.visibleObjects.size(); coin++) {
            if (this.visibleObjects.get(coin) instanceof Coin) {
                if (this.visibleObjects.get(coin).getX() == x && this.visibleObjects.get(coin).getY() == y) {
                    isCoin = true;
                }
            }
        }

        return isCoin;
    }

    public void createCharacters() {
        Random r = new Random();
        boolean decide = r.nextBoolean();
        if (decide) {
            createHuman(this.pads.get(0).getId(),this.pads.get(0).getPlayerName());
            for (int i = 1; i < this.pads.size(); i++) {
                if (i % 2 == 0) {
                    createHuman(this.pads.get(i).getId(),this.pads.get(i).getPlayerName());
                } else {
                    createZombie(this.pads.get(i).getId(),this.pads.get(i).getPlayerName());
                }
            }
        } else {
            createZombie(this.pads.get(0).getId(),this.pads.get(0).getPlayerName());
            for (int i = 1; i < this.pads.size(); i++) {
                if (i % 2 == 0) {
                    createZombie(this.pads.get(i).getId(),this.pads.get(i).getPlayerName());
                } else {
                    createHuman(this.pads.get(i).getId(),this.pads.get(i).getPlayerName());
                }
            }
        }

    }

    public void createWalls() {
        //Izquierda y derecha.
        for (int verticals = 96; verticals < 336; verticals += 48) {
            this.visibleObjects.add(new Wall(0, verticals, true, this, "piezaIzquierda" + verticals, 1, 16, 48));
            this.visibleObjects.add(new Wall(945, verticals, true, this, "piezaDerecha" + verticals, 1, 16, 48));
        }
        //Entradas Izquierda
        this.visibleObjects.add(new Wall(0, 288, true, this, "Entrada_Izquierda_arriba", 3, 16, 48));
        this.visibleObjects.add(new Wall(16, 320, true, this, "Entrada_Iziquierda_arriba2", 4, 32, 16));

        this.visibleObjects.add(new Wall(0, 384, true, this, "Entrada_Izquierda_Abajo", 5, 16, 48));
        this.visibleObjects.add(new Wall(16, 384, true, this, "Entrada_Izquierda_Abajo", 6, 32, 16));

        //Entradas derecha
        this.visibleObjects.add(new Wall(945, 288, true, this, "Entrada_derecha_arriba", 9, 16, 48));
        this.visibleObjects.add(new Wall(913, 320, true, this, "Entrada_derecha_arriba", 10, 32, 16));

        this.visibleObjects.add(new Wall(945, 386, true, this, "Entrada_derecha_arriba", 7, 16, 48));
        this.visibleObjects.add(new Wall(913, 386, true, this, "Entrada_derecha_arriba", 8, 32, 16));

        //Izquierda y derecha
        for (int verticals = 432; verticals < 544; verticals += 48) {
            this.visibleObjects.add(new Wall(0, verticals, true, this, "piezaIzquierda" + verticals, 1, 16, 48));
            this.visibleObjects.add(new Wall(945, verticals, true, this, "piezaDerecha" + verticals, 1, 16, 48));
        }

        this.visibleObjects.add(new Wall(0, 576, true, this, "Pieza_Especial_Izquierda_Abajo", 1, 16, 34));
        this.visibleObjects.add(new Wall(945, 576, true, this, "pieza_Especial_derecha_abajo", 1, 16, 34));

        //Arriba y abajo.
        for (int horitzontals = 30; horitzontals < 414; horitzontals += 48) {
            this.visibleObjects.add(new Wall(horitzontals, 65, true, this, "piezaArriba" + horitzontals, 0, 48, 16));
            this.visibleObjects.add(new Wall(horitzontals, 625, true, this, "piezaAbajo" + horitzontals, 0, 48, 16));
        }

        //Entradas arriba 
        this.visibleObjects.add(new Wall(446, 65, true, this, "Entrada_arriba_izquierda", 7, 16, 48));
        this.visibleObjects.add(new Wall(414, 65, true, this, "Entrada_arriba_izquierda", 8, 32, 16));

        this.visibleObjects.add(new Wall(539, 65, true, this, "Entrada_arriba_derecha", 5, 16, 48));
        this.visibleObjects.add(new Wall(555, 65, true, this, "Entrada_arriba_derecha", 6, 32, 16));

        this.visibleObjects.add(new Wall(586, 65, true, this, "pieza_especial_arriba_entrada", 0, 58, 16));

        //Entradas abajo
        this.visibleObjects.add(new Wall(446, 593, true, this, "Entrada_abajo_izquierda", 9, 16, 48));
        this.visibleObjects.add(new Wall(414, 625, true, this, "Entrada_abajo_izquierda", 10, 32, 16));

        this.visibleObjects.add(new Wall(538, 593, true, this, "Entrada_abajo_derecha", 3, 16, 48));
        this.visibleObjects.add(new Wall(554, 625, true, this, "Entrada_abajo_derecha", 4, 32, 16));

        this.visibleObjects.add(new Wall(586, 625, true, this, "Pieza_especial_abajo_entrada", 0, 58, 16));
        //Arriba y abajo
        for (int horitzontals = 643; horitzontals < 897; horitzontals += 48) {
            this.visibleObjects.add(new Wall(horitzontals, 65, true, this, "piezaArriba" + horitzontals, 0, 48, 16));
            this.visibleObjects.add(new Wall(horitzontals, 625, true, this, "piezaAbajo" + horitzontals, 0, 48, 16));
        }

        //Esquinas.
        this.visibleObjects.add(new Wall(0, 65, true, this, "Esquina00", 2, 32, 32));
        this.visibleObjects.add(new Wall(930, 65, true, this, "Esquina01", 2, 32, 32));
        this.visibleObjects.add(new Wall(0, 609, true, this, "Esquina02", 2, 32, 32));
        this.visibleObjects.add(new Wall(930, 609, true, this, "Esquina03", 2, 32, 32));

    }

    public void createObstacles() {
        this.visibleObjects.add(new Obstacle(435, 180, true, this, "Obstacle0", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(482, 211, true, this, "Obstacle1", 1, 32, 125));
        this.visibleObjects.add(new Obstacle(482, 336, true, this, "Obstacle2", 1, 32, 125));
        this.visibleObjects.add(new Obstacle(435, 460, true, this, "Obstacle3", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(388, 305, true, this, "Obstacle4", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(514, 367, true, this, "Obstacle5", 0, 125, 32));

        this.visibleObjects.add(new Obstacle(450, 367, true, this, "Obstacle7", 2, 32, 32));

        this.visibleObjects.add(new Obstacle(268, 237, true, this, "Obstacle9", 1, 32, 125));
        this.visibleObjects.add(new Obstacle(206, 268, true, this, "Obstacle11", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(268, 361, true, this, "Obstacle12", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(268, 392, true, this, "Obstacle13", 2, 32, 32));

        this.visibleObjects.add(new Obstacle(139, 333, true, this, "Obstacle14", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(139, 364, true, this, "Obstacle15", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(139, 395, true, this, "Obstacle16", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(171, 364, true, this, "Obstacle17", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(108, 364, true, this, "Obstacle18", 2, 32, 32));

        this.visibleObjects.add(new Obstacle(341, 498, true, this, "Obstacle20", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(248, 529, true, this, "Obstacle21", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(124, 529, true, this, "Obstacle22", 0, 125, 32));

        this.visibleObjects.add(new Obstacle(93, 529, true, this, "Obstacle23", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(124, 498, true, this, "Obstacle24", 2, 32, 32));

        this.visibleObjects.add(new Obstacle(189, 140, true, this, "Obstacle25", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(65, 140, true, this, "Obstacle26", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(127, 171, true, this, "Obstacle27", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(127, 202, true, this, "Obstacle28", 2, 32, 32));

        this.visibleObjects.add(new Obstacle(260, 140, true, this, "Obstacle29", 0, 125, 32));

        this.visibleObjects.add(new Obstacle(625, 130, true, this, "Obstacle30", 0, 125, 32));

        this.visibleObjects.add(new Obstacle(750, 130, true, this, "Obstacle32", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(874, 130, true, this, "Obstacle33", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(812, 161, true, this, "Obstacle34", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(812, 192, true, this, "Obstacle35", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(765, 223, true, this, "Obstacle36", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(705, 321, true, this, "Obstacle37", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(830, 321, true, this, "Obstacle38", 1, 32, 125));
        this.visibleObjects.add(new Obstacle(861, 383, true, this, "Obstacle39", 2, 32, 32));

        this.visibleObjects.add(new Obstacle(735, 413, true, this, "Obstacle40", 1, 32, 125));
        this.visibleObjects.add(new Obstacle(735, 538, true, this, "Obstacle41", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(860, 538, true, this, "Obstacle42", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(828, 507, true, this, "Obstacle43", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(611, 475, true, this, "Obstacle45", 0, 125, 32));
        this.visibleObjects.add(new Obstacle(673, 444, true, this, "Obstacle46", 2, 32, 32));
        this.visibleObjects.add(new Obstacle(673, 506, true, this, "Obstacle47", 2, 32, 32));
    }

    public void createZombie(String id, String name) {
        Zombie z = new Zombie(217, 441, true, this, id, 28, 41, 0, 0, 1, 2,name);
        (new Thread(z)).start();
        this.visibleObjects.add(z);
    }

    public void createHuman(String id, String name) {
        Human h = new Human(792, 413, true, this, id, 28, 47, 0, 0, 1, 2,name);
        (new Thread(h)).start();
        this.visibleObjects.add(h);
    }

    public ArrayList<VisibleObject> getVisibleObjects() {
        return visibleObjects;
    }

    public void createPad(Socket clientSock) {
        Pad pad = searchPad(clientSock.getInetAddress().getHostAddress());
        if (pad == null) {
            if (!this.inicioPartida) {
                pad = new Pad(clientSock, this);
                this.pads.add(pad);
                (new Thread(pad)).start();
            }
        } else {
            pad.reconectarMando(clientSock);
            (new Thread(pad)).start();
        }
    }

    public void addPlayerToLooby(String name, String id) {
        this.looby.addPlayer(name, id);
    }

    public Pad searchPad(String id) {
        Pad pad = null;
        for (int pads = 0; pads < this.pads.size(); pads++) {
            if (this.pads.get(pads).getId().equalsIgnoreCase(id)) {
                pad = this.pads.get(pads);
            }
        }

        return pad;
    }

    public Controlled searchControlled(String id) {
        Controlled c = null;
        for (int visibleObject = 0; visibleObject < this.visibleObjects.size(); visibleObject++) {
            if (this.visibleObjects.get(visibleObject).getId().equalsIgnoreCase(id)) {
                c = (Controlled) this.visibleObjects.get(visibleObject);
            }
        }

        return c;
    }

    public void disconnectPad(String id) {
        this.looby.removePlayer(id);
        Pad p = searchPad(id);
        Controlled c = searchControlled(id);
        if (p != null) {
            p.setConnected(false);
            this.pads.remove(p);
        }
        if (c != null) {
            c.setState(false);
            this.visibleObjects.remove(c);
        }

    }

    public void moveControlled(String id, String move) {
        Controlled character = searchControlled(id);

        if (character != null && !this.blockedMoviments) {
            if (move.equals("ability")) {
                if (!character.isBlockedAbility()) {
                    character.ability();
                }
            } else {
                character.cleanLastMoviment();
                switch (move) {
                    case "up":
                        character.setAnimation(0);
                        character.setMoviment(2);
                        break;
                    case "upr":
                        character.setAnimation(3);
                        character.setMoviment(4);
                        break;
                    case "ri":
                        character.setAnimation(3);
                        character.setMoviment(0);
                        break;
                    case "dor":
                        character.setAnimation(3);
                        character.setMoviment(7);
                        break;
                    case "do":
                        character.setAnimation(1);
                        character.setMoviment(3);
                        break;
                    case "dol":
                        character.setAnimation(2);
                        character.setMoviment(6);
                        break;
                    case "le":
                        character.setAnimation(2);
                        character.setMoviment(1);
                        break;
                    case "upl":
                        character.setAnimation(2);
                        character.setMoviment(5);
                        break;
                    case "nm":
                        character.cleanLastMoviment();
                        break;

                }
                character.setDirection();
            }
        }
    }

    public void colisionTestBorders(VisibleObject colisionTestObject) {
        int colision = bordersColision(colisionTestObject);
        if (colision > 0) {
            this.rules.colisionBorders(colision, colisionTestObject);
        }
    }

    public boolean colisionTestX(VisibleObject colisionTestObject) {
        VisibleObject object;
        boolean colisionO = false;

        for (int i = 0; i < this.visibleObjects.size(); i++) {
            object = this.visibleObjects.get(i);
            if (object != colisionTestObject && colisionTestObject.colisionX(object)) {
                if (object instanceof Coin || object instanceof Controlled) {
                    this.rules.proccessCollision(object, colisionTestObject);
                } else {
                    colisionO = true;
                }
            }
        }

        return colisionO;
    }

    public boolean colisionTestY(VisibleObject colisionTestObject) {
        VisibleObject object;
        boolean colisionO = false;

        for (int i = 0; i < this.visibleObjects.size(); i++) {
            object = this.visibleObjects.get(i);
            if (object != colisionTestObject && colisionTestObject.colisionY(object)) {
                if (object instanceof Coin || object instanceof Controlled) {
                    this.rules.proccessCollision(object, colisionTestObject);
                } else {
                    colisionO = true;
                }
            }
        }

        return colisionO;
    }

    public int bordersColision(VisibleObject subject) {

        if (subject.getX() >= 960) {
            return 1;
        }
        if (subject.getX() <= 0) {
            return 2;
        }
        if (subject.getY() >= 640) {
            return 3;
        }
        if (subject.getY() <= 45) {
            return 4;
        }
        return 0;
    }

    public void destroyHuman(VisibleObject human) {
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            if (this.visibleObjects.get(i) instanceof Zombie) {
                System.out.println("Say hi!");
            }
        }
        System.out.println("Destroying human");
        human.setState(false);

        if (this.score.getRonda() == 1) {
            this.score.matarHumanoA();
        } else {
            this.score.matarHumanoB();
        }
    }

    public boolean colisionTeleport(double x, double y, VisibleObject subject) {
        VisibleObject object;
        Zombie z = (Zombie) subject;
        boolean colision = false;
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            object = this.visibleObjects.get(i);
            if (object != subject && z.checkColisionTeleport(x, y, object) && !(object instanceof Coin) && !(object instanceof Human)) {
                colision = true;

            }
        }

        if (!colision) {
            z.setBlockedAbility(true);
        }

        return colision;
    }

    public void collectCoin(VisibleObject coin) {
        Coin c = (Coin) coin;
        this.visibleObjects.remove(coin);

        if (this.score.getRonda() == 1) {
            this.score.cogerMonedaB();
        } else {
            this.score.cogerMonedaA();
        }
    }

    public void sendObjectToLeft(VisibleObject subject) {

        subject.setX(14);

    }

    public void sendObjectToRight(VisibleObject subject) {

        subject.setX(915);

    }

    public void sendObjectToTop(VisibleObject subject) {

        subject.setY(580);

    }

    public void sendObjectToBottom(VisibleObject subject) {
        subject.setY(79);
    }

    public void obstacleColision(VisibleObject object, VisibleObject obstacle) {
    }

    public void destroyPad(Pad pad) {
        this.pads.remove(pad);
    }

    public Server getServer() {
        return server;
    }

    public ScoreBoard getScore() {
        return score;
    }

    public void setScore(ScoreBoard score) {
        this.score = score;
    }

}
