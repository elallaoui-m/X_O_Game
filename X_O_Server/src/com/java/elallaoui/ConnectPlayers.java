package com.java.elallaoui;


import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static java.lang.Thread.sleep;


/**
 * A list for Queued player
 * The game starts after having 2 players online
 * every new player is stocket on the player list until the first game finishes so he can join
 */
public class ConnectPlayers implements Runnable {


    ArrayList<Player> NextPlayers;
    ServerSocket listener;
    ExecutorService pool;
    int X_or_O = 0;



    public ConnectPlayers(ServerSocket listener, ArrayList<Player> NextPlayers, ExecutorService pool) {
        this.listener = listener;
        this.NextPlayers =NextPlayers;
        this.pool = pool;
    }

    @Override
    public void run() {

        while (true)
        {
            try {
                char mark = X_or_O%2 == 0?'X':'O';

                    if (listener.isClosed())
                    {
                        return;
                    }

                    NextPlayers.add(new Player(listener.accept(), mark,null));
                    X_or_O++;

                TicTacToeServer.textArea.append("New Player connected\n");
                TicTacToeServer.textArea.append("Number of connected players : "+NextPlayers.size()+"\n");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void startGame(Player winner,Player nextPlayer)
    {
        System.out.println("Starting the game ....0%");
        TicTacToeServer.textArea.append("Starting the game ....0%\n");
        Game game = new Game();
        System.out.println("Starting the game ....50%");
        TicTacToeServer.textArea.append("Starting the game ....50%\n");
        winner.setGame(game);
        nextPlayer.setGame(game);
        System.out.println("Starting the game ....90%");
        TicTacToeServer.textArea.append("Starting the game ....90%\n");
        pool.execute(winner);
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.execute(nextPlayer);
        TicTacToeServer.textArea.append("Go for it now \n");

    }
}
