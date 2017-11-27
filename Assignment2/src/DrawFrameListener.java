import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.json.JSONObject;


public class DrawFrameListener implements MouseMotionListener, MouseListener {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	// Use to record the start position of the Polygon
	private int x3;
	private int y3;
	private boolean flag;// Record whether is the first line of Polygon
	private DrawFrame drawFrame;
	private Graphics graphics;
	private Graphics2D graphics2d;
	private Socket socket;
	private BufferedWriter dbos;
	static String userID;
	private static ArrayList<String> drawingHistory;

	public DrawFrameListener(DrawFrame drawFrame, Graphics graphics, Socket socket, String userID, ArrayList<String> drawingHistory) {
		this.drawFrame = drawFrame;
		this.graphics = graphics;
		this.flag = false;
		this.graphics2d = (Graphics2D) graphics;
		this.socket = socket;
		this.userID = userID;
		this.drawingHistory = drawingHistory;
		try {
			this.dbos = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("server is not open");
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Color color = drawFrame.getColor();
//		graphics2d.setColor(color);
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
		if (drawFrame.getGraphName().equals("Polygon") && flag) {
			if (e.getClickCount() == 2) {
//				graphics2d.drawLine(x1, y1, x3, y3);
				Map<String, String> argument = new HashMap<String, String>();
				argument.put("shape", "01");
				argument.put("x1", String.valueOf(x1));
				argument.put("y1", String.valueOf(y1));
				argument.put("x2", String.valueOf(x3));
				argument.put("y2", String.valueOf(y3));
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				argument.put("colorR", String.valueOf(r));
				argument.put("colorG", String.valueOf(g));
				argument.put("colorB", String.valueOf(b));
				JSONObject jsona = new JSONObject(argument);
				message.put("argument", jsona.toString());
				JSONObject jsonr = new JSONObject(message);
				try {
//					FileSave.save(filename, jsonr.toString()+"\n");
					drawingHistory.add(new JSONObject(argument).toString());
					dbos.write(jsonr.toString());
					dbos.newLine();
					dbos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				} finally {
					flag = false;
				}

			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Color color = drawFrame.getColor();
		String graphName = drawFrame.getGraphName();
//		graphics2d.setColor(color);
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
		if (graphName.equals("Polygon") && !flag) {
			x1 = e.getX();
			y1 = e.getY();
			// Record the start position of the Polygon
			x3 = x1;
			y3 = y1;
			flag = true;
		} else if (!graphName.equals("Polygon")) {
			x1 = e.getX();
			y1 = e.getY();
			if (graphName.equals("Text")) {
				String inputValue = JOptionPane.showInputDialog("Please input a text");
//				graphics2d.drawString(inputValue, x1, y1);
				argument.put("shape", "08");
				argument.put("x1", String.valueOf(x1));
				argument.put("y1", String.valueOf(y1));
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				argument.put("text", inputValue);
				argument.put("colorR", String.valueOf(r));
				argument.put("colorG", String.valueOf(g));
				argument.put("colorB", String.valueOf(b));
				JSONObject jsona = new JSONObject(argument);
				message.put("argument", jsona.toString());
				JSONObject jsonr = new JSONObject(message);
				try {
//					FileSave.save(filename, jsonr.toString()+"\n");
					drawingHistory.add(new JSONObject(argument).toString());
					dbos.write(jsonr.toString());
					dbos.newLine();
					dbos.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				} finally {
					flag = false;
				}

			} else if (graphName.equals("Eraser")) {
				x1 = e.getX();
				y1 = e.getY();
				int size = drawFrame.getEraserSize();
//				graphics2d.setColor(Color.WHITE);
//				graphics2d.fillRect(x1 - size / 2, y1 - size / 2, size, size);
				argument.put("shape", "05");
				argument.put("x1", String.valueOf(x1 - size / 2));
				argument.put("y1", String.valueOf(y1 - size / 2));
				argument.put("width", String.valueOf(size));
				argument.put("height", String.valueOf(size));
				Color eraserColor = Color.WHITE;
				int r = eraserColor.getRed();
				int g = eraserColor.getGreen();
				int b = eraserColor.getBlue();
				argument.put("colorR", String.valueOf(r));
				argument.put("colorG", String.valueOf(g));
				argument.put("colorB", String.valueOf(b));
				JSONObject jsona = new JSONObject(argument);
				message.put("argument", jsona.toString());
				JSONObject jsonr = new JSONObject(message);
				try {
//					FileSave.save(filename, jsonr.toString()+"\n");
					drawingHistory.add(new JSONObject(argument).toString());
					dbos.write(jsonr.toString());
					dbos.newLine();
					dbos.flush();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				}finally {
//					graphics2d.setColor(color);
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Color color = drawFrame.getColor();
//		graphics2d.setColor(color);
		x2 = e.getX();
		y2 = e.getY();
		String graphName = drawFrame.getGraphName();
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
		if (graphName.equals("Line")) {
//			graphics2d.drawLine(x1, y1, x2, y2);

			argument.put("shape", "01");
			argument.put("x1", String.valueOf(x1));
			argument.put("y1", String.valueOf(y1));
			argument.put("x2", String.valueOf(x2));
			argument.put("y2", String.valueOf(y2));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("server is not open");
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		} else if (graphName.equals("Rect")) {
//			graphics2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));

			argument.put("shape", "02");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(Math.abs(x2 - x1)));
			argument.put("height", String.valueOf(Math.abs(y2 - y1)));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		} else if (graphName.equals("Circle")) {
			int diameter = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
//			graphics2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), diameter, diameter);
			argument.put("shape", "03");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(diameter));
			argument.put("height", String.valueOf(diameter));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		} else if (graphName.equals("Oval")) {
//			graphics2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
			argument.put("shape", "03");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(Math.abs(x2 - x1)));
			argument.put("height", String.valueOf(Math.abs(y2 - y1)));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		} else if (graphName.equals("RoundRect")) {
//			graphics2d.drawRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), 15, 15);
			argument.put("shape", "04");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(Math.abs(x2 - x1)));
			argument.put("height", String.valueOf(Math.abs(y2 - y1)));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		} else if (graphName.equals("Polygon")) {
//			graphics2d.drawLine(x1, y1, x2, y2);
			argument.put("shape", "01");
			argument.put("x1", String.valueOf(x1));
			argument.put("y1", String.valueOf(y1));
			argument.put("x2", String.valueOf(x2));
			argument.put("y2", String.valueOf(y2));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				x1 = x2;
				y1 = y2;
			}

		} else if (graphName.equals("FillCircle")) {
			int diameter = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
//			graphics2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), diameter, diameter);
			argument.put("shape", "06");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(diameter));
			argument.put("height", String.valueOf(diameter));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		} else if (graphName.equals("FillRect")) {
//			graphics2d.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
			argument.put("shape", "05");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(Math.abs(x2 - x1)));
			argument.put("height", String.valueOf(Math.abs(y2 - y1)));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (Exception e1) {
				// TODO Auto-generated catch block

			} finally {
				flag = false;
			}
		} else if (graphName.equals("FillOval")) {
//			graphics2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
			argument.put("shape", "06");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(Math.abs(x2 - x1)));
			argument.put("height", String.valueOf(Math.abs(y2 - y1)));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (Exception e1) {
				// TODO Auto-generated catch block

			} finally {
				flag = false;
			}
		} else if (graphName.equals("FillRoundRect")) {
//			graphics2d.fillRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), 15, 15);
			argument.put("shape", "07");
			argument.put("x1", String.valueOf(Math.min(x1, x2)));
			argument.put("y1", String.valueOf(Math.min(y1, y2)));
			argument.put("width", String.valueOf(Math.abs(x2 - x1)));
			argument.put("height", String.valueOf(Math.abs(y2 - y1)));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			} finally {
				flag = false;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Color color = drawFrame.getColor();
//		graphics2d.setColor(color);
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
		String graphName = drawFrame.getGraphName();
		if (graphName.equals("Eraser")) {
			x2 = e.getX();
			y2 = e.getY();
			int size = drawFrame.getEraserSize();
//			graphics2d.setColor(Color.WHITE);
//			graphics2d.fillRect(x2 - size / 2, y2 - size / 2, size, size);

			argument.put("shape", "05");
			argument.put("x1", String.valueOf(x2 - size / 2));
			argument.put("y1", String.valueOf(y2 - size / 2));
			argument.put("width", String.valueOf(size));
			argument.put("height", String.valueOf(size));
			Color eraserColor = Color.WHITE;
			int r = eraserColor.getRed();
			int g = eraserColor.getGreen();
			int b = eraserColor.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (Exception e1) {
				// TODO Auto-generated catch block

			}
//			graphics2d.setColor(color);
		} else if (graphName.equals("FreeHand")) {
			x2 = e.getX();
			y2 = e.getY();
//			graphics2d.drawLine(x1, y1, x2, y2);
			argument.put("shape", "01");
			argument.put("x1", String.valueOf(x1));
			argument.put("y1", String.valueOf(y1));
			argument.put("x2", String.valueOf(x2));
			argument.put("y2", String.valueOf(y2));
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			argument.put("colorR", String.valueOf(r));
			argument.put("colorG", String.valueOf(g));
			argument.put("colorB", String.valueOf(b));
			JSONObject jsona = new JSONObject(argument);
			message.put("argument", jsona.toString());
			JSONObject jsonr = new JSONObject(message);
			try {
//				FileSave.save(filename, jsonr.toString()+"\n");
				drawingHistory.add(new JSONObject(argument).toString());
				dbos.write(jsonr.toString());
				dbos.newLine();
				dbos.flush();
			} catch (Exception e1) {
				// TODO Auto-generated catch block

			} finally {
				x1 = x2;
				y1 = y2;
			}
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x2 = e.getX();
		y2 = e.getY();

	}

}
