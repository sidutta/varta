package varta;

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
}
