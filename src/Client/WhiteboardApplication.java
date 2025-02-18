package Client;

import java.awt.EventQueue;


import javax.swing.JFrame;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.WindowEvent;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.*;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import Server.IRemoteWhiteboard;
import Server.SerializableBufferedImage;
import Server.IRemoteUserList;
import Server.IRemoteChatBox;
import Server.CreateWhiteBoard;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import javax.imageio.ImageIO;
import Server.SerializableBufferedImage;

public class WhiteboardApplication {

	private JFrame frame;
	private IRemoteWhiteboard remoteWhiteboard;
	private IRemoteUserList remoteUserList;
	private IRemoteChatBox remoteChatBox;
	private boolean isManager;
	private String userName;
	
	private Thread updateUserListThread;
	private Thread updateChatBoxThread;
	
	// depend on if the user is manager or not, one of the following two variables would be assigned
	private CreateWhiteBoard createWhiteboard;
    private JoinWhiteBoard joinWhiteboard;
    private JTextField KickOutTextField;


	/**
	 * Launch the application.
	 */
	public void WhiteboardWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//WhiteboardApplication window = new WhiteboardApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WhiteboardApplication(IRemoteWhiteboard remoteWhiteboard, IRemoteUserList remoteUserList, IRemoteChatBox remoteChatBox, boolean isManager, String userName) {
		this.remoteWhiteboard = remoteWhiteboard;
		this.remoteUserList = remoteUserList;
		this.remoteChatBox = remoteChatBox;
		this.isManager = isManager;
		this.userName = userName;
		
		// If it is a manager, create a CreateWhiteboard variable
        if (isManager) {
            createWhiteboard = new CreateWhiteBoard();
        } else { // If it is a user, create a JoinWhiteboard variable
            joinWhiteboard = new JoinWhiteBoard();
        }
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 750, 500);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set the default close operation to do nothing
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 117, 104, 0, 86, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 190, 0, 190, 0, 0, 0, 190, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		if (isManager) {
			
            // setup manager's app name
            String appTitle = "Distributed Shared Whiteboard (Manager)" + userName;
            frame.setTitle(appTitle);
            
            // The manager has kick-out button and kick-out text area
            JLabel KickOutLabel = new JLabel("Kick Out");
    		GridBagConstraints gbc_KickOutLabel = new GridBagConstraints();
    		gbc_KickOutLabel.gridwidth = 2;
    		gbc_KickOutLabel.insets = new Insets(0, 0, 5, 5);
    		gbc_KickOutLabel.gridx = 1;
    		gbc_KickOutLabel.gridy = 1;
    		frame.getContentPane().add(KickOutLabel, gbc_KickOutLabel);
    		
    		KickOutTextField = new JTextField();
    		GridBagConstraints gbc_kickOutTextField = new GridBagConstraints();
    		gbc_kickOutTextField.gridwidth = 3;
    		gbc_kickOutTextField.insets = new Insets(0, 0, 5, 5);
    		gbc_kickOutTextField.fill = GridBagConstraints.HORIZONTAL;
    		gbc_kickOutTextField.gridx = 3;
    		gbc_kickOutTextField.gridy = 1;
    		frame.getContentPane().add(KickOutTextField, gbc_kickOutTextField);
    		KickOutTextField.setColumns(10);
    		
    		JButton KickOutBtn = new JButton("Kick Out");
    		GridBagConstraints gbc_KickOutBtn = new GridBagConstraints();
    		KickOutBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				String userNameText = KickOutTextField.getText();
    				createWhiteboard.kickOut(userNameText);
    			}
    		});
    		gbc_KickOutBtn.anchor = GridBagConstraints.WEST;
    		gbc_KickOutBtn.gridwidth = 3;
    		gbc_KickOutBtn.insets = new Insets(0, 0, 5, 5);
    		gbc_KickOutBtn.gridx = 1;
    		gbc_KickOutBtn.gridy = 2;
    		frame.getContentPane().add(KickOutBtn, gbc_KickOutBtn);
    		
    		// The Manager's quit button
            JButton QuitBtn = new JButton("Quit");
    		QuitBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    			
					// call the manager's quit method
					createWhiteboard.quit();
    				
    			}
    		});
    		GridBagConstraints gbc_QuitBtn = new GridBagConstraints();
    		gbc_QuitBtn.gridx = 9;
    		gbc_QuitBtn.gridy = 11;
    		frame.getContentPane().add(QuitBtn, gbc_QuitBtn);
    		
    		
    		//--------------------- Manager File Menu  ---------------------//
    		JMenuBar menuBar = new JMenuBar();
    		frame.setJMenuBar(menuBar);
    		
    		JMenu FileMenu = new JMenu("File");
    		menuBar.add(FileMenu);
    		
    		/*** Create a new canvas ***/
    		JMenuItem NewMenuItem = new JMenuItem("New");
    		NewMenuItem.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				
    				try {
    					
    					// create a new canvas by setting the canvas to blank
						remoteWhiteboard.clearCanvas();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
    				
    			}
    		});
    		FileMenu.add(NewMenuItem);
    		
    		/*** Open an existing image file  and set as canvas ***/
    		JMenuItem OpenMenuItem = new JMenuItem("Open");
    		OpenMenuItem.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				
    				// create the filechooser in order to show the file selection process
    				JFileChooser fileChooser = new JFileChooser();
    				int result = fileChooser.showOpenDialog(frame);
    				if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        
                        // create a fileInputStream for reading in the image for opening
                        try (FileInputStream fis = new FileInputStream(selectedFile)) {
                        } catch (FileNotFoundException e1) {
                        	JOptionPane.showConfirmDialog(null, "File not Found", "Error", JOptionPane.OK_CANCEL_OPTION);
                            return;
                        } catch (IOException e1) {
                        	JOptionPane.showConfirmDialog(null, "Failed to Read File", "Error", JOptionPane.OK_CANCEL_OPTION);
                            return;
                        }
                        
                        // read the new bufferedImabe and load it to canvas
                        BufferedImage img = null;
                        try {
                            img = ImageIO.read(selectedFile);
                            // convert the bufferedImage to SerializableBufferedImage
                            SerializableBufferedImage serializableImage = new SerializableBufferedImage(img);
                            
                            // set the remote canvas to the opened image
                            remoteWhiteboard.setCanvas(serializableImage);
                        } catch (IOException e1) {
                        	JOptionPane.showConfirmDialog(null, "Failed to Read Image", "Error", JOptionPane.OK_CANCEL_OPTION);
                        }
                        
                    }
    				
    			}
    		});
    		FileMenu.add(OpenMenuItem);
    		
    		/*** save a image using default filename. The image is saved to my desktop ***/
    		JMenuItem SaveMenuItem = new JMenuItem("Save");
    		SaveMenuItem.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				
    				// generate the default Filename
    				String defaultFileName = generateAutoFileName();
    				try {
    					// save the image to using the fileName
						saveImage(remoteWhiteboard.getCanvas().getImage(), defaultFileName);
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
    				
    			}
    		});
    		FileMenu.add(SaveMenuItem);
    		
    		/** Save the image using the user customized file name and file path. **/
    		JMenuItem SaveAsMenuItem = new JMenuItem("Save As");
    		SaveAsMenuItem.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				
    				// Create a file chooser dialog for the user to choose the file location
    		        JFileChooser fileChooser = new JFileChooser();
    		        int result = fileChooser.showSaveDialog(frame);
    		        
    		        if (result == JFileChooser.APPROVE_OPTION) {
    		            // If the user selects a file location and clicks "Save"
    		            File selectedFile = fileChooser.getSelectedFile();
    		            
    		            //retrieve the file path retrieved by the users,note here we get the absolute path
    		            String selectedFilePath = selectedFile.getAbsolutePath();
    		            
    		            // we should enforce the suffix of the file to be .png, or otherwise, the ImageIO couldn't identify that
    		            if (!selectedFilePath.endsWith(".png")) {
                            selectedFilePath += ".png";
                        }
    		            
    		            // save the whiteboard image to the selected file path given by the user
    		            try {
    		                
    		            	saveImage(remoteWhiteboard.getCanvas().getImage(), selectedFilePath);
    		                
    		                // Display a success message
    		                JOptionPane.showMessageDialog(frame, "File saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    		            } catch (IOException ex) {
    		                // Handle any IO exception
    		                ex.printStackTrace();
    		                JOptionPane.showMessageDialog(frame, "Error occurred while saving the file.", "Error", JOptionPane.ERROR_MESSAGE);
    		            }
    		        }
    		        
    			}
    		});
    		FileMenu.add(SaveAsMenuItem);
    		
    		/** Close the whiteboard window and broadcast to the users **/
    		JMenuItem CloseMenuItem = new JMenuItem("Close");
    		CloseMenuItem.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				// close the window
    				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    				// broadcast to all users
    				createWhiteboard.quit();
    			}
    		});
    		FileMenu.add(CloseMenuItem);
    		
            
            
		} else {
			
            String appTitle = "Distributed Shared Whiteboard (User)" + userName;
            frame.setTitle(appTitle);
            
            // The User's quit button
            JButton QuitBtn = new JButton("Quit");
    		QuitBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    			
					// call the user's quit method
					joinWhiteboard.quit();
    				
    			}
    		});
    		GridBagConstraints gbc_QuitBtn = new GridBagConstraints();
    		gbc_QuitBtn.gridx = 9;
    		gbc_QuitBtn.gridy = 11;
    		frame.getContentPane().add(QuitBtn, gbc_QuitBtn);
            
		}
		
		// defining the drawing panel, this is an instance of the WhitePanel class
		WhiteboardPanel drawingArea = new WhiteboardPanel(remoteWhiteboard);
		//JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridwidth = 4;
		gbc_panel.gridheight = 7;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 6;
		gbc_panel.gridy = 4;
		frame.getContentPane().add(drawingArea, gbc_panel);
		//frame.getContentPane().add(panel, gbc_panel);
		
		
		// Defining the Rectangle Button
		JButton RectangleBtn = new JButton("Rectangle");
		RectangleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.setDrawMode("Rectangle");
			}
		});
		GridBagConstraints gbc_RectangleBtn = new GridBagConstraints();
		gbc_RectangleBtn.insets = new Insets(0, 0, 5, 5);
		gbc_RectangleBtn.gridx = 6;
		gbc_RectangleBtn.gridy = 0;
		frame.getContentPane().add(RectangleBtn, gbc_RectangleBtn);
		
		// Defining the Circle Button
		JButton CircleBtn = new JButton("Circle");
		CircleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.setDrawMode("Circle");
			}
		});
		GridBagConstraints gbc_CircleBtn = new GridBagConstraints();
		gbc_CircleBtn.insets = new Insets(0, 0, 5, 5);
		gbc_CircleBtn.gridx = 7;
		gbc_CircleBtn.gridy = 0;
		frame.getContentPane().add(CircleBtn, gbc_CircleBtn);
		
		
		// Defining the Voal Button
		JButton OvalBtn = new JButton("Oval");
		OvalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.setDrawMode("Oval");
			}
		});
		GridBagConstraints gbc_OvalBtn = new GridBagConstraints();
		gbc_OvalBtn.insets = new Insets(0, 0, 5, 5);
		gbc_OvalBtn.gridx = 8;
		gbc_OvalBtn.gridy = 0;
		frame.getContentPane().add(OvalBtn, gbc_OvalBtn);
		
		// Defining the Line Button
		JButton LineBtn = new JButton("Line");
		LineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.setDrawMode("Line");
			}
		});
		GridBagConstraints gbc_LineBtn = new GridBagConstraints();
		gbc_LineBtn.insets = new Insets(0, 0, 5, 0);
		gbc_LineBtn.gridx = 9;
		gbc_LineBtn.gridy = 0;
		frame.getContentPane().add(LineBtn, gbc_LineBtn);
		
		
		// Defining the Free Draw Button
		JButton FreeDrawBtn = new JButton("Free Draw");
		FreeDrawBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.setDrawMode("FreeDraw");
			}
		});
		GridBagConstraints gbc_FreeDrawBtn = new GridBagConstraints();
		gbc_FreeDrawBtn.insets = new Insets(0, 0, 5, 5);
		gbc_FreeDrawBtn.gridx = 6;
		gbc_FreeDrawBtn.gridy = 1;
		frame.getContentPane().add(FreeDrawBtn, gbc_FreeDrawBtn);
		
		// Defining the colour ComboBox (with 16 colours for choosing)
		JComboBox<String> ColourComboBox = new JComboBox<>(new String[]{
                "Black", "Red", "Green", "Blue", "Yellow", "Orange", "Pink", "Cyan",
                "Magenta", "Gray", "Dark Gray", "Light Gray", "White", "Brown", "Purple", "Turquoise"
        });
		ColourComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedColor = (String) ColourComboBox.getSelectedItem();
				
				switch (selectedColor) {
	                case "Black": drawingArea.setColour(Color.BLACK); break;
	                case "Red": drawingArea.setColour(Color.RED); break;
	                case "Green": drawingArea.setColour(Color.GREEN); break;
	                case "Blue": drawingArea.setColour(Color.BLUE); break;
	                case "Yellow": drawingArea.setColour(Color.YELLOW); break;
	                case "Orange": drawingArea.setColour(Color.ORANGE); break;
	                case "Pink": drawingArea.setColour(Color.PINK); break;
                    case "Cyan": drawingArea.setColour(Color.CYAN); break;
                    case "Magenta": drawingArea.setColour(Color.MAGENTA); break;
                    case "Gray": drawingArea.setColour(Color.GRAY); break;
                    case "Dark Gray": drawingArea.setColour(Color.DARK_GRAY); break;
                    case "Light Gray": drawingArea.setColour(Color.LIGHT_GRAY); break;
                    case "White": drawingArea.setColour(Color.WHITE); break;
                    case "Brown": drawingArea.setColour(new Color(165, 42, 42)); break;
                    case "Purple": drawingArea.setColour(new Color(128, 0, 128)); break; 
                    case "Turquoise": drawingArea.setColour(new Color(64, 224, 208)); break;
                    default:
                        break;
				}
			}
		});
		GridBagConstraints gbc_ColourComboBox = new GridBagConstraints();
		gbc_ColourComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_ColourComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_ColourComboBox.gridx = 7;
		gbc_ColourComboBox.gridy = 1;
		frame.getContentPane().add(ColourComboBox, gbc_ColourComboBox);
		
		// Defining the Text Button
		JButton TextBtn = new JButton("Text");
		TextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.setDrawMode("Text");
			}
		});
		GridBagConstraints gbc_TextBtn = new GridBagConstraints();
		gbc_TextBtn.insets = new Insets(0, 0, 5, 5);
		gbc_TextBtn.gridx = 8;
		gbc_TextBtn.gridy = 1;
		frame.getContentPane().add(TextBtn, gbc_TextBtn);
		
		// Defining the eraser ComboBox (with three sizes of eraser for choosing)
		JComboBox<String> EraserComboBox = new JComboBox<>(new String[]{
            "Eraser Small", "Eraser Medium", "Eraser Large"
		});
		EraserComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedSize = (String) EraserComboBox.getSelectedItem();
				
				switch(selectedSize) {
					case "Eraser Small":
						drawingArea.setEraserSize(10);
						drawingArea.setDrawMode("Eraser");
						drawingArea.setColour(Color.WHITE);
						break;
					case "Eraser Medium":
						drawingArea.setEraserSize(20);
						drawingArea.setDrawMode("Eraser");
						drawingArea.setColour(Color.WHITE);
						break;
					case "Eraser Large":
						drawingArea.setEraserSize(30);
						drawingArea.setDrawMode("Eraser");
						drawingArea.setColour(Color.WHITE);
						break;
					default:
						break;
				
				}
			}
		});
		GridBagConstraints gbc_EraserComboBox = new GridBagConstraints();
		gbc_EraserComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_EraserComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_EraserComboBox.gridx = 9;
		gbc_EraserComboBox.gridy = 1;
		frame.getContentPane().add(EraserComboBox, gbc_EraserComboBox);
		
		JLabel ChatBoxLabel = new JLabel("Chatbox");
		GridBagConstraints gbc_ChatBoxLabel = new GridBagConstraints();
		gbc_ChatBoxLabel.gridwidth = 4;
		gbc_ChatBoxLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ChatBoxLabel.gridx = 1;
		gbc_ChatBoxLabel.gridy = 5;
		frame.getContentPane().add(ChatBoxLabel, gbc_ChatBoxLabel);
		
		JTextArea ChatBoxTextArea = new JTextArea();
		GridBagConstraints gbc_ChatBoxTextArea = new GridBagConstraints();
		gbc_ChatBoxTextArea.gridheight = 3;
		gbc_ChatBoxTextArea.gridwidth = 5;
		gbc_ChatBoxTextArea.insets = new Insets(0, 0, 5, 5);
		gbc_ChatBoxTextArea.fill = GridBagConstraints.BOTH;
		gbc_ChatBoxTextArea.gridx = 1;
		gbc_ChatBoxTextArea.gridy = 6;
		frame.getContentPane().add(ChatBoxTextArea, gbc_ChatBoxTextArea);
		
		JLabel SendTextLabel = new JLabel("Send Text");
		GridBagConstraints gbc_SendTextLabel = new GridBagConstraints();
		gbc_SendTextLabel.gridwidth = 2;
		gbc_SendTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_SendTextLabel.gridx = 1;
		gbc_SendTextLabel.gridy = 9;
		frame.getContentPane().add(SendTextLabel, gbc_SendTextLabel);
		
		JTextArea SendTextArea = new JTextArea();
		GridBagConstraints gbc_SendTextArea = new GridBagConstraints();
		gbc_SendTextArea.gridwidth = 5;
		gbc_SendTextArea.insets = new Insets(0, 0, 5, 5);
		gbc_SendTextArea.fill = GridBagConstraints.BOTH;
		gbc_SendTextArea.gridx = 1;
		gbc_SendTextArea.gridy = 10;
		frame.getContentPane().add(SendTextArea, gbc_SendTextArea);
		
		JButton SendTextBtn = new JButton("Send");
		SendTextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// add the text in the sendtextArea to the remote chatList, with userName as the key
				String textToSend = SendTextArea.getText();
				try {
					remoteChatBox.addText(userName, textToSend);
					
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		GridBagConstraints gbc_SendTextBtn = new GridBagConstraints();
		gbc_SendTextBtn.gridwidth = 2;
		gbc_SendTextBtn.insets = new Insets(0, 0, 5, 5);
		gbc_SendTextBtn.gridx = 3;
		gbc_SendTextBtn.gridy = 9;
		frame.getContentPane().add(SendTextBtn, gbc_SendTextBtn);
		
		JLabel UserListLabel = new JLabel("Whiteboard Users");
		GridBagConstraints gbc_UserListLabel = new GridBagConstraints();
		gbc_UserListLabel.gridwidth = 5;
		gbc_UserListLabel.insets = new Insets(0, 0, 5, 5);
		gbc_UserListLabel.gridx = 1;
		gbc_UserListLabel.gridy = 3;
		frame.getContentPane().add(UserListLabel, gbc_UserListLabel);
		
		JTextArea UserListTextArea = new JTextArea();
		GridBagConstraints gbc_UserListTextArea = new GridBagConstraints();
		gbc_UserListTextArea.gridwidth = 5;
		gbc_UserListTextArea.insets = new Insets(0, 0, 5, 5);
		gbc_UserListTextArea.fill = GridBagConstraints.BOTH;
		gbc_UserListTextArea.gridx = 1;
		gbc_UserListTextArea.gridy = 4;
		frame.getContentPane().add(UserListTextArea, gbc_UserListTextArea);
		
		/******* update the userlist every 2 seconds *******/
		updateRemoteUserList(UserListTextArea);

		/******* update the chatVox every 2 seconds *******/
		updateRemoteChatBox(ChatBoxTextArea);
		
		
	}
	
	// close the app window
	public void closeWindow() {
		 frame.dispose(); // Close the frame
	}
	
	// set the createWhiteboard variable
	public void setCreateWhiteboard(CreateWhiteBoard createWB) {
		this.createWhiteboard = createWB;
	}
	
	// set the joinWhiteboard variable
	public void setJoinWhiteboard(JoinWhiteBoard joinWB) {
		this.joinWhiteboard = joinWB;
		
	}
	
	
	// Update the userList, every 2 seconds
	public void updateRemoteUserList(JTextArea userListTextArea) {

	    updateUserListThread = new Thread(() -> {
	        while (true) {
	            StringBuilder newText = new StringBuilder();
	            try {
	                if (remoteUserList.getManager() != null) {
	                    newText.append("(*) ").append(remoteUserList.getManager()).append("\n");
	                    for (String s : remoteUserList.getUsers()) {
	                        newText.append("(-) ").append(s).append("\n");
	                    }
	                }

	                SwingUtilities.invokeLater(() -> {
	                    // Update the text area on the EDT
	                    userListTextArea.setText(newText.toString());
	                });
	            } catch (RemoteException e) {
	            	JOptionPane.showConfirmDialog(null, "RMI Exception Occurs", "Error", JOptionPane.OK_CANCEL_OPTION);
	                break;
	            }

	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException e) {
	                System.out.println("Thread sleep error");
	                break;
	            }
	        }
	    });

	    updateUserListThread.start();
	}
	
	// Update the chatBox, every 2 seconds
	public void updateRemoteChatBox(JTextArea chatBoxTextArea) {
		
		updateChatBoxThread = new Thread(() -> {
			while (true) {
				StringBuilder newText = new StringBuilder();
				try {
					
					for (String s : remoteChatBox.getTexts()) {
						newText.append(s).append("\n");
					}
	                

	                SwingUtilities.invokeLater(() -> {
	                    // Update the text area on the EDT
	                    chatBoxTextArea.setText(newText.toString());
	                });
	            } catch (RemoteException e) {
	            	JOptionPane.showConfirmDialog(null, "RMI Exception Occurs", "Error", JOptionPane.OK_CANCEL_OPTION);
	                break;
	            }

	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException e) {
	                System.out.println("Thread sleep error");
	                break;
	            }
			}
			
			
		});
		
		updateChatBoxThread.start();
		
	}
	
	// save the whiteboard buffered image to the filepath
	public static void saveImage(BufferedImage bufferedImage, String filePath) {
        File outfile = new File(filePath);
        try {
            ImageIO.write(bufferedImage, "png", outfile);
        } catch (IOException e) {
        	JOptionPane.showConfirmDialog(null, "Failed to save to: "+ filePath, "Error", JOptionPane.OK_CANCEL_OPTION);
         
        }
    }
	
	// generate the default filename for automatic saving of the image. Save to my desktop.
	public static String generateAutoFileName() {
		
		// Get the current date/time
	    Calendar calendar = Calendar.getInstance();
	    
	    // Format the date/time as a string for the filename
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    String formattedDateTime = dateFormat.format(calendar.getTime());
	    
	    // Generate the filename using the formatted date/time
	    String fileName = "/Users/kitty/Desktop/image_" + formattedDateTime + ".png"; // Add file extension if needed
	    
	    return fileName;
    }

}
