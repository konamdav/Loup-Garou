package ui.view;

import javafx.animation.Animation;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.control.Controleur_Terrain;

public class JFXApp extends Application{

	public static void main(String[] args) {
		System.out.println( "Main method inside Thread : " +  Thread.currentThread().getName());
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		System.out.println( "Start method inside Thread : " +  Thread.currentThread().getName());

		stage.setHeight(600);
		// met un titre dans la fenêtre
		stage.setTitle("Joli décor!");

		// la racine du sceneGraph est le root
		Group root = new Group();
		Scene scene = new Scene(root);

		Controleur_Terrain ctTerain = new Controleur_Terrain();
		ViewTerrain vTerrain = new ViewTerrain(root, ctTerain);
		final ViewPlayers players = new ViewPlayers(root);


		ViewPlayer player;
		for(int i = 3; i<5; ++i)
		{
			player = players.newPlayer("player 3"+i,"WAKE", "RIGHT", 2, i);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}

		for(int i = 3; i<5; ++i)
		{
			player = players.newPlayer("player 4"+i,"WAKE", "LEFT", 12, i);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}
		}

		for(int i = 3; i<12; ++i)
		{
			player = players.newPlayer("player 1"+i,"WAKE", "UP", i, 5);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}


		for(int i = 3; i<12; ++i)
		{
			player = players.newPlayer("player 2"+i, "WAKE","DOWN", i, 2);
			if((int)(Math.random()*3) == 1){
				player.getRoles().addNewRole("WEREWOLF");
			}
			else
			{
				player.getRoles().addNewRole("CITIZEN");
			}

		}


		 Button btn = new Button();
	        btn.setText("Say 'Hello World'");
	        btn.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                System.out.println("Hello World!");
	                players.sleep();
	            }
	        });
		
	        root.getChildren().add(btn);
		// ViewPlayer p = new ViewPlayer(0, 0, "WAKE", "RIGHT", root);




		stage.setScene(scene);
		// ouvrir le rideau
		stage.show();
	}
}