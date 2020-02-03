package com.example.dam2a03.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static android.os.Build.HOST;

public class MainActivity extends AppCompatActivity {

    public static final String CONNECTION = "connection";
    public static final String COLOR = "color";
    private static int PORT; // server details
    private static String HOST;
    private static String namePlayer;
    private Socket sock;
    private BufferedReader in; // i/o for the client
    private PrintWriter out;
    private EditText ip;
    private EditText editText_port;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.ip = (EditText) findViewById(R.id.editIp);
        this.editText_port = (EditText) findViewById(R.id.editPort);
        this.name =(EditText)findViewById(R.id.name);

        SharedPreferences settings =
                getSharedPreferences(CONNECTION, MODE_PRIVATE);

        String ip = settings.getString("IP", "192.168.1.8");
        String port = settings.getString("port", "8000");
        String namePlayer=settings.getString("name","Xavier");
        this.ip.setText(ip);
        this.editText_port.setText(port);
        this.name.setText(namePlayer);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


    }

    public static int getPORT() {
        return PORT;
    }

    public static String getHOST() {
        return HOST;
    }

    public static String getNamePlayer(){
        return namePlayer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Destruyendo","Si");
        this.closeLink();
    }

    public void connect(View v) {
        HOST = this.ip.getText().toString();
        PORT = Integer.parseInt(this.editText_port.getText().toString());
        namePlayer=this.name.getText().toString();
        savePreferences();
        makeContact();

    }

    private void makeContact() {


        try {
            sock = new Socket();
            sock.connect(new InetSocketAddress(HOST, PORT), 1000);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);

                Intent intent = new Intent(this, JoyStickActivity.class);
                String connection = this.getHOST() + "&" + this.getPORT()+"&"+this.getNamePlayer();
                intent.putExtra(CONNECTION, connection);
                startActivity(intent);


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void closeLink() {
        try {
            out.println("bye");
            sock.close();
            System.out.println("Socket cerrado");
        } catch (Exception e) {
            System.out.println(e);
        }
        //System.exit(0);
    }


    private void savePreferences(){
        String ip = String.valueOf(this.ip.getText());
        String port = String.valueOf(this.editText_port.getText());
        String name =String.valueOf(this.name.getText());

        SharedPreferences settings =
                getSharedPreferences(CONNECTION, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("IP", ip);
        prefEditor.putString("port", port);
        prefEditor.putString("name",name);
        prefEditor.commit();
    }

}
