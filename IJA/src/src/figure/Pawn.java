package figure;

import game.Field;
import game.Field.Direction;
import game.Game;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Pawn extends Figure{
    /**
     * funkce na nastavení barvy figurky, její zobrazení a vybraní správného označení z Figures
     * @param c string pro natřídu
     * @see Figures
     */
	public Pawn(String c) {
		super(c, Figures.PAWN);
		if(this.color.equals("b")) {
			url = "file:lib/figures/pawn_b.png";
		}else {
			url = "file:lib/figures/pawn_w.png";
		}		
		this.setImage();
	}
    /**
     * Přepisuje funkci třídy Figure pro zjišťovaní dostupných pohybů
     * @param field pole o kterém se rozhodne, jestli se tam může figurka pohybovat
     * @return jestli se na dane pole může figurka přemístit
     * @see Figure
     */
	@Override
	public boolean canMoveTo(Field field) {
		boolean success = false;

		if(field.isEmpty()){
			if(this.color.equals(Game.WHITE)){
				if (this.field.getRow() == (field.getRow()-1) && field.getCol() == this.field.getCol()) {
					success = true;
				}else {
					if(this.field.getRow() == (field.getRow()-2) && field.getCol() == this.field.getCol() && this.field.getRow() == 2) {
						if(this.field.nextField(Direction.U).isEmpty()) {
							success = true;
						}else {
							success = false;
						}
					}else {
						success = false;
					}
				}				
			}else{
				if (this.field.getRow() == (field.getRow()+1) && field.getCol() == this.field.getCol()) {
					success = true;
				}else {
					if(this.field.getRow() == (field.getRow()+2) && field.getCol() == this.field.getCol() && this.field.getRow() == 7) {
						if(this.field.nextField(Direction.D).isEmpty()) {
							success = true;
						}else {
							success = false;
						}
					}else {
						success = false;
					}
				}
			}
		}else {
			if(field.getFigure().getColor().equals(this.getColor())) {
				success = false;
			}else {
				if(this.color.equals(Game.WHITE)){
					if(this.field.getRow() == (field.getRow()-1)) {
						if((this.field.getCol() == (field.getCol()+1)) || (this.field.getCol() == (field.getCol()-1))) {
                                                    if (field.getCol()==5 && field.getRow()==8){
                                                    }
							success = true;
						}
					}
				}else {
					if(this.field.getRow() == (field.getRow()+1)) {
						if((this.field.getCol() == (field.getCol()+1)) || (this.field.getCol() == (field.getCol()-1))) {
							success = true;
						}
					}
				}
			}
		}
		return success;
	}
}
