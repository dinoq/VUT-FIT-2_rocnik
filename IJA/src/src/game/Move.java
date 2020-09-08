package game;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Move {

	private SimpleStringProperty white;
	private SimpleStringProperty black;
	private SimpleStringProperty number;

	private String whiteMove;
	private String blackMove;
	
	private int index;
	
    /**
     *
     * @param white neupravené bílé pohyby
     * @param black neupravené černé pohyby
     * @param number pořadí tahu
     * @param whiteMove upravené pohyby bilých
     * @param blackMove upravené pohyby černých
     */
    public Move(String white,String black, String number, String whiteMove,String blackMove) {
		this.white = new SimpleStringProperty(white);
		this.black = new SimpleStringProperty(black);
		this.number = new SimpleStringProperty(number);
		
		this.whiteMove = whiteMove;
		this.blackMove = blackMove;
		this.index = Integer.parseInt(number);
	}
	
    /**
     *
     * @return vrátí neupravené bílé pohyby
     */
    public StringProperty whiteMoveProperty() {
        return this.white;
    }
	
    /**
     *
     * @return vrátí neupravené černé pohyby
     */
    public StringProperty blackMoveProperty() {
        return this.black;
    }
	
    /**
     *
     * @return vrátí pořadí tahu
     */
    public StringProperty numberMoveProperty() {
        return this.number;
    }
	
    /**
     *
     * @return vrátí upravené pohyby bilých
     */
    public String getWhiteMove() {
		return this.whiteMove;
	}
	
    /**
     *
     * @return vrátí upravené pohyby černých
     */
    public String getBlackMove() {
		return this.blackMove;
	}
	
    /**
     *
     * @return index tohoto pohybu
     */
    public int getIndex() {
		return this.index;
	}
    
    /**
     *
     * @param mov co se má vložit do bílých pohybů
     */
    public void setWhiteMove(StringProperty mov) {
    	this.white = (SimpleStringProperty) mov;
    }
    
    /**
     *
     * @param mov co se má vložit do černých pohybů
     */
    public void setBlackMove(StringProperty mov) {
    	this.black = (SimpleStringProperty) mov;
    }
    
    /**
     *
     * @param mov co se má vložit do černých pohybů
     */
    public void setBlackMoveWithTag(String mov) {
    	this.blackMove = mov;
    }
    
    /**
     *
     * @return string s pomocnymi informacemi o move
     */
    public String printMove() {
    	return this.index + ". " + this.whiteMoveProperty().getValue() + " " + this.blackMoveProperty().getValue();
    }
}
