package blatt5;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DeepThougtInterface extends Remote {

	void notifyClient(String answer) throws RemoteException;
}
