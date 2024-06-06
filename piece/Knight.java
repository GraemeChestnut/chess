package piece;

import main.GamePanel;

public class Knight extends Piece {

    public Knight (int color, int col, int row){
            super(color, col, row);
            
            if(color == GamePanel.WHITE){
                image = getImage("../resources/piece/w-knight");
            }
            else {
                image = getImage("../resources/piece/b-knight");
            }
        }
        
    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow)){

        }
        return false;
    }

}
