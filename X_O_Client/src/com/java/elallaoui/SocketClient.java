package com.java.elallaoui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;


/**
 * Class SocketClient contains  all the needed component
 * contains also the socket  for connexion
 */
public class SocketClient implements Serializable {


    private MainClass frame = new MainClass();
    private JLabel messageLabel = new JLabel("...");

    private Square[] board = new Square[9];
    private Square currentSquare;

    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    public SocketClient() throws Exception {


        socket = new Socket(frame.ipadress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);


        messageLabel.setBackground(Color.lightGray);
        //frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);

        JPanel boardPanel = frame.panelHolder[0][0];
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentSquare = board[j];
                    out.println("MOVE " + j);
                }
            });
            boardPanel.add(board[i]);
        }
        //frame.getContentPane().add(boardPanel, BorderLayout.CENTER);

        frame.panelHolder[0][1].add(messageLabel);

    }

    void resetBoard()
    {
        for (Square s : board)
        {
            s.clean();
            s.repaint();

        }
    }

    public MainClass getFrame() {
        return frame;
    }

    public void setFrame(MainClass frame) {
        this.frame = frame;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * The main thread of the client will listen for messages from the server.
     * The first message will be a "WELCOME" message in which we receive our
     * mark. Then we go into a loop listening for any of the other messages,
     * and handling each message appropriately. The "VICTORY","VIICTORY", "DEFEAT", "TIE",
     * and "OTHER_PLAYER_LEFT" messages will ask the user whether or not to
     * play another game. If the answer is no, the loop is exited and the server
     * is sent a "QUIT" message.
     */
    public void play() throws Exception {
        try {
            String response = in.nextLine();
            char mark = response.charAt(8);
            char opponentMark = mark == 'X' ? 'O' : 'X';
            frame.setTitle("Tic Tac Toe: Player " + mark);
            while (in.hasNextLine()) {
                response = in.nextLine();
                if (response.startsWith("VALID_MOVE")) {
                    messageLabel.setText("Valid move, please wait");
                    currentSquare.setText(mark);
                    currentSquare.repaint();
                } else if (response.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setText(opponentMark);
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");
                } else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }else if (response.startsWith("VICTORY")) {
                    JOptionPane.showMessageDialog(frame, "Winner Winner");
                    out.println("QUIT");
                    break;
                }else if (response.startsWith("VIICTORY")) {
                    JOptionPane.showMessageDialog(frame, "Winner Winner");
                    resetBoard();
                }else if (response.startsWith("DEFEAT")) {
                    JOptionPane.showMessageDialog(frame, "Sorry you lost");
                    out.println("QUIT");
                    frame.dispose();
                    socket.close();
                    break;
                } else if (response.startsWith("TIE")) {
                    JOptionPane.showMessageDialog(frame, "Tie");
                    out.println("QUIT");
                    break;
                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    //out.println("QUIT");
                    //socket.close();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
            frame.dispose();
        }


    }

    static class Square extends JPanel {
        JLabel label = new JLabel();

        public Square() {
            setBackground(Color.white);
            setLayout(new GridBagLayout());
            label.setFont(new Font("Arial", Font.BOLD, 40));
            add(label);

        }

        public void setText(char text) {
            label.setForeground(Color.black);
            label.setText(text + "");
        }
        public void clean()
        {
            label.setText("");
        }


    }

    public static void main(String[] args) throws Exception {

        SocketClient client = new SocketClient();
        try {

            client.frame.setVisible(true);
            client.play();

        }finally {
            client.socket.close();
            client.frame.dispose();
        }

    }
}
