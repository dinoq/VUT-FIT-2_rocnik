package game_logic;

import figure.Figure;
import game.Board;
import game.Field;
import game.Game;
import game.Move;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Model {

	View myView;
	Game games[];
	int numOfGame;
	int capacity;
    private ChessTimer chessTimer;
	
    /**
     *
     */
    public Model() {
		//System.out.println("MY MODEL VYTVOREN:"+this);
		this.capacity = 5;
		games = new Game[this.capacity];
		this.numOfGame = 0;		
	}

    /**
     *
     * @param myView zobrazení
     */
    public void addObserver(View myView) {
		this.myView = myView;
	}
	
    /**
     *
     * @param pane panel na kterem se ma vytvořit nová hra
     */
    public void createNewGame(AnchorPane pane) {
		games[numOfGame] = new Game(pane);
		games[numOfGame].chessTimer = this.chessTimer;
		numOfGame++;
		if(this.numOfGame >= this.numOfGame) {
			games = ExtendArrayOfGames();
		}
	}

	private Game[] ExtendArrayOfGames() {
		this.capacity += 5;
		Game g[] = new Game[this.capacity];
		for(int i = 0; i <= this.numOfGame; i++) {
			g[i] = this.games[i];
		}
		
		return g;
	}

    /**
     *
     * @param event událost myše
     * @param id hry
     * @param mIndex index myše
     * @return jestli bylo kliknuto
     */
    public boolean fieldClickedEvent(MouseEvent event, int id, int mIndex) {	
		//System.out.println("ID:"+id);
		int col = 0, row = 0;
		for(int i = 1; i <= 8; i++) {
			double x = event.getSceneX();
			if((x >= Board.RECT_SIZE * i) && (x < Board.RECT_SIZE * (i+1))) {
				col = i;
				break;
			}
		}
		
		for(int i = 1; i <= 8; i++) {
			double y = event.getSceneY()-View.HEIGHT_OF_TAB_BAR;
			if((y >= Board.RECT_SIZE * i) && y < Board.RECT_SIZE * (i+1)) {
				row = 9-i;
				break;
			}
		}
		
		//System.out.println("mouseX: "+col+", mouseY: "+row);
		
		//Pouze pokud jsme klikli na policko ve hre, tak chceme vyvolat reakci
		if((col != 0) && (row != 0)) {
			games[id].fieldClicked(col, row, mIndex);
			return games[id].getIsSachMat();
		}
		
		return false;
		
	}

 /**
     *
     * @param moveIndex index jaky je to pohyb
     * @param id hry
     * @return jestli se provedl pohyb
     */
    public boolean doMove(int moveIndex, int id) {
		return games[id].doMove(moveIndex);
	}
	
    /**
     *
     * @param moveIndex index jaky je to pohyb
     * @param id hry
     * @return číslo
     */
    public int doReverseMove(int moveIndex, int id) {
		return games[id].doReverseMove(moveIndex);
	}

    /**
     *
     * @param m jaké tahy se mají vykonat
     * @param id hry
     */
    public void setMoves(Move m[], int id) {
		games[id].setMoves(m);
	}

    /**
     *
     * @param length kolik se má vykreslit tahů
     * @param id id hry
     */
    public void setMovesCount(int length, int id) {
		games[id].setMovesCount(length);
	}

    /**
     *
     * @param id id hry
     */
    public void initMoves(int id) {
		games[id].initMoves();
	}

    /**
     *
     * @param time jak rychle se má přehrávat
     */
    public void play(String time) {
		if(chessTimer != null) {
			chessTimer.play(time);
		}
	}
	
    /**
     *
     * @param t časovač ktery se má nastavit
     * @param id hry
     */
    public void setChessTimer(ChessTimer t, int id) {
		this.chessTimer = t;		
		games[id].chessTimer = t;
	}

    /**
     *
     * @return vrátí aktuální časovač
     */
    public ChessTimer getChessTimer() {
		return this.chessTimer;
	}

    /**
     *
     * @param id hry
     * @return získaný error
     */
    public String getError(int id) {
		return games[id].getError();
	}

    /**
     *
     * @param id hry
     * @return jestli je v te hře šach
     */
    public int getInCheck(int id) {
		return games[id].getInCheck();
	}

    /**
     *
     * @param startIndex index od ktereho se mají mazat pohyby
     * @param id hry
     */
    public void deleteMovesFromIndex(int startIndex, int id) {
		games[id].deleteMovesFromIndex(startIndex);
	}

    /**
     *
     * @param tableView kde se to má provest
     * @param id hry
     * @param moveIndex index jaky je to pohyb
     * @return vrátí pohyb
     */
    public int updateTableOfMoves(TableView<Move> tableView, int id, int moveIndex) {
		return games[id].updateTableOfMoves(tableView, moveIndex);
	}

    /**
     *
     * @param id hry
     * @return jestli je kliknuté na pole
     */
    public boolean getFieldClicked(int id) {
		return games[id].getFieldClicked();
	}

    /**
     *
     * @param id hry
     * @return  vrací string aktulního hráče
     */
    public String getCurrentPlayerPlayingAsString(int id) {
		return games[id].getCurrentPlayerPlayingAsString();
	}

    /**
     *
     * @param id hry
     * @return vrací pohyby
     */
    public Move[] getMoves(int id) {
		return games[id].getMoves();
	}

    /**
     *
     * @param id hry
     * @return jestli je tam proměná pěšáka
     */
    public boolean getIsChange(int id) {
		return games[id].getIsChange();
	}

    /**
     *
     * @param tableView kde se to má provest
     * @param figureToChange figurka ktera se ma vyměnit
     * @param id id hry
     * @return vraci jestli je možno provest vyměnu
     */
    public boolean changePawn(TableView<Move> tableView, Figure figureToChange, int id) {
		return games[id].changePawn(tableView, figureToChange);
	}

    /**
     *
     * @param id id hry
     * @return jestli jsou vyplněny pole
     */
    public boolean getFillBlackCell(int id) {
		return games[id].getFillBlackCell();
	}

    /**
     *
     * @param id id hry
     * @return vraci jestli je mat
     */
    public boolean getSachMat(int id) {
		return games[id].getIsSachMat();
	}
	
}

