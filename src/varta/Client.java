package varta;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import varta.view.ChatController;

public class Client extends Panel implements Runnable {
	
	//private TextField sendertf = new TextField();
	/*private TextField receivertf = new TextField();
	private TextField messagetf = new TextField();
	private TextArea displayta = new TextArea();*/
	private Socket socket;
	private ObjectOutputStream dout;
	private ObjectInputStream din;
	private final String username;
	private ChatController chatController;

	public Client( String host, String user, String password,int port ) {
		
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
	
	public void connMessage( String sender, String receiver, String message ) {
		try {
			dout.writeObject( new Packet(0, sender, receiver, message) );
			dout.flush();
			//displayta.append("Me: " + message + "\n");
			//messagetf.setText( "" );
		} catch( IOException ie ) { System.out.println( ie ); }
	}

	public void run() {
		
			while (true) {
				Packet packet = null;
				try {
					packet = (Packet) din.readObject();
					System.out.println(username+" got a message.");
					chatController.printMessage(packet.getSender(),packet.getMessage());
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
	
	/*public static void main(String[] args) {
		//new Client("localhost", 5000);
	}*/
}