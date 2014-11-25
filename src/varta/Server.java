package varta;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	private ServerSocket ss;
	public Map<String, Socket> usernameSocketMapping = new HashMap<String, Socket>();	
	private Hashtable<Socket, ObjectOutputStream> outputStreams = new Hashtable<Socket, ObjectOutputStream>();

	public Map<String, Boolean> isAlive = new HashMap<String, Boolean>();

	public Server( int port ) throws IOException {
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

	Enumeration getOutputStreams() {
		return outputStreams.elements();
	}

	void forwardToReceiver( Packet packet ) {
		//		synchronized( outputStreams ) {
		//			for (Enumeration e = getOutputStreams(); e.hasMoreElements(); ) {
		//				ObjectOutputStream dout = (ObjectOutputStream)e.nextElement();
		//				try {
		//					dout.writeObject( packet );dout.flush();
		//				} catch( IOException ie ) { System.out.println( ie ); }
		//			}
		//		}
		ObjectOutputStream dout = outputStreams.get(usernameSocketMapping.get(packet.getReceiver()));
		try {
			dout.writeObject( packet );
			dout.flush();
		} catch( IOException ie ) { 
			System.out.println( ie ); 
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
		int port = 5003;
		new Server( port );
	}

}
