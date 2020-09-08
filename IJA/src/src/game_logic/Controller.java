package game_logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import assets.MoveParser;
import figure.Bishop;
import figure.Figure;
import figure.Knight;
import figure.Queen;
import figure.Rook;
import game.Board;
import game.Field;
import game.Game;
import game.Move;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Controller {

	int counter = 1;
	
	Model myModel;
	View myView;
	Tab tab;
	
	final FileChooser fileChooser = new FileChooser();

	public Controller() {
		//this.initModel();
		//this.tabs[0] = tab1;
	}

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Pane gamePane;
    
    @FXML
    private Stage stage;

    @FXML
    private Scene scene;
    
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab newTab;

    @FXML
    private Label fileName;

    @FXML
    private Label openFileLabel;

    @FXML
    private TableView<Move> tableView;

    @FXML
    private TableColumn<Move, String> tablePoradi;

    @FXML
    private TableColumn<Move, String> tableBily;

    @FXML
    private TableColumn<Move, String> tableCerny;

    @FXML
    private VBox controlButtons;

    @FXML
    private Button togglePlay;

    @FXML
    private Label gameInfo;
    
    
    
    boolean isAuto = false;
    
    private HBox prevNextBttns;
    private VBox autoButtons;
    private HBox resetMovesBox;
    
    private HBox intervalBox;
    private HBox playBox;

    private HBox directionPlayBox;
    
	int idOfGame;
    

	Button prevMoveButton, nextMoveButton, resetMovesButton;
	Button playButton;
	RadioButton playToStart, playToEnd;

	Label intervalLabel;
	Label intervalUnitLabel;
	TextField intervalField;
	
	Label directionLabel;
	
	Move moves[];
	MoveParser parser;
	int moveIndex = -1;
	int pulTahIndex = -1;
	
	
	boolean tableViewStared = false;
	

	ChessTimer chessTimer;
    
	final ToggleGroup directionPlayToggleGroup = new ToggleGroup();

	public boolean nextMoveSuccessful = true;
	
	ChoiceDialog<String> WrongMoveDialog;
	List<String> choicesWrongMove;
	
	Alert overwriteFileDialog;
	
	ButtonType overwriteFileOKButton;
	ButtonType overwriteFileCancelButton;
	
	
	Alert sachMatAlert;
	
	ButtonType sachMatZpetButton;
	ButtonType sachMatNovySouborButton;
	ButtonType sachMatCancelButton;
	
	Alert figureChangeDialog;

	ButtonType rookFigureButton;
	ButtonType knightFigureButton;
	ButtonType bishopFigureButton;
	ButtonType queenFigureButton;
	ButtonType cancelChoseFigureButton;
	

	Alert wrongFileDialog;

	ButtonType OpenAnotherFileButton;
	ButtonType cancelOpenFileButton;
	

	private boolean fileOpened = false;
	private boolean fileSuccessfullyOpened = false;
	
	private boolean boardDisabled;
	
	
	int playerPlaying = -1;
	
    @FXML
    private void initialize() {
    	if(controlButtons != null) {
    		
    		//Inicializace pro manualni prehravani hry
    		prevNextBttns = new HBox();
    		resetMovesBox = new HBox();

    		prevNextBttns.setAlignment(Pos.CENTER);
    		resetMovesBox.setAlignment(Pos.CENTER);
    		//prevNextBttns.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    		
    		prevMoveButton = new Button("Předchozí tah");
    		nextMoveButton = new Button("První tah");
    		resetMovesButton = new Button("Resetovat do výchozí polohy");
    		
    		prevMoveButton.setOnAction(ActionListener());
    		nextMoveButton.setOnAction(ActionListener());
    		resetMovesButton.setOnAction(ActionListener());

    		prevMoveButton.setId("prev");
    		nextMoveButton.setId("next");
    		resetMovesButton.setId("reset");
    		
    		prevMoveButton.setDisable(true);
    		nextMoveButton.setDisable(true);
    		resetMovesButton.setDisable(true);
    		
    		resetMovesBox.setPadding(new Insets(10, 0, 0, 0));
    		
    		resetMovesBox.getChildren().add(resetMovesButton);
    		
    		prevNextBttns.getChildren().add(prevMoveButton);
    		prevNextBttns.getChildren().add(nextMoveButton);
    		
    		//inicializace pro automaticke prehravani hry
    		
    		autoButtons = new VBox();

    		autoButtons.setAlignment(Pos.CENTER_LEFT);
    		
    		playButton = new Button("play");  

    		intervalField = new TextField ();
    		intervalLabel =  new Label("Interval:");
    		intervalUnitLabel =  new Label("s");
    		
    		intervalField.setText("1");

    		intervalLabel.setPadding(new Insets(5, 20, 0, 0));
    		intervalUnitLabel.setPadding(new Insets(5, 0, 0, 10));

    		intervalBox = new HBox();

    		intervalBox.getChildren().add(intervalLabel);
    		intervalBox.getChildren().add(intervalField);
    		intervalBox.getChildren().add(intervalUnitLabel);
    		playButton.setOnAction(ActionListener());

    		playButton.setId("play");

    		playBox = new HBox();
    		
    		playBox.setPadding(new Insets(10, 0, 0, 0));
    		playBox.setAlignment(Pos.CENTER);
    		
    		
    		directionPlayBox = new HBox();

    		directionPlayBox.setAlignment(Pos.CENTER);
    		
    		directionLabel = new Label("Směr přehrávání:");
    		directionLabel.setPadding(new Insets(10, 0, 0, 0));
    		
    		playToStart = new RadioButton("zpět");
    		playToEnd = new RadioButton("dopředu");

    		playToStart.setToggleGroup(directionPlayToggleGroup);
    		playToEnd.setToggleGroup(directionPlayToggleGroup);

    		playToStart.setPadding(new Insets(10, 10, 0, 0));
    		playToEnd.setPadding(new Insets(10, 0, 0, 0));
    		
    		playToEnd.setSelected(true);
    		
    		directionPlayBox.getChildren().add(playToStart);
    		directionPlayBox.getChildren().add(playToEnd);
    		
    		autoButtons.getChildren().add(intervalBox);
    		autoButtons.getChildren().add(directionLabel);
    		autoButtons.getChildren().add(directionPlayBox);
    		autoButtons.getChildren().add(playBox);
    		//controlButtons.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));


    		//nastaveni manualniho prehravani hry
    		setManual();
    		
    		//Nastaveni casovace
    		chessTimer = new ChessTimer(this);
    		    
    		choicesWrongMove = new ArrayList<>();
    		choicesWrongMove.add("Vrátit se na předchozí tah");
    		choicesWrongMove.add("Načíst nový soubor se záznamem");
    		choicesWrongMove.add("Načíst novou hru");
			
    		WrongMoveDialog = new ChoiceDialog<>("Vrátit se na předchozí tah", choicesWrongMove);
    		WrongMoveDialog.setTitle("Chyba v tahu!");    		
    		WrongMoveDialog.setContentText("Vyberte jak pokračovat.");
    	
    	
    		overwriteFileDialog = new Alert(AlertType.CONFIRMATION);
    		overwriteFileDialog.setTitle("Přemazat šachový záznam?");
    		overwriteFileDialog.setHeaderText("Již máte otevřený šachový záznam a bylo kliknuto na hrací pole.");
    		overwriteFileDialog.setContentText("Přejete si přemazat dosavadní záznam od aktuální pozice?");
    		
    		overwriteFileOKButton = new ButtonType("Přemazat záznam");
    		overwriteFileCancelButton = new ButtonType("Zrušit" , ButtonData.CANCEL_CLOSE);
    		
    		overwriteFileDialog.getButtonTypes().setAll(overwriteFileOKButton, overwriteFileCancelButton);
    		
    		sachMatAlert = new Alert(AlertType.WARNING);
    		sachMatAlert.setTitle("ŠACH MAT!");
    		sachMatAlert.setHeaderText("ŠACH MAT!");
    		sachMatAlert.setContentText("Vyberte jak pokračovat.");

    		
    		sachMatZpetButton = new ButtonType("Vrátit tah zpět");
    		sachMatNovySouborButton = new ButtonType("Načíst nový soubor");
    		sachMatCancelButton = new ButtonType("Zrušit (Zobrazit ukončenou hru)", ButtonData.CANCEL_CLOSE);
    		
    		sachMatAlert.getButtonTypes().setAll(sachMatZpetButton, sachMatNovySouborButton, sachMatCancelButton);


    		wrongFileDialog = new Alert(AlertType.ERROR);
    		wrongFileDialog.setTitle("CHYBNÝ SOUBOR!");
    		wrongFileDialog.setHeaderText("Byl načten chybný soubor, který neobsahuje zápis šachů ve správném formátu!");
    		wrongFileDialog.setContentText("Chcete načíst jiný soubor?");

    		OpenAnotherFileButton = new ButtonType("Načíst jiný soubor");
    		cancelOpenFileButton = new ButtonType("Zrušit", ButtonData.CANCEL_CLOSE);

    		wrongFileDialog.getButtonTypes().setAll(OpenAnotherFileButton, cancelOpenFileButton);

    		figureChangeDialog = new Alert(AlertType.CONFIRMATION);
    		figureChangeDialog.setTitle("Výměna pěšce");
    		figureChangeDialog.setHeaderText("Pěšec dorazil do cíle.");
    		figureChangeDialog.setContentText("Vyberte, za jakou figurku se má vyměnit");

    		rookFigureButton = new ButtonType("Věž");
    		knightFigureButton = new ButtonType("Jezdec");
    		bishopFigureButton = new ButtonType("Střelec");
    		queenFigureButton = new ButtonType("Dáma");
    		cancelChoseFigureButton = new ButtonType("Zrušit", ButtonData.CANCEL_CLOSE);
   
    		
    		figureChangeDialog.getButtonTypes().setAll(rookFigureButton, knightFigureButton, bishopFigureButton, queenFigureButton, cancelChoseFigureButton);


	    	fileChooser.getExtensionFilters().addAll(
	    			new FileChooser.ExtensionFilter("Textový soubor", "*.txt"),
	    			new FileChooser.ExtensionFilter("Všechny soubory", "*.*"));
        	
        	
    		gameInfo.setText("");
    		
    		boardDisabled = false;
    		
    		
    	/* KDYBYSME CHTELI IMPLEMENTOVAT VOLBU BAREV DESKY:
    		final ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(Color.CORAL);
            
            controlButtons.getChildren().add(colorPicker);

            colorPicker.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    //System.out.println(colorPicker.getValue()); 
                    
                }
            });
    		
    		*/
    	
    	}
    }
    
    @FXML
    void onSelectionChanged(Event event) throws IOException {
    	if(myModel == null) {
    		myModel = new Model();
    	}
		if(newTab.isSelected()) { 
    		
			tab = new Tab("Hra č. "+counter++); 
			tab.setClosable(true);
	
//			AnchorPane anchorPane = FXMLLoader.load(View.class.getResource("new_tab_pane.fxml"));
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("new_tab_pane.fxml"));
			AnchorPane anchorPane = (AnchorPane) loader.load();
			Controller controller = loader.<Controller>getController();
			controller.myModel = this.myModel;
			controller.idOfGame = counter-2;
			
			
			anchorPane.getStyleClass().add("anchor-pane");
			

		    // add label to the tab 
		    tab.setContent(anchorPane); 
		    
		    // add tab 
		    tabPane.getTabs().add( 
		            tabPane.getTabs().size() - 1, tab); 
	
		    // select the last tab 
		    tabPane.getSelectionModel().select( 
	            tabPane.getTabs().size() - 2); 
		    
		    myModel.createNewGame(anchorPane);
    		
    	}
    }
    
    @FXML
    void openFileEvent(MouseEvent event) {
		fileOpened = true;
    	fileChooser.setTitle("Otevřít šachový zápis");
    	File file = fileChooser.showOpenDialog(stage);
    	if(file != null) {
    		
        	fileName.setText(file.getName());   
        	
        	parser = new MoveParser();
        	int error = -2;
        	
        	try {
				error = parser.parseFile(file);
			} catch (IOException e) {
	    		fileOpened = false;
	    		return;
			}
        	
        	if(error != 0) {
        		Optional<ButtonType> wrongFileResult = wrongFileDialog.showAndWait();
        		
        		if (wrongFileResult.get().getText() == OpenAnotherFileButton.getText()){
        			openFileEvent(event);
        			return;
        		}else {
    	    		fileOpened = false;
        			return;
        		}
        	}
        	
        	if(tableView.getItems().size() != 0) {
                tableView.getItems().removeAll(moves);
                
				tableView.getSelectionModel().clearSelection();
				nextMoveButton.setText("První tah");
				goToMoveByTarget(-1);
        	}
            moves = new Move[parser.getWhiteMoves().length];
        	
            String w1, w2, b1, b2;
        	for(int i = 0; i < parser.getWhiteMoves().length; i++) {  
        		w1 = parser.getWhiteOriginalMoves()[i];
        		w2 = parser.getWhiteMoves()[i];
        		b1 = parser.getBlackOriginalMoves()[i];
        		b2 = parser.getBlackMoves()[i];

                moves[i] = new Move(w1, b1, String.valueOf(i+1), w2, b2);        		
        	}
        	
            tablePoradi.setCellValueFactory(cellData -> cellData.getValue().numberMoveProperty());
            tableBily.setCellValueFactory(cellData -> cellData.getValue().whiteMoveProperty());
            tableCerny.setCellValueFactory(cellData -> cellData.getValue().blackMoveProperty());
            
            for(int i = 0; i < moves.length; i++) {
                tableView.getItems().add(moves[i]);            	
            }

            myModel.setMoves(moves, idOfGame);
            myModel.setMovesCount(moves.length, idOfGame);
            myModel.initMoves(idOfGame);
            
            tableViewStared = false;     
            moveIndex = -1;

            setDisableButtons();
            fileSuccessfullyOpened = true;
    	}


    	/*
    	tablePoradi.setCellValueFactory(value);
    	tablePoradi.getgetItems().add("1. e4 e5");  
    	listOfMovements.getItems().add("2. Sc4 Df6"); */
    }
    


    @FXML
    void saveFileEvent(MouseEvent event) {
    	fileChooser.setTitle("Uložit šachový zápis");

    	File file = fileChooser.showSaveDialog(stage);
    	if (file != null) {
    		moves = myModel.getMoves(idOfGame);
    		if(moves == null || moves.length == 0) {
    			BufferedWriter writer = null;
	            try {
	                writer = new BufferedWriter(new FileWriter(file));
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	            	try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
    		}else {
	    		BufferedWriter writer = null;
	            try {
	                writer = new BufferedWriter(new FileWriter(file));
	                for(int i = 0; i < moves.length; i++) {
	                	if(moves[i].getBlackMove() == null) {
	                		moves[i].setBlackMove(new SimpleStringProperty(""));
	                	}
	                    writer.write(moves[i].printMove());
	                    if(i != (moves.length-1)) {
	                    	writer.newLine();
	                    }
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	            	try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	                
	            }
	    	}
    	}
    }
    
    @FXML
    void fieldClickedEvent(MouseEvent event) {
		//System.out.println(moveIndex+"!!!");
    	if(boardDisabled) {
    		
    		
    		return;
    	}
    	
    	if(fileOpened) {
    		if(fileSuccessfullyOpened) {
        		Optional<ButtonType> overWriteResult = overwriteFileDialog.showAndWait();
        		
        		if (overWriteResult.get().getText() == overwriteFileOKButton.getText()){
        			fileOpened = false;
        			//moveIndex++;
        			tableView.getItems().remove(moveIndex+1, moves.length);
        			tableView.getSelectionModel().select(moveIndex+1);
        		    myModel.deleteMovesFromIndex(moveIndex+1, idOfGame);
        		} else if (overWriteResult.get().getText() == overwriteFileCancelButton.getText()) {
        		    return;
        		}
    		}else {
    			fileOpened = false;
    		}
    	}
    	
    	if(tablePoradi.getCellValueFactory() == null) {
            tablePoradi.setCellValueFactory(cellData -> cellData.getValue().numberMoveProperty());
            tableBily.setCellValueFactory(cellData -> cellData.getValue().whiteMoveProperty());
            tableCerny.setCellValueFactory(cellData -> cellData.getValue().blackMoveProperty());
    	}
   		
		moveIndex++;

    	boolean sachMat = myModel.fieldClickedEvent(event, idOfGame, moveIndex);
    	

    	int tmpIdx = moveIndex;
    	if(myModel.getFieldClicked(idOfGame)) {
        	moveIndex = myModel.updateTableOfMoves(tableView, idOfGame, moveIndex);
        	if(tmpIdx == moveIndex) {
        		playerPlaying = Game.BLACK_MOVE;
        	}else {
        		playerPlaying = Game.WHITE_MOVE;
        	}
        	pulTahIndex++;
    	}else {       		
    		moveIndex--;
    	}
		//System.out.println("Tah ted je: "+moveIndex);
		

		//System.out.println("Pul tah: "+pulTahIndex);
    	moves = myModel.getMoves(idOfGame);
    	
    	if(moves != null) {
        	setDisableButtons();
    	}
    	
		if(myModel.getIsChange(idOfGame)){
			Figure f = showChoseFigureDialog();			
			sachMat = myModel.changePawn(tableView, f, idOfGame);
		}
		
    	if(sachMat) {
    		gameInfo.setText("Informace ze hry: ŠACH MAT!");
    		//Nastal sach mat
    		int matResult = showSachMatAlert();
			
    		if (matResult == 0){
    			boardDisabled = true;
    		    prevMove();
    		} else if (matResult == 1) {
    		    openFileEvent(null);
    		}
    		
    	}else if(myModel.getInCheck(idOfGame) == -1) {
    		if(gameInfo.getText().contains("je v šachu")) {
    			gameInfo.setText("");
    		}
    	}else {
    		if(myModel.getInCheck(idOfGame) == Game.WHITE_MOVE) {
    			gameInfo.setText("Informace ze hry: Bílý je v šachu");
    		}else {
    			gameInfo.setText("Info ze hry: Černý je v šachu");
    		}
    	}

    	//System.out.println("click index na konci: "+moveIndex+", pultah: "+pulTahIndex);
    	//System.out.println("-----------------------------------");
    }
    

    private Figure showChoseFigureDialog() {
    	Optional<ButtonType> result = figureChangeDialog.showAndWait();
		if (result.get() == rookFigureButton){
			return new Rook("w");
		} else if (result.get() == knightFigureButton) {
			return new Knight("w");
		} else if (result.get() == bishopFigureButton) {
			return new Bishop("w");
		} else if (result.get() == queenFigureButton) {
			return new Queen("w");
		}else {
		    return showChoseFigureDialog();
		}
	}
    

    private int showSachMatAlert() {
    	int res = -1;
    	Optional<ButtonType> matResult = sachMatAlert.showAndWait();
    	
    	if (matResult.get().getText() == sachMatZpetButton.getText()){
    		res = 0;
		} else if (matResult.get().getText() == sachMatNovySouborButton.getText()) {
		    res = 1;
		} else if (matResult.get().getText() == sachMatCancelButton.getText()) {
		    res = showSachMatAlert();
		}
    	return res;
	}

	@FXML
    void click(MouseEvent event) {

    }


    @FXML
    void changeWayOfPlaying(ActionEvent event) {
    	if(isAuto) {
    		togglePlay.setText("Přepnout na automatické přehrávání");
    		setManual();
    		isAuto = false;

    		playBox.getChildren().remove(prevMoveButton);
    		playBox.getChildren().remove(playButton);
    		playBox.getChildren().remove(nextMoveButton);

    		prevNextBttns.getChildren().add(prevMoveButton);
    		prevNextBttns.getChildren().add(nextMoveButton);
    	}else {
    		togglePlay.setText("Přepnout na manuální přehrávání");
    		setAutomatic();
    		isAuto = true;

    		playBox.getChildren().add(prevMoveButton);
    		playBox.getChildren().add(playButton);
    		playBox.getChildren().add(nextMoveButton);
    	}
    }



    @FXML
    void changeGameEvent(MouseEvent event) {
    	if(tableView.getSelectionModel().getSelectedItem() == null) {
    		return;
    	}
    	if(!tableViewStared) {
			tableViewStared = true;
			nextMoveButton.setText("Následující tah");
		}
    	int target = Integer.parseInt(tableView.getSelectionModel().getSelectedItem().numberMoveProperty().getValue())-1;
    	
    	/*if(!tableViewStared) {
			tableViewStared = true;
			nextMoveButton.setText("Následující tah");
		}*/

    	goToMoveByTarget(target);
    	
    	
    }
    
    private void goToMoveByTarget(int target) {
    	while(target != moveIndex) {
    		
        	if(target>moveIndex) {
				boolean success = nextMove();//myModel.doMove(moveIndex, idOfGame);	

				if(!success) {
					moveIndex--;
					setDisableButtons();
					tableView.getSelectionModel().select(moveIndex);
					break;
				}else {
					tableView.getSelectionModel().selectPrevious();
					//System.out.println("USPECH KLIKNUTI-------------");
				}
        	}else {
        		moveIndex--;
				myModel.doReverseMove(moveIndex, idOfGame);
        	}
    		setDisableButtons();
    	}
	}

	/**
     *
     * @param myView aktuální pohled
     */
    public void addView(View myView) {
		this.myView = myView;
		
	}

    /**
     *
     */
    public void initModel() {
		this.myModel = new Model();
	}
	
	private void setManual() {
		controlButtons.getChildren().remove(resetMovesBox);
		controlButtons.getChildren().remove(autoButtons);
		controlButtons.getChildren().add(prevNextBttns);
		controlButtons.getChildren().add(resetMovesBox);
	}
	
	private void setAutomatic(){
		controlButtons.getChildren().remove(resetMovesBox);
		controlButtons.getChildren().remove(prevNextBttns);
		controlButtons.getChildren().add(autoButtons);
		controlButtons.getChildren().add(resetMovesBox);
		
	}

    /**
     * vypnutí tlačítek
     */
    public void setDisableButtons() {
    	//System.out.println(moveIndex+"disable idx");
		if(moveIndex > 0) {
			if(prevMoveButton.isDisabled()) {
				prevMoveButton.setDisable(false);
			}
			if(resetMovesButton.isDisabled()) {
				resetMovesButton.setDisable(false);
			}
		}else {
			prevMoveButton.setDisable(true);
			resetMovesButton.setDisable(true);
		}
		
		//System.out.println("HELELELELELELELE "+moveIndex+"  "+pulTahIndex+"   len: "+ (moves.length-1));
		//System.out.println("PODM: "+(moveIndex < moves.length-1));
		if(moveIndex < moves.length-1 && (pulTahIndex%2 !=0)) {
			if(nextMoveButton.isDisabled()) {
				nextMoveButton.setDisable(false);
			}
		}else{
			nextMoveButton.setDisable(true);
		}
	}

    /**
     *
     * @return jestli se může pohnout
     */
    public boolean nextMove() {
		boolean success = true;
		moveIndex++;	
		pulTahIndex += 2;
		//System.out.println("MOVEEEEEEEEEEEEEEEEEEEEEEEEEE: "+moveIndex+", leeeeeeeeeeeen: "+moves.length);
		setDisableButtons();
		if(moves.length>0) {
			tableViewStared = true;
		}
		if(!tableViewStared) {
            tableView.getSelectionModel().selectFirst();
			tableViewStared = true;
			nextMoveButton.setText("Následující tah");
			success = myModel.doMove(moveIndex, idOfGame);
		}else {
			success = myModel.doMove(moveIndex, idOfGame);	
			if(myModel.getFillBlackCell(idOfGame)) {
				moveIndex--;
				//System.out.println("JAJAJA");
			}
			if(success) {
				tableView.getSelectionModel().selectNext();
			}else {
				if(chessTimer != null) {
					playButton.setText("play");
				}
				String error = myModel.getError(idOfGame);
				WrongMoveDialog.setHeaderText(error);
				Optional<String> result = WrongMoveDialog.showAndWait();
				

				// The Java 8 way to get the response value (with lambda expression).
				result.ifPresent(letter -> System.out.println("Your choice: " + letter));
			}
					
		}
		if(myModel.getSachMat(idOfGame)) {
			int matResult = showSachMatAlert();
			
    		if (matResult == 0){
    			boardDisabled = true;
    			nextMoveButton.setDisable(true);
    		} else if (matResult == 1) {
    		    openFileEvent(null);
    		}
		}
		return success;
	}

    /**
     *
     * @return jestli se může vratit zpátky
     */
    public boolean prevMove() {
    	if(chessTimer != null && playButton.getText().equals("stop")) {
    		if(moveIndex <= 0){
    			chessTimer.interruptThread();
    			playButton.setText("play");
    			nextMoveButton.setText("");
    		}
    	}
		boolean success = true;

		moveIndex--;

		pulTahIndex -= 2;
		
		//System.out.println("PREV"+moves+", index: "+moveIndex);
		
		moves = myModel.getMoves(idOfGame);
		
		//tableView.getSelectionModel().selectPrevious();	
		

		if(tableView.getSelectionModel().getSelectedIndex() == 0) {
			tableView.getSelectionModel().clearSelection();
		}else {
			tableView.getSelectionModel().selectPrevious();
		}

		
		if(fileOpened) {
			tableView.getSelectionModel().select(moveIndex);
			myModel.doReverseMove(moveIndex, idOfGame);	
		}else {
			moveIndex = myModel.doReverseMove(moveIndex, idOfGame);	
			tableView.getSelectionModel().select(moveIndex);
		}
		
		setDisableButtons();	
		return success;		

	}
	
    /**
     *
     * @return event
     */
    public EventHandler<ActionEvent> ActionListener() {
	      return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String buttonId = ((Button) event.getSource()).getId();
				
				
				if(moves == null) {
					return;
				}
				if(buttonId.equals("prev")) {
					/*moveIndex--;
					pulTahIndex--;
					
					//System.out.println("PREV");
		
					
					if(fileOpened) {
						tableView.getSelectionModel().select(moveIndex);
						myModel.doReverseMove(moveIndex, idOfGame);	
					}else {
						moveIndex = myModel.doReverseMove(moveIndex, idOfGame);	
						tableView.getSelectionModel().select(moveIndex);
					}
					//System.out.println("KUK"+moveIndex);
					setDisableButtons();	

					*/
					prevMove();
					
				}else if(buttonId.equals("next")) {
					boolean success = nextMove();
					if(!success) {
						moveIndex--;
						setDisableButtons();	
					}

			    	//System.out.println("handle index na konci: "+moveIndex);
					
				}else if(buttonId.equals("play")) {
					if(playButton.getText().equals("play")) {
						playButton.setText("stop");
					}else {
						playButton.setText("play");
					}
					
					if(myModel.getChessTimer() == null) {
			    		myModel.setChessTimer(chessTimer, idOfGame);
					}
					String t = intervalField.getText();
					if(t.length() != 0) {
						myModel.play(t);
					}
				}else if(buttonId.equals("reset")) {
					tableView.getSelectionModel().clearSelection();
					nextMoveButton.setText("První tah");
					goToMoveByTarget(-1);
				}

				
			}
	          // code here can refer to Foo
	      };
	   }

    /**
     *
     * @return event
     */
    public  EventHandler<WindowEvent> WindowListener() {
	      return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				try {
					if(chessTimer != null)
						chessTimer.t.interrupt();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	      };
	   }

  /**
     *
     * @return tlačítko 
     */
    public Button getNextMoveButton() {
		return this.nextMoveButton;
	}

    /**
     *
     * @return vrátí konkrétní tlačítko
     */
    public Button getPlayButton() {
		return playButton;
	}

    /**
     *
     * @return vrátí tlačítko pro přehrávání dopředu
     */
    public RadioButton getPlayToEnd() {
    	return this.playToEnd;
    }
    
    /**
     *
     * @return vrátí tlačítko pro přehrávání pozpátku
     */
    public RadioButton getPlayToStart() {
    	return this.playToStart;
    }
    
    /**
     *
     * @return toggle group
     */
    public ToggleGroup getDirectionPlayToggleGroup() {
    	return this.directionPlayToggleGroup;
    }

}


