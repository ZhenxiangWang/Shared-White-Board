import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.json.*;
public class Server {
	private static String userName = "manager";
	public static int boardPort;
	private static ServerSocket chatServer = null;
	private static ServerSocket boardServer = null;
	public static ConnectionGroup group;
	public static ConnectionGroup Tempgroup;
	private static ArrayList<String> drawingHistory = new ArrayList<String>();
	
	public static void server()
	{
		try 
		{
			boardServer = new ServerSocket(boardPort);
		} 
		catch (IOException e) 
		{
			System.out.println("Server established error");
		}
		try 
		{
			chatServer = new ServerSocket(boardPort + 1);
		} 
		catch (IOException e) 
		{
			System.out.println("Server established error");
		}
		
		
	
		new Thread()
		{
			public void run()
			{
				while(true)
				{
					try 
					{
						new chatThread(chatServer.accept()).start();
					} 
					catch (IOException e) 
					{
						System.out.println("Accept client connection exception");
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
					try 
					{
						new boardThread(boardServer.accept(),drawingHistory).start();
					} 
					catch (IOException e) 
					{
						System.out.println("Accept client connection exception");
					}
				}
			}
		}.start();
		
		
	}
}
