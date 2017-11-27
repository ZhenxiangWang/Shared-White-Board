import java.awt.*;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.metal.MetalSliderUI;
import javax.swing.text.BadLocationException;

import org.json.JSONArray;
import org.json.JSONObject;

public class DrawFrame extends JFrame {

	public DrawFrame(boolean isManager) {
		this.ismanager = isManager;
	}

	private static final long serialVersionUID = 1L;
	private static String filename = null;
	private String graphName = "FreeHand";
	private Color color = Color.BLACK;
	private int eraserSize = 50;
	private JPanel shapePanel = new JPanel();
	private JPanel basicPanel = new JPanel();
	private Graphics drawPanelGraphics;
	private ChatWindow chatWindow;
	private Socket boardSocket;
	private Socket chatSocket;
	private String userID;
	private String userName = "";
	private boolean ismanager = true;
	private JPanel drawPanel = new JPanel();

//	public boolean joinApproval = false;

	private static ArrayList<String> drawingHistory = new ArrayList<String>();

	private JMenuBar menu;
	private JMenu fileMenu;
	private JMenu manageMenu;///////////////////
	private JMenuItem newMenu;
	private JMenuItem openMenu;
	private JMenuItem saveMenu;
	private JMenuItem saveAsMenu;
	private JMenuItem closeMenu;
	private JMenuItem delete;

	public void init(Socket boardSocket, Socket chatSocket, String userID) {
		this.setTitle("Shared White Board");
		this.setSize(1000, 800);
		// this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if (ismanager == true) {
					int value = JOptionPane.showConfirmDialog(null, "Save?", "Warning!", 0);
					if (value == 0) {
						if (filename != null)
							save();
						else
							saveas();

					}
				}

				Map<String, String> messageMap = new HashMap<String, String>();
				messageMap.put("messageType", "0");
				try {
					messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
				} catch (UnknownHostException e3) {
					// TODO Auto-generated catch block
//					e3.printStackTrace();
				}
				Date nowTime = new Date(System.currentTimeMillis());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = format.format(nowTime);
				messageMap.put("timestamp", time);
				messageMap.put("userID", "server");
				messageMap.put("opt", "04");
				Map<String, String> argumentMap = new HashMap<String, String>();
				argumentMap.put("manage", "06");
				argumentMap.put("userName", userName);
				argumentMap.put("userID", userID);
				JSONObject jsonargument = new JSONObject(argumentMap);
				messageMap.put("argument", jsonargument.toString());
				JSONObject jsonr = new JSONObject(messageMap);
				BufferedWriter dss = null;
				try {
					dss = new BufferedWriter(new OutputStreamWriter(boardSocket.getOutputStream()));
				} catch (IOException e2) {
					// TODO Auto-generated catch block
//					e2.printStackTrace();
				}
				try {
					dss.write(jsonr.toString());
					dss.newLine();
					dss.flush();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("client board thread closed");
//					e1.printStackTrace();
				}

				if (!ismanager) {
					try {
						boardSocket.close();
						chatSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
					System.exit(0);
				}

			}
		});
		
		this.boardSocket = boardSocket;
		this.chatSocket = chatSocket;
		this.userID = userID;
		this.setResizable(false);
		// this.setUndecorated(true);
		// this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

		drawPanel = new JPanel();
		drawPanel.setBackground(Color.WHITE);
		drawPanel.setLayout(null);
		JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(new BorderLayout());

		basicPanel.setLayout(new GridLayout(1, 0));
		// Initialize the color chooser button
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(0, 1));
		JTextField colorArea = new JTextField();
		colorArea.setEditable(false);
		colorArea.setBackground(color);
		colorPanel.add(colorArea);
		colorArea.addMouseListener(new paletteListener());
		basicPanel.add(colorPanel);
		menu = new JMenuBar();
		fileMenu = new JMenu("File");
		closeMenu = new JMenuItem("Close");
		fileMenu.add(closeMenu);
		menu.add(fileMenu);

		ActionListener closeMenu_ActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ismanager == true) {
					int value = JOptionPane.showConfirmDialog(null, "Save?", "Warning!", 0);
					if (value == 0) {
						if (filename != null)
							save();
						else
							saveas();

					}
				}

				Map<String, String> messageMap = new HashMap<String, String>();
				messageMap.put("messageType", "0");
				try {
					messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
				} catch (UnknownHostException e3) {
					// TODO Auto-generated catch block
//					e3.printStackTrace();
				}
				Date nowTime = new Date(System.currentTimeMillis());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = format.format(nowTime);
				messageMap.put("timestamp", time);
				messageMap.put("userID", "server");
				messageMap.put("opt", "04");
				Map<String, String> argumentMap = new HashMap<String, String>();
				argumentMap.put("manage", "06");
				argumentMap.put("userName", userName);
				argumentMap.put("userID", userID);
				JSONObject jsonargument = new JSONObject(argumentMap);
				messageMap.put("argument", jsonargument.toString());
				JSONObject jsonr = new JSONObject(messageMap);
				BufferedWriter dss = null;
				try {
					dss = new BufferedWriter(new OutputStreamWriter(boardSocket.getOutputStream()));
				} catch (IOException e2) {
					// TODO Auto-generated catch block
//					e2.printStackTrace();
				}
				try {
					dss.write(jsonr.toString());
					dss.newLine();
					dss.flush();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("client board thread closed");
//					e1.printStackTrace();
				}
				if (!ismanager) {
					try {
						boardSocket.close();
						chatSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
					
					System.exit(0);
				}
			}
		};

		closeMenu.addActionListener(closeMenu_ActionListener);

		if (ismanager == true) {
			newMenu = new JMenuItem("New");
			openMenu = new JMenuItem("Open");
			saveMenu = new JMenuItem("Save");
			saveAsMenu = new JMenuItem("SaveAs");

			fileMenu.add(newMenu);
			fileMenu.add(openMenu);
			fileMenu.add(saveMenu);
			fileMenu.add(saveAsMenu);

			manageMenu = new JMenu("Manage");
			delete = new JMenuItem("Delete");
			manageMenu.add(delete);
			menu.add(manageMenu);

			ActionListener newMenu_ActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int value = JOptionPane.showConfirmDialog(null, "Save?", "Warning!", 0);
					if (value == 0) {
						if (filename != null)
							save();
						else
							saveas();
					}
					clearDrawPanel();
				}
			};
			newMenu.addActionListener(newMenu_ActionListener);

			
			ActionListener openMenu_ActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser openFile = new JFileChooser("OPEN");
					openFile.setAcceptAllFileFilterUsed(false);

					openFile.addChoosableFileFilter(new FileNameExtensionFilter("datFile", "dat"));
					int val = openFile.showOpenDialog(null);

					if (val == JFileChooser.APPROVE_OPTION) {

						clearDrawPanel();
						File file = openFile.getSelectedFile();
						String br1 = FileOpen.read(file.getPath());
						String[] brSplit = br1.split("\n");
						for (int i = 0; i < brSplit.length; i++) {
							JSONObject argumentJson = new JSONObject(brSplit[i]);
							BufferedWriter dbos = null;
							try {
								dbos = new BufferedWriter(new OutputStreamWriter(boardSocket.getOutputStream()));
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("server is not open");
							}
							Map<String, String> message = new HashMap<String, String>();
							message.put("messageType", "0");
							try {
								message.put("address", InetAddress.getLocalHost().getHostAddress());
							} catch (UnknownHostException e2) {
								// TODO Auto-generated catch block
//								e2.printStackTrace();
							}
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							message.put("timestamp", time);
							message.put("userID", userID);
							message.put("opt", "01");
							message.put("argument", argumentJson.toString());
							JSONObject jsonr = new JSONObject(message);
							try {
								dbos.write(jsonr.toString());
								dbos.newLine();
								dbos.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
							}
							drawingHistory.add(argumentJson.toString());
							String shape = (String) argumentJson.get("shape");
							int x1 = new Integer((String) argumentJson.get("x1"));
							int y1 = new Integer((String) argumentJson.get("y1"));
							int x2 = 0;
							if (argumentJson.has("x2"))
								x2 = new Integer((String) argumentJson.get("x2"));
							int y2 = 0;
							if (argumentJson.has("y2"))
								y2 = new Integer((String) argumentJson.get("y2"));
							int width = 0;
							if (argumentJson.has("width"))
								width = new Integer((String) argumentJson.get("width"));
							int height = 0;
							if (argumentJson.has("height"))
								height = new Integer((String) argumentJson.get("height"));
							String text = "";
							if (argumentJson.has("text"))
								text = (String) argumentJson.get("text");
							int r = new Integer((String) argumentJson.get("colorR"));
							int g = new Integer((String) argumentJson.get("colorG"));
							int b = new Integer((String) argumentJson.get("colorB"));

							drawPanelGraphics.setColor(new Color(r, g, b));
							switch (shape) {
							case "01":
								drawPanelGraphics.drawLine(x1, y1, x2, y2);
								break;
							case "02":
								drawPanelGraphics.drawRect(x1, y1, width, height);
								break;
							case "03":
								drawPanelGraphics.drawOval(x1, y1, width, height);
								break;
							case "04":
								drawPanelGraphics.drawRoundRect(x1, y1, width, height, 15, 15);
								break;
							case "05":
								drawPanelGraphics.fillRect(x1, y1, width, height);
								break;
							case "06":
								drawPanelGraphics.fillOval(x1, y1, width, height);
								break;
							case "07":
								drawPanelGraphics.fillRoundRect(x1, y1, width, height, 15, 15);
								break;
							case "08":
								drawPanelGraphics.drawString(text, x1, y1);
							default:
								break;
							}
						}
					}

				}
			};

			openMenu.addActionListener(openMenu_ActionListener);

			ActionListener saveMenu_ActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			};
			saveMenu.addActionListener(saveMenu_ActionListener);

			ActionListener saveAsMenu_ActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					saveas();
				}
			};
			saveAsMenu.addActionListener(saveAsMenu_ActionListener);

			ActionListener delete_ActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					deleteUser();
				}
			};
			delete.addActionListener(delete_ActionListener);

			toolsPanel.add(menu, BorderLayout.NORTH);
		}
		// Initialize the buttons used to choose shapes
		String[] basicArray = { "FreeHand", "Line", "Polygon", "Text" };
		ShapeButtonListener sl = new ShapeButtonListener();
		for (int i = 0; i < basicArray.length; i++) {
			JButton btn = new JButton(basicArray[i]);
			btn.setToolTipText(basicArray[i]);
			btn.addActionListener(sl);
			if (i == 0)
				btn.setForeground(Color.BLACK);
			else
				btn.setForeground(Color.lightGray);
			basicPanel.add(btn);
		}
		JButton eraser = new JButton("Eraser");
		eraser.setToolTipText("Eraser");
		eraser.addActionListener(sl);
		eraser.setForeground(Color.lightGray);
		basicPanel.add(eraser);
		JSlider eraserSlider = new JSlider(0, 30, 100, 30);
		eraserSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				eraserSize = ((JSlider) e.getSource()).getValue();
			}
		});
		eraserSlider.setToolTipText("Eraser Size");
		String[] shapeArray = { "Rect", "Circle", "Oval", "RoundRect", "FillRect", "FillCircle", "FillOval",
				"FillRoundRect", };
		shapePanel.setLayout(new GridLayout(1, 0));
		for (int i = 0; i < shapeArray.length; i++) {
			JButton btn = new JButton(shapeArray[i]);
			btn.setToolTipText(shapeArray[i]);
			btn.addActionListener(sl);
			btn.setForeground(Color.lightGray);
			shapePanel.add(btn);
		}
		toolsPanel.add(basicPanel, BorderLayout.WEST);
		toolsPanel.add(shapePanel, BorderLayout.SOUTH);
		toolsPanel.add(eraserSlider, BorderLayout.CENTER);

		chatWindow = new ChatWindow(this.getX() + this.getWidth(), this.getY(), chatSocket, this.userID);
		JButton chatButton = new JButton("Chat Window");
		chatButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chatWindow.windowSwitch();
			}
		});
		toolsPanel.add(chatButton, BorderLayout.EAST);
		getContentPane().add(toolsPanel, BorderLayout.NORTH);
		getContentPane().add(drawPanel, BorderLayout.CENTER);

//		JPanel userListPanel = new JPanel();
//		userListPanel.setLayout(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		JLabel userListLabel = new JLabel("User List");
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridx = 0;
//		c.gridy = 0;
//		userListPanel.add(userListLabel, c);
//
//		JTable userList = new JTable();
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridx = 0;
//		c.gridy = 1;
//		userListPanel.add(userList, c);
//		getContentPane().add(userListPanel, BorderLayout.WEST);

		// this.setResizable(false);
		this.setVisible(true);
		drawPanelGraphics = drawPanel.getGraphics();

		// Add mouse event listener
		DrawFrameListener listener = new DrawFrameListener(this, drawPanel.getGraphics(), this.boardSocket, this.userID,
				this.drawingHistory);
		drawPanel.addMouseListener(listener);
		drawPanel.addMouseMotionListener(listener);

	}

	private class ShapeButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// Get graph name
			graphName = e.getActionCommand();
			// Change the font color of all shape buttons to gray
			Component[] components = basicPanel.getComponents();
			for (int i = 1; i < components.length; i++) {// Exclude the palette
				components[i].setForeground(Color.lightGray);
			}
			Component[] shapeBtns = shapePanel.getComponents();
			for (int i = 0; i < shapeBtns.length; i++) {
				shapeBtns[i].setForeground(Color.lightGray);
			}
			// Change the font color of focused button
			((JButton) e.getSource()).setForeground(Color.BLACK);

		}
	}

	private class paletteListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			JColorChooser chooser = new JColorChooser();
			Color newColor = chooser.showDialog((JComponent) e.getSource(), "palette", Color.lightGray);
			if (newColor != null)
				color = newColor;
			Component[] components = ((JComponent) e.getSource()).getParent().getComponents();
			components[0].setBackground(color);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public void saveas() {

		JFileChooser saveFile = new JFileChooser("saveas");
		saveFile.setAcceptAllFileFilterUsed(false);

		saveFile.addChoosableFileFilter(new FileNameExtensionFilter("datFile", "dat"));
		int val = saveFile.showSaveDialog(this);
		if (val == JFileChooser.APPROVE_OPTION) {
			File selectedFile = saveFile.getSelectedFile();
			String fileName = selectedFile.getName();

			if (fileName.lastIndexOf(".") == -1) {
				String selectedExtension = saveFile.getFileFilter().getDescription();
				String end = selectedExtension.substring(0, selectedExtension.lastIndexOf("File"));
				fileName = fileName + "." + end;
			}
			String filePath = selectedFile.getParent() + "/";
			filename = filePath + fileName;
			File newFile = new File(filePath + fileName);
			String allDrawing = "";
			for (String drawing : drawingHistory) {
				allDrawing += drawing + "\n";
			}
			try {
				if (newFile.exists()) {
					int result = JOptionPane.showConfirmDialog(null, "Do you want to overwrite it");
					if (result != JOptionPane.YES_OPTION) {
						return;
					}
				} else {
					newFile.createNewFile();
				}

				FileSave.save(newFile.getPath(), allDrawing);

			} catch (Exception e1) {
//				e1.printStackTrace();
			}
		}
	}

	public void save() {
		try {
			if (filename != null) {
//				System.out.println(filename);
				File newFile = new File(filename);
				String allDrawing = "";
				for (String drawing : drawingHistory) {
					allDrawing += drawing + "\n";
				}
				FileSave.save(newFile.getPath(), allDrawing);
			} else {
				saveas();
			}
		} catch (Exception e1) {
//			e1.printStackTrace();
		}
	}

	public void clearDrawPanel() {
		BufferedWriter dbos = null;
		try {
			dbos = new BufferedWriter(new OutputStreamWriter(boardSocket.getOutputStream()));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("server is not open");
		}
		drawingHistory.clear();
		drawPanelGraphics.setColor(Color.WHITE);
		Map<String, String> message = new HashMap<String, String>();
		message.put("messageType", "0");
		try {
			message.put("address", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
//			e2.printStackTrace();
		}
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(nowTime);
		message.put("timestamp", time);
		message.put("userID", userID);
		message.put("opt", "01");
		Map<String, String> argument = new HashMap<String, String>();
		drawPanelGraphics.fillRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
		argument.put("shape", "09");
		argument.put("x1", "0");
		argument.put("y1", "0");
		argument.put("width", String.valueOf(drawPanel.getWidth()));
		argument.put("height", String.valueOf(drawPanel.getHeight()+100));
		argument.put("colorR", String.valueOf(255));
		argument.put("colorG", String.valueOf(255));
		argument.put("colorB", String.valueOf(255));
		JSONObject jsona = new JSONObject(argument);
		message.put("argument", jsona.toString());
		JSONObject jsonr = new JSONObject(message);
		try {
			dbos.write(jsonr.toString());
			dbos.newLine();
			dbos.flush();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}
		drawPanelGraphics.setColor(color);
	}

	public void deleteUser() {
		JLabel jLabel1, jLabel2;
		JPanel jp1, jp2, jp3;
		JButton jb1, jb2;
		JFrame frame1 = new JFrame();
		JTextField jTextField;

		jTextField = new JTextField(12);

		frame1.setVisible(true);
		jLabel1 = new JLabel("User ID");

		jb1 = new JButton("Confirm");
		jb2 = new JButton("Cancel");
		jp1 = new JPanel();
		jp2 = new JPanel();

		frame1.setLayout(new GridLayout(3, 2));

		jp1.add(jTextField);

		jp2.add(jb1);
		jp2.add(jb2);

		frame1.getContentPane().add(jLabel1);
		frame1.getContentPane().add(jp1);
		frame1.getContentPane().add(jp2);

		frame1.setSize(300, 150);
		frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame1.setVisible(true);
		frame1.setTitle("Remove User");

		jb1.addActionListener(new ActionListener() {
			JOptionPane jop = new JOptionPane();

			public void actionPerformed(ActionEvent e) {
				if (jTextField.getText().trim().equals("")) {
					jop.showMessageDialog(null, "Empty Input");
				}

				String userIdDelete = jTextField.getText().trim();
				if(userIdDelete.equals("0")){
					showUserNotExist();
					return;
				}
				String userId = new String();
				
				String[] userlists = ChatWindow.userList.split("\\|");
				for(int i = 0; i < userlists.length; i++)
    			{
    				if (userIdDelete.equals(String.valueOf(i))){
						userId = userlists[i].split("\\+")[0];
					}
    			}
				
				if(ChatWindow.userList.contains(userId))
				{
					Map<String, String> messageMap = new HashMap<String, String>();
					messageMap.put("messageType", "0");
					try {
						messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
					} catch (UnknownHostException e3) {
						// TODO Auto-generated catch block
//						e3.printStackTrace();
					}
					Date nowTime = new Date(System.currentTimeMillis());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(nowTime);
					messageMap.put("timestamp", time);
					messageMap.put("userID", userID);
					messageMap.put("opt", "04");
					Map<String, String> argumentMap = new HashMap<String, String>();
					argumentMap.put("manage", "02");
//					argumentMap.put("userName", userNamedelete);
					argumentMap.put("userID", userId);
					JSONObject jsonargument = new JSONObject(argumentMap);
					messageMap.put("argument", jsonargument.toString());
					JSONObject jsonr = new JSONObject(messageMap);
					BufferedWriter outputStream = null;
					try {
						outputStream = new BufferedWriter(new OutputStreamWriter(boardSocket.getOutputStream()));
					} catch (IOException e2) {
						// TODO Auto-generated catch block
//						e2.printStackTrace();
					}
					try {
						outputStream.write(jsonr.toString());
						outputStream.newLine();
						outputStream.flush();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.out.println("client board thread closed");
//						e1.printStackTrace();
					}
				}
				else
				{
					JOptionPane jop = new JOptionPane();
					jop.showMessageDialog(null, "Ineligible User Id.");
				}
				frame1.setVisible(false);

			}
		});

		jb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame1.setVisible(false);

			}
		});
	}

	public boolean joinApproval(String requestName) {
		int value = JOptionPane.showConfirmDialog(null, "New user '"+ requestName +"' applies to join the white board.", "Alert", 0);
		if (value == 0) {
			return true;
		}else {
			return false;
		}
	}

	public void showdelete() {
		JOptionPane.showMessageDialog(null, "You have been removed from the white board", "Alert", 0);
	}
	
	public void showUserNotExist() {
		JOptionPane.showMessageDialog(null, "Ineligible User Id", "Alert", 0);
	}


	public void showexit() {
		JOptionPane.showMessageDialog(null, "The white board is closed!", "Alert",0);
	}

	public void showdeny() {
		JOptionPane.showMessageDialog(null, "You join application is declined.", "Alert",0);
	}
	public void showServerNotAccessible() {
		JOptionPane.showMessageDialog(null, "The server is not accessible!", "Alert",0);
	}
	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Graphics getDrawPanelGraphics() {
		return drawPanelGraphics;
	}

	public void setDrawPanelGraphics(Graphics drawPanelGraphics) {
		this.drawPanelGraphics = drawPanelGraphics;
	}

	public int getEraserSize() {
		return eraserSize;
	}

	public void setEraserSize(int eraserSize) {
		this.eraserSize = eraserSize;
	}

	public static ArrayList<String> getDrawingHistory() {
		return drawingHistory;
	}

	public static void setDrawingHistory(ArrayList<String> drawingHistory) {
		DrawFrame.drawingHistory = drawingHistory;
	}

	public boolean isIsmanager() {
		return ismanager;
	}

	public void setIsmanager(boolean ismanager) {
		this.ismanager = ismanager;
	}

}
