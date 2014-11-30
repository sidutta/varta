package varta;

import java.sql.Date;

/*
 * 1 => normal message
 * 2 => Sign up / validation
 * 3 => addign friend
 * 4 => Sent by me
 * 5 => is Typing message
 * 6 => message received
 * 7 =>
 * 8 => getting friend list
 * 9 => Message sent by me
 * 10 => Picture
 * 11 => Video
 * 12 => 
 * 13 => Webcam status broadcast
 * 14 => Webcam closed 
 * 15 => I need to acces somebody's webcam
 */
public class Packet implements java.io.Serializable {

	private static final long serialVersionUID = 3274127957428709427L;
	private int type;
	private String sender;
	private String receiver;
	private String message;

	public Packet(int type, String sender, String receiver, String message) {
		this.type = type; // 0: connection; 1: message
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
