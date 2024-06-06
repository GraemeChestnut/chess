package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece {
    
   public Queen (int color, int col, int row){
      super(color, col, row);

      type = Type.QUEEN;
        
      if(color == GamePanel.WHITE){
         image = getImage("../resources/piece/w-queen");
      }
      else {
         image = getImage("../resources/piece/b-queen");
      }
   }

   public boolean canMove(int targetCol, int targetRow){

      if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
         if((targetCol == preCol || targetRow == preRow) || (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow))){
            
            if((isValidSquare(targetCol, targetRow)) 
            // checks for both diagonal and straight since queen is like rook and bishop 
            && (pieceIsOnDiagonalLine(targetCol, targetRow) || pieceIsOnStraightLine(targetCol, targetRow)) == false){

               return true;
            }
         }
      }

      return false;
   }
}
