package blatt6_2;

import java.rmi.RemoteException;

public interface IServer2 {

	int getRegistryPortNumber() throws RemoteException;

	String getServiceName() throws RemoteException;

}
