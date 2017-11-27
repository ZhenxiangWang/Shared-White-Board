
import javax.net.ssl.ExtendedSSLSession;
import javax.sql.rowset.Joinable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.JTextComponent;

import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.acl.Group;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends JFrame {
	private Socket socket;
	private String host;
	private int port;
	private String userName;

	

	Login() {
		madeFrame();
		try {
			System.out.println("IP = " + InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void madeFrame() {
		
		ArrayList<User> group =new ArrayList<User>();//
		ConnectionGroup newGroup= new ConnectionGroup(group);//
		
		
		JLabel jLabel1, jLabel2;
		JPanel jp1, jp2, jp3;
		JButton jb1, jb2;
		JFrame frame1 = new JFrame();
		JTextField jTextField;

		jTextField = new JTextField(12);

		frame1.setVisible(true);
		jLabel1 = new JLabel("Username");

		jb1 = new JButton("Create");
		jb2 = new JButton("Join");
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();

		frame1.setLayout(new GridLayout(3, 2));

		jp1.add(jTextField);

		jp2.add(jb1);
		jp2.add(jb2);

		frame1.getContentPane().add(jLabel1);
		frame1.getContentPane().add(jp1);
		frame1.getContentPane().add(jp2);

		// set visible
		frame1.setSize(300, 150);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setVisible(true);
		frame1.setTitle("Login");

		jb1.addActionListener(new ActionListener() {
			JOptionPane jop = new JOptionPane();

			public void actionPerformed(ActionEvent e) {
				if (jTextField.getText().trim().equals("")) {
					frame1.setVisible(false);
					jop.showMessageDialog(null, "Empty Input");
					frame1.setVisible(true);
				}
				
			
				else {
                   
					String userName = jTextField.getText().trim();
					Client.userName = userName;
					
					boolean userNotExist=true;
					for(int i=0;i<newGroup.group.size();i++){
						if(userName.equals(group.get(i).getUserName())){
							jop.showMessageDialog(null, "This user name already exists. Please enter another one.");
							userNotExist=false;
						}
					}
					frame1.setVisible(false);
				    create();
				    }
			}
		});

		jb2.addActionListener(new ActionListener() {
			JOptionPane jop = new JOptionPane();

			public void actionPerformed(ActionEvent e) {
				if (jTextField.getText().trim().equals("")) {
					frame1.setVisible(false);
					jop.showMessageDialog(null, "Empty Input");
					frame1.setVisible(true);
				}
				
			
				else {
                   
					String userName = jTextField.getText().trim();
					Client.userName = userName;
					
					boolean userNotExist=true;
					for(int i=0;i<newGroup.group.size();i++){
						if(userName.equals(group.get(i).getUserName())){
							jop.showMessageDialog(null, "This user name already exists. Please enter another one.");
							userNotExist=false;
						}
					}
					frame1.setVisible(false);
				    join();
				    }
			}
		});
	}

	public void create() {
		JLabel jLabel1, jLabel2;
		JPanel jp1, jp2, jp3;
		JButton jb1, jb2;
		JFrame frame1 = new JFrame();
		JTextField jTextField;

		jTextField = new JTextField(12);

		frame1.setVisible(true);
		jLabel1 = new JLabel("PortNumber");

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

		// frame.add(jp1);
		// this.add(jp2);

		// set visible
		frame1.setSize(300, 150);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setVisible(true);
		frame1.setTitle("Create");

		jb1.addActionListener(new ActionListener() {
			JOptionPane jop = new JOptionPane();

			public void actionPerformed(ActionEvent e) {
				if (jTextField.getText().trim().equals("")) {
					jop.showMessageDialog(null, "Empty Input");
				}
                Client.userType = "01";
				String portNumber = jTextField.getText().trim();
				Client.boardport = Integer.parseInt(portNumber);
				Server.boardPort = Integer.parseInt(portNumber);
//				System.out.println(portNumber);
				frame1.setVisible(false);
				
				Server.server();
				Client.client(true);

			}
		});

		jb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				madeFrame();
				frame1.setVisible(false);

			}
		});
	}

	public void join() {
		JLabel jLabel1, jLabel2;
		JPanel jp1, jp2, jp3;
		JButton jb1, jb2;
		JFrame frame1 = new JFrame();
		JTextField jTextField1, jTextField2;

		jTextField1 = new JTextField(12);
		jTextField2 = new JTextField(12);

		frame1.setVisible(true);
		jLabel1 = new JLabel("IP");
		jLabel2 = new JLabel("PortNumber");
		jb1 = new JButton("Confirm");
		jb2 = new JButton("Cancel");
		jp1 = new JPanel();
		jp2 = new JPanel();

		frame1.setLayout(new GridLayout(3, 2));

		jp1.add(jTextField1);
		jp2.add(jTextField2);

		jp1.add(jb1);
		jp2.add(jb2);

		frame1.getContentPane().add(jLabel1, BorderLayout.NORTH);

		frame1.getContentPane().add(jLabel2, BorderLayout.NORTH);
		frame1.getContentPane().add(jp1);
		frame1.getContentPane().add(jp2);
		frame1.getContentPane().add(jb1);
		frame1.getContentPane().add(jb2);

		frame1.setSize(300, 150);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setVisible(true);
		frame1.setTitle("Join");

		jb1.addActionListener(new ActionListener() {

			JOptionPane jop = new JOptionPane();

			public void actionPerformed(ActionEvent e) {
				
				if (jTextField1.getText().trim().equals("")||jTextField2.getText().trim().equals("")) {
					frame1.setVisible(false);
					jop.showMessageDialog(null, "Empty Input");
					frame1.setVisible(false);
				}
				else {
                   
                Client.userType = "02";
				String IP = jTextField1.getText().trim();
				Client.boardhost = IP;
				Client.chathost = IP;
//				System.out.println(IP);
				String PortNumber = jTextField2.getText().trim();
				Client.boardport = Integer.parseInt(PortNumber);
				}	
				frame1.setVisible(false);
				Client.client(false);
			}
		});

	
		jb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				System.out.println("back");
				frame1.setVisible(false);
				madeFrame();
			}
		});
	}

}
