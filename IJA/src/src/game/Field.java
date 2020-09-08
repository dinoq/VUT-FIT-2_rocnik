package game;

import figure.Figure;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Field {

    /**
     * barva vybrané figurky
     */
	public static final Color CHOSEN_FIGURE = Color.LAWNGREEN;

    /**
     * barva polí kam může figurka jít
     */
	public static final Color CAN_MOVE_THERE_FIELD = Color.AQUA;

    /**
     * barva polí kde může figurka vyhazovat
     */
	public static final Color KICK_OUT_FIELD = Color.RED;

	Color color;
	private int col, row, boardSize;
	Rectangle rect, border;
	private Figure figure;
	private boolean empty;

	private Field fields[];


    /**
     * Výčet směrů možných pohybů figurek
     */
    public static enum Direction {
    	D, L, LD, LU, R, RD, RU, U

    }

    /**
     * nastavení jednoho pole - barva, velikost, číslo řádku a sloupce
     * @param col číslo sloupce
     * @param row číslo řádku
     */
    public Field(int col, int row) {
		this.col = col;
		this.row = row;
		this.empty = true;
		if((col+row) % 2 != 0) {
			color = Color.WHITE;				
		}else {
			color = Color.CADETBLUE; //Color.rgb(50, 50, 50);	
		}
		
		init();
		
		fields = new Field[Board.BOARD_SIZE];
	}

    /**
     * Funkce vrací objekt typu Rectangle, který slouží pro vykreslení
     * políčka.
     * @return Rectangle, který se má vykreslit
     */
    public Rectangle getRect() {
		return rect;
	}

    /**
     * Funkce vrací objekt typu Rectangle, který slouží pro vykreslení
     * obrysu políčka.
     * @return obrys, který se má vykreslit
     */
    public Rectangle getBorder() {
		return border;
	}
	
    /**
     * Nastaví obrys políčku (např. při kliknutí na políčko)
     * @param color - barva, jakou se má obrys políčka obarvit.
     */
    public void setBorder(Color color) {
		this.border.setStrokeWidth(10);
		this.border.setStroke(color);
	}
	
    /**
     * odstraní políčku obrys
     */
    public void clearBorder() {
		this.border.setStrokeWidth(0);
	}
	
    /**
     * Nastaví velikost desky
     * @param size - velikost desky, jaká se má nastavit
     */
    public void setBoardSize(int size) {
		boardSize = size;
		
	}
	
	private void init() {
		
		rect = new Rectangle();
		rect.setFill(this.color);			
		
	    rect.setX(col * Board.RECT_SIZE); 
	    rect.setY((9-row) * Board.RECT_SIZE); 
	    rect.setWidth(Board.RECT_SIZE); 
	    rect.setHeight(Board.RECT_SIZE);
	    
	    rect.setMouseTransparent(true);
		
		border = new Rectangle();
		border.setFill(this.color);		
		
		
		border.setX(col * Board.RECT_SIZE); 
		border.setY((9-row) * Board.RECT_SIZE); 
		border.setWidth(Board.RECT_SIZE); 
		border.setHeight(Board.RECT_SIZE);
	    
		border.setMouseTransparent(true);
		

		border.setFill(Color.TRANSPARENT);
		border.setStrokeWidth(0);
		border.setStroke(Color.LAWNGREEN);
	    
		
	}

    /**
     * Nastaví políčku informaci o tom, která figurka na něm stojí
     * @param figure - figurka, která se má políčku uložit
     */
    public void setFigure(Figure figure)
	{
		this.figure = figure;
		this.empty = false;
		figure.setField(this);
	}

    /**
     * Vrací figurku, která stojí na políčku.
     * @return vrací figurku stojící na políčku.
     */
    public Figure getFigure() {
		return this.figure;
	}
	
    /**
     * Vrací informaci o tom, jestli je políčko prázdné
     * @return - pokud je pole prázdné vrátí true jinak false
     */
    public boolean isEmpty() {
		return this.empty;
	}
	
    /**
     * Získání řádku aktuálního pole
     * @return řádek pole
     */
    public int getRow() {
		return this.row;
	}
    
    /**
     * Získání sloupce aktuálního pole
     * @return sloupec pole
     */
    public int getCol() {
		return this.col;
	}

    /**
     * Odstraní figurku z aktuálního pole
     */
    public void removeFigure() {
		this.figure = null;
		this.empty = true;
	}

    /**
     * Získání dalšího políčka podle směru zadaného parametrem
     * @param dirs směr od aktuálního pole
     * @return pole vedle aktulního pole
     */
    public Field nextField(Direction dirs) {
    	//Nejprve premapujeme smer na index a pak vratime pole na pozadovanem indexu v poli field
    	int index = getIdxFromDir(dirs);
        return fields[index];
    }

    /**
     * nastavení okolních polí
     * @param f pole, která jsou na hrací desce
     */
    public void setFields(Field[][] f){
    	int c = this.col - 1;
    	int r = this.row - 1;
    	
    	if((c>0) && (r>0)) {
        	this.addNextField(Field.Direction.LD, f[c-1][r-1]);
    	}else {
        	this.addNextField(Field.Direction.LD, null);
    	}
    	
    	if(r>0) {
        	this.addNextField(Field.Direction.D, f[c][r-1]);
    	}else {
        	this.addNextField(Field.Direction.D, null);
    	}
    	
    	if((c< (this.boardSize-1)) && (r>0)) {
        	this.addNextField(Field.Direction.RD, f[c+1][r-1]);
    	}else {
        	this.addNextField(Field.Direction.RD, null);
    	}
    	
    	if(c < (this.boardSize-1)) {
        	this.addNextField(Field.Direction.R, f[c+1][r]);
    	}else {
        	this.addNextField(Field.Direction.R, null);
    	}
    	
    	if((c < (this.boardSize-1)) && (r < (this.boardSize-1))) {
        	this.addNextField(Field.Direction.RU, f[c+1][r+1]);
    	}else {
        	this.addNextField(Field.Direction.RU, null);
    	}
    	
    	if(r < (this.boardSize-1)) {
        	this.addNextField(Field.Direction.U, f[c][r+1]);
    	}else {
        	this.addNextField(Field.Direction.U, null);
    	}
    	
    	if((c > 0) && (r < (this.boardSize-1))) {
        	this.addNextField(Field.Direction.LU, f[c-1][r+1]);
    	}else {
        	this.addNextField(Field.Direction.LU, null);
    	}
    	
    	if(c > 0) {
        	this.addNextField(Field.Direction.L, f[c-1][r]);
    	}else {
        	this.addNextField(Field.Direction.L, null);
    	}
    	
    }
    
    private int getIdxFromDir(Direction dir) {
    	int idx = 0;
    	
    	switch(dir) {
		case D:
			idx = 3;
			break;
		case L:
			idx = 1;
			break;
		case LD:
			idx = 0;
			break;
		case LU:
			idx = 2;
			break;
		case R:
			idx = 6;
			break;
		case RD:
			idx = 5;
			break;
		case RU:
			idx = 7;
			break;
		case U:
			idx = 4;
			break;
		default:
			break;
    	
    	}
    	
    	return idx;
    }

    /**
     * Přidání vedlejsího pole
     * @param dirs směr pole
     * @param field pole vedle kterého se to nové má položit
     */
    public void addNextField(Direction dirs, Field field) {
    	//nejprve je premapovansmer na index a potom se ulozi pole field na ziskany index
    	int index = getIdxFromDir(dirs);
        this.fields[index] = (Field) field;
    }

    /**
     * pomocná funkce na tisknutí pole
     * @return string s několik informacemi o aktulním poli
     */
    public String printField() {
    	return "Pole je volné: "+this.empty+" a stojí na: col:"+this.col+", row: "+this.row;
    }

}
