package varta;
import java.io.*;
import java.net.*;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
			
			server.usernameSocketMapping.put(username, socket);
			server.isAlive.add(username);
			//Sh: Login validation message
			if(packet.getType()==0)
			{
			try
			{
			String password=packet.getMessage();	
				Statement st=Server.conn.createStatement();
				ResultSet rs=st.executeQuery("SELECT * FROM user_info where username='"
						+username+"' and password='"+password+"'");
				if (!rs.next()){//User does not exist
					server.forwardToReceiver( new Packet(0,"Server",username,"Invalid"));
					synchronized(server.usernameSocketMapping) {
						server.usernameSocketMapping.remove(username);
					}
					synchronized(server.isAlive) {
						server.isAlive.remove(username);
					}
					return;
					}
				else
				{//Authentic user
					server.forwardToReceiver( new Packet(0,"Server",username,"Authentic"));
				}
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			}
			//Sh: Signup message
			if(packet.getType()==2)
			{
				try
				{
				String[] message=packet.getMessage().split(":");
				String name=message[0];
				String password=message[1];
				//String sex=message[2];
				String username=message[2];
				Statement st=Server.conn.createStatement();
				ResultSet rs=st.executeQuery("SELECT * FROM user_info where username='"
						+username+"'");
				if(rs.next())
				{//Username already present
					server.forwardToReceiver( new Packet(2,"Server",username,"Username present"));
					synchronized(server.usernameSocketMapping) {
						server.usernameSocketMapping.remove(username);
					}
					synchronized(server.isAlive) {
						server.isAlive.remove(username);
						
					}
					return;
				}
				else
				{//Register user
					String query="INSERT INTO user_info VALUES('"+name+"','"+password+"',"
					           +"NULL,'"+username+"')";
					System.out.println(query);
					st.executeUpdate(query);
					server.forwardToReceiver( new Packet(2,"Server",username,"User Registererd"));
				}
					
				}
				catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			System.out.println("State of isAlive map is: " + server.isAlive);
			
			try
			{
			Statement st=Server.conn.createStatement();	
			ResultSet rs=st.executeQuery("SELECT sender,message FROM msg_buffer where receiver='"
			+username+"' order by sender,sno");
			String sendflag=null;
			String sender=null;
			while(rs.next())
			{
				
				sender=rs.getString("sender");
				String message=rs.getString("message");
				System.out.println("sender: "+ sender);
				System.out.println("receiver: "+ username);
				System.out.println("message: "+ message);
				server.forwardToReceiver( new Packet(1,sender,username,message));
				System.out.println( "Sending " + packet );
				if (!sender.equals(sendflag))
				{
					server.forwardToReceiver( new Packet(6,username,sender,"Message Received"));
					sendflag=sender;
				}
				
			}
			
				
			st.executeUpdate("DELETE FROM msg_buffer where receiver='"+username+"'");
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
			
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
				server.isAlive.remove(username);
			}
			synchronized(server.usernameSocketMapping) {
				server.usernameSocketMapping.remove(username);
			}
			System.out.println( "State of isAlive map is: " + server.isAlive );
			server.removeConnection( socket );
		}
	}
}