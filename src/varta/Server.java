package varta;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

public class Server {

	private ServerSocket ss;
	public Map<String, Socket> usernameSocketMapping = new HashMap<String, Socket>();
	public Map<String, String> webcamAliveMapping = new HashMap<String, String>();
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

			//System.out.println("ip is "+s.getRemoteSocketAddress().toString());
			new ServerThread( this, s );
		}
	}
	
	
	public  int addFriend(String sender, String reciever, boolean isActive)
	{
		
		PreparedStatement pstmt;
		try {

			System.out.println("insert into friendList values("+sender+","+reciever+","+isActive+")");
			pstmt = conn.prepareStatement("insert into friendList values(?,?,?)");
			pstmt.setString(1,sender);
			System.out.println("SteSTring");
			pstmt.setString(2,reciever);
			if (isActive)
				pstmt.setInt(3,0);
			else
				pstmt.setInt(3,1);
			pstmt.executeUpdate();
			System.out.println("executed Query");
			
			return 0;
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if (e.getMessage().contains
					("ERROR: duplicate key value violates unique constraint ")) 
			return 1;
			return -1;
		}	
	}
	
	public String getRandomPerson(String sender)
	{
		if (isAlive.contains(sender))
			isAlive.remove(sender);
		Random randomGenerator= new Random();
		if(isAlive.size() == 0)
		{
			 isAlive.add(sender);
			return "No One Online";
		}
		 int randomInt = randomGenerator.nextInt(isAlive.size());
		 int i = 0;
		 for(String s : isAlive )
		 {
		     if (randomInt == i)
		     {
		    	 isAlive.add(sender);
		         return s;
		     }
		     i = i + 1;
		 }
		 isAlive.add(sender);
		 return "No One Online";
	}
	
	/* get a list of friends for a user Sender*/
	ArrayList<String> getfriendlist(String sender){
		ArrayList<String> friends = new ArrayList<String>();
		try
		{
		PreparedStatement pstmt = conn.prepareStatement(
				"select friend from friendlist where username = ?");
		pstmt.setString(1, sender);
		ResultSet rs = pstmt.executeQuery();
		System.out.println("Getting friend list");
		while(rs.next())
			{
			System.out.println("Friend is "+rs.getString(1));
				friends.add(rs.getString("friend"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return friends;
		}
	
	//Sh:Connect to database
	private void connectPostgres()
	{
		String hostname = "127.0.0.1";
		String dbname = "Varta-Chat";
		String username = "postgres";
		String password = "killer20=20";

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
				
				Statement st=Server.conn.createStatement();
				ResultSet rs=st.executeQuery("SELECT * FROM user_info where username='"
						+packet.getReceiver()+"'");
				if(rs.next())
				{
					PreparedStatement pstmt = conn.prepareStatement("insert into msg_buffer(sender,receiver,message,status) values('"
							+ packet.getSender()+"','"+packet.getReceiver()+"',?,'1')");	
					pstmt.setString(1,packet.getMessage());
					pstmt.executeUpdate();
					System.out.println("Message added to buffer");
				}
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}

		}
		else if(packet.getType()==10)
		{/*Sh:Add the message to msg_buffer in the database to send it later*/
			try
			{
				Statement st=Server.conn.createStatement();
				ResultSet rs=st.executeQuery("SELECT * FROM user_info where username='"
						+packet.getReceiver()+"'");
				if(rs.next())
				{	
					PreparedStatement pstmt = conn.prepareStatement("insert into msg_buffer(sender,receiver,message,status) values('"
							+ packet.getSender()+"','"+packet.getReceiver()+"',?,'10')");	
					pstmt.setString(1,packet.getMessage());
					pstmt.executeUpdate();
					System.out.println("Message added to buffer");
				}
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}

		}
		else if(packet.getType()==11)
		{/*Sh:Add the message to msg_buffer in the database to send it later*/
			try
			{
				Statement st=Server.conn.createStatement();
				ResultSet rs=st.executeQuery("SELECT * FROM user_info where username='"
						+packet.getReceiver()+"'");
				if(rs.next())
				{
					PreparedStatement pstmt = conn.prepareStatement("insert into msg_buffer(sender,receiver,message,status) values('"
							+ packet.getSender()+"','"+packet.getReceiver()+"',?,'11')");	
					pstmt.setString(1,packet.getMessage());
					pstmt.executeUpdate();
					System.out.println("Message added to buffer");
				}
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
