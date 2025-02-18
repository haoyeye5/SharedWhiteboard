package Server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class RemoteUserListServant extends UnicastRemoteObject implements IRemoteUserList {

	private static final long serialVersionUID = 1L;
	private String managerName = null;
	private ArrayList<String> userList;

	public RemoteUserListServant() throws RemoteException {
		
		userList = new ArrayList<>();
	}

	@Override
	public void addUser(String userName) throws RemoteException {
		// TODO Auto-generated method stub
		this.userList.add(userName);
	}

	@Override
	public ArrayList<String> getUsers() throws RemoteException {
		// TODO Auto-generated method stub
		return this.userList;
	}

	@Override
	public void setManager(String managerName) throws RemoteException {
		// TODO Auto-generated method stub
		this.managerName = managerName;
	}

	@Override
	public String getManager() throws RemoteException {
		// TODO Auto-generated method stub
		return this.managerName;
	}

	@Override
	public void removeUser(String userName) throws RemoteException {
		// TODO Auto-generated method stub
		this.userList.remove(userName);
	}
	
}
