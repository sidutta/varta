package varta;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import varta.view.ChatController;
import varta.view.LoginController;
import varta.view.SignupController;
import javafx.scene.control.Button;


public class Client extends Panel implements Runnable {
	
	//private TextField sendertf = new TextField();
	/*private TextField receivertf = new TextField();
	private TextField messagetf = new TextField();
	private TextArea displayta = new TextArea();*/
	private Socket socket;
	private ObjectOutputStream dout;
	private ObjectInputStream din;
	private final String username;
	private ChatController chatController;//Sh:chatController object for the client
	private LoginController loginController;//Sh:logicController object for the client
	private SignupController signupController;//Sh: SignupController object for the client
	public Set<String>  talkingTo =new HashSet<String>();
	public HashMap<String,ArrayList<Packet>> allChats = new HashMap<String,ArrayList<Packet>>();
	
	public Client( String host, String user,int port ) {
		
		username=user;

		/*GridLayout experimentLayout = new GridLayout(0,2);
		experimentLayout.setVgap(2);
		setLayout( experimentLayout );
		add(new Label("Display Name: " + username));
		add(new Label("To: "));
		add( receivertf );
		add(new Label("Type your message here: "));
		add( messagetf );
		add( displayta );
		
		messagetf.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
//				if(sendertf.getText()=="") {
//					;
//				}
				if(receivertf.getText()=="") {
					;
				}
				else if(messagetf.getText()=="") {
					;
				}
				else {
					processMessage( username, receivertf.getText(), e.getActionCommand() );
				}
			}
		} );
*/
		try {
			socket = new Socket( host, port );
			System.out.println( "connected to "+socket );
			din = new ObjectInputStream( socket.getInputStream() );
			dout = new ObjectOutputStream( socket.getOutputStream() );
			//dout.writeObject( new Packet(0, username, null, password) );

			dout.flush();
			new Thread( this ).start();
		} catch( IOException ie ) { System.out.println( ie ); }
	}

	public void processMessage( String sender, String receiver, String message ) {
		try {
			dout.writeObject( new Packet(1, sender, receiver, message) );
			dout.flush();
			//displayta.append("Me: " + message + "\n");
			//messagetf.setText( "" );
		} catch( IOException ie ) { System.out.println( ie ); }
	}
	
	//Aditya N => Continous packets sent to tell teh receiver i'm typing
	public void isTyping( String sender, String receiver ) {
		try {
			dout.writeObject( new Packet(5, sender, receiver, null) );
			dout.flush();
			//displayta.append("Me: " + message + "\n");
			//messagetf.setText( "" );
		} catch( IOException ie ) { System.out.println( ie ); }
	}

	//Messages for connection
	public void connMessage(int type ,String sender, String receiver, String message ) {
		try {
			dout.writeObject( new Packet(type, sender, receiver, message) );
			dout.flush();
			//displayta.append("Me: " + message + "\n");
			//messagetf.setText( "" );
		} catch( IOException ie ) { System.out.println( ie ); }
	}

	public void run() {
		int flag =0;
			while (true) {
				Packet packet = null;
				try {
					packet = (Packet) din.readObject();
					//Sh:Reply msg from server for signup
					if(flag==0 && packet.getType()==2)
					{
						flag=1;
						while(true)
						{
							System.out.println("Initialized signup"+ signupController);
							if(signupController!=null)
							{
								break;
							}	
						}
						if(packet.getType()==2 && packet.getMessage().equals("Username present"))
						{//Valid login
							signupController.signupInValid();
							System.out.println("This username already exists");
							return;
							
						}
						
						
						else if(packet.getType()==2 && packet.getMessage().equals("User Registererd"))
						{//Valid login
							System.out.println("Authentic user msg rcvd");
							signupController.signupValid();
						}
					}
					else if(flag==0 && packet.getType()==0)
					{
						
							while(true)
							{
								System.out.println("Initialized login "+ loginController);
								if(loginController!=null)
								{
									break;
								}	
							}
							System.out.println("Message is :"+ packet.getMessage());
							System.out.println("Type is :"+ packet.getType());
							
							flag=1;
							if(packet.getType()==0 && packet.getMessage().equals("Invalid"))
							{//Login invalid
								System.out.println("Invalid user msg rcvd");
								loginController.loginInValid();
								return;
							}
							else if(packet.getType()==0 && packet.getMessage().equals("Authentic"))
							{//Valid login
								System.out.println("Authentic user msg rcvd");
								loginController.loginValid();
							}					
						
					}
					else{
						while(true)
						{
							System.out.println("Initialized"+ chatController);
							if(chatController!=null)
							{
								break;
							}	
						}
						if(packet.getType() == 5){
							chatController.printIsTyping(packet.getSender());
	
						}
						else{
							System.out.println(username+" got a message.");
							//Aditya N => Check if the reciver all ready has a tab for the incoming msg
							if(!talkingTo.contains(packet.getSender())){
								talkingTo.add(packet.getSender());
								chatController.openNewTab(packet);
							}
							//Aditya N =>On new incoming message make the tab green in color 
							if(allChats.containsKey(packet.getSender()))
							{allChats.get(packet.getSender()).add(packet) ;
							if(packet.getType() != 6)
							chatController.makeGreen(packet.getSender());
							else
							chatController.makeBlue(packet.getSender());

							}
							else
							{   ArrayList<Packet> temp = new ArrayList<Packet>();
								temp.add(packet);
								allChats.put(packet.getSender(),temp);
								//recToButton.get(packet.getSender()).setStyle("-fx-background-color:#CCFFCC");;
		
							}
							
							if(chatController.getRec().equals(packet.getSender()) || chatController.getRec().equals(packet.getSender()+"....is typing")){
								chatController.printMessage(packet.getSender(), packet.getMessage());;
								chatController.makeGrey(packet.getSender());

							}
						}
					}
						
					//chatController.printMessage(packet.getSender(),packet.getMessage());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//displayta.append( packet.getSender() + ": " + packet.getMessage() +"\n" );
			}
		
	}
	public String getUsername()
	{
		return username;
	}
	
	public void setController(ChatController controller)
	{
		chatController=controller;
	}
	
	//Sh:Function to set the logicContoller
		public void setLoginController(LoginController controller)
		{
			loginController=controller;
		}
		//Sh:Function to set the signupContoller
		public void setSignupController(SignupController controller)
		{
			signupController=controller;
		}
	
	/*public static void main(String[] args) {
		//new Client("localhost", 5000);
	}*/
}