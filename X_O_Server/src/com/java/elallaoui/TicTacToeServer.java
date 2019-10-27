package com.java.elallaoui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicTacToeServer extends JFrame {

    public static ArrayList<Player> NextPlayers = new ArrayList<>();
    public static TextArea textArea =  new TextArea();
    public static ConnectPlayers connectPlayers;
    ServerSocket listener;
    ExecutorService pool;




    TicTacToeServer() throws IOException {

        setSize(300,200);
        setTitle("X O Game server");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         setVisible(true);

        add(textArea);
        textArea.setEditable(false);


            listener = new ServerSocket(58901);
            System.out.println("Server ON ....");
            textArea.append("Server ON ....\n");
            textArea.append("IP Adresse :" + InetAddress.getLocalHost().getHostAddress()+"\n");
            textArea.append("Waiting for player ....\n");
            pool = Executors.newFixedThreadPool(200);

            connectPlayers = new ConnectPlayers(listener,NextPlayers,pool);
            Thread thread = new Thread(connectPlayers);
            thread.start();



            while (NextPlayers.size() < 2)
            {
                System.out.flush();
            }


            connectPlayers.startGame(NextPlayers.remove(0),NextPlayers.remove(0));




    }

    public static void main(String[] args) throws Exception {

            TicTacToeServer server = new TicTacToeServer();

    }
}
