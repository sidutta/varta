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
import multimedia.agent.StreamClient;

public class Client extends Panel implements Runnable {

	// private TextField sendertf = new TextField();
	/*
	 * private TextField receivertf = new TextField(); private TextField
	 * messagetf = new TextField(); private TextArea displayta = new TextArea();
	 */
	private Socket socket;
	private ObjectOutputStream dout;
	private ObjectInputStream din;
	private final String username;
	private ChatController chatController;// Sh:chatController object for the
											// client

	public ChatController getChatController() {
		return chatController;
	}

	public void setChatController(ChatController chatController) {
		this.chatController = chatController;
	}

	public LoginController getLoginController() {
		return loginController;
	}

	public SignupController getSignupController() {
		return signupController;
	}

	private LoginController loginController;// Sh:logicController object for the
											// client
	private SignupController signupController;// Sh: SignupController object for
												// the client
	public Set<String> talkingTo = new HashSet<String>();
	public HashMap<String, ArrayList<Packet>> allChats = new HashMap<String, ArrayList<Packet>>();
	public HashMap<String, Queue<Packet>> SnapQ = new HashMap<String, Queue<Packet>>();
	public HashMap<String, Queue<Packet>> VideoQ = new HashMap<String, Queue<Packet>>();
	public HashMap<String, Integer> snap_but = new HashMap<String, Integer>();
	public HashMap<String, Integer> video_but = new HashMap<String, Integer>();

	public Client(String host, String user, int port) {

		username = user;

		/*
		 * GridLayout experimentLayout = new GridLayout(0,2);
		 * experimentLayout.setVgap(2); setLayout( experimentLayout ); add(new
		 * Label("Display Name: " + username)); add(new Label("To: ")); add(
		 * receivertf ); add(new Label("Type your message here: ")); add(
		 * messagetf ); add( displayta );
		 * 
		 * messagetf.addActionListener( new ActionListener() { public void
		 * actionPerformed( ActionEvent e ) { // if(sendertf.getText()=="") { //
		 * ; // } if(receivertf.getText()=="") { ; } else
		 * if(messagetf.getText()=="") { ; } else { processMessage( username,
		 * receivertf.getText(), e.getActionCommand() ); } } } );
		 */
		try {
			socket = new Socket(host, port);
			System.out.println("connected to " + socket);
			din = new ObjectInputStream(socket.getInputStream());
			dout = new ObjectOutputStream(socket.getOutputStream());
			// dout.writeObject( new Packet(0, username, null, password) );

			dout.flush();
			new Thread(this).start();
		} catch (IOException ie) {
			System.out.println(ie);
		}
	}

	public void processMessage(String sender, String receiver, String message) {
		try {
			dout.writeObject(new Packet(1, sender, receiver, message));
			dout.flush();
			// displayta.append("Me: " + message + "\n");
			// messagetf.setText( "" );
		} catch (IOException ie) {
			System.out.println(ie);
		}
	}

	// Aditya N => Continous packets sent to tell the receiver i'm typing
	public void isTyping(String sender, String receiver) {
		try {
			System.out.println("is typing msgs");
			dout.writeObject(new Packet(5, sender, receiver, null));
			dout.flush();
			// displayta.append("Me: " + message + "\n");
			// messagetf.setText( "" );
		} catch (IOException ie) {
			System.out.println(ie);
		}
	}

	// Messages for connection
	public void connMessage(int type, String sender, String receiver,
			String message) {
		try {
			System.out.println("Random String " + message);
			dout.writeObject(new Packet(type, sender, receiver, message));
			dout.flush();
			// displayta.append("Me: " + message + "\n");
			// messagetf.setText( "" );
		} catch (IOException ie) {
			System.out.println(ie);
		}
	}

	public void run() {
		int flag = 0;
		while (true) {
			Packet packet = null;
			try {
				packet = (Packet) din.readObject();
				// Sh:Reply msg from server for signup
				if (flag == 0 && packet.getType() == 2) {
					flag = 1;
					while (true) {
						System.out.println("Initialized signup"
								+ signupController);
						if (signupController != null) {
							break;
						}
					}
					if (packet.getType() == 2
							&& packet.getMessage().equals("Username present")) {// Valid
																				// login
						signupController.signupInValid();
						System.out.println("This username already exists");
						return;

					}

					else if (packet.getType() == 2
							&& packet.getMessage().equals("User Registererd")) {// Valid
																				// login
						System.out.println("Authentic user msg rcvd");
						signupController.signupValid();
					}
				} else if (flag == 0 && packet.getType() == 0) {

					while (true) {
						System.out.println("Initialized login "
								+ loginController);
						if (loginController != null) {
							break;
						}
					}
					System.out.println("Message is :" + packet.getMessage());
					System.out.println("Type is :" + packet.getType());

					flag = 1;
					if (packet.getType() == 0
							&& packet.getMessage().equals("Invalid")) {// Login
																		// invalid
						System.out.println("Invalid user msg rcvd");
						loginController.loginInValid();
						return;
					} else if (packet.getType() == 0
							&& packet.getMessage().equals("Authentic")) {// Valid
																			// login
						System.out.println("Authentic user msg rcvd");
						loginController.loginValid();
					}

				} else if (packet.getType() == 10) { // 1- for pictures
					if (!talkingTo.contains(packet.getSender())) {
						talkingTo.add(packet.getSender());
						chatController.openNewTab(packet);
					}

					if (SnapQ.containsKey(packet.getSender())) {
						SnapQ.get(packet.getSender()).add(packet);
					} else {
						Queue<Packet> tp = new LinkedList<Packet>();
						tp.add(packet);
						SnapQ.put(packet.getSender(), tp);
					}
					if (!chatController.getRec().equals(packet.getSender())) {
						snap_but.put(packet.getSender(), 1);
					} else
						chatController.change_pic();

					System.out.println("picture received by "
							+ packet.getReceiver());

					chatController.makeColor(packet.getSender(), "#FF66FF");

				} else if (packet.getType() == 11) {
					if (!talkingTo.contains(packet.getSender())) {
						talkingTo.add(packet.getSender());
						chatController.openNewTab(packet);
					}

					if (VideoQ.containsKey(packet.getSender())) {
						VideoQ.get(packet.getSender()).add(packet);
					} else {
						Queue<Packet> tp = new LinkedList<Packet>();
						tp.add(packet);
						VideoQ.put(packet.getSender(), tp);
					}
					System.out.println("Video received by "
							+ packet.getReceiver());
					chatController.makeColor(packet.getSender(), "#CCFFFF");

					if (!chatController.getRec().equals(packet.getSender())) {
						video_but.put(packet.getSender(), 1);

					} else
						chatController.video_pic();

				} else if (packet.getType() == 15) {

					if (!packet.getMessage().equals("Webcam Not Found")) {
						new Thread(new StreamClient(packet.getMessage()))
								.start();
					}
				} else if (packet.getType() == 16) {
					System.out.println("No one online");
				} else {
					while (true) {
						System.out.println("Initialized" + chatController);
						if (chatController != null) {
							break;
						}
					}
					if (packet.getType() == 8) {
						System.out.println("Type 8 in client");

						String[] pktData = packet.getMessage().split("\\|");
						if (pktData.length > 1)
							chatController.prepareFriends(pktData[1], "", true);
						else
							chatController.prepareFriends(":", "", true);
					}
					// else
					// {
					// if (packet.getType()==16)
					//
					// {
					// System.out.println("Type 16 in client");
					// String newReceiver = packet.getMessage().split("\\|")[0];
					// packet.setMessage(packet.getMessage().replace("|", ""));
					// System.out.println(packet.getMessage());
					// if(!newReceiver.equals(null))
					// chatController.setReceiver(newReceiver);
					//
					// }
					// chatController.printMessage(packet.getSender(),packet.getMessage());
					// }
					if (packet.getType() == 5) {
						chatController.printIsTyping(packet.getSender());

					} else if (packet.getType() == 6 || packet.getType() == 1) {

						System.out.println(username + " got a message.");
						if (packet.getSender().equals("Server")) {
							System.out.println("server sent a msg");
							chatController.ticker(packet.getMessage());
						} else {
							// Aditya N => Check if the reciver all ready has a
							// tab for the incoming msg
							if (!talkingTo.contains(packet.getSender())) {
								talkingTo.add(packet.getSender());
								chatController.openNewTab(packet);
							}
							// Aditya N =>On new incoming message make the tab
							// green in color
							if (allChats.containsKey(packet.getSender())) {
								allChats.get(packet.getSender()).add(packet);
								if (packet.getType() != 6)
									chatController.makeColor(
											packet.getSender(), "#CCFFCC");
								else
									chatController.makeColor(
											packet.getSender(), "#99CCFF");

							} else {
								ArrayList<Packet> temp = new ArrayList<Packet>();
								temp.add(packet);
								allChats.put(packet.getSender(), temp);
								// recToButton.get(packet.getSender()).setStyle("-fx-background-color:#CCFFCC");;

							}

							if (chatController.getRec().equals(
									packet.getSender())) {
								// chatController.printMessage(packet.getSender(),
								// packet.getMessage());;
								chatController.refresh(packet.getSender());
								chatController.makeColor(packet.getSender(),
										"#E0E0E0");

							}
						}
					}
				}

				// chatController.printMessage(packet.getSender(),packet.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// displayta.append( packet.getSender() + ": " + packet.getMessage()
			// +"\n" );
		}

	}

	public String getUsername() {
		return username;
	}

	public void setController(ChatController controller) {
		chatController = controller;
	}

	// Sh:Function to set the logicContoller
	public void setLoginController(LoginController controller) {
		loginController = controller;
	}

	// Sh:Function to set the signupContoller
	public void setSignupController(SignupController controller) {
		signupController = controller;
	}

	/*
	 * public static void main(String[] args) { //new Client("localhost", 5000);
	 * }
	 */
}