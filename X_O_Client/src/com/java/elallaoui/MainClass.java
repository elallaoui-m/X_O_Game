package com.java.elallaoui;

import javax.swing.*;
import java.awt.*;

/**
 *
 * class main for player
 * the playing board and all the notification from serverSocket are going to be displayed here
 */

public class MainClass extends JFrame {

    int i = 2;
    int j = 2;
    public JPanel[][] panelHolder = new JPanel[i][j];
    String ipadress;

    public MainClass()
    {
        setSize(600,500);
        setLayout(new GridLayout(2,2));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("X O");

        do {
            ipadress= JOptionPane.showInputDialog("Please enter the server ip adresse ");
        }while (ipadress == null || ipadress.isEmpty() || !validIP(ipadress)) ;

        //ipadress = "127.0.0.1";





        setLayout(new GridLayout(i,j));

        for(int m = 0; m < i; m++) {
            for(int n = 0; n < j; n++) {
                panelHolder[m][n] = new JPanel();
                add(panelHolder[m][n]);
            }
        }


        panelHolder[0][1].setBackground(Color.lightGray);
        //panelHolder[1][0].setBackground(Color.YELLOW);
        panelHolder[1][1].setBackground(Color.CYAN);




    }

    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
