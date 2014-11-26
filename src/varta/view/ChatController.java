package varta.view;

import varta.Client;



import varta.Packet;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import varta.Client;


public class ChatController {


	@FXML
	private ImageView happy;

	@FXML
	private Button sendButton;

	@FXML
	private TextField receiverId;

	@FXML
	private TextArea sendMsg;

	@FXML
	private TextArea chatBox;

	@FXML
	private AnchorPane friends;
	
	@FXML
	private Slider timeToLive;
	
	@FXML
	public HashMap<String,javafx.scene.control.Button>  recToButton = new HashMap<String,javafx.scene.control.Button>();

	
	private Integer offset =0;
	
	public ChatController() {
	}

	
	@FXML
	private void initialize() {

		System.out.println("Entered initialize");
		System.out.println(LoginController.client.getUsername());
		LoginController.client.setController(this);
    	chatBox.setEditable(false);

		sendButton.setOnAction((event) -> {

			String recId=receiverId.getText();
			String msgText=sendMsg.getText();
			Integer time = timeToLive.getMinorTickCount();
			if(!recId.equals("") && !msgText.equals(""))
			{
				System.out.println("Sending ...");
				LoginController.client.processMessage(LoginController.client.getUsername(),recId,msgText,time);
				Packet temp = new Packet(4, LoginController.client.getUsername(), recId, msgText,time);
				if(LoginController.client.allChats.containsKey(recId))
				LoginController.client.allChats.get(recId).add(temp);
				else
				{
					ArrayList<Packet> temp1 = new ArrayList<Packet>();
					temp1.add(temp);
					LoginController.client.allChats.put(recId,temp1);
				}
				System.out.println("time "+time);
				chatBox.appendText("Me: "+msgText+"\n");
				sendMsg.setText("");
			}
		});
		
//		happy.setOnMouseClicked((event) -> {
//			sendMsg.appendText(":)");
//		});
		
	}

	public void printMessage(String sender, String message)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println(message);
				chatBox.appendText(sender+": "+message+"\n");    	      
			}
		});

	}
	
	@FXML
	public void makeGreen(String sender){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				recToButton.get(sender).setStyle("-fx-background-color:#CCFFCC");;
			}
		});
	}
	
	@FXML
	public void openNewTab(String sender){
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Button tp = new Button(sender);
				//LoginController.client.recToButton.put(sender, tp);
				tp.setLayoutY(10+offset);
				tp.setMinWidth(125);
				tp.setMaxWidth(125);
				tp.setStyle("-fx-background-color:#CCFFCC");;
				recToButton.put(sender, tp);
				tp.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
		            public void handle(ActionEvent event) {
		            	chatBox.setText("");
//		            	System.out.println("Mihir maxxx");
//		            	for(int i=0; i<LoginController.client.allChats.get(sender).size(); i++){
//			            	   System.out.println(LoginController.client.allChats.get(sender).get(i).getMessage());
//			            	    
//			            			   
//			               }
//						System.out.println("------");
						
						receiverId.setText(sender);
						tp.setStyle("-fx-background-color:#E0E0E0");;

		               for(int i=0; i<LoginController.client.allChats.get(sender).size(); i++){
		            	   Packet temp = LoginController.client.allChats.get(sender).get(i);
		            	   
		            	   if(temp.getType() == 4){
		            		   printMessage("Me",temp.getMessage());

		            	   }
		            	   else if( temp.getType() ==1 ){

		            		   printMessage(temp.getSender(),temp.getMessage());
		            	   }
		            	    
		            			   
		               }
		            }
		            
					
					
		            
			
		            
				
		        });
				
				offset+=35;
				friends.getChildren().add(tp);
				System.out.println("dsf");
				}
		});
		
	}



}
