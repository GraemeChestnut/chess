package piece;

import main.GamePanel;

public class Rook extends Piece{
    public Rook (int color, int col, int row){
        super(color, col, row);
        
         if(color == GamePanel.WHITE){
            image = getImage("../resources/piece/w-rook");
         }
         else {
            image = getImage("../resources/piece/b-rook");
         }
    }

   public boolean canMove(int targetCol, int targetRow){

      if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
         // rooks col or row value cannot change at the same time
         // rook only move horizontal or veritcal
         if(targetCol == preCol || targetRow == preRow){
            if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
               return true;
            }
         }
      }
      return false;
   }
}
