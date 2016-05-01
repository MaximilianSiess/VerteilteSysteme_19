package blatt6_2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import blatt6_1.Job;

public interface IDispatcher<T> extends Remote {
	public Job<T> submit(Callable<T> task) throws RemoteException, Exception;

	public void register(Server2<T> server) throws RemoteException;

	public int getRegistryPortNumber() throws RemoteException;

	public String getServiceName() throws RemoteException;

}
