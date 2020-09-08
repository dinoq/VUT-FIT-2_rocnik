package game;


import assets.MoveParser;
import figure.Bishop;
import figure.Figure;
import figure.Figures;
import figure.King;
import figure.Pawn;
import figure.Rook;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

// TODO: Auto-generated Javadoc
/**
 * The Class Board.
 *
 * @author xfridr07 a xmarek69
 */
public class Board {

	/** The Constant RECT_SIZE. */
	public static final float RECT_SIZE = 64.0f;
	
	/** The Constant BOARD_SIZE. */
	public static final int BOARD_SIZE = 8;

	/** The pane. */
	Pane pane;
	
	/** The fields. */
	private Field[][] fields;
	
	/** The active borders. */
	private Field[] activeBorders;
	
	/** The chosen field. */
	private Field chosenField;
	
	/** The active borders index. */
	private int activeBordersIndex;
	
	/** The player playing. */
	private String playerPlaying;
	
	/** The is check. */
	private boolean isCheck[];

	private Move lastMove;
	private Move lastReverseMove;
	
	private Figure deletedFigures[];
	private int deletedFiguresIndex;
	
	private boolean isSachMat;
	
	private boolean sameFieldClicked;
	
	/** Rozhoduje, ktery hrac je v sachu. Pokud zadny, tak je ulozena hodnota -1 */
	private int check;

	private Field changeWhiteOnField;
	private Field changeBlackOnField;
	
	
    /**
     * Instantiates a new board.
     *
     * @param p the p
     */
    public Board(Pane p) {
		pane = p;
		chosenField = null;
		
		isCheck = new boolean[2];
		isCheck[0] =  false;
		isCheck[1] =  false;
		
		this.initBoard();
		this.initGame();
		
		lastMove = null;
		changeWhiteOnField = null;
		changeWhiteOnField = null;
		deletedFigures = new Figure[32];
		deletedFiguresIndex = 0;
	}
	
	/**
	 * Inits the game.
	 */
	private void initGame() {
		playerPlaying = Game.WHITE;
		activeBordersIndex = 0;
		check = -1;
	}
	
	/**
	 * Inits the board.
	 */
	private void initBoard() {
		fields = new Field[BOARD_SIZE][BOARD_SIZE];
		
		activeBorders = new Field[BOARD_SIZE*BOARD_SIZE*2];
		
    	for(int i = 0; i < BOARD_SIZE; i++){
    		for(int j = 0; j < BOARD_SIZE; j++){
    			fields[i][j] = new Field(i+1, j+1);
    			fields[i][j].setBoardSize(BOARD_SIZE);

    			this.pane.getChildren().add(fields[i][j].getRect());
    			
    		}
    		
    	}
    	

    	//Nastavi okolni pole pro kazde z poli
    	for(int i = 0; i < BOARD_SIZE; i++){
    		for(int j = 0; j < BOARD_SIZE; j++){
    			fields[i][j].setFields(fields);
    		}
    	}

		String pismena[] = {"a", "b", "c", "d", "e", "f", "g", "h"};
		Text description;
    	for(int i = 0; i < BOARD_SIZE; i++){
    		description = new Text(Integer.toString(8-i));
    		description.setLayoutX(RECT_SIZE*((double) 3/4));
    		description.setLayoutY(RECT_SIZE*(1.5+i));

			this.pane.getChildren().add(description);

    		description = new Text(pismena[i]);
    		description.setLayoutX(RECT_SIZE*(i+1.5));
    		description.setLayoutY(RECT_SIZE*(9+ (double) 1/4));

			this.pane.getChildren().add(description);
    	}
		
	}

    /**
     * Gets the pane.
     *
     * @return the pane
     */
    public Pane getPane() {
		// TODO Auto-generated method stub
		return this.pane;
	}

    /**
     * Gets the field.
     *
     * @param col the col
     * @param row the row
     * @return the field
     */
    public Field getField(int col, int row) {
		//System.out.println("SS:"+fields[col-1][row-1].getCol()+"|"+fields[col-1][row-1].getRow());
		return fields[col-1][row-1];
	}
	

    /**
     * Chose figure.
     *
     * @param col the col
     * @param row the row
     * @return true, if successful
     */
    public boolean choseFigure(int col, int row) {
    	//System.out.println("FIG");
		
		boolean success = false;
		//System.out.println("in choseFigure "+col+" "+row);
		
		if(!getField(col, row).isEmpty() && getField(col, row).getFigure().getColor().equals(playerPlaying)) {
			getField(col, row).setBorder(Field.CHOSEN_FIGURE);
			chosenField = getField(col, row);

			//Pokud se nemuze figurka pohnout na zadne policko, tak nakonec odstranime ramecek znacici, ze byla oznacena
    		boolean canMoveAnywhere = false;
			for(int i = 1; i <= Board.BOARD_SIZE; i++) {
				for(int j = 1; j <= BOARD_SIZE; j++){
					if(chosenField.getFigure().canMoveTo(getField(i, j))) {
						
				    	if(isCheck[getAnotherPlayerThanPlaying()]) {
							success = true;

				    		if(canSaveKing(getField(i, j), getCurrentPlayerPlayingAsString())) {
				    			getField(i, j).setBorder(Field.CAN_MOVE_THERE_FIELD);
								activeBorders[activeBordersIndex++] = getField(i, j);
								
								if((chosenField.getFigure()).canKickOut(getField(i, j)) && !getField(i, j).isEmpty() && !chosenField.getFigure().getColor().equals(getField(i, j).getFigure().getColor())){
									
									//Vyzkousej kde vsude muze figurka vyhazovat a obarvi tato policka cervene
									getField(i, j).clearBorder();
									getField(i, j).setBorder(Field.KICK_OUT_FIELD);	
									activeBorders[activeBordersIndex-1] = getField(i, j);	
									
								}	
				    		}
				    	}else if(canSaveKing(getField(i, j), getCurrentPlayerPlayingAsString())){
							canMoveAnywhere = true;
							success = true;
							//Vyzkousej kam vsude muze figurka a obarvy policka zlute
							getField(i, j).setBorder(Field.CAN_MOVE_THERE_FIELD);
							activeBorders[activeBordersIndex++] = getField(i, j);
	
							if((chosenField.getFigure()).canKickOut(getField(i, j)) && !getField(i, j).isEmpty() && !chosenField.getFigure().getColor().equals(getField(i, j).getFigure().getColor())){
	
								//Vyzkousej kde vsude muze figurka vyhazovat a obarvi tato policka cervene
								getField(i, j).clearBorder();
								getField(i, j).setBorder(Field.KICK_OUT_FIELD);	
								activeBorders[activeBordersIndex-1] = getField(i, j);	
								
							}	
				    	}
					}else if(isCheck[getAnotherPlayerThanPlaying()]) {
						success = true;
						//System.out.println("ZDE8");
						//System.out.println("řekl bych že sem to nikdy nedojde");
			    		//Tenhle dostal sach

			    		/*
			    		canSaveKing(getField(i, j));
			    		//System.out.println("else: JSEM V šachu: "+playerPlaying);
						//System.out.println("ch f :"+chosenField.printField());
						//System.out.println("ch f fig:"+chosenField.getFigure());
						//System.out.println("cil:"+getField(i, j).printField());*/
			    		
			    	}else if(isCheck[getCurrentPlayerPlaying()]) {
						//success = true;
						//System.out.println("No tohlr prave");
						//System.out.println("řekl bych že sem to nikdy nedojde");
			    		//Tenhle dostal sach

			    		/*
			    		canSaveKing(getField(i, j));
			    		//System.out.println("else: JSEM V šachu: "+playerPlaying);
						//System.out.println("ch f :"+chosenField.printField());
						//System.out.println("ch f fig:"+chosenField.getFigure());
						//System.out.println("cil:"+getField(i, j).printField());*/
			    		
			    	}else {//figurka se nemuze nikam hnout a neni sach

						//System.out.println("ZDE7");
			    	}
				}
			}
			
			if(!canMoveAnywhere) {
				success = true;
			}
			this.repaint();		
		}
		return success;
		
	}
    
    
    /**
     * Chose field.
     *
     * @param col sloupec pole
     * @param row řádek pole
     * @param moveIndex index ole
     * @return true, jestli pravda
     */
    public boolean choseField(int col, int row, int moveIndex) {
    	//System.out.println("FIELD");
    	
    	boolean success = false;

    	sameFieldClicked = false;
		if(chosenField == getField(col, row)) {
			this.removeBorders();
			//System.out.println("JEa");
			sameFieldClicked = true;
			success = true;
		}else if(getField(col, row).getFigure() != null && getField(col, row).getFigure().getColor() == playerPlaying) {
			//System.out.println("LLLLAAA");

			removeBorders();

			this.repaint();	
			
			success = !this.choseFigure(col, row);
				
			this.repaint();	
			/*
			//System.out.println("NEMUZE");
			//System.out.println(chosenField.col);
			//System.out.println(chosenField.row);
			//System.out.println(getField((int) col,(int) row).col);
			//System.out.println(getField((int) col,(int) row).row);*/
		}else if(isCheck[getAnotherPlayerThanPlaying()] && !canSaveKing(getField(col, row), getCurrentPlayerPlayingAsString())/* && !canSaveKing(getField(col, row))*/) {
    		
			//Tenhle dostal sach

    		//Byl sice sach, ale figurka se na kliknutou pozici smi hnout (zrusi tim sach)
			//System.out.println("JSEM ZDe");
			
    		
    	}else if(chosenField.getFigure().canMoveTo(getField(col, row))
    			&& (canSaveKing(getField(col, row), getCurrentPlayerPlayingAsString()))) {//Pokud se na policko normalne figurka muze pohnout nebo je sach a pohybem na policko se vyjde ze sachu
				
			isCheck[getAnotherPlayerThanPlaying()] = false;


			String takeFlag = "";
			if(!getField(col, row).isEmpty()) {
				takeFlag = "x";
				this.deletedFigures[deletedFiguresIndex++] = getField(col, row).getFigure();
				//System.out.println("DELETE INDEXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+deletedFiguresIndex);
				//System.out.println(this.deletedFigures[deletedFiguresIndex-1]);
			}

			
			if(!getField(col, row).isEmpty()) {
				this.pane.getChildren().remove(getField(col, row).getFigure().getImage());
				getField(col, row).removeFigure();
			}

			
			this.removeBorders();
			
			chosenField.getFigure().moveTo(getField(col, row));		

			setChangePawn(getField(col, row));
			
			//chosenField.removeFigure();
			
			isCheck[getAnotherPlayerThanPlaying()] = false;
			isCheck[getAnotherPlayerThanPlaying()] = isInCheck(getAnotherPlayerThanPlayingAsString());
			//System.out.println("ChOSEFIELD IN CHECK:||||||||||||||||||||||||||||"+isInCheck(getAnotherPlayerThanPlayingAsString()));
			//System.out.println("ChOSEFIELD IN CHECK:||||||||||||||||||||||||||||"+isInCheck(getCurrentPlayerPlayingAsString()));

			String checkFlag = "";
			String mateFlag = "";
			if(isCheck[getAnotherPlayerThanPlaying()]) {
				//Je sach a musis vyhodnotit jestli neni i mat
				check = getAnotherPlayerThanPlaying();
				checkFlag = "+";
				
				if(isCheckMate()) {
					checkFlag = "";
					mateFlag = "#";
				}
			}else {
				check = -1;
			}
			

			
			
			if(getCurrentPlayerPlayingAsString().equals(Game.WHITE)) {
				String str = getField(col, row).getFigure().getType().getFirstCharOfName() + takeFlag + Game.charFromCol(chosenField.getCol())+String.valueOf(chosenField.getRow())+Game.charFromCol(col)+String.valueOf(row) + checkFlag + mateFlag;
				lastMove = new Move(str, "" , String.valueOf(moveIndex+1),  MoveParser.parseString(str), "");
				lastReverseMove = getReverseMoveFromMove(MoveParser.parseString(str), getCurrentPlayerPlaying(), moveIndex);
			}else {
				String str = getField(col, row).getFigure().getType().getFirstCharOfName() + takeFlag + Game.charFromCol(chosenField.getCol())+String.valueOf(chosenField.getRow())+Game.charFromCol(col)+String.valueOf(row) + checkFlag + mateFlag;
				lastMove = new Move("", str, String.valueOf(moveIndex+1), "" , MoveParser.parseString(str));
				lastReverseMove = getReverseMoveFromMove(MoveParser.parseString(str), getCurrentPlayerPlaying(), moveIndex);
			}
			
			

			changePlayer();
			success = true;
			this.repaint();		
		}else {

		}
		
		/*Kontrola jestli neni sach mat - nastavime promennou sachMat na true a jestli se 
		 *ve funkci canSaveKing nezmeni na false, tak se kral uz nemuze pohnout ani nemuze byt vyhozena 
		 *ohrozujici figurka, tudiz je sach mat
		 * 
		*/
		if(success) {
			chosenField = null;			
		}
		return success;
	}
	

	private Move getReverseMoveFromMove(String parseString, int player, int moveIdx) {
		String strMove = "";
		
		if(player == Game.WHITE_MOVE) {
                    int lastIdx = parseString.length();
                    if ("p".equals(parseString.substring(parseString.length()-5, parseString.length()-4)) && "8".equals(parseString.substring(parseString.length()-1, parseString.length()))){
                               strMove = parseString.substring(0, lastIdx-5)+"CHANGEV p"+parseString.substring(lastIdx-2, lastIdx)+parseString.substring(lastIdx-4, lastIdx-2);
                               if (parseString.contains("NOTAG")){
                                   strMove = "CHANGEV p"+parseString.substring(lastIdx-2, lastIdx)+parseString.substring(lastIdx-4, lastIdx-2);
                               }                       
                        }
                    else if(parseString.contains("NOTAG") || parseString.contains("TAKE")) {
				
				strMove = parseString.substring(0, lastIdx-4)+parseString.substring(lastIdx-2, lastIdx)+parseString.substring(lastIdx-4, lastIdx-2);
			}
			return new Move(strMove, "", String.valueOf(moveIdx), strMove, "");
		}else {
                    int lastIdx = parseString.length();
                    if ("p".equals(parseString.substring(parseString.length()-5, parseString.length()-4)) && "1".equals(parseString.substring(parseString.length()-1, parseString.length()))){
                               strMove = parseString.substring(0, lastIdx-5)+"CHANGEV p"+parseString.substring(lastIdx-2, lastIdx)+parseString.substring(lastIdx-4, lastIdx-2);
                               if (parseString.contains("NOTAG")){
                                   strMove = "CHANGEV p"+parseString.substring(lastIdx-2, lastIdx)+parseString.substring(lastIdx-4, lastIdx-2);
                               }                      
                        }
                    else if(parseString.contains("NOTAG") || parseString.contains("TAKE")) {
				
				strMove = parseString.substring(0, lastIdx-4)+parseString.substring(lastIdx-2, lastIdx)+parseString.substring(lastIdx-4, lastIdx-2);
			}
			return new Move("", strMove, String.valueOf(moveIdx), "", strMove);
		}
	}

	private void setChangePawn(Field destination) {
		if(getCurrentPlayerPlayingAsString().equals(Game.WHITE)) {
			if(destination.getRow() == 8 && destination.getFigure().getType().equals(Figures.PAWN)) {
				changeWhiteOnField = destination;
			}
		}else {
			if(destination.getRow() == 1 && destination.getFigure().getType().equals(Figures.PAWN)) {
				changeBlackOnField = destination;
			}
		}
	}

	/**
	 * isCheckMate
	 */
	private boolean isCheckMate() {
		Field tmp = null;
		isSachMat = true;
		Field puvodniChosenField = chosenField;
    
		for(int i = 1; i <= Board.BOARD_SIZE; i++) {
			for(int j = 1; j <= BOARD_SIZE; j++){
				tmp = getField(i, j);
				if(tmp.getFigure() != null && !tmp.getFigure().getColor().equals(playerPlaying)) {
					chosenField = tmp;

		    		for(int k = 1; k <= Board.BOARD_SIZE; k++) {
		    			for(int l = 1; l <= BOARD_SIZE; l++){

							
							
		    				if(tmp != getField(k, l) && tmp.getFigure().canMoveTo(getField(k, l))) {
		    					
		    					isSachMat = !canSaveKing(getField(k, l), getAnotherPlayerThanPlayingAsString());
			    				if(!isSachMat) {
			    					break;
			    				}
		    				}
		    			}
	    				if(!isSachMat) {
	    					break;
	    				}
		    		}
				}
				if(!isSachMat) {
					break;
				}
			}
			if(!isSachMat) {
				break;
			}
		}
		chosenField = puvodniChosenField;
    	
		
		return isSachMat;		
	}
	/**
	 * Removes the borders.
	 */
	private void removeBorders() {
		chosenField.clearBorder();
		
		for(int i = activeBordersIndex+1; i >= 0; i--) {
			if(activeBorders[i] != null) {
				activeBorders[i].clearBorder();
			}
		}
		
		activeBordersIndex = 0;
		
	}
	
    /**
     * Repaint.
     */
    public void repaint() {
		for(int i = 0; i < Board.BOARD_SIZE; i++) {
			for(int j = 0; j < BOARD_SIZE; j++){
				//this.pane.getChildren().remove(fields[i][j].getRect());
				this.pane.getChildren().remove(fields[i][j].getBorder());
				
				Figure fig = fields[i][j].getFigure();
				if(fig != null) {
					ImageView img = fig.getImage();
					this.pane.getChildren().remove(img);
				}
				
				//add
				this.pane.getChildren().add(fields[i][j].getBorder());

				if(fig != null) {
					ImageView img = fig.getImage();
					this.pane.getChildren().add(img);
				}
			}
		}
	}
	
    /**
     * Change player.
     */
    public void changePlayer() {
		if(this.playerPlaying.equals(Game.WHITE)) {
			playerPlaying = Game.BLACK;
		}else {
			playerPlaying = Game.WHITE;			
		}
	}
	
    
    
    
    /**
     * Metoda zjisti, jestli je mozne dat sach z policka predavaneho parametrem. Vyhodnoti jestli muze figurka 
     * stojici na zadanem policku (parametr dest) dat sach protihraci.
     *
     * @param dest Policko na kterem stoji figurka pro kterou zjistujeme jestli muze dat sach
     * @return true, pokud muze dat figurka stojici na poli dest sach
     */
    private boolean isInCheck(String playerColorTested) {
    	boolean success = false;
    	/*
    	 * Zde ziskame krale zvoleneho hrace
    	 */
    	Figure king = null;
    	for(int i = 0; i < BOARD_SIZE; i++){
    		boolean breakCycle = false;
    		
    		for(int j = 0; j < BOARD_SIZE; j++){
    			king = fields[i][j].getFigure();
    			if(king != null) {
    				if(king.getColor().equals(playerColorTested) && king.getType().equals(Figures.KING)) {
    					breakCycle = true;
    					break;
    				}
    			}
    		}
    		
    		if(breakCycle) {
    			break;
    		}
    	}
    	
    	/*
    	 * Zde otestujeme, zda je kral zvoleneho krale v sachu (ohrozen)
    	 */
    	Field f;
    	Figure opponentsFigure;
    	for(int i = 1; i <= Board.BOARD_SIZE; i++) {
			for(int j = 1; j <= BOARD_SIZE; j++){
				f = getField(i, j);
				opponentsFigure = f.getFigure();
				if(opponentsFigure != null && !opponentsFigure.getColor().equals(playerColorTested)) {
					if(king != null && opponentsFigure.canMoveTo(king.getField())) {
						success = true;
					}
				}
			}
    	}
		//System.out.println("isInCheck: "+success);
    	return success;
    }


	/**
	 * Metoda zjisti, jestli pri pohybu jiz zvolene figurky (ta ktera stoji na policku chosenField) 
	 * na policko field zachrani krale pred sachem.
	 *
	 * @param field the field
	 * @return true, if successful
	 */
	private boolean canSaveKing(Field field, String player) {
		//System.out.println("Z: "+chosenField.printField()+" DO: "+field.printField()+" | Může: "+chosenField.getFigure().canMoveTo(field));
		boolean success = false;
		boolean isStillCheck = false;
                boolean vratmovedking=false;
                boolean vratmovedrook=false;

		Figure deletadFigure = null;
		
		if(chosenField.getFigure()!= null) {
                    
                        /*
			 * Musime zkontrolovat jestli se na poli nenachazi figurka a pokud ano tak 
			 * si ji musime ulozit abychom ji mohli pozdeji zpet vlozit, protoze moveTo() 
			 * premaze puvodni figurku
			*/
			if(!field.isEmpty()) {
				deletadFigure = field.getFigure();
			}
                        if (chosenField.getFigure().getType()==Figures.KING && !((King)chosenField.getFigure()).moved){
                            vratmovedking=true;
                            ((King)chosenField.getFigure()).moved=true;
                        }
                        if (chosenField.getFigure().getType()==Figures.ROOK && !((Rook)chosenField.getFigure()).moved){
                            vratmovedrook=true;
                            ((Rook)chosenField.getFigure()).moved=true;
                        }
			chosenField.getFigure().moveTo(field);

			isStillCheck = isInCheck(player);

			field.getFigure().moveTo(chosenField);
                        if(vratmovedking){
                            ((King)chosenField.getFigure()).moved=false;
                        }
                        if(vratmovedrook){
                            ((Rook)chosenField.getFigure()).moved=false;
                        }
			if(deletadFigure != null) {
				deletadFigure.moveTo(field);
			}
			
			if(!isStillCheck && chosenField.getFigure().canMoveTo(field)) {
				isSachMat = false;
			}
		}
		
		return !isStillCheck;
	}
	
	/**
	 * Gets the another player than playing.
	 *
	 * @return the another player than playing
	 */
	public int getAnotherPlayerThanPlaying() {
		if(playerPlaying.equals(Game.WHITE)) {
			return Game.BLACK_MOVE;
		}else {
			return Game.WHITE_MOVE;
		}
	}
	
	/**
	 * Gets the another player than playing.
	 *
	 * @return the another player than playing
	 */
	public String getAnotherPlayerThanPlayingAsString() {
		if(playerPlaying.equals(Game.WHITE)) {
			return Game.BLACK;
		}else {
			return Game.WHITE;
		}
	}
	
	/**
	 * Gets the current player playing.
	 *
	 * @return the current player playing
	 */
	public int getCurrentPlayerPlaying() {
		if(playerPlaying.equals(Game.WHITE)) {
			return Game.WHITE_MOVE;
		}else {
			return Game.BLACK_MOVE;
		}
	}
	
	/**
	 * Gets the current player playing.
	 *
	 * @return the current player playing
	 */
	public String getCurrentPlayerPlayingAsString() {
		if(playerPlaying.equals(Game.WHITE)) {
			return Game.WHITE;
		}else {
			return Game.BLACK;
		}
	}
	
	private Figure[] getOpponentsFigure() {
		Figure[] figs = new Figure[16];
		int index = 0;
		
		for(int i = 1; i <= Board.BOARD_SIZE; i++) {
			for(int j = 1; j <= BOARD_SIZE; j++){
				if(!getField(i, j).isEmpty() && !getField(i, j).getFigure().getColor().equals(playerPlaying)) {
					figs[index++] = getField(i, j).getFigure();
				}
			}
		}
		
		return figs;
	}

	public Move getLastMove() {
		return lastMove;
	}

	public Move getLastReverseMove() {
		return lastReverseMove;
	}

	public boolean getIsSachMat() {
		return isSachMat;
	}

	/**
	 * Vraci informaci o tom, ktery hrac je v sachu. Pokud bily, vraci se Game.WHITE_MOVE, 
	 * pokud cerny, tak se vraci Game.BLACK_MOVE. Pokud neni v sachu zadny hrac, tak se vraci -1
	 *
	 * @return the in check
	 */
	public int getInCheck() {
		return this.check;
	}
	
	public Field getChosenField() {
		return this.chosenField;
	}
	
	public boolean getSameFieldClicked() {
		return this.sameFieldClicked;
	}
	
	public int changePawn(Figure figureToChange, Pane pane) {
		if(changeWhiteOnField != null) {
			figureToChange.setImage(Game.WHITE);
			figureToChange.setColor(Game.WHITE);
			pane.getChildren().remove(changeWhiteOnField.getFigure().getImage());
			changeWhiteOnField.removeFigure();
			changeWhiteOnField.setFigure(figureToChange);	
			changeWhiteOnField = null;
			this.repaint();

			if(!isCheck[getCurrentPlayerPlaying()]) {
				isCheck[getCurrentPlayerPlaying()] = isInCheck(getCurrentPlayerPlayingAsString());
			}
			if(isCheck[getCurrentPlayerPlaying()]) {
				//Je sach a musis vyhodnotit jestli neni i mat
				check = getCurrentPlayerPlaying();
				//System.out.println(isCheckMate()+"helééééééééééééééé");
				isCheckMate();
			}else {
				check = -1;
			}
			
			return Game.WHITE_MOVE;
		}else if(changeBlackOnField != null){
			figureToChange.setImage(Game.BLACK);
			figureToChange.setColor(Game.BLACK);
			pane.getChildren().remove(changeBlackOnField.getFigure().getImage());
			changeBlackOnField.removeFigure();
			changeBlackOnField.setFigure(figureToChange);		
			changeBlackOnField = null;
			this.repaint();
			return Game.BLACK_MOVE;
		}
		return -1;
	}
	
	public boolean getIsChange() {
		return ((changeWhiteOnField != null) || (changeBlackOnField != null));
	}
	
	public boolean getIsCheckArray(int player) {
		return this.isCheck[player];
	}
	
	public void setDeletedFigures(Figure fig[]) {
		this.deletedFigures = fig;
		//System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"+deletedFigures[1]);
	}
	
	public Figure[] getDeletedFigures() {
		return this.deletedFigures;
	}
	
	public int getDeletedFiguresindex() {
		return this.deletedFiguresIndex;
	}
	
	public void decrementDeletedFiguresindex() {
		this.deletedFiguresIndex--;
	}
	
}
