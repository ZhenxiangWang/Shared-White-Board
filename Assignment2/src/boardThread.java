import java.io.BufferedReader;

import java.io.BufferedWriter;
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

import org.json.*;

public class boardThread extends Thread {
	private Socket socket;
	private BufferedReader dis;
	private BufferedWriter dos;
	private boolean run = true;
	private static ArrayList<String> drawingHistory;

	public boardThread(Socket clientSocket, ArrayList<String> drawingHistory) {
		socket = clientSocket;
		this.drawingHistory = drawingHistory;
		try {
			dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			dos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Acquire Server IO stream Exception");
		}
	}

	private void executeAction(String request) throws Exception {
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
					String host = InetAddress.getLocalHost().getHostAddress();
					User newuser = new User(userID, userName, host, port, socket, null, this, null, usertype);
					if (Server.group == null) {
						Server.group = new ConnectionGroup();

						Server.group.addUser(newuser);
					} else {
						boolean check = false;
						for (User user : Server.group.getGroup()) {
							if (user.getUserID().equals(userID)) {
								user.setBoardSocket(socket);
								user.setBoardThread1(this);
								check = true;
							}
						}
						if (check == false) {
							Server.group.addUser(newuser);
						}
					}

					for (User usermanager : Server.group.getGroup()) {
						if (usermanager.isManager()) {
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", "server");
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("userList", Server.group.toString());
							argumentMap.put("manage", "03");
							JSONObject argumentjson = new JSONObject(argumentMap);
							messageMap.put("argument", argumentjson.toString());

							JSONObject jsonr = new JSONObject(messageMap);
							BufferedWriter dss = new BufferedWriter(
									new OutputStreamWriter(newuser.getBoardSocket().getOutputStream()));
							try {
								dss.write(jsonr.toString());
								dss.newLine();
								dss.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
						}
					}

				} else if (userType.equals("02")) {
					int port = socket.getPort();
					String host = InetAddress.getLocalHost().getHostAddress();
					User newuser = new User(userID, userName, host, port, socket, null, this, null, usertype);

					// send to server, the server send to manager

					if (Server.Tempgroup == null) {
						Server.Tempgroup = new ConnectionGroup();
						Server.Tempgroup.addUser(newuser);
					} else {
						boolean check = false;
						for (User user : Server.Tempgroup.getGroup()) {
							if (user.getUserID().equals(userID)) {
								user.setBoardSocket(socket);
								user.setBoardThread1(this);

								check = true;
							}
						}
						if (check == false) {
							Server.Tempgroup.addUser(newuser);
						}
					}

					Map<String, String> messageMap = new HashMap<String, String>();
					messageMap.put("messageType", "0");
					messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
					Date nowTime = new Date(System.currentTimeMillis());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(nowTime);
					messageMap.put("timestamp", time);
					messageMap.put("userID", "server");
					messageMap.put("opt", "04");
					Map<String, String> argumentMap = new HashMap<String, String>();
					argumentMap.put("manage", "01");
					argumentMap.put("userID", userID);
					argumentMap.put("userName", userName);
					JSONObject jsonargument = new JSONObject(argumentMap);
					messageMap.put("argument", jsonargument.toString());
					JSONObject jsonr = new JSONObject(messageMap);
					for (User userformanager : Server.group.getGroup()) {
						if (userformanager.isManager()) {
							BufferedWriter dss = new BufferedWriter(
									new OutputStreamWriter(userformanager.getBoardSocket().getOutputStream()));
							try {
								dss.write(jsonr.toString());
								dss.newLine();
								dss.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
						}
					}
				}

			} else if (opt.equals("01")) {
				boolean userInGroup = false;
				for (User user : Server.group.getGroup()) {
					if (user.getUserID().equals(userID)) {
						userInGroup = true;
					}
				}
				if (userInGroup) {
					drawingHistory.add(argument);
					JSONObject argumentJson = new JSONObject(argument);
					if (((String) argumentJson.get("shape")).equals("09"))
						drawingHistory.clear();
					for (User newuser : Server.group.getGroup()) {
						// if (!(newuser.getUserID().equals(userID))) {
						Map<String, String> messageMap = new HashMap<String, String>();
						messageMap.put("messageType", "0");
						messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
						Date nowTime = new Date(System.currentTimeMillis());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = format.format(nowTime);
						messageMap.put("timestamp", time);
						messageMap.put("userID", "server");
						messageMap.put("opt", "01");
						messageMap.put("argument", argument);

						JSONObject jsonr = new JSONObject(messageMap);
						BufferedWriter dss = new BufferedWriter(
								new OutputStreamWriter(newuser.getBoardSocket().getOutputStream()));
						try {
							dss.write(jsonr.toString());
							dss.newLine();
							dss.flush();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							System.out.println("client board thread closed");
//							e1.printStackTrace();
						}
						// }
					}
				}
			} else if (opt.equals("04")) {
				String manage = (String) jsonar.get("manage");
				String requestUserID = (String) jsonar.get("userID");
				if (manage.equals("04")) {
					for (User tempUser : Server.Tempgroup.getGroup()) {
						if (tempUser.getUserID().equals(requestUserID)) {
							Server.group.addUser(tempUser);
							Map<String, String> messageMap1 = new HashMap<String, String>();
							messageMap1.put("messageType", "0");
							messageMap1.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime1 = new Date(System.currentTimeMillis());
							SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time1 = format1.format(nowTime1);
							messageMap1.put("timestamp", time1);
							messageMap1.put("userID", "server");
							messageMap1.put("opt", "04");
							Map<String, String> argumentMap1 = new HashMap<String, String>();
							argumentMap1.put("userList", Server.group.toString());
							argumentMap1.put("manage", "08");
							JSONObject argumentjson = new JSONObject(argumentMap1);
							messageMap1.put("argument", argumentjson.toString());

							JSONObject jsonr1 = new JSONObject(messageMap1);
							BufferedWriter dss1 = new BufferedWriter(
									new OutputStreamWriter(tempUser.getBoardSocket().getOutputStream()));
							try {
								dss1.write(jsonr1.toString());
								dss1.newLine();
								dss1.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}

							ArrayList<String> tempDrawingHistory = new ArrayList<String>();
							for (String drawing : drawingHistory) {
								tempDrawingHistory.add(drawing);
							}
							for (String drawing : tempDrawingHistory) {
								Map<String, String> messageMap = new HashMap<String, String>();
								messageMap.put("messageType", "0");
								messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
								Date nowTime = new Date(System.currentTimeMillis());
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = format.format(nowTime);
								messageMap.put("timestamp", time);
								messageMap.put("userID", "server");
								messageMap.put("opt", "01");
								messageMap.put("argument", drawing);
								JSONObject jsonr = new JSONObject(messageMap);
								BufferedWriter dss = new BufferedWriter(
										new OutputStreamWriter(tempUser.getBoardSocket().getOutputStream()));
								try {
									dss.write(jsonr.toString());
									dss.newLine();
									dss.flush();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									System.out.println("client board thread closed");
//									e1.printStackTrace();
								}

							}
							for (User normaluser : Server.group.getGroup()) {
								Map<String, String> messageMap = new HashMap<String, String>();
								messageMap.put("messageType", "0");
								messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
								Date nowTime = new Date(System.currentTimeMillis());
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = format.format(nowTime);
								messageMap.put("timestamp", time);
								messageMap.put("userID", "server");
								messageMap.put("opt", "04");
								Map<String, String> argumentMap = new HashMap<String, String>();
								argumentMap.put("manage", "03");
								argumentMap.put("userID", tempUser.getUserID());
								argumentMap.put("userList", Server.group.toString());
								JSONObject jsonargument = new JSONObject(argumentMap);
								messageMap.put("argument", jsonargument.toString());
								JSONObject jsonr = new JSONObject(messageMap);
								BufferedWriter dss = new BufferedWriter(
										new OutputStreamWriter(normaluser.getBoardSocket().getOutputStream()));
								try {
									dss.write(jsonr.toString());
									dss.newLine();
									dss.flush();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									System.out.println("client board thread closed");
//									e1.printStackTrace();
								}
							}
						}
					}
				} else if (manage.equals("05")) {
					User tempUserToRemove = new User();
					for (User tempUser : Server.Tempgroup.getGroup()) {
						if (tempUser.getUserID().equals(requestUserID)) {
							tempUserToRemove = tempUser;
						}
					}
					Server.Tempgroup.deleteUser(tempUserToRemove);
					Map<String, String> messageMap = new HashMap<String, String>();
					messageMap.put("messageType", "0");
					messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
					Date nowTime = new Date(System.currentTimeMillis());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(nowTime);
					messageMap.put("timestamp", time);
					messageMap.put("userID", "server");
					messageMap.put("opt", "04");
					Map<String, String> argumentMap = new HashMap<String, String>();
					argumentMap.put("manage", "05");
					argumentMap.put("userID", tempUserToRemove.getUserID());
					argumentMap.put("userList", Server.group.toString());
					JSONObject jsonargument = new JSONObject(argumentMap);
					messageMap.put("argument", jsonargument.toString());
					JSONObject jsonr = new JSONObject(messageMap);
					BufferedWriter dss = new BufferedWriter(
							new OutputStreamWriter(tempUserToRemove.getBoardSocket().getOutputStream()));
					try {
						dss.write(jsonr.toString());
						dss.newLine();
						dss.flush();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.out.println("client board thread closed");
//						e1.printStackTrace();
					}
				} else if (manage.equals("02")) {
					String targetUserId = (String) jsonar.get("userID");
					User userToRemove = null;
					for (User normaluser : Server.group.getGroup()) {
						if (normaluser.getUserID().equals(targetUserId)) {
							userToRemove = normaluser;
							userToRemove.getBoardThread1().setRun(false);
							userToRemove.getChatThread1().setRun(false);
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", "server");
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("manage", "02");
							argumentMap.put("userName", userToRemove.getUserName());
							JSONObject jsonargument = new JSONObject(argumentMap);
							messageMap.put("argument", jsonargument.toString());
							JSONObject jsonr = new JSONObject(messageMap);
							BufferedWriter dss = new BufferedWriter(
									new OutputStreamWriter(normaluser.getBoardSocket().getOutputStream()));
							try {
								dss.write(jsonr.toString());
								dss.newLine();
								dss.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}

						}
					}
					if (userToRemove != null) {
						userToRemove.getBoardSocket().close();
						userToRemove.getChatSocket().close();
						Server.group.deleteUser(userToRemove);

						for (User normaluser : Server.group.getGroup()) {
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", "server");
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("manage", "03");
							argumentMap.put("userList", Server.group.toString());
							JSONObject jsonargument = new JSONObject(argumentMap);
							messageMap.put("argument", jsonargument.toString());
							JSONObject jsonr = new JSONObject(messageMap);
							BufferedWriter dss = new BufferedWriter(
									new OutputStreamWriter(normaluser.getBoardSocket().getOutputStream()));
							try {
								dss.write(jsonr.toString());
								dss.newLine();
								dss.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
						}
					}
					else{
						for (User normaluser : Server.group.getGroup()) {
							if (normaluser.isManager()){
								Map<String, String> messageMap = new HashMap<String, String>();
								messageMap.put("messageType", "0");
								messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
								Date nowTime = new Date(System.currentTimeMillis());
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String time = format.format(nowTime);
								messageMap.put("timestamp", time);
								messageMap.put("userID", "server");
								messageMap.put("opt", "04");
								Map<String, String> argumentMap = new HashMap<String, String>();
								argumentMap.put("manage", "10");
								argumentMap.put("userList", Server.group.toString());
								JSONObject jsonargument = new JSONObject(argumentMap);
								messageMap.put("argument", jsonargument.toString());
								JSONObject jsonr = new JSONObject(messageMap);
								BufferedWriter dss = new BufferedWriter(
										new OutputStreamWriter(normaluser.getBoardSocket().getOutputStream()));
								try {
									dss.write(jsonr.toString());
									dss.newLine();
									dss.flush();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									System.out.println("client board thread closed");
//									e1.printStackTrace();
								}	
							}
						}
					}
				} else if (manage.equals("06")) {
					User userToQuit = new User();
					for (User quitUser : Server.group.getGroup()) {
						if (quitUser.getUserID().equals(requestUserID)) {
							userToQuit = quitUser;
						}
					}

					Server.group.deleteUser(userToQuit);
					if (userToQuit.isManager()) {
						for (User normaluser : Server.group.getGroup()) {
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", userToQuit.getUserID());
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("manage", "07");
							argumentMap.put("userList", null);
							JSONObject jsonargument = new JSONObject(argumentMap);
							messageMap.put("argument", jsonargument.toString());
							JSONObject jsonr = new JSONObject(messageMap);
							BufferedWriter dss = new BufferedWriter(
									new OutputStreamWriter(normaluser.getBoardSocket().getOutputStream()));
							try {
								dss.write(jsonr.toString());
								dss.newLine();
								dss.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch
								// block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
						}
						Map<String, String> messageMap = new HashMap<String, String>();
						messageMap.put("messageType", "0");
						messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
						Date nowTime = new Date(System.currentTimeMillis());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = format.format(nowTime);
						messageMap.put("timestamp", time);
						messageMap.put("userID", userToQuit.getUserID());
						messageMap.put("opt", "04");
						Map<String, String> argumentMap = new HashMap<String, String>();
						argumentMap.put("manage", "09");
						argumentMap.put("userList", null);
						JSONObject jsonargument = new JSONObject(argumentMap);
						messageMap.put("argument", jsonargument.toString());
						JSONObject jsonr = new JSONObject(messageMap);
						BufferedWriter dss = new BufferedWriter(
								new OutputStreamWriter(userToQuit.getBoardSocket().getOutputStream()));
						try {
							dss.write(jsonr.toString());
							dss.newLine();
							dss.flush();
						} catch (Exception e1) {
							// TODO Auto-generated catch
							// block
							System.out.println("client board thread closed");
//							e1.printStackTrace();
						}

					} else {
						for (User normaluser : Server.group.getGroup()) {
							// if (!normaluser.equals(quitUser)) {
							Map<String, String> messageMap = new HashMap<String, String>();
							messageMap.put("messageType", "0");
							messageMap.put("address", InetAddress.getLocalHost().getHostAddress().toString());
							Date nowTime = new Date(System.currentTimeMillis());
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(nowTime);
							messageMap.put("timestamp", time);
							messageMap.put("userID", userToQuit.getUserID());
							messageMap.put("opt", "04");
							Map<String, String> argumentMap = new HashMap<String, String>();
							argumentMap.put("manage", "03");
							argumentMap.put("userList", Server.group.toString());
							JSONObject jsonargument = new JSONObject(argumentMap);
							messageMap.put("argument", jsonargument.toString());
							JSONObject jsonr = new JSONObject(messageMap);
							BufferedWriter dss = new BufferedWriter(
									new OutputStreamWriter(normaluser.getBoardSocket().getOutputStream()));
							try {
								dss.write(jsonr.toString());
								dss.newLine();
								dss.flush();
							} catch (Exception e1) {
								// TODO Auto-generated catch
								// block
								System.out.println("client board thread closed");
//								e1.printStackTrace();
							}
							// }
							// }
							// }
						}
					}
					userToQuit.getBoardThread1().setRun(false);
					userToQuit.getChatThread1().setRun(false);
					userToQuit.getBoardSocket().close();
					userToQuit.getChatSocket().close();

				}
			}

		}
	}

	public void run() {
		try {
			while (run) {
				if (dis.ready()) {
					String request = dis.readLine();
					if (request == null)
						continue;
//					System.out.println(request);
					executeAction(request);
				}
			}
		} catch (

		Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("client closed");
		}

	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

}
