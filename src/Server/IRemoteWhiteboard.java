package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.awt.*;

public interface IRemoteWhiteboard extends Remote{
	
	void drawRect(int startX, int startY, Color colour) throws RemoteException;
	
	void drawCircle(int startX, int startY, Color colour) throws RemoteException;
	
	void drawOval(int startX, int startY, Color colour) throws RemoteException;
	
	void drawLine(int startX, int startY, Color colour) throws RemoteException;
	
	void drawText(int startX, int startY, Color colour) throws RemoteException;
	
	void freeDraw(int prevX, int prevY, int x, int y, Color colour) throws RemoteException;
	
	void eraser(int prevX, int prevY, int x, int y, Color colour, int eraserSize) throws RemoteException;
	
	SerializableBufferedImage getCanvas() throws RemoteException;
	
	void setCanvas(SerializableBufferedImage image) throws RemoteException;
	
	void clearCanvas() throws RemoteException;

}
