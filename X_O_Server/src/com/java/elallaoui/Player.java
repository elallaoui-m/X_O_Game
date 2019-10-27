package com.java.elallaoui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A Player is identified by a character mark which is either 'X' or 'O'.
 * For communication with the client the player has a socket and associated
 * Scanner and PrintWriter.
 */
class Player implements Runnable {
    char mark;
    Player opponent;
    Socket socket;
    Scanner input;
    PrintWriter output;
    Game game;

    public Player(Socket socket, char mark,Game game) {
        this.socket = socket;
        this.mark = mark;
        this.game = game;
    }



    @Override
    public void run() {
        try {
            setup();
            TicTacToeServer.textArea.append("Player added to game\n");
            TicTacToeServer.textArea.append("Number of connected players : "+TicTacToeServer.NextPlayers.size()+"\n");
            processCommands();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (opponent != null && opponent.output != null && TicTacToeServer.NextPlayers.size() == 0 ) {
                output.println("OTHER_PLAYER_LEFT");
                TicTacToeServer.textArea.append("PLAYER LEFT\n");
                TicTacToeServer.textArea.append("Number of connected players : "+TicTacToeServer.NextPlayers.size()+"\n");

            }
            else if (TicTacToeServer.NextPlayers.size() > 0 )
            {
                TicTacToeServer.textArea.append("==================== next game =============\n");
            }

        }
    }

    private void setup() throws IOException {
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        output.println("WELCOME " + mark);
        if (mark == 'X') {
            game.currentPlayer = this;
            output.println("MESSAGE Waiting for other players to connect");
            TicTacToeServer.textArea.append("Waiting for other players to connect\n");
            TicTacToeServer.textArea.append("Number of connected players : "+TicTacToeServer.NextPlayers.size()+"\n");



        } else {
            opponent = game.currentPlayer;
            opponent.opponent = this;
            opponent.output.println("MESSAGE Your Turn");
            game.setOn(true);
            TicTacToeServer.textArea.append("Game"+ (game.getOn()?"is One\n":" is Off\n"));
            TicTacToeServer.textArea.append("Number of connected players : "+TicTacToeServer.NextPlayers.size()+"\n");
        }
    }

    private void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();
            if (command.startsWith("QUIT")) {
                return;
            } else if (command.startsWith("MOVE")) {
                processMoveCommand(Integer.parseInt(command.substring(5)));
            }
        }
    }

    private void processMoveCommand(int location) {
        try {
            game.move(location, this);
            output.println("VALID_MOVE");
            opponent.output.println("OPPONENT_MOVED " + location);
            if (game.hasWinner()) {
                if (TicTacToeServer.NextPlayers.size() == 0)
                output.println("VICTORY");
                else
                {
                    output.println("VIICTORY");
                    output.println("MESSAGE loading new player");
                    game.winner = this;
                    opponent.output.println("DEFEAT");
                    game.setOn(false);
                    System.out.println("Game"+ (game.getOn()?"is One":" is Off"));

                    if (TicTacToeServer.NextPlayers.size() > 0)
                    {
                        Player player  = TicTacToeServer.NextPlayers.remove(0);
                        player.setMark(game.winner.getMark()=='X'?'O':'X');

                        TicTacToeServer.textArea.append("#Number of connected players : "+TicTacToeServer.NextPlayers.size()+"\n");
                        TicTacToeServer.connectPlayers.startGame(game.winner, player);
                        TicTacToeServer.textArea.append("##Number of connected players : "+TicTacToeServer.NextPlayers.size()+"\n");
                    }
                    else
                    {
                        game.winner.output.println("MESSAGE Your are the hero");
                    }
                }

            } else if (game.boardFilledUp()) {
                output.println("TIE");
                opponent.output.println("TIE");
                game.setOn(false);
                System.out.println("Game"+ (game.getOn()?"is One":" is Off"));

            }
        } catch (IllegalStateException e) {
            output.println("MESSAGE " + e.getMessage());
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMark(char mark) {
        this.mark = mark;
    }

    public char getMark() {
        return mark;
    }
}
