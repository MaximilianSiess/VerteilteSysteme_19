package blatt5;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {

	public int add(int value1, int value2) throws RemoteException;

	public int sub(int value1, int value2) throws RemoteException;

	public int mul(int value1, int value2) throws RemoteException;

	public int lucas(int value) throws RemoteException;

	public int calculate(Operation operation, int[] values) throws RemoteException;

	public void deepThought(String question, DeepThougtInterface event) throws RemoteException;

}
