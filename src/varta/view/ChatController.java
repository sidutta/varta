package varta.view;

import varta.Client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import varta.Client;


public class ChatController {
	
	

	@FXML
	private Button sendButton;
	
	@FXML
	private TextField receiverId;
	
	@FXML
	private TextArea sendMsg;
	
	@FXML
	private TextArea chatBox;
	

     public ChatController() {
    }

    
    @FXML
    private void initialize() {
    	
    	System.out.println("Entered initialize");
    	System.out.println(LoginController.client.getUsername());
    	LoginController.client.setController(this);
    	

    	
    	sendButton.setOnAction((event) -> {
    		
    		System.out.println("Button Pressed");
    		String recId=receiverId.getText();
    		String msgText=sendMsg.getText();
    		System.out.println(recId);
    		System.out.println(msgText);
    		if(!recId.equals("") && !msgText.equals(""))
    		{
    			LoginController.client.processMessage(LoginController.client.getUsername(),recId,msgText);
    			chatBox.appendText("Me: "+msgText+"\n");
    		}
		});
    	
   }
    
    public void printMessage(String sender, String message)
    {
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	chatBox.appendText(sender+": "+message+"\n");    	      
    	    }
    	});
    	
    }

    
    
    }
