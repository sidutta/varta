package varta;
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import varta.Client;

public class ClientApplet extends Applet {
	
	public void init() {
		String host = "localhost";
		int port = 5000;
		setLayout( new BorderLayout() );
		add( "Center", new Client( host, port ) );
	}
	
}