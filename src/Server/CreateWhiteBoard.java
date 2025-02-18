package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
//import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.*;

import Client.WhiteboardApplication;

public class CreateWhiteBoard {
	
	private static String ip;
	private static int port;
	private static String userName;
	private static WhiteboardApplication app;
	private static IRemoteWhiteboard whiteboard;
	private static IRemoteUserList userList;
	private static IRemoteChatBox chatBox;
	
	// This ArrayList holds all the client connection, this is built for broadcasting manager's message
	private static ArrayList<Socket> clientConnections = new ArrayList<>();
	

	public static void main(String[] args) {
		
		ip = args[0];
		port = Integer.parseInt(args[1]);
		userName = args[2];
		
		
		/******************** Set up the remote services and open the whiteboard application ********************/
		try {
			
			// initialize remote objects
			whiteboard = new RemoteWhiteboardServant();
			userList = new RemoteUserListServant();
			chatBox = new RemoteChatBoxServant();
			
			// bind the remote services
			Registry registry = LocateRegistry.createRegistry(5500);
			registry.bind("whiteboardService", whiteboard);
			registry.bind("userListService", userList);
			registry.bind("chatBoxService", chatBox);
			System.out.println("Server RMI ready.");
			
			// create a whiteboard application and show up the whiteboard window
			userList.setManager(userName);
			app = new WhiteboardApplication(whiteboard, userList, chatBox, true, userName);
			app.WhiteboardWindow();
			
			try (ServerSocket server = new ServerSocket(port)) 
			{
				System.out.println("Manager server ready. Waiting for users connections.");
				
				while (true){
					
					Socket clientSocket = server.accept();    // accept the client
					clientConnections.add(clientSocket);      // add the connection to the connection arraylist
	                Thread connectionThread = new Thread(() -> serveClient(clientSocket));
	                connectionThread.start();
					
				}
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showConfirmDialog(null, "Server Error. Unable to Connect to the Server.", "Error", JOptionPane.OK_CANCEL_OPTION);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showConfirmDialog(null, "Server Error. Unable to Connect to the Server.", "Error", JOptionPane.OK_CANCEL_OPTION);
			} 
			
			
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("AlreadyBoundException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Already Bound", "Error", JOptionPane.OK_CANCEL_OPTION);
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("RemoteException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Connection Fail", "Error", JOptionPane.OK_CANCEL_OPTION);
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void serveClient(Socket client) {
		
		Socket clientSocket = null;
		
		try {
			
			clientSocket = client;
			DataInputStream is = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
			
			// read in the JSON data sent from the user
			String jsonData = is.readUTF();
			
			// parse that JSON data, and read the action of the user
			JSONObject jsonObject = new JSONObject(jsonData);
			String action = jsonObject.getString("action");
			
			//------------------- Handle users connection ------------------//
			if (action.equals("join whiteboard")) {
				String response = "";
				
				// extract the userName
				String userName = jsonObject.getString("username");
				
				if (userList.getUsers().contains(userName) || userList.getManager().equals(userName)) {
					response = "duplicate username, failed to join";
				}
				else {
					int result = JOptionPane.showConfirmDialog(null, "Someone wants to share your whiteboard. Accept?", "Connection Request", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						// if manager confirm with the connection, send back success response 
						response = "successfully join";
						userList.addUser(userName);
		            } else {
		            	// Handle rejection
		            	response = "connection rejected by whiteboard manager";
		            }
					
				}
				
				// create a JSON object and sent it to user
				JSONObject responseJsonObject = new JSONObject();
				responseJsonObject.put("responseType", "connection response");
				responseJsonObject.put("response", response);
				
				os.writeUTF(responseJsonObject.toString());
				os.flush();
				
			}

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	// The Manager can kick out a user based on the userName
	public void kickOut(String userName) {
		
		// iterate through all the client connection, broadcasting the message of kick out
		for (Socket clientSocket: clientConnections) {
			
			if (!clientSocket.isClosed()) {
				try {
					
					DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
					
					// create a JSON object containing the commandType 'kick out' and the userName
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("commandType", "kick out");
					jsonObject.put("username", userName);
					
					// send to user
					os.writeUTF(jsonObject.toString());
					os.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
		}
		
	}
	
	// The method implement the manager quit the whiteboard and broadcasts to each of the client
	public void quit() {
		
		// close the app window
		app.closeWindow();
		
		// iterate through all the client connection, broadcasting the quit message if the client is still connected
		for (Socket clientSocket: clientConnections) {
			
			// check if the socket us still open
			if (!clientSocket.isClosed()) {
				
				// if still open, broadcast the message
				try {
					
					DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
					
					// create a JSON object containing the commandType 'manager quit' and the userName
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("commandType", "manager quit");
					
					// send to user
					os.writeUTF(jsonObject.toString());
					os.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
			}
				
		}
		
	}
	
}
