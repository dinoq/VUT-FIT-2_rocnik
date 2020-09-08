package game_logic;

import java.io.IOException;

import game.Board;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class View extends Application{

    /**
     *
     */
    public static final int HEIGHT_OF_TAB_BAR = 45;
	public int gameCounter;
	private Controller myController;
	Tab newTab;
	
    /**
     *
     */
    public View() {
		gameCounter = 10;
	}
    
    @Override
	public void start(Stage stage) throws IOException{ 	
		
		FXMLLoader loader = new FXMLLoader(View.class.getResource("gui.fxml"));
		TabPane tabPane = loader.load();
		myController = loader.getController();
	    
	    
	    
		Scene scene = new Scene(tabPane);

		scene.getStylesheets().add
		 (View.class.getResource("style.css").toExternalForm());
		
	    stage.setScene(scene);

	    stage.setOnCloseRequest(myController.WindowListener());
	    
	    stage.show(); 
	}
	
	
	private void initGame(Pane board) {
		
		Image image = new Image("file:lib/figures/p_b.png");
		 
	 // simple displays ImageView the image as is
	 ImageView img = new ImageView();
	 img.setImage(image);
	 img.setFitWidth(Board.RECT_SIZE);
	 img.setFitHeight(Board.RECT_SIZE);
	//img.setTranslateX(32);
		board.getChildren().add(img);
		
	}

    /**
     *
     * @param args string na zobrazení
     */
    public void launchView(String[] args) {
		launch(args);
	}

    /**
     * 
     * @param loader FXMLLoader 
     * @return vratí aktuálni kontroler
     */
    public Controller getController(FXMLLoader loader) {
		Controller c = loader.getController();
		c.addView(this);
		return c;
	}
	
}