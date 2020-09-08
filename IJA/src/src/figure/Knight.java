package figure;

import game.Field;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Knight extends Figure{
    /**
     * funkce na nastavení barvy figurky, její zobrazení a vybraní správného označení z Figures
     * @param c string pro natřídu
     * @see Figures
     */
	public Knight(String c) {
		super(c, Figures.KNIGHT);
		if(this.color.equals("b")) {
			url = "file:lib/figures/knight_b.png";
		}else {
			url = "file:lib/figures/knight_w.png";
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
		
		
                if(field.getFigure()!=null && field.getFigure().getColor().equals(this.getColor())) {
                            success = false;
                }else{
                        if (this.field.getRow() == (field.getRow()-1) && (field.getCol()+2) == this.field.getCol()) {
                                success = true; 
                        }else if(this.field.getRow() == (field.getRow()-1) && (field.getCol()-2) == this.field.getCol()) {
                                success = true;
                        }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()-2) == this.field.getCol()) {
                                success = true;
                        }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()+2) == this.field.getCol()) {
                                success = true;
                        }else if(this.field.getRow() == (field.getRow()-2) && (field.getCol()-1) == this.field.getCol()) {
                                success = true;
                        }else if(this.field.getRow() == (field.getRow()-2) && (field.getCol()+1) == this.field.getCol()) {
                                success = true;
                        }else if(this.field.getRow() == (field.getRow()+2) && (field.getCol()-1) == this.field.getCol()) {
                                success = true;
                        }else if(this.field.getRow() == (field.getRow()+2) && (field.getCol()+1) == this.field.getCol()) {
                                success = true;
                        }else {
                            success = false;
                        }

		}
		return success;
	}

}
