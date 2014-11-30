package varta;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class ServerThread extends Thread {
	private Server server;
	private Socket socket;
	public String username;

	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		start();
	}

	private String getFriendsMessage(Packet packet) {
		ArrayList<String> friends = new ArrayList<String>();
		friends = server.getfriendlist(packet.getSender());
		String friendsMessage = "";
		// Prepare delimited list to send to sender
		for (int i = 0; i < friends.size(); i++) {
			friendsMessage = friendsMessage + friends.get(i) + ":";
		}
		return friendsMessage;
	}

	public void run() {
		ObjectInputStream din = null;
		try {
			din = new ObjectInputStream(socket.getInputStream());
			Packet packet = (Packet) din.readObject();
			username = packet.getSender();

			server.usernameSocketMapping.put(username, socket);
			server.isAlive.add(username);
			// Sh: Login validation message
			if (packet.getType() == 0) {
				try {
					String password = packet.getMessage();
					Statement st = Server.conn.createStatement();
					ResultSet rs = st
							.executeQuery("SELECT * FROM user_info where username='"
									+ username
									+ "' and password='"
									+ password
									+ "'");
					if (!rs.next()) {// User does not exist
						server.forwardToReceiver(new Packet(0, "Server",
								username, "Invalid"));
						synchronized (server.usernameSocketMapping) {
							server.usernameSocketMapping.remove(username);
						}
						synchronized (server.isAlive) {
							server.isAlive.remove(username);
						}
						return;
					} else {// Authentic user
						server.forwardToReceiver(new Packet(0, "Server",
								username, "Authentic"));

					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			}
			// Sh: Signup message
			if (packet.getType() == 2) {
				try {
					String[] message = packet.getMessage().split(":");
					String name = message[0];
					String password = message[1];
					// String sex=message[2];
					String username = message[2];
					Statement st = Server.conn.createStatement();
					ResultSet rs = st
							.executeQuery("SELECT * FROM user_info where username='"
									+ username + "'");
					if (rs.next()) {// Username already present
						server.forwardToReceiver(new Packet(2, "Server",
								username, "Username present"));
						synchronized (server.usernameSocketMapping) {
							server.usernameSocketMapping.remove(username);
						}
						synchronized (server.isAlive) {
							server.isAlive.remove(username);

						}
						return;
					} else {// Register user
						String query = "INSERT INTO user_info VALUES('" + name
								+ "','" + password + "'," + "NULL,'" + username
								+ "')";
						System.out.println(query);
						st.executeUpdate(query);
						server.forwardToReceiver(new Packet(2, "Server",
								username, "User Registererd"));

					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			System.out.println("State of isAlive map is: " + server.isAlive);

			try {
				Statement st = Server.conn.createStatement();
				ResultSet rs = st
						.executeQuery("SELECT sender,message,status FROM msg_buffer where receiver='"
								+ username + "' order by sender,sno");
				String sendflag = null;
				String sender = null;
				while (rs.next()) {

					sender = rs.getString("sender");
					int status = rs.getInt("status");
					String message = rs.getString("message");
					System.out.println("sender: " + sender);
					System.out.println("receiver: " + username);
					System.out.println("message: " + message);
					server.forwardToReceiver(new Packet(status, sender,
							username, message));
					System.out.println("Sending " + packet);
					if (!sender.equals(sendflag)) {
						server.forwardToReceiver(new Packet(6, username,
								sender, "Message Received"));
						sendflag = sender;
					}

				}

				st.executeUpdate("DELETE FROM msg_buffer where receiver='"
						+ username + "'");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			while (true) {
				packet = (Packet) din.readObject();

				if (packet.getType() == 3)// is an OK message
				{
					try {
						packet.setSender(username);
						System.out.println("Is OK message");
						Statement st = Server.conn.createStatement();
						ResultSet rs = st
								.executeQuery("SELECT * FROM user_info where username='"
										+ packet.getReceiver() + "'");
						if (rs.next()) {

							if (server.addFriend(packet.getSender(),
									packet.getReceiver(), true) == 1) {
								// packet.setMessage("Already in friend list");
								server.forwardToReceiver(new Packet(1,
										"Server", packet.getSender(),
										"Already in friend list"));
								System.out.println("ALready in list");
							} else {
								System.out.println("Sending " + packet);
								server.forwardToReceiver(new Packet(1,
										"Server", packet.getReceiver(), packet
												.getMessage()));
								server.forwardToReceiver(new Packet(1,
										"Server", packet.getSender(), packet
												.getMessage()));
							}
						} else {
							server.forwardToReceiver(new Packet(1, "Server",
									packet.getSender(), "User does not exist"));
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				} else if ((packet.getType() == 8))// is a getFriends message
				{
					packet.setSender(username);
					System.out.println("Type 8");
					/*
					 * try { //Thread.sleep(3000); } catch (InterruptedException
					 * e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); }
					 */
					String FriendsMessage = getFriendsMessage(packet);
					server.forwardToReceiver(new Packet(8, "Server", packet
							.getSender(), "Friends|" + FriendsMessage));
				} else if (packet.getType() == 16) // Random conversation.
				{
					packet.setSender(username);
					String receiver = server
							.getRandomPerson(packet.getSender());
					if (!receiver.equals("No One Online")) {
						server.forwardToReceiver(new Packet(1, "Server", packet
								.getSender(), receiver
								+ "| will talk to you now."));
						server.forwardToReceiver(new Packet(1, "Server",
								receiver, packet.getSender()
										+ "| will talk to you now."));
					} else {
						System.out.println("No one online");
						server.forwardToReceiver(new Packet(16, "Server",
								packet.getSender(), "No One Online"));
						server.forwardToReceiver(new Packet(1, "Server", packet
								.getSender(), "No One Online"));
					}
				}

				else if (packet.getType() == 13) {// Webcam of live is started
					synchronized (server.webcamAliveMapping) {
						server.webcamAliveMapping.put(packet.getSender(),
								packet.getMessage());
					}
					System.out.println("State of webcamAliveMapping is: "
							+ server.webcamAliveMapping);

				} else if (packet.getType() == 14) {// Webcam of client is
													// closed
					if (server.webcamAliveMapping.containsKey(packet
							.getSender())) {
						synchronized (server.webcamAliveMapping) {
							server.webcamAliveMapping
									.remove(packet.getSender());
						}
					}
				} else if (packet.getType() == 15) {// Client requested for
													// another client's webcam
					if (server.webcamAliveMapping.containsKey(packet
							.getReceiver())) {// User Webcam is available
						server.forwardToReceiver(new Packet(15, "Server",
								packet.getSender(), server.webcamAliveMapping
										.get(packet.getReceiver())));
					} else {// User Webcam not available
						server.forwardToReceiver(new Packet(15, "Server",
								packet.getSender(), "Webcam Not Found"));
						server.forwardToReceiver(new Packet(1, "Server", packet
								.getSender(), "Webcam for "
								+ packet.getReceiver() + " is not available"));

					}
				} else if (packet.getType() == 17) {
					ArrayList<String> friends = new ArrayList<String>();
					friends = server.getfriendlist(packet.getSender());
					if (friends.isEmpty()) {
						server.forwardToReceiver(new Packet(1, "Server", packet
								.getSender(), "Your friendlist is empty."));
					} else {
						for (int i = 0; i < friends.size(); i++) {
							server.forwardToReceiver(new Packet(1, packet
									.getSender(), friends.get(i), packet
									.getMessage()));
						}
					}

				} else {
					packet.setSender(username);
					System.out.println("Sending " + packet);
					server.forwardToReceiver(packet);
				}
			}
		} catch (EOFException ie) {
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				din.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			synchronized (server.isAlive) {
				server.isAlive.remove(username);
			}
			synchronized (server.usernameSocketMapping) {
				server.usernameSocketMapping.remove(username);
			}
			System.out.println("State of isAlive map is: " + server.isAlive);
			server.removeConnection(socket);
		}
	}
}