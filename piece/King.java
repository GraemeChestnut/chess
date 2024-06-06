package piece;

import main.GamePanel;

public class King extends Piece {

    public King (int color, int col, int row){
            super(color, col, row);
            
            if(color == GamePanel.WHITE){
                image = getImage("../resources/piece/w-king");
            }
            else {
                image = getImage("../resources/piece/b-king");
            }
        }

    public boolean canMove(int targetCol, int targetRow){
        
        
        if(isWithinBoard(targetCol, targetRow)){

            ////// BASIC MOVEMENT //////
            //checks if is moving left, right, up, or down. 
            //checking if one because king can only move one square at a time
            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 
                //checks if is moving diagonal 
                || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1){
                
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }

            // Castling 
            if(moved == false){

                //right castle
                if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    for(Piece piece : GamePanel.simPieces){
                        if(piece.col == preCol + 3 && piece.row == preRow && piece.moved == false){
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }

                //left castle

                if(targetCol == preCol - 2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    Piece p[] = new Piece[2];
                    for(Piece piece : GamePanel.simPieces){
                        if(piece.col == preCol - 3 && piece.row == targetRow){
                            p[0] = piece;
                        }
                        if(piece.col == preCol - 4 && piece.row == targetRow){
                            p[1] = piece;
                        }
                        if(p[0] == null && p[1] != null && p[1].moved == false) {
                            GamePanel.castlingP = p[1];
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

}
