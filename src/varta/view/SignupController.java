package varta.view;

import varta.Client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	
	@FXML
	private Label pwd_match;
	
	@FXML
	private Label user_exists;
	
	@FXML
	private Label username_empty;
	
	@FXML
	private Label pwd_empty;
	
	
	 public SignupController() {
	    }

	    
	    @FXML
	    private void initialize() {
	    	
	    	signup_button.setOnAction((event) -> {
	    		user_exists.setVisible(false);
	    		pwd_match.setVisible(false);
	    		//username_empty.setVisible(false);
	    		//pwd_empty.setVisible(false);
	    		//Sh:Check if password and confirm password match
	    		if(!passwd.getText().equals(confirmpasswd.getText()))
	    		{
	    			pwd_match.setVisible(true);
	    		}
	    		else
	    		{
	    		String name=fullname.getText();
	    		String password=passwd.getText();
	    		String confirm_password = confirmpasswd.getText();
	    		//String sex  = gender.getText();
	    		String userid = username.getText();
				LoginController.client=new Client("localhost",userid,5002);
				LoginController.client.setSignupController(this);
					

			        	String all_data = name+":"+password+":"+userid;
					    LoginController.client.connMessage(2,LoginController.client.getUsername(),null,all_data);
	    		}

			});
	    	
	    	
	    }
	    //Sh: If valid username is fine, open the chat window
	    public  void signupValid()
	    {
	    	
	    	Platform.runLater(new Runnable() {
				@Override
				public void run() {
			    	try
			    	{
			        	Stage loginStage = (Stage) signup_button.getScene().getWindow();	        	
			        	Stage chatStage=new Stage();
			        	chatStage.setTitle("Varta. Welcome "+LoginController.client.getUsername()+".");
			            FXMLLoader loader = new FXMLLoader();
			            loader.setLocation(getClass().getResource("ChatWindow.fxml"));
			            AnchorPane chatpage = (AnchorPane) loader.load();
			            Scene scene = new Scene(chatpage);
			            chatStage.setScene(scene);
			            loginStage.close();
			            chatStage.show();
			    	}
					catch (IOException e) {
			            e.printStackTrace();
			        }    	      
				}
			});

	    	

	    }
	    //If username already exists,show error
	    public void signupInValid()
	    {
	    	Platform.runLater(new Runnable() {
				@Override
				public void run() {
					user_exists.setVisible(true);
					System.out.println("Username already exist");	      
				}
			});
	    }
	
}
