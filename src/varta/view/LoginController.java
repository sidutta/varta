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
	
	@FXML
	private TextField password;
	
	@FXML
	private Button signup;
	
	public static Client client;
	

	

     public LoginController() {
    }

    
    @FXML
    private void initialize() {
    	
    	loginButton.setOnAction((event) -> {
    		String username=userid.getText();
    		String passwd=password.getText();
			System.out.println(username);
			System.out.println(passwd);
			client=new Client("localhost",username,null,5002);
			
			
	        try {
	        	LoginController.client.connMessage(LoginController.client.getUsername(),null,passwd);
	        	Stage loginStage = (Stage) loginButton.getScene().getWindow();	        	
	        	Stage chatStage=new Stage();
	        	chatStage.setTitle("Varta. Welcome "+client.getUsername()+".");
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(getClass().getResource("ChatWindow.fxml"));
	            AnchorPane chatpage = (AnchorPane) loader.load();
	            Scene scene = new Scene(chatpage);
	            chatStage.setScene(scene);
	            loginStage.close();
	            chatStage.show();
	            
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		});
    	
    	signup.setOnAction((event)->{
    		try{
    			

	        	Stage loginStage = (Stage) loginButton.getScene().getWindow();	    
	        	Stage signupStage = new Stage();
	        	signupStage.setTitle("SignUp for MASS CHAT");
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(getClass().getResource("Signup.fxml"));
	            AnchorPane signupPage = (AnchorPane) loader.load();
	            Scene scene = new Scene(signupPage);
	            signupStage.setScene(scene);
	            loginStage.close();
	            signupStage.show();

    		}
    		catch (IOException e) {
	            e.printStackTrace();
	        }
    	});
    }
    
    
    }