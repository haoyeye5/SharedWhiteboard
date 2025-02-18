package Server;

import java.rmi.*;
import java.rmi.server.*;

import javax.swing.JOptionPane;

import java.awt.image.BufferedImage;
import java.awt.*;

public class RemoteWhiteboardServant extends UnicastRemoteObject implements IRemoteWhiteboard{
	
	private static final long serialVersionUID = 1L;
	private final int CANVAS_WIDTH = 563;
	private final int CANVAS_HEIGHT = 405;
	
	private SerializableBufferedImage canvas;        // The WhiteboardServant class contains a shared canvas, all the drawing actions are done on this canvas

	public RemoteWhiteboardServant() throws RemoteException {
		
		canvas = new SerializableBufferedImage(new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB));
	}

	@Override
	public void drawRect(int startX, int startY, Color colour) throws RemoteException {
		// TODO Auto-generated method stub
		
		// prompt the user to input the width of rectangle
		String widthInput = null;
		while (widthInput == null || widthInput.isEmpty()) {
			widthInput = JOptionPane.showInputDialog("Enter the width of the rectangle:");
        } 
		// Convert the input string to an integer
        int width = Integer.parseInt(widthInput);
		
		// prompt the user to input the height of rectangle
		String heightInput = null;
		while (heightInput == null || heightInput.isEmpty()) {
			heightInput = JOptionPane.showInputDialog("Enter the height of the rectangle:");
		}
        // Convert the input string to an integer
        int height = Integer.parseInt(heightInput);
        
        // draw on the shared canvas
		Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.drawRect(startX, startY, width, height);
		g2d.dispose();
		
	}

	@Override
	public void drawCircle(int startX, int startY, Color colour) throws RemoteException {
		// TODO Auto-generated method stub
		
		// prompt the user to input the radius of circle
		String radiusInput = null;
		while (radiusInput == null || radiusInput.isEmpty()) {
			radiusInput = JOptionPane.showInputDialog("Enter the radius of the circle:");
        } 
		// Convert the input string to an integer
        int radius = Integer.parseInt(radiusInput);
        
        Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.drawOval(startX - radius, startY - radius, 2 * radius, 2 * radius);
		g2d.dispose();
	}

	@Override
	public void drawOval(int startX, int startY, Color colour) throws RemoteException {
		// TODO Auto-generated method stub
		
		// prompt the user to input the width of the bounded rectangle for the oval
		String widthInput = null;
		while (widthInput == null || widthInput.isEmpty()) {
			widthInput = JOptionPane.showInputDialog("Enter the width of the oval's bounded rectangle:");
        } 
		// Convert the input string to an integer
        int width = Integer.parseInt(widthInput);
		
		// prompt the user to input the height of rectangle
		String heightInput = null;
		while (heightInput == null || heightInput.isEmpty()) {
			heightInput = JOptionPane.showInputDialog("Enter the height of the oval's bounded rectangle:");
		}
        // Convert the input string to an integer
        int height = Integer.parseInt(heightInput);
        
        Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.drawOval(startX, startY, width, height);
		g2d.dispose();
	}

	@Override
	public void drawLine(int startX, int startY, Color colour) throws RemoteException {
		// TODO Auto-generated method stub
		
		// prompt the user to input the width of the bounded rectangle for the oval
		String lengthInput = null;
		while (lengthInput == null || lengthInput.isEmpty()) {
			lengthInput = JOptionPane.showInputDialog("Enter the length of the line:");
        } 
		// Convert the input string to an integer
        int length = Integer.parseInt(lengthInput);
        
        // prompt the user to input the width of the bounded rectangle for the oval
		String angleInput = null;
		while (angleInput == null || angleInput.isEmpty()) {
			angleInput = JOptionPane.showInputDialog("Enter the angle of the line (in degrees):");
        } 
		// Convert the input string to an integer
        int angle = Integer.parseInt(angleInput);
        // Convert the angle from degrees to radians
        double angleRadians = Math.toRadians(angle);

        // Calculate the x and y components of the line based on length and angle
        int endX = startX + (int) (length * Math.cos(angleRadians));
        int endY = startY + (int) (length * Math.sin(angleRadians));
    	
        Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.drawLine(startX, startY, endX, endY);
		g2d.dispose();
	}

	@Override
	public void drawText(int startX, int startY, Color colour) throws RemoteException {
		// TODO Auto-generated method stub
		
		// prompt the user to input some non-empty text
		String userInput = null;
		while (userInput == null || userInput.isEmpty()) {
			userInput =JOptionPane.showInputDialog("Please enter a text:");
        } 
		
		Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.drawString(userInput, startX, startY);
		g2d.dispose();
	}

	@Override
	public void freeDraw(int prevX, int prevY, int x, int y, Color colour) throws RemoteException {
		// TODO Auto-generated method stub
		
		Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.drawLine(prevX, prevY, x, y);
		g2d.dispose();
	}

	@Override
	public void eraser(int prevX, int prevY, int x, int y, Color colour, int eraserSize) throws RemoteException {
		// TODO Auto-generated method stub
		
		Graphics2D g2d = canvas.getImage().createGraphics();
		g2d.setColor(colour);
		g2d.setStroke(new BasicStroke(eraserSize));
		g2d.drawLine(prevX, prevY, x, y);
		g2d.dispose();
		
	}
	
	@Override
	public SerializableBufferedImage getCanvas() throws RemoteException {
		// TODO Auto-generated method stub
		
		return canvas;
	}

	@Override
	public void setCanvas(SerializableBufferedImage image) throws RemoteException {
		// TODO Auto-generated method stub
		
		canvas = image;
		
	}
	
	public void clearCanvas() throws RemoteException {
		
		canvas.clear();
		
	}

}
