package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteChatBoxServant extends UnicastRemoteObject implements IRemoteChatBox {
	
	private static final long serialVersionUID = 1L;
	ArrayList<String> chatList;

	public RemoteChatBoxServant() throws RemoteException {
		
		chatList = new ArrayList<>();
		
	}

	@Override
	public void addText(String userName, String text) throws RemoteException {
		// TODO Auto-generated method stub
		chatList.add("(" + userName + ") " + text);
		
	}

	@Override
	public ArrayList<String> getTexts() throws RemoteException {
		// TODO Auto-generated method stub
		return this.chatList;
	}
	

}
