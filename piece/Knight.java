package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece {

    public Knight (int color, int col, int row){
            super(color, col, row);
            
            type = Type.KNIGHT;

            if(color == GamePanel.WHITE){
                image = getImage("../resources/piece/w-knight");
            }
            else {
                image = getImage("../resources/piece/b-knight");
            }
        }
        
    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow)){
            //knight move ration is 2:1 or 1:2
            //col * row must be 2 in order for knight to move there
            if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
                
            }
        }
        return false;
    }

}
