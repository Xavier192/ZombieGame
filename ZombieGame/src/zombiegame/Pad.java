/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class Pad implements Runnable {

    private ZombieGame gamePad;
    private Socket clientSocket;
    private String id;
    private String localIp;
    private boolean connected;
    private Controlled controlled;
    private BufferedReader in;
    private PrintWriter out;
    private boolean invisible;
    private int port;
    private double timeOut;
    private String playerName;

    public Pad(Socket clientSock, ZombieGame GamePad) {
        this.clientSocket = clientSock;
        this.gamePad = GamePad;
        this.connected = true;
        this.invisible = false;
        this.localIp = this.clientSocket.getLocalAddress().getHostAddress();
        this.id = this.clientSocket.getInetAddress().getHostAddress();
        this.timeOut = System.nanoTime() / 1000000000;
    }

    private void processClient() {
        String line;
        this.out.println("como estas");
        while (this.connected) {
            comprobarDesconexion();
            try {
                if (this.in.ready()) {
                    line = this.in.readLine();
                    decideMessage(line);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    System.out.println("App cerrada");
                }
            } catch (IOException ex) {
                Logger.getLogger(Pad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void reconectarMando(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.connected = true;
        this.timeOut = System.nanoTime() / 1000000000;
    }

    public void comprobarDesconexion() {
        if (System.nanoTime() / 1000000000 - this.timeOut > 3) {
            this.connected = false;
            this.gamePad.checkPadDisconnected(this.id);
        }
    }

    public void decideMessage(String line) {

        switch (line) {
            case "conectado":
                this.timeOut = System.nanoTime() / 1000000000;
                this.out.println("como estas");
                break;
            case "bye":
                this.gamePad.disconnectPad(this.id);
                break;
            default:
                processMessage(line);
                break;
        }
    }

    public void processMessage(String message) {
        if (message.contains("name")) {
            String[] separator = message.split("&");
            this.gamePad.addPlayerToLooby(separator[1], this.id);
            setPlayerName(separator[1]);
        } else {
            this.gamePad.moveControlled(this.id, message);
        }

    }

    public void sendMessage(String message) {
        this.out.println(message);
    }

    public ZombieGame getGamePad() {
        return gamePad;
    }

    public void setGamePad(ZombieGame gamePad) {
        this.gamePad = gamePad;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getShipId() {
        return id;
    }

    public void setShipId(String shipId) {
        this.id = shipId;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Controlled getControlled() {
        return controlled;
    }

    public void setControlled(Controlled controlled) {
        this.controlled = controlled;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.port = this.clientSocket.getPort();
            System.out.println(this.id);
            processClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
