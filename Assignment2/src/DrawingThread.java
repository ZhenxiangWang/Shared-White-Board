import java.awt.Color;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
public class DrawingThread extends Thread {
	DrawFrame drawFrame;
	Graphics graphics;
	Socket boardSocket;
	Socket chatSocket;
	private static ArrayList<String> drawingHistory;
	private static ArrayList<String> useraddHistory ;
	private String userID;
	private String userName;
	public DrawingThread() {

	}

	public DrawingThread(DrawFrame drawFrame, Socket boardSocket, Socket chatSocket, ArrayList<String> drawingHistory, String userID, String userName) {
		this.drawFrame = drawFrame;
		this.graphics = drawFrame.getDrawPanelGraphics();
		this.boardSocket = boardSocket;
		this.chatSocket = chatSocket;
		this.drawingHistory = drawingHistory;
		this.useraddHistory = drawingHistory;
		this.userID = userID;
		this.userName = userName;
	}

	@Override
	public void run() {
		// DataInputStream inputStream;
		try {
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(boardSocket.getInputStream()));
			BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(boardSocket.getOutputStream()));
			while (true) {
				String strInputstream = inputStream.readLine();
//				System.out.println(strInputstream);
				if (strInputstream == null)
					continue;
				
				JSONObject json = new JSONObject(strInputstream);
				String opt = (String) json.get("opt");
				String argument = (String) json.get("argument");
				JSONObject argumentJson = new JSONObject(argument);
				
				
				if (opt.equals("01")) {
					drawingHistory.add(argument);
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
					graphics.setColor(new Color(r, g, b));
					switch (shape) {
					case "01":
						graphics.drawLine(x1, y1, x2, y2);
						break;
					case "02":
						graphics.drawRect(x1, y1, width, height);
						break;
					case "03":
						graphics.drawOval(x1, y1, width, height);
						break;
					case "04":
						graphics.drawRoundRect(x1, y1, width, height, 15, 15);
						break;
					case "05":
						graphics.fillRect(x1, y1, width, height);
						break;
					case "06":
						graphics.fillOval(x1, y1, width, height);
						break;
					case "07":
						graphics.fillRoundRect(x1, y1, width, height, 15, 15);
						break;
					case "08":
						graphics.drawString(text, x1, y1);
					case "09":
						drawingHistory.clear();
						graphics.fillRect(x1, y1, width, height);
					default:
						break;
					}
					//outputStream.println("Success!");
				}
				
				if (opt.equals("04")) {
//					useraddHistory.add(argument);					
//					String approvel = (String) argumentJson.get("isapproval");					
//					if (approvel.equals("true")) {
//						
//					}
					//outputStream.println("Success!");
					String manage = (String)argumentJson.get("manage");
					if(manage.equals("01"))
					{
						String requestUserId = (String)argumentJson.get("userID");
						String userName = (String)argumentJson.get("userName");
						boolean answer = this.drawFrame.joinApproval(userName);
						if(answer)
						{
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", this.userID);
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("manage", "04");
							argumentMap.put("userID", requestUserId);
							argumentMap.put("userName", userName);
							JSONObject argumentJsonar = new JSONObject(argumentMap);
							messageMap.put("argument", argumentJsonar.toString());
							JSONObject jsonr = new JSONObject(messageMap);
							try {
								outputStream.write(jsonr.toString());
								outputStream.newLine();
								outputStream.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
						}
						else
						{
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", this.userID);
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("manage", "05");
							argumentMap.put("userID", requestUserId);
							JSONObject jsonsrgument = new JSONObject(argumentMap);
							messageMap.put("argument", jsonsrgument.toString());
							JSONObject jsonr = new JSONObject(messageMap);
							try {
								outputStream.write(jsonr.toString());
								outputStream.newLine();
								outputStream.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
						}
						
					}
					else if(manage.equals("02"))
					{
						String target = (String)argumentJson.get("userName");
						if(target.equals(this.userName))
						{
							drawFrame.showdelete();
							boardSocket.close();
							chatSocket.close();
							System.exit(0);
						}
					}
					else if(manage.equals("03"))
					{
						String userList = (String)argumentJson.get("userList");
						ChatWindow.userList = userList;
					}
					else if(manage.equals("07"))
					{
						drawFrame.showexit();
						boardSocket.close();
						chatSocket.close();
						System.exit(0);
					}
					else if(manage.equals("05"))
					{
						drawFrame.showdeny();
						System.exit(0);
					}else if(manage.equals("08"))
					{
						drawingHistory.clear();
						drawFrame.getDrawPanelGraphics().setColor(Color.WHITE);
						drawFrame.getDrawPanelGraphics().fillRect(0, 0, drawFrame.getWidth(), drawFrame.getHeight());
					}
					else if(manage.equals("09"))// Manager exit
					{
						System.exit(0);
					}
					else if(manage.equals("10"))
					{
						drawFrame.showUserNotExist();
					}
				}
				
				
				

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}

}

