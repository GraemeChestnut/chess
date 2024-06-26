package main;

import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
    Mouse mouse = new Mouse();

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;


    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;


    // BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;

    //creating panel for window
    public GamePanel(){
        setPreferredSize(new Dimension (WIDTH, HEIGHT));
        setBackground(Color.black);    
        addMouseMotionListener(mouse); 
        addMouseListener(mouse);

        //setPieces();
        //testing();
        test2();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
        
        //setPieces();
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
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Bishop(BLACK, 2,0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
        

    }
    public void testing(){
        pieces.add(new Pawn(BLACK, 0 , 1));
        pieces.add(new King(WHITE, 7,6));
        pieces.add(new King(BLACK, 1,0));
        
    }
    public void test2(){
            pieces.add(new Pawn(BLACK, 0 , 1));
            pieces.add(new King(WHITE, 7,7));
            pieces.add(new King(BLACK, 1,0));
            pieces.add(new Pawn(WHITE, 0 , 4));
            pieces.add(new Queen(BLACK, 5, 5));
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

        if(promotion){
            promoting();
        }
        if (gameover == false && stalemate == false){

        ///// MOUSE BUTTON PRESSED
            if(mouse.pressed){

                if(activeP == null){

                    /// checks all pieces if in the same square as the mouse location when it was pressed//
                    for(Piece piece : simPieces){

                        //checks only if it is the current color

                        if(piece.color == currentColor &&
                            piece.col == mouse.x/Board.SQUARE_SIZE && 
                            piece.row == mouse.y/Board.SQUARE_SIZE) {
                            
                            activeP = piece;
                        }
                    }
                        
                }
                else {
                    //if playing is already holding a piece, simulate method is called
                    simulate();
                }
                
            }
            //////// MOUSE BUTTON RELEASED ////////
            if(mouse.pressed == false){

                if(activeP != null){
                    
                    if(validSquare){

                        //MOVE CONFIRMED

                        //updates piece list for pieces that have been removed or taken
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if(castlingP != null){
                            castlingP.updatePosition();
                        }
                        else if(isStalemate() && isKinginCheck() == false){
                            stalemate = true;
                        }
                        if(isKinginCheck() && isCheckmate()){
                            // GAME IS OVER
                            gameover = true;
                        }

                        else{
                            if(canPromote()){
                                promotion = true;
                            }
                            else{
                                changePlayer(); // this is not the problem for double capture
                            }  
                        }    
                                       
                    }
                    else {
                        // reset everything since move is invalid
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    } 
                }
            }
        }
    }

    //simulates next move, checks if can move to legal spot
    private void simulate(){

        canMove = false;
        validSquare = false;

        // reset the piece list in every loop
        // needed to restore piece that is removed during the simulation
        copyPieces(pieces, simPieces);

        ///reset castling piece position
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        //when piece is held, update its position based on mouse position

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE; 
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // check if the piece is hovering over a reachable square
        if(activeP.canMove(activeP.col, activeP.row)){

            canMove = true;
            

            //if hitting piece, remove from list, which removes from board
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();

            if(isIllegal(activeP) == false && opponentCanCaptureKing() == false){
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king){

        if(king.type == Type.KING){
            for(Piece piece : simPieces){
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                } 
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing(){

        Piece king = getKing(false);

        for(Piece piece : simPieces){
            //if statements looks to see if any piece of differnt color can move to the king's location
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }

        return false;
    }

    private boolean isKinginCheck(){

        Piece king = getKing(true);

        if(activeP.canMove(king.col, king.row)){
            checkingP = activeP;
            return true;
        }
        else{
            checkingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent){

        Piece king = null;

        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    king = piece;
                }
            }
            else{
                if(piece.type == Type.KING && piece.color == currentColor){
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isCheckmate(){

        Piece king = getKing(true);

        if(kingCanMove(king)){
            return false;
        }
        else{
            //check for getting out of check by blocking a piece

            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if(colDiff == 0){
                // checking piece is attacking vertically
                if(checkingP.row < king.row){
                    //checking piece is above
                    for(int row = checkingP.row; row < king.row; row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.col != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }

                }
                if(checkingP.row > king.row){
                    //checking piece is attacking below
                    for(int row = checkingP.row; row > king.row; row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.col != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }

                }
            }
            else if(rowDiff == 0){
                // checking piece is attacking horizontally
                if(checkingP.col < king.col){
                    // to the left
                    for(int col = checkingP.col; col < king.col; col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.row != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col > king.col){
                    // to the right
                    for(int col = checkingP.col; col > king.col; col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.row != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(colDiff == rowDiff){
                // checking piece is attacking diagonally
                if(checkingP.row < king.row){
                    //above king
                    if(checkingP.col < king.col){
                        //upper left
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        //upper right
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    //below king
                    if(checkingP.col < king.col){
                        //lower left
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        //lower right
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    private boolean kingCanMove(Piece king){

        //simulate if king can move to any squares
        if(isValidMove(king, -1, -1)){return true;}
        if(isValidMove(king, 0, -1)) {return true;}
        if(isValidMove(king, 1, -1)) {return true;}
        if(isValidMove(king, -1, 0)) {return true;}
        if(isValidMove(king, 1, 0)) {return true;}
        if(isValidMove(king, -1, 1)) {return true;}
        if(isValidMove(king, 0, 1)) {return true;}
        if(isValidMove(king, 1, 1)) {return true;}

        return false;
    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){

        boolean isValidMove = false;

        //update king position to see if can move
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(isIllegal(king) == false){
                isValidMove = true;
            }
        }

        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private boolean isStalemate(){
        
        Piece king = getKing(true);

        boolean draw = false;

        ArrayList<Piece> opponentsPiece = new ArrayList<>();

        //count the number of pieces
        //checking if the only pieces left are kings
        if(simPieces.get(0).type == Type.KING && simPieces.get(1).type == Type.KING 
                && simPieces.size() == 2){
            return true;
        }
        if(isKinginCheck() == false && kingCanMove(king) == false){
            opponentsPiece.clear();
            //System.out.println("\ntesting");
            for(Piece piece : simPieces){
                //if piece is other teams color
                if(piece.color != currentColor){
                    System.out.println("should print twice");
                    opponentsPiece.add(piece);
                }

            }
            for (Piece piece : opponentsPiece){
                for(int c = 0 ; c < 8; c++){
                    for(int r = 0; r < 8; r++){
                        if(piece.canMove(c, r) && isIllegal(king) == false){
                            //stalemate is not possible if 
                            return false;
                        }
                        else draw = true;
                    }
                }   
            }              
        }
        //returns not stalemate if king can move
        return draw;
    }

    private void checkCastling(){

        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col += 3;
            }
            else if(castlingP.col == 7){
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
            //reset blakcs two stepped status
            for(Piece piece : pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else{
            currentColor = WHITE;
            // reset whites two stepped status
            for(Piece piece : pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote(){

        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Knight(currentColor, 10, 2));
                promoPieces.add(new Bishop(currentColor, 10, 3));
                promoPieces.add(new Rook(currentColor, 10, 4));
                promoPieces.add(new Queen(currentColor, 10, 5));
                return true;
            }
        }

        return false;
    }
    
    private void promoting(){
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                    case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
                    case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
                    case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
                    case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
                    default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

//DRAWING EVERYTHING
//PAINT COMPENTNET METHOD
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        //board
        board.draw(g2);
        
        //pieces
    
        for(int i = 0; i < simPieces.size(); i++){
            simPieces.get(i).draw(g2);
        }

        //changes square where active piece is hovering
        
        
        if(activeP != null){
            
            // hover animation over where piece is
            // shows where piece wil go when dropped
            if(canMove){
                if(isIllegal(activeP) || opponentCanCaptureKing()){
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
                else{
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }  
            }
            // hover animation over last spot when picking up piece
            // easier to see where piece was before and where you can move it
            g2.setColor(Color.blue);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.fillRect(activeP.preCol*Board.SQUARE_SIZE, activeP.preRow*Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            
            //draw the piece, wont be hidden by board or by board hover
            activeP.draw(g2);
        }

        // STATUS MESSAGES
        // SHOWS WHICH TURNS IS WHICH
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion){
            g2.drawString("Promote to: ", 730, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), 
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }
        else{
            if(currentColor == WHITE){
                g2.drawString("White's Turn", 750, 500);
                if(checkingP != null && checkingP.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 750, 300);
                    g2.drawString("is in check!", 750, 350);
                }
            }
            else {
                g2.drawString("Black's Turn", 750, 150);
                if(checkingP != null && checkingP.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 750, 300);
                    g2.drawString("is in check!", 750, 350);
                }
            }
        }

        if(gameover){
            String s = "";
            
            if(currentColor == WHITE){
                s = "WHITE WINS!!!";
            }
            else{
                s = "BLACK WINS";
            }
            g2.setFont(new Font("Arial", Font.BOLD, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 400, 400);
        }
        if(stalemate){
            g2.setFont(new Font("Arial", Font.BOLD, 90));
            g2.setColor(Color.black);
            g2.drawString("DRAW!!!", 200, 400);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.setColor(Color.green);
            g2.drawString("its a stalemate", 200, 300);
        }
    }
}