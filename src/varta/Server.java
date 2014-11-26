package varta;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

public class Server {

	private ServerSocket ss;
	public Map<String, Socket> usernameSocketMapping = new HashMap<String, Socket>();	
	private Hashtable<Socket, ObjectOutputStream> outputStreams = new Hashtable<Socket, ObjectOutputStream>();

	//Sh:Set of online clients
	public Set<String> isAlive = new HashSet<String>();
	//Sh:Connection variable
	static public Connection conn = null;

	public Server( int port ) throws IOException {
		connectPostgres();
		listen( port );
		
	}

	private void listen( int port ) throws IOException {
		ss = new ServerSocket( port, 0, InetAddress.getByName("localhost") );
		System.out.println( "Listening on " + ss );
		while (true) {
			Socket s = ss.accept();
			System.out.println( "Connection from "+s );
			ObjectOutputStream dout = new ObjectOutputStream( s.getOutputStream() );
			outputStreams.put( s, dout );
			new ServerThread( this, s );
		}
	}
	//Sh:Connect to database
	private void connectPostgres()
	{
		String hostname = "127.0.0.1";
		String dbname = "myapp";
		String username = "admineamcgrq";
		String password = "78wFsNQqyyi5";
		
		String dbURL = "jdbc:postgresql://"+hostname+"/"+dbname;
		
		try {
			Class.forName("org.postgresql.Driver") ;
			conn = DriverManager.getConnection(dbURL, username, password);
			System.out.println("initialized connection: "+conn);


		} catch (Exception e) {
			System.out.println("JDBC Connection/ db initialization Exception");
			System.out.println(e.getMessage() + " " + e.toString() + " " + e.getCause());
			e.printStackTrace();

		}
		
	}

	Enumeration getOutputStreams() {
		return outputStreams.elements();
	}

	void forwardToReceiver( Packet packet ) {
		if(isAlive.contains(packet.getReceiver()))
		{/*if other user is online, send the message*/
		ObjectOutputStream dout = outputStreams.get(usernameSocketMapping.get(packet.getReceiver()));
		
		try {
			dout.writeObject( packet );
			dout.flush();
			System.out.println("Message sent to "+ packet.getReceiver());
			
		} catch( IOException ie ) { 
			System.out.println( ie ); 
		}
		}
		else if(packet.getType()==1)
		{/*Sh:Add the message to msg_buffer in the database to send it later*/
			try
			{
			PreparedStatement pstmt = conn.prepareStatement("insert into msg_buffer(sender,receiver,message,status) values('"
					+ packet.getSender()+"','"+packet.getReceiver()+"',?,'1')");	
			pstmt.setString(1,packet.getMessage());
			pstmt.executeUpdate();
			System.out.println("Message added to buffer");
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		}
	}

	void removeConnection( Socket s ) {
		synchronized( outputStreams ) {
			System.out.println( "Closing connection from " + s );
			outputStreams.remove( s );
			try {
				s.close();
			} catch( IOException ie ) {
				System.out.println( "Error closing " + s );
				ie.printStackTrace();
			}
		}
	}

	static public void main( String args[] ) throws Exception {
		// int port = Integer.parseInt( args[0] );
		int port = 5002;
		new Server( port );
	}

}
