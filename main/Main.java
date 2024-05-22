package main;

import javax.swing.JFrame;

public class Main{
    public static void main(String[] args ){

        //create window
        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // add game panel
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        //add board
        
        
        gp.launchGame();
    }
}