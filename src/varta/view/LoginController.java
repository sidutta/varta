package varta.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import varta.Client;


public class LoginController {

	@FXML
	private Button loginButton;

	@FXML
	private TextField userid;

	public static Client client;


	public LoginController() {
	}

	@FXML
	private void initialize() {

		loginButton.setOnAction((event) -> {

			String username=userid.getText();
			System.out.println(username);
			client=new Client("localhost",username,5002);

			try {

				Stage loginStage = (Stage) loginButton.getScene().getWindow();	        	
				Stage chatStage=new Stage();
				chatStage.setTitle( "Varta. Welcome " + client.getUsername() + "." );
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation( getClass().getResource( "ChatWindow.fxml" ) );
				AnchorPane chatpage = (AnchorPane) loader.load();
				Scene scene = new Scene( chatpage );
				chatStage.setScene( scene );
				loginStage.close();
				chatStage.show();


			} catch (IOException e) {
				e.printStackTrace();
			}

		});

	}

}