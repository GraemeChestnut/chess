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

            
            //checks if is moving left, right, up, or down. 
            //checking if one because king can only move one square at a time
            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 
                //checks if is moving diagonal 
                || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1){
                return true;
            }


        }
        return false;
    }
}
