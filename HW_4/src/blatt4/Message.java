package blatt4;

import java.io.Serializable;
import java.util.LinkedList;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private MessageType type;
	private String text;
	private LinkedList<NodeAddress> subject;

	public Message(MessageType type, LinkedList<NodeAddress> subject) {
		this.type = type;
		this.subject = subject;
	}

	public Message(String text) {
		this.type = MessageType.INFO;
		this.text = text;
		this.subject = new LinkedList<NodeAddress>();
	}

	public MessageType getType() {
		return type;
	}

	public LinkedList<NodeAddress> getSubject() {
		return subject;
	}

	public void setSubject(LinkedList<NodeAddress> subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}
}
