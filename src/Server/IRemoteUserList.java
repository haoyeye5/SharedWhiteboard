package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteUserList extends Remote {
	
	void addUser(String userName) throws RemoteException;
	
	ArrayList<String> getUsers() throws RemoteException;
	
	void setManager(String managerName) throws RemoteException;
	
	String getManager() throws RemoteException;
	
	void removeUser(String userName) throws RemoteException;

}
