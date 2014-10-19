package varta;
import java.io.*;
import java.net.*;
public class ServerThread extends Thread
{
	// The Server that spawned us
	private Server server;
	// The Socket connected to our client
	private Socket socket;
	public String username;
	public ServerThread( Server server, Socket socket ) {
		// Save the parameters
		this.server = server;
		this.socket = socket;
		//Start up the thread
		start();
	}
	//This runs in a separate thread when start() is called in the
	//constructor.
	public void run() {
		ObjectInputStream din = null;
		try {
			din = new ObjectInputStream( socket.getInputStream() );
			Packet packet = (Packet) din.readObject();
			username = packet.getSender();
			server.isAlive.put(username, Boolean.parseBoolean("True"));
			server.usernameSocketMapping.put(username, socket);
			System.out.println("State of isAlive map is: " + server.isAlive);
			while (true) {
				packet = (Packet) din.readObject();
				packet.setSender(username);
				System.out.println( "Sending " + packet );
				server.sendToAll( packet );
			}
		} catch( EOFException ie ) {
		} catch( IOException ie ) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				din.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			synchronized(server.isAlive) {
				server.isAlive.put(username, Boolean.parseBoolean("False"));
			}
			synchronized(server.usernameSocketMapping) {
				server.usernameSocketMapping.remove(username);
			}
			System.out.println( "State of isAlive map is: " + server.isAlive );
			server.removeConnection( socket );
		}
	}
}
