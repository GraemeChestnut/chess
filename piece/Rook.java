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
}
