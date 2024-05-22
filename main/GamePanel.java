package main;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{
    
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();

    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();

    //COLOR
    public static final int WHITE = 0;
    public static final int BLACk = 1;
    int currentColor = WHITE;


    //creating panel for window
    public GamePanel(){
        setPreferredSize(new Dimension (WIDTH, HEIGHT));
        setBackground(Color.black);    

        setPieces();
        copyPieces(pieces, simPieces );
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();

        setPieces();
    }

    public void setPieces(){

        //white team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //Black team 
        pieces.add(new Pawn(BLACk, 0, 1));
        pieces.add(new Pawn(BLACk, 1, 1));
        pieces.add(new Pawn(BLACk, 2, 1));
        pieces.add(new Pawn(BLACk, 3, 1));
        pieces.add(new Pawn(BLACk, 4, 1));
        pieces.add(new Pawn(BLACk, 5, 1));
        pieces.add(new Pawn(BLACk, 6, 1));
        pieces.add(new Pawn(BLACk, 7, 1));
        pieces.add(new Knight(BLACk, 1, 0));
        pieces.add(new Knight(BLACk, 6, 0));
        pieces.add(new Rook(BLACk, 0, 0));
        pieces.add(new Rook(BLACk, 7, 0));
        pieces.add(new Bishop(BLACk, 2,0));
        pieces.add(new Bishop(BLACk, 5, 0));
        pieces.add(new Queen(BLACk, 3, 0));
        pieces.add(new King(BLACk, 4, 0));

    }

    public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){

        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
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

        //board
        board.draw(g2);

        //pieces
        for(Piece p : simPieces){
            p.draw(g2);
        }
    }

    
}