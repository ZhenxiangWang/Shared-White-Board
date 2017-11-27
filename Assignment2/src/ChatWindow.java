
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.json.JSONObject;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Font;
import javax.swing.JTextPane;

public class ChatWindow {

	private JFrame frame;
    private Socket chatSocket;
    private BufferedReader cis;
    private BufferedWriter cos;
    private String userID;
    private Document doc;
    private Document doc2;
    private SimpleAttributeSet attr1;
    private SimpleAttributeSet attr2;
    private SimpleAttributeSet attr3;
    private String userNames;
    public static String current;
    public static String userList = null;
    
	

	/**
	 * Create the application.
	 */
	public ChatWindow(int x, int y, Socket chatSocket, String userID) {
		
		initialize(x,y);
//		frame.setVisible(true);
		this.chatSocket = chatSocket;
		this.userID = userID;
		try {
			cis = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
			cos = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("server is not open");
		}
		new Thread()
		{
			public void run()
			{
				while(true)
				{
					String strInputStream = null;
					try 
					{
						    strInputStream = cis.readLine();
						   
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
					if(strInputStream == null)
						continue;
					JSONObject json = new JSONObject(strInputStream);
					String opt =(String)json.get("opt");
					String messageType = (String)json.get("messageType");
//					String userID = (String)json.get("userID");
					String argument = (String) json.get("argument");
					
					if(messageType.equals("0"))//Request
					{
						if(opt.equals("02"))//Chat Message
						{
							JSONObject jsonr = new JSONObject(argument);
							String text = jsonr.getString("message");
							String userName = jsonr.getString("userName");
							Date date = new Date();
							SimpleDateFormat ft = new SimpleDateFormat("dd/MM HH:mm");
							try {
								doc.insertString(doc.getLength(), userName + " (" + ft.format(date) + ")" + ":" + "\n", attr1);
								doc.insertString(doc.getLength(), text + "\n" + "\n", attr2);
							} catch (Exception e) {
								// TODO Auto-generated catch block
//								e.printStackTrace();
							}
							
						}
					}
				}
			}
		}.start();
		new Thread()
		{
			public void run()
			{
				while(true)
	    	    	{
	    	    		String state = userList;
	    	    		if((state != null)&&(!state.equals(current)))
	    	    		{
	    	    			String[] userlists = state.split("\\|");
	    	    			
	    	    			
	    	    			try {
								doc2.remove(0, doc2.getLength());
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
//								e1.printStackTrace();
							}
	    	    			for(int i = 0; i < userlists.length; i++)
	    	    			{
	    	    				try {
//	    	    					textArea_userList.setText();
//	    	    					textArea_input.setText(null);
	    	    					
	    	    					doc2.insertString(doc2.getLength(), i+"-"+userlists[i].split("\\+")[1] + "\n", attr3);
	    	    					
	    	    					
								} catch (BadLocationException e) {
									// TODO Auto-generated catch block
//									e.printStackTrace();
								}
	    	    			}
	    	    			current = state;
	    	    		}
	    	    		try 
	    	    		{
	    					Thread.currentThread();
	    					Thread.sleep(100);
	    				} 
	    	    		catch (InterruptedException e) 
	    	    		{
	    	    			
	    				}
	    	    	}
			}
		}.start();
		windowSwitch();
	}
	
	public void windowSwitch() {
		if(frame.isVisible()){
			frame.setVisible(false);
		}else {
			frame.setVisible(true);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(int x, int y) {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(x, y, 430, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 430, 800);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		

		JScrollPane scrollPane_userList = new JScrollPane();
		scrollPane_userList.setBounds(5, 31, 130, 585);
		panel.add(scrollPane_userList);
		
		JTextPane textPane_userList = new JTextPane();
		scrollPane_userList.setViewportView(textPane_userList);
		textPane_userList.setEditable(false);
		///////////////
		

		///////////
		
		JScrollPane scrollPane_output = new JScrollPane();
		scrollPane_output.setBounds(140, 31, 280, 585);
		panel.add(scrollPane_output);
		
		JTextPane textPane_output = new JTextPane();
		scrollPane_output.setViewportView(textPane_output);

		textPane_output.setEditable(false);
		JScrollPane scrollPane_input = new JScrollPane();
		scrollPane_input.setBounds(5, 615, 420, 129);
		panel.add(scrollPane_input);
		
	

		JTextArea textArea_input = new JTextArea();
		textArea_input.setFont(new Font("ź", Font.PLAIN, 15));
		textArea_input.setLineWrap(true);

		textArea_input.setBackground(new Color(248, 248, 255));
		textArea_input.setWrapStyleWord(true);
		scrollPane_input.setViewportView(textArea_input);

		JButton Button_send = new JButton("Send");
		
		doc2=textPane_userList.getStyledDocument();
		attr3=new SimpleAttributeSet();
		StyleConstants.setForeground(attr3, Color.BLUE);
		StyleConstants.setFontSize(attr3, 15);
		
		
		
		doc =textPane_output.getStyledDocument();
		attr1=new SimpleAttributeSet();
		StyleConstants.setForeground(attr1, Color.BLUE);
		StyleConstants.setFontSize(attr1, 16);
		
		attr2=new SimpleAttributeSet();
		StyleConstants.setForeground(attr2, Color.black);
		StyleConstants.setFontSize(attr2, 15);
		
		
		
		Button_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str = textArea_input.getText();
				if (!(str.equals(""))) {
					Map<String, String> message = new HashMap<String, String>();
					message.put("messageType", "0");
					try {
						message.put("address", InetAddress.getLocalHost().toString());
					} catch (UnknownHostException e2) {
						// TODO Auto-generated catch block
//						e2.printStackTrace();
					}
					Date nowTime = new Date(System.currentTimeMillis());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(nowTime);
					message.put("timestamp", time);
					message.put("userID", userID);
					message.put("opt", "02");
					Map<String, String> argument = new HashMap<String, String>();
					argument.put("message", str);
					JSONObject jsona = new JSONObject(argument);
					message.put("argument", jsona.toString());
					JSONObject jsonr = new JSONObject(message);
					try {
						cos.write(jsonr.toString());
						cos.newLine();
						cos.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
//						e1.printStackTrace();
					}
					

					



					textArea_input.setText(null);
					textArea_input.setCaretPosition(0);
				}
			}
		});

		textArea_input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && (e.isControlDown())) {
					Button_send.doClick();
				}
			}
		});
		Button_send.setBounds(246, 743, 70, 36);
		panel.add(Button_send);


		JPanel lable_roomName = new JPanel();
		lable_roomName.setBackground(new Color(255, 255, 255));
		lable_roomName.setBounds(0, 0, 430, 31);
		panel.add(lable_roomName);
		lable_roomName.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Chat Room");
		lblNewLabel_1.setBackground(new Color(220, 220, 220));
		lblNewLabel_1.setBounds(0, 0, 420, 31);
		lable_roomName.add(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
	}
}
