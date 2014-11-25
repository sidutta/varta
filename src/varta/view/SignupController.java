package varta.view;

import varta.Client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import varta.Client;

public class SignupController {

	@FXML
	private TextField fullname;
	
	@FXML
	private TextField username;
	
	@FXML
	private TextField passwd;
	
	@FXML
	private TextField confirmpasswd;
	
	@FXML
	private Button signup_button;
	
	@FXML
	private RadioButton gender;
	
	public static Client client;
	
	 public SignupController() {
	    }

	    
	    @FXML
	    private void initialize() {
	    	
	    	signup_button.setOnAction((event) -> {
	    		String name=fullname.getText();
	    		String password=passwd.getText();
	    		String confirm_password = confirmpasswd.getText();
	    		String sex  = gender.getText();
	    		String userid = username.getText();
				client=new Client("localhost",userid,5002);

					
				 try {
			        	String all_data = name+":"+password+":"+confirm_password+":"+sex+":"+userid;
					    LoginController.client.connMessage(LoginController.client.getUsername(),null,all_data);
			        	Stage loginStage = (Stage) signup_button.getScene().getWindow();	        	
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
	    	
	    	
	    }
	
}
