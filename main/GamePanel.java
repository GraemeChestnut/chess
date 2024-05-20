package main;

public class GamePanel extends JPanel{
    
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    public GamePanel(){
        setPerferredSize(new Dimension (WIDTH, HEIGHT));
        setBackground(Color.black);
         
    }
}