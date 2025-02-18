package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;

import Server.IRemoteUserList;
import Server.IRemoteWhiteboard;
import Server.IRemoteChatBox;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.*;

public class JoinWhiteBoard {

	private static String ip;
	private static int port;
	private static String userName;
	private static WhiteboardApplication app;
	private static IRemoteWhiteboard remoteWhiteboard;
	private static IRemoteUserList remoteUserList;
	private static IRemoteChatBox remoteChatBox;
	
	private static boolean isKickOut = false;
	private static boolean isManagerQuit = false;
	
	private static Socket clientSocket = null;
	
	public static void main(String[] args) {
		
		ip = args[0];
		port = Integer.parseInt(args[1]);
		userName = args[2];
				
		
		/***************** Lookup for the remote services *******************/
		try {
			
			// connect to a registry and lookup the whiteboard service, set the service of whiteboard app
			Registry registry = LocateRegistry.getRegistry(ip, 5500);
			remoteWhiteboard = (IRemoteWhiteboard) registry.lookup("whiteboardService");
			remoteUserList = (IRemoteUserList) registry.lookup("userListService");
			remoteChatBox = (IRemoteChatBox) registry.lookup("chatBoxService");
			
			
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("AlreadyBoundException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Already Bound", "Error", JOptionPane.OK_CANCEL_OPTION);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("RemoteException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Lookup Fail", "Error", JOptionPane.OK_CANCEL_OPTION);
			//e.printStackTrace();
		} 
		
		/**************** Open up the connection to the manager **************/
		//Socket clientSocket = null;
		try {
			
			clientSocket = new Socket(ip, port);
			DataInputStream is = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
			
			//------------------ Handle connection to the whiteboard manager ---------------------//
			// create a JSON object containing action and userName
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "join whiteboard");
			jsonObject.put("username", userName);
			
			os.writeUTF(jsonObject.toString());
			os.flush();
			
			/****** receive response from the manager ******/
				
			// read in the JSON data sent from the manager
			String managerJsonData = is.readUTF();
			
			// parse that JSON data
			JSONObject managerResponseJsonObject = new JSONObject(managerJsonData);
			String responseType = managerResponseJsonObject.getString("responseType");
			
			
			/****** If the response type is connection response ******/
			if (responseType.equals("connection response")) {
				
				String managerResponse = managerResponseJsonObject.getString("response");
				if (managerResponse.equals("successfully join")) {
					
					// create a whiteboard application and show up the whiteboard window
					app = new WhiteboardApplication(remoteWhiteboard, remoteUserList, remoteChatBox, false, userName);
					app.WhiteboardWindow();
					
				}
				else {
					System.out.println(managerResponse);
					System.exit(0);     // exit the system successfully
				}
				
			}
			
			
			//--------------------- Handle the manager commands, keep listen to the commands from the manager ---------------------//
			while (true) {
			
				if (is.available()>0) {
					
					// read in the JSON data sent from the manager
					String managerCommandJsonData = is.readUTF();
					
					// parse that JSON data
					JSONObject managerCommandJsonObject = new JSONObject(managerCommandJsonData);
					String commandType = managerCommandJsonObject.getString("commandType");
					
					/********** If the manager command is to kick out a user **************/
					if (commandType.equals("kick out")) {
						
						String kickOutUserName = managerCommandJsonObject.getString("username");
						
						// check if the user itself is the user to be kicked out
						if (kickOutUserName.equals(userName)) {
							
							// if the user is exactly the user the manager wants to kick out
							kickOutByManager();
						}
						
					}
					
					/************** If the manager quit the whiteboard ***************/
					if (commandType.equals("manager quit")) {
						
						// invoke the manager quit mechanism
						managerQuit();
						
					}
				}
				
				// check if the user has been kicked out, or if the manager has closed the whiteboard
				if (isKickOut) {
					break;
				}
				if (isManagerQuit) {
					break;
				}
			}
			
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException Occurs.");
			JOptionPane.showConfirmDialog(null, "ServerError", "Error", JOptionPane.OK_CANCEL_OPTION);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException Occurs.");
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, "ServerError", "Error", JOptionPane.OK_CANCEL_OPTION);
		} finally {
            // Close socket 
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
	         // finally, after closing the socket, exit the system successfully
	         System.exit(0);
	    }
		
	}
	
	// users can choose to quit at anytime, the user's UserName would be removed from remote userList and close the window.
	public void quit() {
		
		try {
			remoteUserList.removeUser(userName);
			app.closeWindow();
			clientSocket.close();
			System.exit(0);   // exit the system successfully
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, "Remote Exception Occurs", "Error", JOptionPane.OK_CANCEL_OPTION);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// The method implement the function that the user been kick out by the manager, 
	// remove itself from the remote userList and close the window.
	private static void kickOutByManager() {
		
		try {
			remoteUserList.removeUser(userName);
			app.closeWindow();
			JOptionPane.showConfirmDialog(null, "You have been kick out by the whiteboard manager.", "Manager Message", JOptionPane.OK_CANCEL_OPTION);
			isKickOut = true;
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, "Remote Exception Occurs", "Error", JOptionPane.OK_CANCEL_OPTION);
		}
		
	}
	
	// manager has quit, all users have to quit
	private static void managerQuit() {
		
		try {
			remoteUserList.removeUser(userName);
			app.closeWindow();
			JOptionPane.showConfirmDialog(null, "The manager quits the whiteboard. Whiteboard closed.", "Manager Message", JOptionPane.OK_CANCEL_OPTION);
			isManagerQuit = true;
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
