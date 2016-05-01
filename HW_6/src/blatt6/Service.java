package blatt6;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

public interface Service<T> extends Remote {

	public void setLimit(int limit) throws RemoteException;

	public Job<T> submit(Callable<T> task) throws RemoteException, Exception;

	public boolean isDone(int id) throws RemoteException;

	public T getResult(int id) throws RemoteException;

	public void close() throws RemoteException;
}
