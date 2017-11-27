
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class chatThread extends Thread {
	private Socket socket;
	private BufferedReader cis;
	private BufferedWriter cos;
	private boolean run = true;

	public chatThread(Socket clientSocket) {
		socket = clientSocket;
		try {
			cis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			cos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Acquire Server IO stream Exception");
		}

	}

	public void run() {
		try {
			while (run) {
				if (cis.ready()) {
					String request = cis.readLine();
					if (request == null)
						continue;
//					System.out.println(request);
					JSONObject json = new JSONObject(request);
					String opt = (String) json.get("opt");
					String messageType = (String) json.get("messageType");
					String userID = (String) json.get("userID");
					String argument = (String) json.get("argument");
					JSONObject jsonar = new JSONObject(argument);

					if (messageType.equals("0")) {

						if (opt.equals("03")) {
							String userType = (String) jsonar.get("userType");
							boolean usertype;
							if (userType.equals("01")) {
								usertype = true;
							} else {
								usertype = false;
							}
							String userName = (String) jsonar.get("userName");
							if (userType.equals("01")) {
								int port = socket.getPort();
								String host = socket.getInetAddress().getHostAddress();
								User newuser = new User(userID, userName, host, port, null, socket, null, this,
										usertype);
								if (Server.group == null) {
									Server.group = new ConnectionGroup();
									Server.group.addUser(newuser);
								} else {
									boolean check = false;
									for (User user : Server.group.getGroup()) {
										if (user.getUserID().equals(userID)) {

											user.setChatSocket(socket);
											user.setChatThread1(this);
											check = true;
										}
									}
									if (check == false) {
										Server.group.addUser(newuser);
									}

								}

							} else if (userType.equals("02")) {
								int port = socket.getPort();
								String host = socket.getInetAddress().getHostAddress();
								User newuser = new User(userID, userName, host, port, null, socket, null, this,
										usertype);
								if (Server.Tempgroup == null) {
									Server.Tempgroup = new ConnectionGroup();
									Server.Tempgroup.addUser(newuser);
								} else {
									boolean check = false;
									for (User user : Server.Tempgroup.getGroup()) {
										if (user.getUserID().equals(userID)) {
											user.setChatSocket(socket);
											user.setChatThread1(this);
											check = true;
										}
									}
									if (check == false) {
										Server.Tempgroup.addUser(newuser);
									}

								}
							}
						}

						else if (opt.equals("02")) {
							for (User newuser : Server.group.getGroup()) {
								Map<String, String> messageMap = new HashMap<String, String>();
								messageMap.put("messageType", "0");
								messageMap.put("address", InetAddress.getLocalHost().getHostAddress());
								Date nowTime = new Date(System.currentTimeMillis());
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = format.format(nowTime);
								messageMap.put("timestamp", time);
								messageMap.put("userID", userID);
								messageMap.put("opt", "02");
								String senderName = " ";
								// Get the user name of message sender
								for (User user : Server.group.getGroup()) {
									if (user.getUserID().equals(userID))
										senderName = user.getUserName();
								}

								JSONObject argumentJSON = new JSONObject(argument);
								argumentJSON.put("userName", senderName);
								messageMap.put("argument", argumentJSON.toString());
								JSONObject jsonr = new JSONObject(messageMap);
								BufferedWriter dss = new BufferedWriter(
										new OutputStreamWriter(newuser.getChatSocket().getOutputStream()));
								try {
									dss.write(jsonr.toString());
									dss.newLine();
									dss.flush();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									System.out.println("transfer chat message error");
								}
								// }
							}
						}
					} else if (messageType.equals("1")) {
						JSONObject jsonResponse = new JSONObject(argument);
						String updateAnswer = (String) jsonResponse.get("status");
						if (updateAnswer.equals("001")) {
							System.out.println("Update operation success");
						} else {
							System.out.println("Update operation error");
						}
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

}
