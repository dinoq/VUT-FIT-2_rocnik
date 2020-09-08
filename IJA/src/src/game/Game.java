/*
 * 
 */
package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collector.Characteristics;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import figure.Bishop;
import figure.Figure;
import figure.Figures;
import figure.King;
import figure.Knight;
import figure.Pawn;
import figure.Queen;
import figure.Rook;
import game.Board;
import game_logic.ChessTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Game {

    /**
     *
     */
    public static final String WHITE = "w";

    /**
     *
     */
    public static final String BLACK = "b";

    /**
     *
     */
    public static final int WHITE_MOVE = 0;

    /**
     *
     */
    public static final int BLACK_MOVE = 1;


	
	
	private Pane pane;
	private Board board;
	private boolean figureChosen;
	//list of moves made
	private Figures figureToMove[];
	

	private boolean NOTAG[] = {false, false};
	private boolean TAKE[] = {false, false};
	private boolean MATE[] = {false, false};
	private boolean CHECK[] = {false, false};
	private boolean CHANGE[] = {false, false};
	private boolean SMALLCASTLING[] = {false, false};
	private boolean BIGCASTLING[] = {false, false};
        private boolean CHANGEV[] = {false, false};
        private boolean CHANGEK[] = {false, false};
        private boolean CHANGES[] = {false, false};
        private boolean CHANGEJ[] = {false, false};
        private boolean CHANGED[] = {false, false};
	
	
	private Figure deletedFigures[];
        private boolean matCerna=false;
        private boolean sachCerna=false;
        private String pismenkoNahrady="";

    private boolean isHumanPlaying = false;
	
	Move moves[];
	Move reverseMoves[];
	int movesCount;

	Field fieldWhichWasClicked;
	
    public ChessTimer chessTimer;
    
    //Slouzi pro popis chyby pokud nejaka nastane
	private String error;
	
	//Tuto pomocnou promennou potrebujeme pro pripad, ze probehl pohyb bile figurky v poradku ale jiz ne cerne. Vime tak snadno odkud kam se ma zpet prekopirovat figurka.
	Field fieldFromToWhiteMoved[];
	
	//Zde ukladame figurky, ktere byli vyhozeny. Potrebujeme je pro pohybu zpet, abychom je mohli opet umistit na hraci plochu
	private int deletedFiguresIndex;

	private boolean isSachMat = false;

	private int indexToAddChangeFlag;
	
	private boolean fillBlackCell = false;
    /**
     * Instantiates a new game.
     *
     * @param p the p
     */
    public Game(Pane p)
	{
		this.board = new Board(p);
		this.pane = p;
		this.figureChosen = false;
		this.SetUpGame();
		figureToMove = new Figures[2];
		fieldFromToWhiteMoved = new Field[2];
		error = "";
		deletedFigures = new Figure[32];
		deletedFiguresIndex = 0;
		fieldWhichWasClicked = null;
		indexToAddChangeFlag = -1;
	}

	/**
	 * Sets the up game.
	 */
	private void SetUpGame() {
		//Pawns
		for (int i = 1; i <= 8; i++) {

			this.putFigureOnField(new Pawn("b"),i,7);
			this.putFigureOnField(new Pawn("w"),i,2);
        }

		//Kings
		this.putFigureOnField(new King("b"),5,8);
		this.putFigureOnField(new King("w"),5,1);
		
		//Queens
		this.putFigureOnField(new Queen("b"),4,8);
		this.putFigureOnField(new Queen("w"),4,1);
		
		//Bishops
		this.putFigureOnField(new Bishop("b"),3,8);
		this.putFigureOnField(new Bishop("b"),6,8);
		this.putFigureOnField(new Bishop("w"),3,1);
		this.putFigureOnField(new Bishop("w"),6,1);
		
		//Knights
		this.putFigureOnField(new Knight("b"),2,8);
		this.putFigureOnField(new Knight("b"),7,8);
		this.putFigureOnField(new Knight("w"),2,1);
		this.putFigureOnField(new Knight("w"),7,1);
		
		//Rooks
		this.putFigureOnField(new Rook("b"),1,8);
		this.putFigureOnField(new Rook("b"),8,8);
		this.putFigureOnField(new Rook("w"),1,1);
		this.putFigureOnField(new Rook("w"),8,1);
		
	}
	
	private void putFigureOnField(Figure fig, int x, int y) {
				
		Field f = board.getField(x, y);
		
		f.setFigure(fig); 

		ImageView i = f.getFigure().getImage();
		
		this.pane.getChildren().add(i);
		
	}
	

    /**
     * Označení figurky když se na ni klikne
     * @param col sloupec pole
     * @param row řádek pole
     * @param moveIndex index pole
     * @return index vykonaného pohybu
     */
    public int fieldClicked(int col, int row, int moveIndex) {
    	isHumanPlaying = true;
    	
		int index = moveIndex;
		if(moves == null) {
			moves = initMovesToOneMove();
		}
		if(reverseMoves == null) {
			reverseMoves = initMovesToOneMove();
		}
		if(figureChosen) {			
			figureChosen = !board.choseField(col, row, index);	
			if(!figureChosen) {
				fieldWhichWasClicked = board.getField(col, row);
				
				if(!board.getSameFieldClicked()) {
					if(board.getAnotherPlayerThanPlaying() == WHITE_MOVE){
						if(board.getIsChange()) {
							indexToAddChangeFlag = index;
						}
						moves = addToArray(moves, index, board.getLastMove());
						reverseMoves = addToArray(reverseMoves, index, board.getLastReverseMove());	
						//System.out.println( board.getLastMove().printMove()+",,,");
						//printMovesArray(moves);					
					}else {
						if(board.getIsChange()) {
							indexToAddChangeFlag = index;
						}
						moves = updateBlackMove(moves, index, board.getLastMove());
						reverseMoves = updateBlackMove(reverseMoves, index, board.getLastReverseMove());
						//printMovesArray(moves);					
					}
					//printMovesArray(reverseMoves);	
				}
				
				isSachMat = board.getIsSachMat();
			}else {
				fieldWhichWasClicked = null;
			}
		}else {	
			figureChosen = board.choseFigure(col, row);	
			fieldWhichWasClicked = null;
		}
		
		return index;
	}
    


	private Move[] updateBlackMove(Move[] m, int index, Move lastMove) {
		//System.out.println("INDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEX:"+index);
		m[index].setBlackMove(lastMove.blackMoveProperty());
		m[index].setBlackMoveWithTag(lastMove.getBlackMove());
		return m;
	}

	private void printMovesArray(Move[] m) {
		System.out.println("----------------------------");
		System.out.println("Array lenght: "+m.length+"\nPrvky:");
		for(int i = 0; i < m.length; i++) {
			System.out.println("index: "+m[i].getIndex());
			System.out.println("do tableview bily: "+m[i].whiteMoveProperty());
			System.out.println("do tableview cerny: "+m[i].blackMoveProperty());
			System.out.println("upraveny bily: "+m[i].getWhiteMove());
			System.out.println("upraveny cerny: "+m[i].getBlackMove());
			System.out.println("----------------------------");
		}
		System.out.println("----------------------------");
	}

	private Move[] addToArray(Move[] m, int movIndex, Move lMove) {
		if(m.length >= movIndex) {
			m = extendArray(m);
			m[movIndex] = lMove;
		}
		//System.out.println("??????" + movIndex+"??TOHLE BY MELO FUNGOVAT: "+m[movIndex]+"+++++++++++++++++++++++++++++++++++++++++++");
		return m;
	}

	
	/**
	 * Rozsiri (zvetsi) pole o jeden prvek.
	 *
	 * @param m the m
	 */
	private Move[] extendArray(Move[] m) {
		Move[] res = new Move[m.length+1];
		for(int i = 0; i < m.length; i++) {
			res[i] = m[i];
		}
		
		return res;		
	}

	private Move[] initMovesToOneMove() {
		return new Move[0];
		
	}


	public boolean getIsSachMat() {
		return isSachMat;
	}


	public void deleteMovesFromIndex(int startIndex) {
		Move mov[] = new Move[startIndex];
		for(int i = 0; i < mov.length; i++) {
			mov[i] = moves[i];
		}
		moves = mov;
	}


	public int updateTableOfMoves(TableView<Move> tableView, int mIndex) {
		if(board.getSameFieldClicked()) {
			//System.out.println("board.getSameFieldClicked()");
			if(mIndex == 0) {
				return mIndex-1;
			}else {
				return (mIndex-1);
			}
		}else {
			if(board.getCurrentPlayerPlayingAsString().equals(Game.BLACK)) {
		        tableView.getItems().add(moves[mIndex]); 
		        tableView.getSelectionModel().select(mIndex);
		        mIndex -= 1;
			}else { 
		        //mIndex -= 1;
	    		//tableView.getItems().remove(moves[mIndex]);
				tableView.getItems().set(mIndex, moves[mIndex]);
			}
		}
		//System.out.println("VRATIM IDX: "+mIndex);
		return mIndex;
	}


	public boolean getFieldClicked() {
		if(fieldWhichWasClicked != null) {
			return !figureChosen;
		}
		return false;
	}


	public boolean getIsChange() {
		return board.getIsChange();
	}
	

	public boolean changePawn(TableView<Move> tableView, Figure figureToChange) {
		boolean mat = false;
		
		int player = board.changePawn(figureToChange, this.pane);
		if(player == Game.WHITE_MOVE) {
			if(board.getIsCheckArray(board.getCurrentPlayerPlaying())) {
				moves[indexToAddChangeFlag].setWhiteMove(new SimpleStringProperty(moves[indexToAddChangeFlag].whiteMoveProperty().getValue()+"+"));
				if(board.getIsSachMat()) {
					//System.out.println("JUPIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
				}
			}
			moves[indexToAddChangeFlag].setWhiteMove(getMoveWhenFigureChanged(Game.WHITE_MOVE, figureToChange.getType().getFirstCharOfName()));	
		}else {
			moves[indexToAddChangeFlag].setBlackMove(getMoveWhenFigureChanged(Game.BLACK_MOVE, figureToChange.getType().getFirstCharOfName()));			
		}
		//System.out.println("LOKEH_"+tableView.getItems().size());
		tableView.getItems().set(indexToAddChangeFlag, moves[indexToAddChangeFlag]);
		return mat;
	}

	private SimpleStringProperty getMoveWhenFigureChanged(int player, String figure) {
		SimpleStringProperty str = null;
		String stringToEdit = "";
		if(player == Game.WHITE_MOVE) {
			stringToEdit = moves[indexToAddChangeFlag].whiteMoveProperty().getValue() + figure;
		}else {
			stringToEdit = moves[indexToAddChangeFlag].blackMoveProperty().getValue() + figure;			
		}
		
		if(stringToEdit.contains("#")) {
			stringToEdit = stringToEdit.substring(0, stringToEdit.length()-1) + figure + "#";
		}else if(stringToEdit.contains("+")) {
			stringToEdit = stringToEdit.substring(0, stringToEdit.length()-1) + figure + "+";
			//System.out.println(stringToEdit+"|||||||||||||||||||||||||||||||||||||||||||||||");
		}
		str = new SimpleStringProperty(stringToEdit);
		//System.out.println("NA TOHLE: "+str);
		return str;
	}



	private Field[] getFieldsFromString(String str, int figureMoveIndex) {
		NOTAG[figureMoveIndex] = false;
		TAKE[figureMoveIndex] = false;
		MATE[figureMoveIndex] = false;
		CHECK[figureMoveIndex] = false;
		SMALLCASTLING[figureMoveIndex] = false;
		BIGCASTLING[figureMoveIndex] = false;
		CHANGE[figureMoveIndex] = false;
		CHANGEV[figureMoveIndex] = false;
                CHANGES[figureMoveIndex] = false;
                CHANGEJ[figureMoveIndex] = false;
                CHANGED[figureMoveIndex] = false;
		Field f[];
		//System.out.println(str+" by nemělo být null");
		String [] tags = str.split(" ");
		
		//Zkraceny zapis
		if(str.charAt(str.length()-1) == '*' || str.charAt(str.length()-2) == '&' ) {
                    if (str.charAt(str.length()-2) == '&'){
                        pismenkoNahrady=tags[tags.length-1].substring(1,2);
                        tags[tags.length-1]=tags[tags.length-1].replaceFirst(pismenkoNahrady,"");
                       // System.out.println("SSSSSSSSSSSSSSSSSSSS   "+tags[tags.length-1]);

                    }
			f = new Field[1];
			//Pohyb pinclika
			if(tags[0].equals("NOTAG")) {
				NOTAG[figureMoveIndex] = true;
				figureToMove[figureMoveIndex] = Figures.getFigureTypeByName(String.valueOf(tags[1].charAt(0)));
				//System.out.println("Hýbu se s:"+figureToMove[figureMoveIndex]+":::"+String.valueOf(tags[1].charAt(0))+" na: "+tags[1].charAt(1)+""+tags[1].charAt(2));
				//System.out.println("Hýbu se sssssssssssssssss:"+String.valueOf(tags[1].charAt(0))+"||"+tags[1]);
				//System.out.println("Extrahuji ze stringu: x: "+colFromChar(tags[1].charAt(1))+", y:"+ Character.getNumericValue(tags[1].charAt(2)));
				f[0] = board.getField(colFromChar(tags[1].charAt(1)), Character.getNumericValue(tags[1].charAt(2)));				
			}else {
				/*check take
				 * CHANGEV TAKE MATE
				 * TAKE MATE
				 * CHANGEV CHECK
				 * CHANGEV CHECK TAKE
				 * SMALLCASTLING MATE
				 * SMALLCASTLING CHECK
				 * BIGCASTLING CHECK
				 * BIGCASTLING MATE
				 */
				for(String s: tags){
					if(s.equals("TAKE"))
						TAKE[figureMoveIndex] = true;
					if(s.equals("MATE"))
						MATE[figureMoveIndex] = true;
					if(s.equals("CHECK"))
						CHECK[figureMoveIndex] = true;
					if(s.equals("SMALLCASTLING"))
						SMALLCASTLING[figureMoveIndex] = true;
					if(s.equals("BIGCASTLING"))
						BIGCASTLING[figureMoveIndex] = true;
					if(s.contains("CHANGE")){
						CHANGE[figureMoveIndex] = true;
                                                if (s.equals("CHANGEV"))
                                                    CHANGEV[figureMoveIndex] = true;
                                                else if (s.equals("CHANGED"))
                                                    CHANGED[figureMoveIndex] = true;
                                                else if (s.equals("CHANGEK"))
                                                    CHANGEK[figureMoveIndex] = true;
                                                else if (s.equals("CHANGES"))
                                                    CHANGES[figureMoveIndex] = true;
                                                else if (s.equals("CHANGEJ"))
                                                    CHANGEJ[figureMoveIndex] = true;
                                        }
				}
				figureToMove[figureMoveIndex] = Figures.getFigureTypeByName(String.valueOf(tags[tags.length-1].charAt(0)));	
				
				/*System.out.println("|||"+colFromChar(tags[tags.length-1].charAt(1)));
				//System.out.println("|"+Character.getNumericValue(tags[tags.length-1].charAt(2)));
				//System.out.println("|L"+tags[tags.length-1]);*/
				f[0] = board.getField(colFromChar(tags[tags.length-1].charAt(1)), Character.getNumericValue(tags[tags.length-1].charAt(2)));	
				
				
			}
		}else {//delsi zapis
			f = new Field[2];

			if(tags[0].equals("NOTAG")) {
				NOTAG[figureMoveIndex] = true;
				figureToMove[figureMoveIndex] = Figures.getFigureTypeByName(String.valueOf(tags[1].charAt(0)));
				//System.out.println("Extrahuji ze stringu: x: "+colFromChar(tags[1].charAt(1))+", y:"+ Character.getNumericValue(tags[1].charAt(2)));
				//System.out.println("Extrahuji ze stringu: x: "+colFromChar(tags[1].charAt(1))+", y:"+ Character.getNumericValue(tags[1].charAt(2)));
				//System.out.println("SMAZATTTT "+colFromChar(tags[1].charAt(3))+" NEBOLI "+tags[1].charAt(3));
				f[0] = board.getField(colFromChar(tags[1].charAt(1)), Character.getNumericValue(tags[1].charAt(2)));
				/*System.out.println("------------------\n"+tags[1]);
				//System.out.println(colFromChar(tags[1].charAt(3))+"***");
				//System.out.println( Character.getNumericValue(tags[1].charAt(4))+"***");*/
				f[1] = board.getField(colFromChar(tags[1].charAt(3)), Character.getNumericValue(tags[1].charAt(4)));				
			}else {
				/*check take
				 * CHANGEV TAKE MATE
				 * TAKE MATE
				 * CHANGEV CHECK
				 * CHANGEV CHECK TAKE
				 * SMALLCASTLING MATE
				 * SMALLCASTLING CHECK
				 * BIGCASTLING CHECK
				 * BIGCASTLING MATE
				 */
				for(String s: tags){
					if(s.equals("TAKE"))
						TAKE[figureMoveIndex] = true;
					if(s.equals("MATE"))
						MATE[figureMoveIndex] = true;
					if(s.equals("CHECK"))
						CHECK[figureMoveIndex] = true;
					if(s.equals("SMALLCASTLING"))
						SMALLCASTLING[figureMoveIndex] = true;
					if(s.equals("BIGCASTLING"))
						BIGCASTLING[figureMoveIndex] = true;
					if(s.contains("CHANGE")){
						CHANGE[figureMoveIndex] = true;
                                                if (s.equals("CHANGEV"))
                                                    CHANGEV[figureMoveIndex] = true;
                                                else if (s.equals("CHANGED"))
                                                    CHANGED[figureMoveIndex] = true;
                                                else if (s.equals("CHANGEK"))
                                                    CHANGEK[figureMoveIndex] = true;
                                                else if (s.equals("CHANGES"))
                                                    CHANGES[figureMoveIndex] = true;
                                                else if (s.equals("CHANGEJ"))
                                                    CHANGEJ[figureMoveIndex] = true;
                                        }
                                                                               
                                        
				}
				if(SMALLCASTLING[figureMoveIndex] || BIGCASTLING[figureMoveIndex]) {
					//figureToMove[figureMoveIndex] = Figures.KING;
					//TODO
					//Pravdepodobne bude potreba pridat dalsi pomocnou promennou a pokud bude rosada tak se to vyhodnoti dale ve funkci doMove()
				}else {
					figureToMove[figureMoveIndex] = Figures.getFigureTypeByName(String.valueOf(tags[tags.length-1].charAt(0)));	
					f[0] = board.getField(colFromChar(tags[tags.length-1].charAt(1)), Character.getNumericValue(tags[tags.length-1].charAt(2)));
					f[1] = board.getField(colFromChar(tags[tags.length-1].charAt(3)), Character.getNumericValue(tags[tags.length-1].charAt(4)));
					
				}
			}
			
			
			
		}
		return f;
	}

    /**
     *
     * @param moveIndex index pohybu
     * @return jestli bylo možné vykonat pohyb
     */
    public boolean doMove(int moveIndex) {
		boolean success = true;
		
		//Retezce pro vytvoreni objektu typu Move pro pohyb zpet
		String whiteStartEnd = null;
		String blackStartEnd = null;
		
		//System.out.println("moveIndex:"+moveIndex);
		
		String whiteM = moves[moveIndex].getWhiteMove();
		String blackM = moves[moveIndex].getBlackMove();

		//System.out.println(whiteM+"ASD----------------------------------------");
		pismenkoNahrady="";
		Field f1[] = getFieldsFromString(whiteM, WHITE_MOVE);
                String pismenkoNahradyBily=pismenkoNahrady;
                pismenkoNahrady="";
		Field f2[] = null;
		if(blackM != "") {
			f2 = getFieldsFromString(blackM, BLACK_MOVE);
		}
                String pismenkoNahradyCerny=pismenkoNahrady;
                pismenkoNahrady="";

		//Tah bileho
		//Zkraceny zapis
		if(f1.length == 1) {
			Field end = f1[0];
			//System.out.println("f:"+end.getCol()+" "+end.getRow());
			//System.out.println("CIL: "+end.printField());
			
			
			Figure[] figsCanMove = new Figure[32];
			Field[] starts = new Field[20];
			int idx = 0;
			int idx2 = 0;
			for(int i = 1; i <= Board.BOARD_SIZE; i++) {
                            for(int j = 1; j <= Board.BOARD_SIZE; j++) {
                                Figure fig = board.getField(i, j).getFigure();
                                if(fig != null) {
                                    if(fig.canMoveTo(end) && fig.getColor().equals(board.getCurrentPlayerPlayingAsString())){
                                        if(!"".equals(pismenkoNahradyBily)){

                                            if (pismenkoNahradyBily.matches("[0-8]") && Integer.parseInt(pismenkoNahradyBily)==fig.getField().getRow()){
                                                figsCanMove[idx++] = fig;
                                                starts[idx2++] = board.getField(i, j);
                                            }
                                            else if (pismenkoNahradyBily.matches("[a-e]")){
                                                figsCanMove[idx++] = fig;
                                                starts[idx2++] = board.getField(i, j);
                                            }

                                        }else{
                                            figsCanMove[idx++] = fig;
                                            starts[idx2++] = board.getField(i, j);
                                        }
                                    }
                                }
                            }
			}

			
			if(figsCanMove.length > 0) {

				int figsIdxToMove = -1;
				for(int i = 0; i < figsCanMove.length; i++) {
					if(figsCanMove[i] == null) {
						break;
					}
					if(figsCanMove[i].getType() == figureToMove[WHITE_MOVE]) {
						figsIdxToMove = i;
					}
				}
				if(figsIdxToMove != -1) {
                                    if (!"".equals(pismenkoNahradyBily)){
                                        whiteStartEnd = whiteM.substring(0, whiteM.length()-5)+getColAndRowFromField(end)+getColAndRowFromField(starts[figsIdxToMove]);
                                    }else {

					whiteStartEnd = whiteM.substring(0, whiteM.length()-1)+getColAndRowFromField(starts[figsIdxToMove]);
                                    }

					if(NOTAG[WHITE_MOVE] && !end.isEmpty()) {
                                                //System.out.println("END"+end.getCol()+end.getRow()+end.getFigure()+end.getFigure().getType());
						success = false;
						error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' je figurka, v zápise však chybí informace o braní figurky!";
					}else if(TAKE[WHITE_MOVE] && end.isEmpty()){
						success = false;
						error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' není žádná figurka, která by mohla být vyhozena!";	
					}else if (CHANGE[WHITE_MOVE] && !end.isEmpty() && !TAKE[WHITE_MOVE]){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu je zapomenut udaj o vyhozeni figurky na sebrani bile figurky";
                                        }else if (!CHANGE[WHITE_MOVE] && end.getRow()==8 && figsCanMove[figsIdxToMove].getType()==Figures.PAWN){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu neni napsano na jakou figurku se má pěšák proměnit";
					}else {
						
                                                boolean skipni=false;
                                                for(int i = 1; i <= 8; i++){
                                                    for(int j = 1; j <= 8; j++){
                                                        Figure fig = board.getField(i, j).getFigure();
                                                        if(fig != null) {
                                                            if(fig.getColor().equals(WHITE) && fig.getType() == Figures.KING) {
                                                                if ((matCerna &&(((King)fig).canCancelCheck())) ||(!matCerna && !(((King)fig).canCancelCheck()))){
                                                                    success = false;
                                                                    error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je chybně zapsán mat a nebo došlo k matu a přitom není v zapisu ";     
                                                                }else{
                                                                        //System.out.println("((King) fig).inCheck()"+((King) fig).inCheck(board)+"CHECK[BLACK_MOVE]"+sachCerna);
                                                                    if((!((King) fig).inCheck(board) && sachCerna) ||(((King) fig).inCheck(board) && !sachCerna)) {
                                                                        
                                                                       // System.out.println("end.getCol()"+end.getCol()+"   end.getFigure()"+end.getFigure());
                                                                        success = false;
                                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je zapsán šach, ale k žádnému šachu nedošlo, nebo došlo k šachu ale není napsan v záznamu";    
                                                                    }
                                                                }
                                                                skipni=true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (skipni){
                                                        break;
                                                    }
                                                }
                                                if(!end.isEmpty() && TAKE[WHITE_MOVE]) {
							deletedFigures[deletedFiguresIndex++] = end.getFigure();
							//System.out.println("ULOZENA TATO FIGURKA DO DELETED:"+deletedFigures[deletedFiguresIndex-1]);
							this.pane.getChildren().remove(end.getFigure().getImage());
							end.removeFigure();
							//System.out.println("VYHAZUJI FIGURKU KTERA STOJI ZDE: "+end.getCol()+""+end.getRow());
						}
						fieldFromToWhiteMoved[0] = figsCanMove[figsIdxToMove].getField();
						fieldFromToWhiteMoved[1] = end;
						
						figsCanMove[figsIdxToMove].moveTo(end);	
                                                if (CHANGE[WHITE_MOVE]){
                                                    if (CHANGEV[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Rook(WHITE));
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGED[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Queen(WHITE)); 
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGES[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Bishop(WHITE));  
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGEJ[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Knight(WHITE));
                                                        end.getFigure().setField(end);
                                                    }else{
                                                        success = false;
                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Něco je v nepořádku v zápisu výměny figurky";  
                                                    }
                                                }
						board.repaint();
						board.changePlayer();
                                               
					}
					
					
				}else {//Chyba
					//Pokud je aktivovane automaticke prehravani, tak ho zastav
					if(chessTimer != null) {
						chessTimer.interruptThread();
					}
					error = "Žádná figurka se nemůže pohnout na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' !";	
					success = false;
				}
			}
		}//tady musi byt podminka pro dlouhy zapis
                else if (f1.length==2){
                    Field end = f1[1];
                    Field start=f1[0];
                    //System.out.println("f:"+end.getCol()+" "+end.getRow());
                    //System.out.println("CIL: "+end.printField());


                    Figure[] figsCanMove = new Figure[2];
                    if (start.getFigure().canMoveTo(end))
                        figsCanMove[0]=start.getFigure();
                        
                        //System.out.println("AAAAAAAAAAAAAAAAAAA"+figsCanMove[0]+figsCanMove.length);

                    if(figsCanMove.length > 0) {

				int figsIdxToMove = -1;
				for(int i = 0; i < figsCanMove.length; i++) {
					if(figsCanMove[i] == null) {
						break;
					}
					if(figsCanMove[i].getType() == figureToMove[WHITE_MOVE]) {
						figsIdxToMove = i;
					}
		
                                } 
                                if(figsIdxToMove != -1) {
					whiteStartEnd = whiteM.substring(0, whiteM.length()-4)+getColAndRowFromField(end)+getColAndRowFromField(start);

					if(NOTAG[WHITE_MOVE] && !end.isEmpty()) {
                                                //System.out.println("END"+end.getCol()+end.getRow()+end.getFigure()+end.getFigure().getType());
						success = false;
						error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() +"' je figurka, v zápise však chybí informace o braní figurky!";
					}else if(TAKE[WHITE_MOVE] && end.isEmpty()){
						success = false;
						error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' není žádná figurka, která by mohla být vyhozena!";	
					}else if (CHANGE[WHITE_MOVE] && !end.isEmpty() && !TAKE[WHITE_MOVE]){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu je zapomenut udaj o vyhozeni figurky na sebrani bile figurky";
                                        }else if (!CHANGE[WHITE_MOVE] && end.getRow()==8 && start.getFigure().getType()==Figures.PAWN){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu neni napsano na jakou figurku se má pěšák proměnit";
					}else {
						
                                                boolean skipni=false;
                                                for(int i = 1; i <= 8; i++){
                                                    for(int j = 1; j <= 8; j++){
                                                        Figure fig = board.getField(i, j).getFigure();
                                                        if(fig != null) {
                                                            if(fig.getColor().equals(WHITE) && fig.getType() == Figures.KING) {
                                                                if ((matCerna &&(((King)fig).canCancelCheck())) ||(!matCerna && !(((King)fig).canCancelCheck()))){
                                                                    success = false;
                                                                    error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je chybně zapsán mat a nebo došlo k matu a přitom není v zapisu ";     
                                                                }else{
                                                                    //System.out.println("whiteM"+whiteM);
                                                                       // System.out.println("((King) fig).inCheck()"+((King) fig).inCheck(board)+"CHECK[BLACK_MOVE]"+sachCerna);
                                                                    if((!((King) fig).inCheck(board) && sachCerna) ||(((King) fig).inCheck(board) && !sachCerna)) {
                                                                        
                                                                        //System.out.println("end.getCol()"+end.getCol()+"   end.getFigure()"+end.getFigure());
                                                                        success = false;
                                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je zapsán šach, ale k žádnému šachu nedošlo, nebo došlo k šachu ale není napsan v záznamu"; 
                                                                    }
                                                                }
                                                                skipni=true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (skipni){
                                                        break;
                                                    }
                                                }
                                                if(!end.isEmpty() && TAKE[WHITE_MOVE]) {
							deletedFigures[deletedFiguresIndex++] = end.getFigure();
							//System.out.println("ULOZENA TATO FIGURKA DO DELETED:"+deletedFigures[deletedFiguresIndex-1]);
							this.pane.getChildren().remove(end.getFigure().getImage());
							end.removeFigure();
							//System.out.println("VYHAZUJI FIGURKU KTERA STOJI ZDE: "+end.getCol()+""+end.getRow());
						}
						fieldFromToWhiteMoved[0] = figsCanMove[figsIdxToMove].getField();
						fieldFromToWhiteMoved[1] = end;
						//System.out.println("start"+start.getFigure());
						figsCanMove[figsIdxToMove].moveTo(end);	
                                                if (CHANGE[WHITE_MOVE]){
                                                    if (CHANGEV[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Rook(WHITE));  
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGED[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Queen(WHITE));   
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGES[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Bishop(WHITE));  
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGEJ[WHITE_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Knight(WHITE));
                                                        end.getFigure().setField(end);
                                                    }else{
                                                        success = false;
                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Něco je v nepořádku v zápisu výměny figurky";  
                                                    }
                                                }
						board.repaint();
						board.changePlayer();
                                                }
					
					
				}else {//Chyba
					//Pokud je aktivovane automaticke prehravani, tak ho zastav
					if(chessTimer != null) {
						chessTimer.interruptThread();
					}
					error = "Žádná figurka se nemůže pohnout na pozici '"+ Game.charFromCol(end.getCol()) + end.getRow() + "' !";	
					success = false;
				}
                    }
                }
                   
		if(f2 == null) {
	    	if(board.getCurrentPlayerPlaying() == Game.BLACK_MOVE) {
	    		//moveIndex--;
	    	}
	    	fillBlackCell = true;
			return success;
		}
		
		//Tah cerneho
		//Zkraceny zapis
		if(f2.length == 1 && "".equals(error)) {
			Field end = f2[0];
			//System.out.println("f:"+end.getCol()+" "+end.getRow());
			sachCerna=false;
                        matCerna=false;
			Figure[] figsCanMove = new Figure[32];
			Field[] starts = new Field[20];
			int idx = 0;
			int idx2 = 0;
			for(int i = 1; i <= Board.BOARD_SIZE; i++) {
                            for(int j = 1; j <= Board.BOARD_SIZE; j++) {
                                Figure fig = board.getField(i, j).getFigure();
                                if(fig != null) {
                                    if(fig.canMoveTo(end) && fig.getColor().equals(board.getCurrentPlayerPlayingAsString())){
                                        
                                        if(!"".equals(pismenkoNahradyCerny)){
                                            if (pismenkoNahradyCerny.matches("[0-8]") && Integer.parseInt(pismenkoNahradyCerny)==fig.getField().getRow()){
                                                figsCanMove[idx++] = fig;
                                                starts[idx2++] = board.getField(i, j);
                                            }
                                            else if (pismenkoNahradyCerny.matches("[a-e]")){
                                                figsCanMove[idx++] = fig;
                                                starts[idx2++] = board.getField(i, j);

                                            }
                                        }else{
                                            figsCanMove[idx++] = fig;
                                            starts[idx2++] = board.getField(i, j);
                                        }
                                    }
                                }
                            }
			}

			if(figsCanMove.length > 0) {
				int figsIdxToMove = -1;
				for(int i = 0; i < figsCanMove.length; i++) {
					if(figsCanMove[i] == null) {
						break;
					}
					if(figsCanMove[i].getType() == figureToMove[BLACK_MOVE]) {
						figsIdxToMove = i;
					}
				}
				if(figsIdxToMove != -1) {
                                    if (!"".equals(pismenkoNahradyCerny)){
                                        blackStartEnd = blackM.substring(0, blackM.length()-5)+getColAndRowFromField(end)+getColAndRowFromField(starts[figsIdxToMove]);
                                    }else{
					blackStartEnd = blackM.substring(0, blackM.length()-1)+getColAndRowFromField(starts[figsIdxToMove]);
                                    }
					if(NOTAG[BLACK_MOVE] && !end.isEmpty()) {
						success = false;
						error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' je figurka, v zápise však chybí informace o braní figurky!";
					}else if(TAKE[BLACK_MOVE] && end.isEmpty()){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() +"' není žádná figurka, která by mohla být vyhozena!";
                                        }else if (CHANGE[BLACK_MOVE] && !end.isEmpty() && !TAKE[BLACK_MOVE]){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu je zapomenut udaj o vyhozeni figurky na sebrani bile figurky";
                                        }else if (!CHANGE[BLACK_MOVE] && end.getRow()==1 && figsCanMove[figsIdxToMove].getType()==Figures.PAWN){
                                            success = false;
                                            error = "Na pozici '"+ Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu neni napsano na jakou figurku se má pěšák proměnit";
					}else {
						                                              
                                                if (CHECK[BLACK_MOVE])
                                                    sachCerna=true;
                                                if (MATE[BLACK_MOVE])
                                                    matCerna=true;
                                                boolean skipni=false;
                                                for(int i = 1; i <= 8; i++){
                                                    for(int j = 1; j <= 8; j++){
                                                        Figure fig = board.getField(i, j).getFigure();
                                                        if(fig != null) {
                                                            if(fig.getColor().equals(BLACK) && fig.getType() == Figures.KING) {
                                                                if ((MATE[WHITE_MOVE] &&(((King)fig).canCancelCheck())) ||(!MATE[WHITE_MOVE] && !(((King)fig).canCancelCheck()))){
                                                                    success = false;
                                                                    error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je chybně zapsán mat a nebo došlo k matu a přitom není v zapisu ";     
                                                                }else{
                                                                    //System.out.println("((King) fig).inCheck()"+((King) fig).inCheck()+"CHECK[WHITE_MOVE]"+CHECK[WHITE_MOVE]);
                                                                          
                                                                    if((!((King) fig).inCheck(board) && CHECK[WHITE_MOVE]) || (((King) fig).inCheck(board) && !CHECK[WHITE_MOVE])) {
                                                                        success = false;
                                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je zapsán šach, ale k žádnému šachu nedošlo, nebo došlo k šachu ale není napsan v záznamu";     
                                                                    }
                                                                }
                                                                skipni=true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (skipni){
                                                        break;
                                                    }
                                                }
                                                if(!end.isEmpty() && TAKE[BLACK_MOVE]) {
							deletedFigures[deletedFiguresIndex++] = end.getFigure();
							//System.out.println("ULOZENA TATO FIGURKA DO DELETED:"+deletedFigures[deletedFiguresIndex-1]);
							this.pane.getChildren().remove(end.getFigure().getImage());
							end.removeFigure();
                                                } 
                                                
						figsCanMove[figsIdxToMove].moveTo(end);
                                                if (CHANGE[BLACK_MOVE]){
                                                    if (CHANGEV[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Rook(BLACK));
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGED[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Queen(BLACK));
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGES[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Bishop(BLACK)); 
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGEJ[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Knight(BLACK));
                                                        end.getFigure().setField(end);
                                                    }else{
                                                        success = false;
                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Něco je v nepořádku v zápisu výměny figurky";  
                                                    }
                                                }
						//System.out.println("JDU ZAPSAT TENTO TAH: "+charFromCol(end.getCol())+""+end.getRow());
						//System.out.println("JDU ZAPSAT TENTO TAH:(startend) "+blackStartEnd);
						board.repaint();
						board.changePlayer();
                                                

					}					
					
				}else {//Chyba
					if(chessTimer != null) {
						chessTimer.interruptThread();
					}
					board.changePlayer();

					//Musime vratit bilou figurku zpet, protoze ta uz se pohnula a potom opet prekreslit hraci desku
					fieldFromToWhiteMoved[1].getFigure().moveTo(fieldFromToWhiteMoved[0]);
					board.repaint();
					
					error = "Žádná figurka se nemůže pohnout na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' !";	
					success = false;
				}
			}
		}else if (f2.length==2 && "".equals(error)){
                    sachCerna=false;
                    matCerna=false;
                    Field end = f2[1];
                    Field start=f2[0];
                    //System.out.println("f:"+end.getCol()+" "+end.getRow());
                    //System.out.println("CIL: "+end.printField());


                    Figure[] figsCanMove = new Figure[2];
                    if (start!=null && start.getFigure()!=null && start.getFigure().canMoveTo(end))
                        figsCanMove[0]=start.getFigure();
                        
                    if(figsCanMove.length > 0) {
				int figsIdxToMove = -1;
				for(int i = 0; i < figsCanMove.length; i++) {
					if(figsCanMove[i] == null) {
						break;
					}
					if(figsCanMove[i].getType() == figureToMove[BLACK_MOVE]) {
						figsIdxToMove = i;
					}
				}
				if(figsIdxToMove != -1) {
					blackStartEnd = blackM.substring(0, blackM.length()-4)+getColAndRowFromField(end)+getColAndRowFromField(start);
					if(NOTAG[BLACK_MOVE] && !end.isEmpty()) {
						success = false;
						error = "Na pozici '"+ Game.charFromCol(end.getCol()) + end.getRow() + "' je figurka, v zápise však chybí informace o braní figurky!";
					}else if(TAKE[BLACK_MOVE] && end.isEmpty()){
                                            success = false;
                                            error = "Na pozici '"+ Game.charFromCol(end.getCol()) + end.getRow() + "' není žádná figurka, která by mohla být vyhozena!";
                                        }else if (CHANGE[BLACK_MOVE] && !end.isEmpty() && !TAKE[BLACK_MOVE]){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu je zapomenut udaj o vyhozeni figurky na sebrani bile figurky";
                                        }else if (!CHANGE[BLACK_MOVE] && end.getRow()==1 && start.getFigure().getType()==Figures.PAWN){
                                            success = false;
                                            error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' V zapisu neni napsano na jakou figurku se má pěšák proměnit";
					}else {
						                                               
                                                if (CHECK[BLACK_MOVE])
                                                    sachCerna=true;
                                                if (MATE[BLACK_MOVE])
                                                    matCerna=true;
                                                boolean skipni=false;
                                                for(int i = 1; i <= 8; i++){
                                                    for(int j = 1; j <= 8; j++){
                                                        Figure fig = board.getField(i, j).getFigure();
                                                        if(fig != null) {
                                                            if(fig.getColor().equals(BLACK) && fig.getType() == Figures.KING) {
                                                                if ((MATE[WHITE_MOVE] &&(((King)fig).canCancelCheck())) ||(!MATE[WHITE_MOVE] && !(((King)fig).canCancelCheck()))){
                                                                    success = false;
                                                                    error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je chybně zapsán mat a nebo došlo k matu a přitom není v zapisu ";     
                                                                }else{
                                                                   // System.out.println("((King) fig).inCheck()   "+((King) fig).getField().getRow()+"   "+((King) fig).inCheck(board)+"   CHECK[WHITE_MOVE]"+CHECK[WHITE_MOVE]);
                                                                          
                                                                    if((!((King) fig).inCheck(board) && CHECK[WHITE_MOVE]) || (((King) fig).inCheck(board) && !CHECK[WHITE_MOVE])) {
                                                                        success = false;
                                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Je zapsán šach, ale k žádnému šachu nedošlo, nebo došlo k šachu ale není napsan v záznamu";    
                                                                    }
                                                                }
                                                                skipni=true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (skipni){
                                                        break;
                                                    }
                                                }
                                                if(!end.isEmpty() && TAKE[BLACK_MOVE]) {
							deletedFigures[deletedFiguresIndex++] = end.getFigure();
							//System.out.println("ULOZENA TATO FIGURKA DO DELETED:"+deletedFigures[deletedFiguresIndex-1]);
							this.pane.getChildren().remove(end.getFigure().getImage());
							end.removeFigure();
                                                }
                                                
						figsCanMove[figsIdxToMove].moveTo(end);
                                                if (CHANGE[BLACK_MOVE]){
                                                    if (CHANGEV[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Rook(BLACK));  
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGED[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Queen(BLACK)); 
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGES[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Bishop(BLACK));
                                                        end.getFigure().setField(end);
                                                    }else if (CHANGEJ[BLACK_MOVE]){
                                                        this.pane.getChildren().remove(end.getFigure().getImage());
                                                        end.removeFigure();
                                                        end.setFigure(new Knight(BLACK));
                                                        end.getFigure().setField(end);
                                                    }else{
                                                        success = false;
                                                        error = "Na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "' Něco je v nepořádku v zápisu výměny figurky";  
                                                    }
                                                }
						//System.out.println("JDU ZAPSAT TENTO TAH: "+charFromCol(end.getCol())+""+end.getRow());
						//System.out.println("JDU ZAPSAT TENTO TAH:(startend) "+blackStartEnd);
						board.repaint();
						board.changePlayer();
                                                

					}					
					
				}else {//Chyba
					if(chessTimer != null) {
						chessTimer.interruptThread();
					}
					board.changePlayer();

					//Musime vratit bilou figurku zpet, protoze ta uz se pohnula a potom opet prekreslit hraci desku
					fieldFromToWhiteMoved[1].getFigure().moveTo(fieldFromToWhiteMoved[0]);
					board.repaint();
					
					error = "Žádná figurka se nemůže pohnout na pozici '" + Game.charFromCol(end.getCol()) + end.getRow() + "'!";	
					success = false;
				}
			}
		}

                //System.out.println("whiteStartEnd: " +whiteStartEnd);
                //System.out.println("blackStartEnd: " +blackStartEnd);

                if(whiteStartEnd == null) {
                	whiteStartEnd = "";
                }
                if(blackStartEnd == null) {
                	blackStartEnd = "";
                }
                
                if ("".equals(error)) {
                	//System.out.println("NA TENHLE INDEX"+moveIndex);
                    reverseMoves[moveIndex] = new Move(whiteStartEnd, blackStartEnd, String.valueOf(moveIndex), whiteStartEnd, blackStartEnd);
                }
                //System.out.println("tak heleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee: "+MATE[0]+" "+MATE[1]);
                if(MATE[0] || MATE[1]) {
                	isSachMat = true;
                }
                return success;
	}
	
	
    /**
     *
     * @param moveIndex index pohybu
     * @return chybové stavy/ jestli je vše v pořádku
     */
    public int doReverseMove(int moveIndex) {
    	if(board.getCurrentPlayerPlaying() == Game.BLACK_MOVE) {
    		moveIndex++;
    	}
		//System.out.println("moveIndex:"+moveIndex);
		/*check take
				 * CHANGEV TAKE MATE
				 * TAKE MATE
				 * CHANGEV CHECK
				 * CHANGEV CHECK TAKE
				 * SMALLCASTLING MATE
				 * SMALLCASTLING CHECK
				 * BIGCASTLING CHECK
				 * BIGCASTLING MATE
				 */
		String whiteM = reverseMoves[moveIndex+1].getWhiteMove();
		String blackM = reverseMoves[moveIndex+1].getBlackMove();

		//System.out.println(whiteM+" TOHLE JE REVERSE w"); //NOTAG pe5e4
		//System.out.println(blackM+" TOHLE JE REVERSE b");
		
		Field f1[] = getFieldsFromString(whiteM, WHITE_MOVE);
		Field f2[] = null;
		if(blackM != "") {
			f2 = getFieldsFromString(blackM, BLACK_MOVE);
		}
                //System.out.println("CHANGE[BLACK_MOVE] "+CHANGE[BLACK_MOVE]+ "  CHANGE[WHITE_MOVE]  "+CHANGE[WHITE_MOVE]);
		//System.out.println(f1+"°°");
		//System.out.println(f2+"°°|");
		
		//Tah bilych
		//Zkraceny zapis

        if(f1.length == 1) {
        	//System.out.println("ZDE BY TO NIKDY NEMELO DOJIT :D");
        }else {

               if (CHANGE[WHITE_MOVE]){
                    //System.out.println("changingBLACK");
                    this.pane.getChildren().remove(f1[0].getFigure().getImage());
                    f1[0].removeFigure();
                    f1[0].setFigure(new Pawn(WHITE));
                    f1[0].getFigure().setField(f1[0]);                           
                }
                if(TAKE[WHITE_MOVE]) {
                     //System.out.println("BLACK and TAKE");
                     
                     if(!isHumanPlaying || deletedFigures[0] != null) {
                 		f1[0].getFigure().moveTo(f1[1]);
                		deletedFiguresIndex--;
                		f1[0].setFigure(deletedFigures[deletedFiguresIndex]);
                     }else {
                 		f1[0].getFigure().moveTo(f1[1]);
                		board.decrementDeletedFiguresindex();
                		f1[0].setFigure(board.getDeletedFigures()[board.getDeletedFiguresindex()]);
                     }
		board.repaint();				
				
	}else{
                    if (BIGCASTLING[WHITE_MOVE]){
                        f1[0]=board.getField(3,1);                                
                        f1[1]=f1[0].nextField(Field.Direction.R).nextField(Field.Direction.R);
                        f1[0].nextField(Field.Direction.R).getFigure().moveTo( f1[0].nextField(Field.Direction.L).nextField(Field.Direction.L));
                        this.pane.getChildren().remove(f1[0].getFigure().getImage());
                        f1[0].removeFigure();
                        f1[1].setFigure(new King(WHITE));                               
                        f1[1].getFigure().setField(f1[1]);  
                        ((Rook)f1[0].nextField(Field.Direction.L).nextField(Field.Direction.L).getFigure()).moved=false;
                        ((King)f1[1].getFigure()).moved=false;
                        board.repaint();
                    }else if (SMALLCASTLING[WHITE_MOVE]){
                        f1[0]=board.getField(7,1);
                        f1[1]=f1[0].nextField(Field.Direction.L).nextField(Field.Direction.L);
                        f1[0].nextField(Field.Direction.L).getFigure().moveTo( f1[0].nextField(Field.Direction.R));
                        this.pane.getChildren().remove(f1[0].getFigure().getImage());
                        f1[0].removeFigure();
                        ((Rook)f1[0].nextField(Field.Direction.R).getFigure()).moved=false;
                        f1[1].setFigure(new King(WHITE));
                        ((King)f1[1].getFigure()).moved=false;
                        f1[1].getFigure().setField(f1[1]);  
                        board.repaint();
                    }else{
                        f1[0].getFigure().moveTo(f1[1]);
                        board.repaint();	
                    }
	}
}
		
		if(f2 == null) {
	    	if(board.getCurrentPlayerPlaying() == Game.BLACK_MOVE) {
	    		//moveIndex--;
	    	}
	    	board.changePlayer();
			return moveIndex;
		}
		
		//Tah cernych
		//Zkraceny zapis
		if(f2.length == 1) {
			//System.out.println("ZDE BY TO NIKDY NEMELO DOJIT :D");
			
		}else {//pravdepodobne bude vzdy delsi zapis
			if (CHANGE[BLACK_MOVE]){
                            //System.out.println("changingBLACK");
                            this.pane.getChildren().remove(f2[0].getFigure().getImage());
                            f2[0].removeFigure();
                            f2[0].setFigure(new Pawn(BLACK));
                            f2[0].getFigure().setField(f2[0]);                           
                        }
                        if(TAKE[BLACK_MOVE]) {
                             //System.out.println("BLACK and TAKE");

				f2[0].getFigure().moveTo(f2[1]);
				f2[0].setFigure(deletedFigures[deletedFiguresIndex-1]);
				deletedFiguresIndex--;
				//System.out.println("CERNY SEBRAN");
				board.repaint();				
						
			}else{
                            if (BIGCASTLING[BLACK_MOVE]){
                                f2[0]=board.getField(3,8);                               
                                f2[1]=f2[0].nextField(Field.Direction.R).nextField(Field.Direction.R);
                                f2[0].nextField(Field.Direction.R).getFigure().moveTo( f2[0].nextField(Field.Direction.L).nextField(Field.Direction.L));
                                this.pane.getChildren().remove(f2[0].getFigure().getImage());
                                f2[0].removeFigure();
                                f2[1].setFigure(new King(BLACK));                               
                                f2[1].getFigure().setField(f2[1]);  
                                ((Rook)f2[0].nextField(Field.Direction.L).nextField(Field.Direction.L).getFigure()).moved=false;
                                ((King)f2[1].getFigure()).moved=false;
                                board.repaint();
                            }else if (SMALLCASTLING[BLACK_MOVE]){
                                f2[0]=board.getField(7,8);
                                f2[1]=f2[0].nextField(Field.Direction.L).nextField(Field.Direction.L);
                                f2[0].nextField(Field.Direction.L).getFigure().moveTo( f2[0].nextField(Field.Direction.R));
                                this.pane.getChildren().remove(f2[0].getFigure().getImage());
                                f2[0].removeFigure();
                                ((Rook)f2[0].nextField(Field.Direction.R).getFigure()).moved=false;
                                f2[1].setFigure(new King(BLACK));
                                ((King)f2[1].getFigure()).moved=false;
                                f2[1].getFigure().setField(f2[1]);  
                                board.repaint();
                            }else{
                                f2[0].getFigure().moveTo(f2[1]);
                                board.repaint();	
                            }
			}
                     
		}

    	if(board.getCurrentPlayerPlaying() == Game.BLACK_MOVE) {
    		moveIndex--;
    	}
		return moveIndex;
		
	}
    
	private int colFromChar(char ch) {
		int col = 0;
		switch (ch) {
		case 'a':
			col = 1;
			break;
		case 'b':
			col = 2;
			break;
		case 'c':
			col = 3;
			break;
		case 'd':
			col = 4;
			break;
		case 'e':
			col = 5;
			break;
		case 'f':
			col = 6;
			break;
		case 'g':
			col = 7;
			break;
		case 'h':
			col = 8;
			break;

		default:
			break;
		}
		
		return col;
	}
	
    /**
     *
     * @param c číselné označní sloupce
     * @return abecední označení sloupce
     */
    public static String charFromCol(int c) {
		String char_to_return = "";
		switch (c) {
		case 1:
			char_to_return = "a";
			break;
		case 2:
			char_to_return = "b";
			break;
		case 3:
			char_to_return = "c";
			break;
		case 4:
			char_to_return = "d";
			break;
		case 5:
			char_to_return = "e";
			break;
		case 6:
			char_to_return = "f";
			break;
		case 7:
			char_to_return = "g";
			break;
		case 8:
			char_to_return = "h";
			break;

		default:
			break;
		}
		
		return char_to_return;
	}
	
	private String getColAndRowFromField(Field f) {
		return charFromCol(f.getCol())+f.getRow();
	}

    /**
     *
     * @param m pohyby které se mají nastavit
     */
    public void setMoves(Move m[]) {
		moves = m;
	}

    /**
     *
     * @param length kolik se má nastavit pohybů
     */
    public void setMovesCount(int length) {
		this.movesCount = length;
	}

    /**
     *
     */
    public void initMoves() {
		this.reverseMoves = new Move[this.movesCount];
	}

    /**
     *
     * @return jestli se vyskytla chyba v této hře
     */
    public String getError() {
		return this.error;
	}

    /**
     *
     * @return jestli je šach ve hře
     */
    public int getInCheck() {
		return board.getInCheck();
	}

    /**
     *
     * @return vrátí aktuálního hráče 
     */
    public String getCurrentPlayerPlayingAsString() {
		return board.getCurrentPlayerPlayingAsString();
	}

    /**
     *
     * @return vrátí pohyby této hry
     */
    public Move[] getMoves() {
		return this.moves;
	}

    /**
     *
     * @return jestli se má vyplňovat buňka pro černou figurku
     */
    public boolean getFillBlackCell() {
		if(this.fillBlackCell) {
			this.fillBlackCell = false;
			return true;
		}
		return false;
	}
		
}

