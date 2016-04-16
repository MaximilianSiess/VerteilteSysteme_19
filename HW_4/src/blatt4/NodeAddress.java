package blatt4;

import java.io.Serializable;
import java.net.InetAddress;

public class NodeAddress implements Serializable {

	private static final long serialVersionUID = 1L;
	private InetAddress address;
	private int port;
	private String name;
	private boolean searchQuest = false;

	public NodeAddress(InetAddress address, int port, String name) {
		super();
		this.address = address;
		this.port = port;
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeAddress other = (NodeAddress) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public String getName() {
		return name;
	}
	
	public void makeSearchNodeAddress() {
		searchQuest = true;
	}
	
	public boolean getSearchQuest() {
		return searchQuest;
	}

	@Override
	public String toString() {
		return "" + port;
	}

}