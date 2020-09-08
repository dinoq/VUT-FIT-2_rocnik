package chess;


import java.io.IOException;

import game_logic.Controller;
import game_logic.Model;
import game_logic.View;
// Java program to create multiple tabs and 
// add it to the tabPane and also create a  
// tab which on selected will create new tabs 
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene; 
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage; 
import javafx.scene.Group; 
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.Event; 
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
  
/**
 * The Class Main.
 */
public class Main { 

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String args[]) throws IOException{ 
		View myView 	= new View();

		myView.launchView(args);
		//System.out.println("KONEC MAINU");
		
	}
} 