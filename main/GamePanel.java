package main;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable{
    
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();


    //creating panel for window
    public GamePanel(){
        setPreferredSize(new Dimension (WIDTH, HEIGHT));
        setBackground(Color.black);    
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
        public void run() {
            double drawInterval = 1000000000/FPS;
            double delta = 0;
            long lastTime = System.nanoTime();
            long currentTime;

            while(gameThread != null) {

                currentTime = System.nanoTime();

                delta += (currentTime - lastTime)/drawInterval;
                lastTime = currentTime;

                if(delta >= 1){
                    update();
                    repaint();
                    delta--;
                }
            }
        }

    //game loop

    private void update(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        board.draw(g2);
    }

    
}