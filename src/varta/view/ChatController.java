package varta.view;

import varta.Client;



import varta.Packet;
import varta.RandomStringGenerator;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import multimedia.WebcamViewerExample;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import varta.Client;
import multimedia.*;
import multimedia.agent.StreamServer;





public class ChatController {


	@FXML
	private ImageView happy;
	
	@FXML
	private Button webcam;

	@FXML
	private Button sendButton;

	@FXML
	private TextField receiverId;

	@FXML
	private HTMLEditor sendMsg;

	@FXML
	private WebView chatBox;

	@FXML
	private AnchorPane friends;
	
	@FXML
	private Slider timeToLive;
	
	@FXML
	private ContextMenu friendMenu =new ContextMenu();;
	
	@FXML
	private Button recButton;
	
	@FXML
	private Button rand;

	@FXML
	private Button friendButton;
	
	@FXML
	public HashMap<String,javafx.scene.control.Button>  recToButton = new HashMap<String,javafx.scene.control.Button>();
	
	@FXML
	public Button snap;
	
	@FXML
	public Button p2p;
	
	@FXML
	public Label isTyping;
	
	@FXML
	public Button video;
	
	@FXML
	public Button view_video;
	
	@FXML
	public Button view_pic;
	
	private static int flag = 0; 

	private Integer offset =0;
	
	private Timer timer = new Timer();

	
	private static int flag_ist = 0;
	public static String ip = "localhost";
        public static Integer port = 20000;
	private String friendMessage;
	private ArrayList <javafx.scene.control.MenuItem> friendList = friendList = new ArrayList<javafx.scene.control.MenuItem>();;
	
	public ChatController() {
	}

	public void setReceiver(String receiver)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
		receiverId.setText(receiver);
			}
		}
		);
	}
	public void prepareFriends(String friendMessage, String prefix, boolean reinit)
	{
		this.friendMessage = friendMessage;
		System.out.println("Entered Prepfrns1");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				friendMenu = new ContextMenu();
				System.out.println("Entered Prepfrns");
			String [] friendarr = friendMessage.split(":");
			
			if(reinit)
			{
				friendList = new ArrayList<javafx.scene.control.MenuItem>();
				for (int i = 0; i < friendarr.length; i++) { 
				
		
					friendList.add (
							new javafx.scene.control.MenuItem());
					//friendarr[i]
				}
		}
			for (int i = 0; i < friendList.size(); i++) {
	    		
	    		javafx.scene.control.MenuItem curr = friendList.get(i);
	    		System.out.println("Iteration"+i+","+curr.getText());
	    		if(friendarr[i].startsWith(prefix))
	    	{
	     //receiverId.setMinWidth(receiverId.getPrefWidth());
	     //receiverId.setMaxWidth(receiverId.getPrefWidth());
	    	System.out.println("inner Iteration"+i+","+curr.getText()+","+prefix);
	    	Label lbl = new Label(friendarr[i]);
	    	lbl.setId(friendarr[i]);
	    	lbl.setMaxWidth(receiverId.getPrefWidth());
	    	lbl.setPrefWidth(receiverId.getPrefWidth());
	    	//lbl.setWrapText(true);
	    	curr.setGraphic(lbl);
	    	
	    	friendMenu.getItems().add(curr);		// null prefix is prefix for all
	    	curr.setOnAction((event)-> {
	    	System.out.println(curr.getGraphic().getId());
	    	receiverId.setText(curr.getGraphic().getId() );
	    	
			});
			}
	    	}
			}
		});		
	}

	@FXML
	private void initialize() {

		System.out.println("Entered initialize");
		System.out.println(LoginController.client.getUsername());
		LoginController.client.setController(this);
    	//Aditya N Detec if the sender is typing
		LoginController.client.connMessage(8, LoginController.client.getUsername(),
    			"Server","Friends plis");
    	sendMsg.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
             @Override
             public void handle(KeyEvent event) {
                 // Do not filter for TextFields
     			String recId=receiverId.getText();

            	 LoginController.client.isTyping(LoginController.client.getUsername(),recId);
             }
         });

    	receiverId.setContextMenu(friendMenu);
    	//friendMenu.setWidth(receiverId.getWidth());
    	
    	receiverId.setOnKeyReleased ((event) -> {
    		System.out.println("Prefix is "+receiverId.getText());
    		prepareFriends(friendMessage, receiverId.getText(), false);
    	});
    	

    	friendButton.setOnAction((event) -> {
        friendMenu.show
        (receiverId,
        receiverId.getScene().getX() + receiverId.getScene().getWindow().getX() + receiverId.getLayoutX() ,
        receiverId.getScene().getY() + receiverId.getScene().getWindow().getY() + receiverId.getLayoutY()
         + receiverId.getHeight());
    });
    	    
    	webcam.setOnAction((event) -> {
        
        LoginController.client.connMessage(13,LoginController.client.getUsername(),"server",ip+":"+port.toString());
        new Thread(new StreamServer()).start();
        });
        
    	recButton.setOnAction((event) -> {
			System.out.println("recButton Pressed");
			String reciever=receiverId.getText();
			String sender = LoginController.client.getUsername();
			LoginController.client.connMessage(3,"Server",
					reciever,sender+" added "+reciever+" to friend list just now");
			System.out.println("Added to friendList");
			LoginController.client.connMessage(8,LoginController.client.getUsername(),
	    			"Server","Friends plis");
			
		});
                
                p2p.setOnAction((event) -> {
                LoginController.client.connMessage(15,LoginController.client.getUsername(),
	    			receiverId.getText(),"random");
                
                });
                
		sendButton.setOnAction((event) -> {

			String recId=receiverId.getText();
			String msgText=sendMsg.getHtmlText();
			msgText = msgText.replace("<p>", "");
			msgText = msgText.replace("</p>", "");
			if(!recId.equals("") && !msgText.equals(""))
			{
				System.out.println("Sending ...");
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
				
	
//            	System.out.println("Mihir maxxx");
//            	for(int i=0; i<LoginController.client.allChats.get(sender).size(); i++){
//	            	   System.out.println(LoginController.client.allChats.get(sender).get(i).getMessage());
//	            	    
//	            			   
//	               }
//				System.out.println("------");
								
				String page = "";
               for(int i=0; i<LoginController.client.allChats.get(recId).size(); i++){
            	   Packet temp2 = LoginController.client.allChats.get(recId).get(i);
            	   
            	  
            	   if(temp2.getType() == 4){
            		  page = page + "Me: "+temp2.getMessage();
            		  page = page + "<br>";

            	   }
            	   else if( temp2.getType() ==1 ){
            		   page = page + temp2.getSender()+": "+temp2.getMessage();
             		   page = page + "<br>";

            	   }
            	   else if(temp2.getType() == 6){
            		   page = page + "---"+temp2.getMessage()+"--- ";
             		   page = page + "<br>";

            	   }
            	   
            	   System.out.println(temp2.getMessage());
            			   
               }
               printMessage(page);
				//recToButton.get(recId).setStyle("-fx-background-color:#E0E0E0");;
				makeColor(recId,"#E0E0E0");

				//chatBox.appendText("Me: "+msgText+"\n");
				sendMsg.setHtmlText("");
			
			}
			
			
//		happy.setOnMouseClicked((event) -> {
//			sendMsg.appendText(":)");
//		});
		
	});
		video.setOnAction((event) -> {
			
			//Encoder e=new Encoder();
			new Thread(new Encoder()).start();
				
	});

		snap.setOnAction((event) -> {
			new Thread(new WebcamViewerExample()).start();

			
				
					
		});
		
		rand.setOnAction((event) -> {
			System.out.println("Sending random \n");
			LoginController.client.connMessage(16,LoginController.client.getUsername(),"Server","random");
			
		});

		view_pic.setOnAction((event) -> {
			while(LoginController.client.SnapQ.containsKey(receiverId.getText()) || LoginController.client.SnapQ.get(receiverId.getText()).size() != 0 ){
				Packet p =LoginController.client.SnapQ.get(receiverId.getText()).peek();
				
				LoginController.client.SnapQ.get(receiverId.getText()).remove();
				new LoadImage( p.getMessage());
				//Application.launch(LoadImage.class);
				//p.getMessage()
			}
		});	
		
		view_video.setOnAction((event) -> {
		
			while(LoginController.client.VideoQ.containsKey(receiverId.getText()) || LoginController.client.VideoQ.get(receiverId.getText()).size() != 0){
				Packet p =LoginController.client.VideoQ.get(receiverId.getText()).peek();
				LoginController.client.VideoQ.get(receiverId.getText()).remove();
				new EmbeddedMediaPlayer(p.getMessage());
				//new LoadImage( p.getMessage());
				//Application.launch(LoadImage.class);
				//p.getMessage()
			}
		});
	}
	
	
	
	
	//Aditya => Used by tasker
	class SayHello extends TimerTask {
		
		
			public void run() {
               setup();
            }
	

	 }
	
	public void refresh(String str){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				String page = "";
	               for(int i=0; i<LoginController.client.allChats.get(str).size(); i++){
	            	   Packet temp2 = LoginController.client.allChats.get(str).get(i);
	            	   
	            	  
	            	   if(temp2.getType() == 4){
	            		  page = page + "Me: "+temp2.getMessage();
	            		  page = page + "<br>";

	            	   }
	            	   else if( temp2.getType() ==1 ){
	            		   page = page + temp2.getSender()+": "+temp2.getMessage();
		            	   page = page + "<br>";

	            	   }
	            	   else if(temp2.getType() == 6){
	            		   page = page + "---"+temp2.getMessage()+"--- ";
		            	   page = page + "<br>";
	            	   }
	            	   
	            	   System.out.println(temp2.getMessage());
	            			   
	               }
	               printMessage(page);
	             }
		});
	}
	//Aditya N => Restore the id back from ..typing to normal
	@FXML
	public void setup(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				isTyping.setVisible(false);
				flag_ist = 0;
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
				if( receiverId.getText().equals(sender) && flag_ist ==0){
					System.out.println("comeon");
					isTyping.setVisible(true);
					flag_ist = 1;
					timer.schedule(new SayHello(), 2500);
					
					
				}
				
				
			}
		});

	}
	
	@FXML
	public String getRec(){
		return receiverId.getText();
	}
	
	public void printMessage(String message)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatBox.getEngine().loadContent(message);  

			}
		});

	}
	//Aditya N => make the tab green in color
	
	public void makeColor(String sender,String color){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				recToButton.get(sender).setStyle("-fx-background-color:"+color);
			}
		});
	}
	
	public void change_pic(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				view_pic.setStyle("-fx-background-color:#FF66FF");
			}
		});
	}

	public void video_pic(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				view_video.setStyle("-fx-background-color:#CCFFFF");
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
				recToButton.put(sender, tp);

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

				tp.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
		            public void handle(ActionEvent event) {
						System.out.println("Its Wokring");
						System.out.println(event);
//		            	chatBox.setText("");
//		            	System.out.println("Mihir maxxx");
//		            	for(int i=0; i<LoginController.client.allChats.get(sender).size(); i++){
//			            	   System.out.println(LoginController.client.allChats.get(sender).get(i).getMessage());
//			            	    
//			            			   
//			               }
//						System.out.println("------");
						
						receiverId.setText(sender);
						tp.setStyle("-fx-background-color:#E0E0E0");;
					if(LoginController.client.allChats.containsKey(sender)){
//						String page = "";
//			               for(int i=0; i<LoginController.client.allChats.get(sender).size(); i++){
//			            	   Packet temp2 = LoginController.client.allChats.get(sender).get(i);
//			            	   
//			            	   if(temp2.getType() == 4){
//			            		  page = page + "Me:"+temp2.getMessage();
//
//			            	   }
//			            	   else if( temp2.getType() ==1 ){
//			            		   page = page + temp2.getSender()+":"+temp2.getMessage();
//			            	   }
//			            	   else if(temp2.getType() == 6){
//			            		   page = page + "---"+temp2.getMessage()+"--- \n";
//			            	   }
//			            			   
//			               }
//			               printMessage(page);
			            }
						refresh(sender);
					}
					
					
		            
			
		            
				
		        });
				
				offset+=35;
				friends.getChildren().add(tp);
				System.out.println("dsf");
				}
		});
		
	}
	
	public void MoveTimerBySegment()
	{
	System.out.println("Sliiiiiide");
	}

	public void SlideTimer()
	{
		Timeline timeline = new Timeline(new KeyFrame(
		        Duration.millis(2500),
		        ae -> MoveTimerBySegment()
		        ));
		timeline.setCycleCount(3);
		timeline.play();
	}



}
