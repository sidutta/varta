package varta.view;

import varta.Client;



import varta.Packet;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
import javafx.scene.input.KeyEvent;
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
	
	@FXML
	public Button snap;
	
	@FXML
	public Button p2p;
	
	@FXML
	public Button video;
	
	private static int flag = 0; 

	private Integer offset =0;
	
	private Timer timer = new Timer();

	private String tempsender ="";
	
	public ChatController() {
	}

	
	@FXML
	private void initialize() {

		System.out.println("Entered initialize");
		System.out.println(LoginController.client.getUsername());
		LoginController.client.setController(this);
    	chatBox.setEditable(false);
    	//Aditya N Detec if the sender is typing
    	sendMsg.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
             @Override
             public void handle(KeyEvent event) {
                 // Do not filter for TextFields
     			String recId=receiverId.getText();

            	 LoginController.client.isTyping(LoginController.client.getUsername(),recId);
             }
         });


		sendButton.setOnAction((event) -> {

			String recId=receiverId.getText();
			String msgText=sendMsg.getText();
			if(!recId.equals("") && !msgText.equals(""))
			{
				System.out.println("Sending ...111");
				System.out.println(recId);
				if(!LoginController.client.talkingTo.contains(recId)){
					System.out.println("asdfghjk");
					LoginController.client.talkingTo.add(recId);
					openNewTab(new Packet(9,recId,recId,null));
				}
			
				LoginController.client.processMessage(LoginController.client.getUsername(),recId,msgText);
				//Aditya N create a new packet (send to self) and add the packets to appropraite Array List
				Packet temp = new Packet(4, LoginController.client.getUsername(), recId, msgText);
				if(LoginController.client.allChats.containsKey(recId))
				LoginController.client.allChats.get(recId).add(temp);
				else
				{
					ArrayList<Packet> temp1 = new ArrayList<Packet>();
					temp1.add(temp);
					LoginController.client.allChats.put(recId,temp1);
				}
				
				chatBox.setText("");
//            	System.out.println("Mihir maxxx");
//            	for(int i=0; i<LoginController.client.allChats.get(sender).size(); i++){
//	            	   System.out.println(LoginController.client.allChats.get(sender).get(i).getMessage());
//	            	    
//	            			   
//	               }
//				System.out.println("------");
								

               for(int i=0; i<LoginController.client.allChats.get(recId).size(); i++){
            	   Packet temp2 = LoginController.client.allChats.get(recId).get(i);
            	   
            	   if(temp2.getType() == 4){
            		   printMessage("Me",temp2.getMessage());

            	   }
            	   else if( temp2.getType() ==1 ){

            		   printMessage(temp2.getSender(),temp2.getMessage());
            	   }
            	   else if(temp2.getType() == 6){
            		   printMessage(null,"---"+temp2.getMessage()+"--- \n");
            	   }
            			   
               }
            
				

				//chatBox.appendText("Me: "+msgText+"\n");
				sendMsg.setText("");
			
			}
			
			
//		happy.setOnMouseClicked((event) -> {
//			sendMsg.appendText(":)");
//		});
		
	});
		
		snap.setOnAction((event) -> {
			String recId=receiverId.getText();

			String rand_str = "";
				try {
					rand_str =RandomStringGenerator.generateRandomString(10,RandomStringGenerator.Mode.ALPHANUMERIC);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LoginController.client.processMessage(LoginController.client.getUsername(),recId,rand_str);
					
		});

		
	
	}
	//Aditya => Used by tasker
	class SayHello extends TimerTask {
		
		
			public void run() {
               setup();
            }
	

	 }
	//Aditya N => Restore the id back from ..typing to normal
	@FXML
	public void setup(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
			    receiverId.setText(tempsender);   	      
			}
		});
	}
	
//	//Sh: To show the status of the message sent
//		public void printStatus(Packet p)
//		{
//			Platform.runLater(new Runnable() {
//				@Override
//				public void run() {
//					LoginController.client.allChats.get(p.getSender()).add(p);    	      
//				}
//			});
//
//		}
		
	//Aditya N => When it gets a type 5 packet make it ..is typing
	public void printIsTyping(String sender)
	{
		Platform.runLater(new Runnable() {
			@Override 
			public void run() {
				System.out.println("yoyo "+sender+" "+LoginController.client.getUsername() );
				if( receiverId.getText().equals(sender)){
					System.out.println("comeon");
					receiverId.setText(sender+"....is typing");
					receiverId.setStyle( "-fx-font-style: italic;");
					tempsender=sender;
					timer.schedule(new SayHello(), 2500);
					
					
				}
				
				
			}
		});

	}
	
	@FXML
	public String getRec(){
		return receiverId.getText();
	}
	public void printMessage(String sender, String message)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(sender!=null)
				chatBox.appendText(sender+": "+message+"\n");    
				else
				chatBox.appendText(message);    

			}
		});

	}
	//Aditya N => make the tab green in color
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
	public void makeBlue(String sender){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				recToButton.get(sender).setStyle("-fx-background-color:#99CCFF");;
			}
		});
	}
	
	@FXML
	public void makeGrey(String sender){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				recToButton.get(sender).setStyle("-fx-background-color:#E0E0E0");;
			}
		});
	}
	//Aditya N when a new user comes to chat open a new tab for him
	@FXML
	public void openNewTab(Packet p){
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String sender;
				if(p.getType()==9){
				 sender = p.getReceiver();	
				}
				else
				sender = p.getSender();
				
				Button tp = new Button(sender);
				//LoginController.client.recToButton.put(sender, tp);
				tp.setLayoutY(10+offset);
				tp.setMinWidth(125);
				tp.setMaxWidth(125);
				if(p.getType() != 9){
			        if(p.getType() != 6)
					tp.setStyle("-fx-background-color:#CCFFCC");
					else
				    tp.setStyle("-fx-background-color:#99CCFF");
				}
				else
					tp.setStyle("-fx-background-color:#E0E0E0");
				
				System.out.println("heeee");

				recToButton.put(sender, tp);
				tp.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
		            public void handle(ActionEvent event) {
						System.out.println("Its Wokring");
						System.out.println(event);
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
		            	   else if(temp.getType() == 6){
		            		   printMessage(null,"---"+temp.getMessage()+"--- \n");
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
