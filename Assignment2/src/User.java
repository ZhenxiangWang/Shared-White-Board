import java.net.Socket;

public class User {

	@Override
	public String toString() {
		if (this.isManager) {
			return userID + "+" + userName + "-" + "Manager";
		} else {
			return userID + "+" + userName + "-" + "Client";
		}

	}

	private String userID;
	private String userName;
	private String host;
	private int port;
	private boardThread boardThread1;
	private chatThread chatThread1;
	private Socket boardSocket;
	private Socket chatSocket;
	private boolean isManager;

	public User() {

	}

	public User(String userID, String userName, String host, int port, Socket boardsocket, Socket chatSocket,
			boardThread boardThread1,chatThread chatThread1, Boolean ismanager) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.host = host;
		this.port = port;
		this.boardSocket = boardsocket;
		this.chatSocket = chatSocket;
		this.setBoardThread1(boardThread1);
		this.setChatThread1(chatThread1);
		this.isManager = ismanager;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getBoardSocket() {
		return boardSocket;
	}

	public void setBoardSocket(Socket boardSocket) {
		this.boardSocket = boardSocket;
	}

	public Socket getChatSocket() {
		return chatSocket;
	}

	public void setChatSocket(Socket chatSocket) {
		this.chatSocket = chatSocket;
	}

	public boolean isManager() {
		return isManager;
	}

	public void setManager(boolean isManager) {
		this.isManager = isManager;
	}

	public boardThread getBoardThread1() {
		return boardThread1;
	}

	public void setBoardThread1(boardThread boardThread1) {
		this.boardThread1 = boardThread1;
	}

	public chatThread getChatThread1() {
		return chatThread1;
	}

	public void setChatThread1(chatThread chatThread1) {
		this.chatThread1 = chatThread1;
	}

}
