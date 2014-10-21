package varta;
import java.io.*;
import java.net.*;
public class ServerThread extends Thread
{
	private Server server;
	private Socket socket;
	public String username;
	public ServerThread( Server server, Socket socket ) {
		this.server = server;
		this.socket = socket;
		start();
	}
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
				server.forwardToReceiver( packet );
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