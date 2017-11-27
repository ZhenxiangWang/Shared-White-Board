import java.io.*;
import java.net.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.json.*;
public class Client {
	static Socket chatSocket = null;
	static InputStream cis = null;
	static OutputStream cos = null;
	static Socket boardSocket = null;
	static OutputStream bos = null;
	static String userName;
	static String userID = UUID.randomUUID().toString(); 
	static String userType = null;
	static int boardport;
	static String chathost;
	static String boardhost;

	
	
	public static void client(boolean isManager)
	{
		try 
		{
			chatSocket = new Socket(chathost, boardport+1);
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("Server host parsing Exception");
		} 
		catch (Exception e) 
		{
			
			JOptionPane.showMessageDialog(null, "The server is not available!", "Alert",0);
			System.out.println("Server is not available");
			System.exit(0);
		}
		new Thread()
		{
	        public void run()
	        {
	        	chatConnection();
	        }
			
		}.start();
		try 
		{
			boardSocket = new Socket(boardhost, boardport);
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("Server host parsing Exception");
		} 
		catch (Exception e) 
		{
			System.out.println("Server is not available");
		}
		
		new Thread()
		{
			public void run()
			{
				boardConnection(isManager);
		    }
		}.start();
	}
	
	public static void chatConnection()
	{
		try 
		{
			cos = chatSocket.getOutputStream();
			Map<String, String> message = new HashMap<String, String>();
			message.put("messageType", "0");
			message.put("address", InetAddress.getLocalHost().getHostAddress());
			Date nowTime = new Date(System.currentTimeMillis());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(nowTime);
			
			message.put("timestamp", time);
			message.put("userID", userID);
			message.put("opt", "03");
			
			Map<String, String> argument = new HashMap<String, String>();
			argument.put("userName", userName);
			argument.put("userType", userType);
			message.put("argument", new JSONObject(argument).toString());
			JSONObject jsonr = new JSONObject(message);
			BufferedWriter dcos = new BufferedWriter(new OutputStreamWriter(cos));
			dcos.write(jsonr.toString());
			dcos.newLine();
			dcos.flush();
		} 
		catch (Exception e) 
		{
			System.out.println("error");
		}
	    		
	}
	
	public static void boardConnection(boolean isManager)
	{
		DrawFrame drawFrame = new DrawFrame(isManager);
		drawFrame.init(boardSocket, chatSocket, userID);
		DrawingThread thread = new DrawingThread(drawFrame,boardSocket,chatSocket,drawFrame.getDrawingHistory(), userID, userName);
		thread.start();
		try 
		{
			bos = boardSocket.getOutputStream();
			Map<String, String> message = new HashMap<String, String>();
			message.put("messageType", "0");
			message.put("address", InetAddress.getLocalHost().getHostAddress());
			Date nowTime = new Date(System.currentTimeMillis());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(nowTime);
			message.put("timestamp", time);
			message.put("userID", userID);
			message.put("opt", "03");
			
			Map<String, String> argument = new HashMap<String, String>();
			argument.put("userName", userName);
			argument.put("userType", userType);
//			System.out.println(userType);
			message.put("argument", new JSONObject(argument).toString());
			JSONObject jsonr = new JSONObject(message);
			BufferedWriter dbos = new BufferedWriter(new OutputStreamWriter(bos));
			dbos.write(jsonr.toString());
			dbos.newLine();
			dbos.flush();
		} 
		catch (Exception e) 
		{
			System.out.println("error");
		}
		
	}
	
	
	
	
	
}
