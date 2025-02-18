package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
//import java.util.Map;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.LinkedHashMultimap;
import java.util.ArrayList;

public interface IRemoteChatBox extends Remote {
	
	void addText(String userName, String text) throws RemoteException;
	
	ArrayList<String> getTexts() throws RemoteException;

}
