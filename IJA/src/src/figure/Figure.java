package figure;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import game.Board;
import game.Field;
import game.Game;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Figure {
	
	String url;
	protected String color;
	ImageView img;
	Field field;
	protected Figures type;
	
    /**
     * Nastavení figurky
     * @param color barva figurky
     * @param fig typ figurky
     */
    public Figure(String color, Figures fig) {
		this.color = color;	
		type = fig;
	}
	
    /**
     * Vyčte číslo sloupce
     * @return číslo sloupce
     */
    public int getXPos() {
		return field.getCol();
	}

    /**
     * Vyčte číslo řádku
     * @return číslo řádku
     */
    public int getYPos() {
		return field.getRow();
	}
	
    /**
     * Zjištění obrázku figurky pro zobrazení
     * @return obrázek figurky
     * @see ImageView
     */
    public ImageView getImage() {

	    img.setTranslateX(this.field.getCol() * Board.RECT_SIZE);
	    img.setTranslateY((9-this.field.getRow()) * Board.RECT_SIZE);
	    return img;
	    
	}
	
    /**
     *  Nastavení obrázku figurky podle jejího typu
     *  
     */
    protected void setImage() {

		Image image = new Image(url);
		 
	    // simple displays ImageView the image as is
	    img = new ImageView();
	    img.setImage(image);
	    
	    img.setFitWidth(Board.RECT_SIZE);
	    img.setFitHeight(Board.RECT_SIZE);
	    
	    img.setMouseTransparent(true);
	}

    /**
     * Nastaví figurce obrázek podle barvy parametru. Slouží při proměně pěšce v 
     * jinou figurku - k nastavení správného obrázku barvy
     * @param color barva, podle které se načte obrázek figurky
     */
    public void setImage(String color) {

		Image image = new Image(url.substring(0, url.length()-5)+color+".png");
	    // simple displays ImageView the image as is
	    img = new ImageView();
	    img.setImage(image);
	    
	    img.setFitWidth(Board.RECT_SIZE);
	    img.setFitHeight(Board.RECT_SIZE);
	    
	    img.setMouseTransparent(true);
	}

    /**
     * Nastaví figurce barvu (a tudíž určue hráče ke kterému patří). Slouží při proměně pěšce v 
     * jinou figurku - k nastavení její barvy
     * @param color barva, která se figurce nastaví
     */
    public void setColor(String color) {
    	this.color = color;
    }
    
    /**
     * Zjišťování pohybu figurky
     * @param field pole o kterém se rozhodne jestli se tam figurka může pohnout
     * @return jestli se na dané pole může figurka přemístit
     */
    public boolean canMoveTo(Field field) {
		return false;
	}

    /**
     * Pohyb figurky na zadané pole
     * @param field pole kam se figurka má pohnout
     */
    public void moveTo(Field field) {
		this.field.removeFigure();
		field.setFigure(this);
		this.setField(field);
		//System.out.println(this.getXPos()+"|||"+this.getYPos());					
		//System.out.println("HELE:"+field.getFigure().getXPos()+" "+field.getFigure().getYPos());
	}

    /**
     * Nastaví pole figurce
     * @param field pole které se nastaví této figurce
     */
    public void setField(Field field) {
		this.field = field;
	}

    /**
     * Vyčtení barvy figurky
     * @return barvu figurky
     */
    public String getColor() {
		return this.color;
	}

    /**
     * Zjištění typu figurky
     * @return typ figurky
     * @see Figures
     */
    public Figures getType() {
		return this.type;
	}
	
    /**
     * Vyčtení pole figurky
     * @return vrátí pole figurky
     * @see Field
     */
    public Field getField() {
		return this.field;
	}
	
    /**
     * Zjišťovaní, jestli může daná figurka vyhodit někoho na daném políčku
     * @param field pole, na kterém může vyhazovat
     * @return jestli může vyhodit figurku na poli zadané parametrem
     */
    public boolean canKickOut(Field field) {
		boolean success;
        if (!field.isEmpty()&& canMoveTo(field)){
            success=true;
        }else{
            success=false;
        }
        return success;
	}

    /**
     * Pomocná funkce na výpis užitečných informací
     * @return typ, barvu, sloupec, řádek figurky
     */
    public String printFigure() {
    	return "Figure: "+this.getType() + ", color: "+this.getColor()+", sloupec: "+this.getField().getCol()+"("+Game.charFromCol(this.getField().getCol())+"), řádek: "+this.getField().getRow();
    }
}
