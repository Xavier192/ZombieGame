/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zombiegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class ConnectionHandler implements Runnable{

     private Socket clientSocket;
    private String cliAddr;
    private ZombieGame zombieGameConnection;
    private int port;
    private BufferedReader in;
    private PrintWriter out;

    public ConnectionHandler(Socket clientSock, String cliAddr, int port, ZombieGame zombieGameConnection) {
        this.clientSocket = clientSock;
        this.cliAddr = cliAddr;
        this.zombieGameConnection = zombieGameConnection;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            processClient(this.in, this.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processClient(BufferedReader in, PrintWriter out) {
        String line;
        boolean done = false;
        try {
            if ((line = in.readLine()) == null) {
                done = true;
            } else {
                System.out.println("Client msg: " + line);
                if (line.trim().equals("byye")) {
                    done = true;
                } else {
                    doRequest(line);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
  
    
   

    private void doRequest(String line) throws IOException {
        System.out.println(line);
       
        switch (line) {
            case "cntpad":
                this.zombieGameConnection.createPad(this.clientSocket);
                System.out.println("Pad detected");
                break;
            case "bye":
                this.zombieGameConnection.disconnectPad(this.clientSocket.getInetAddress().getHostAddress());
                break;
            default:
                System.out.println("Ignoring input line");
                this.clientSocket.close();
                break;
        }
    }
    
    

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getCliAddr() {
        return cliAddr;
    }

    public void setCliAddr(String cliAddr) {
        this.cliAddr = cliAddr;
    }
    
}
