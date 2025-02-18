package Client;

import javax.swing.*;

import Server.IRemoteWhiteboard;

import java.awt.*;
import java.rmi.RemoteException;
import java.awt.event.*;

public class WhiteboardPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	private int startX, startY;
	private Point prevPoint;
    private String drawMode;
    private int eraserSize;
    private Color currentColour;
    private IRemoteWhiteboard remoteWhiteboard;
 

    public WhiteboardPanel(IRemoteWhiteboard remoteWhiteboard) {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setBackground(Color.white);
        this.drawMode = "FreeDraw";        // default mode
        this.currentColour = Color.BLACK; // Default color of drawing
        this.remoteWhiteboard = remoteWhiteboard;    // each whiteboard is indeed drawing on the shared remote canvas
    }
    
    
    
    @Override
    protected void paintComponent(Graphics g) {
    	
        super.paintComponent(g);
        
    	//System.out.println("You are drawing");
    	
    	try {
    		
    		// repeatedly rendering the canvas image 
			g.drawImage(remoteWhiteboard.getCanvas().getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
			
			repaint();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("RemoteException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Connection Fail", "Error", JOptionPane.OK_CANCEL_OPTION);
			e.printStackTrace();
		}    
    }
    	
    
    /**
     * MouseClicked event handled the drawing of shapes and texts, it draws on the remote canvas when users 
     * perform drawing operations at the client side.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    	
    	// get the start drawing coordinates from where the mouse is clicked
    	startX = e.getX();
    	startY = e.getY();
    	
    	try {
	    	switch (drawMode) {
		        case "Line":
		        	remoteWhiteboard.drawLine(startX, startY, currentColour);
		            break;
		            
		        case "Rectangle":
		        	remoteWhiteboard.drawRect(startX, startY, currentColour);
		        	//System.out.println(currentColour);
		            break;
		            
		        case "Oval":
		        	remoteWhiteboard.drawOval(startX, startY, currentColour);
		            break;
		            
		        case "Circle":
		        	remoteWhiteboard.drawCircle(startX, startY, currentColour);
		            break;
		            
		        case "Text":
		        	remoteWhiteboard.drawText(startX, startY, currentColour);
		        	break;
		        	
		        default:
		            break;
	    	}
	
    	} catch (RemoteException e1) {
    		System.out.println("RemoteException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Connection Fail", "Error", JOptionPane.OK_CANCEL_OPTION);
    	}
    	
    	//repaint();
    	
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    /**
     * mouseReleased event handles the reset of the point state for free-draw and eraser
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    	
    	// if we stop doing free-draw or erasing, we should reset the prevPoint to null, in order to ensure the next starting point 
    	// can be detected by the next mouse dragging events.
    	switch (drawMode) {
        
	        case "FreeDraw":
	        	prevPoint = null;
	        	break;
	        	
	        case "Eraser":
	        	prevPoint = null;
	        	break;
	        default:
	            break;
    	}
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * MouseDragged event handles free-drawing and erasing performed by the users
     */
    @Override
    public void mouseDragged(MouseEvent e) {
    
    	// get the current position of the mouse
        int x = e.getX();
        int y = e.getY(); 
        
        try {
	        switch (drawMode) {
	        
		        case "FreeDraw":
		        	if (prevPoint == null) {
		        		prevPoint = new Point();            // if we just start drawing, we need to create the new start point
		        		prevPoint.x = x;
		        		prevPoint.y = y;
		        	}
		        	else {
		        		remoteWhiteboard.freeDraw(prevPoint.x, prevPoint.y, x, y, currentColour);
			            prevPoint.x = x;
			            prevPoint.y = y;
		        	}
		        	
		        	break;
		        	
		        case "Eraser":
		        	if (prevPoint == null) {
		        		prevPoint = new Point();          // same mechanism for eraser.
		        		prevPoint.x = x;
		        		prevPoint.y = y;
		        	}
		        	else {
		        		remoteWhiteboard.eraser(prevPoint.x, prevPoint.y, x, y, currentColour, eraserSize);
			            prevPoint.x = x;
			            prevPoint.y = y;
		        	}
		        	break;
		        	
		        default:
		            break;
	        }
	        
	        
        } catch (RemoteException e2) {
        	System.out.println("RemoteException Occurs.");
			JOptionPane.showConfirmDialog(null, "RMI Connection Fail", "Error", JOptionPane.OK_CANCEL_OPTION);
        }
    	
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    
    /********** Setters **********/
    // set the current draw mode of the panel, this is based on the users input by clicking the button
    public void setDrawMode(String mode) {
        drawMode = mode;
    }
    
    // set the current colour of the drawing on panel, this is based on the users input
    public void setColour(Color colour) {
    	this.currentColour = colour;
    }
    
    // set the current eraser size
    public void setEraserSize(int eraserSize) {
    	this.eraserSize = eraserSize;
    }
    
    
}
