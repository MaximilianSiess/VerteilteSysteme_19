package blatt6;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote{
 
    public void setLimit (int limit) throws RemoteException;
    
    public <T> Job submit (TaskCallable task) throws RemoteException, Exception;
    
    public void close() throws RemoteException;
}
