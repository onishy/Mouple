package jp.mouple.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Collections;
import java.util.LinkedList;

import jp.mouple.core.*;
import jp.mouple.gui.GetMousePos;
import jp.mouple.gui.MainWindow;
import jp.mouple.gui.InterpretMessage;

public class ConnectionManager {
	public enum Mode {
		MODE_VOID,
		MODE_CLIENT,
		MODE_SERVER
	};
		
	private CommThread m_thread;
	private Mode m_mode;
	
	public ConnectionManager(ConnectionInfo info) throws IOException {
		m_mode = Mode.MODE_VOID;
		if (info.mode == Mode.MODE_CLIENT) {
			// Client Mode
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(info.address, info.port), info.timeout);
				InetAddress inadr = socket.getInetAddress();
				if (inadr != null) {
					MainWindow.setInfo("Client Connect to " + inadr);
					System.out.println("Client Connect to " + inadr);
				} else {
					MainWindow.setErr("Connection Failed.");
					System.out.println("Connection Failed.");
					socket.close();
					return;
				}
				m_thread = new ClientThread(socket, new InterpretMessage());
				m_thread.start();
			} catch (IOException ex) {
				throw ex;
			}
			m_mode = Mode.MODE_CLIENT;				
		} else if (info.mode == Mode.MODE_SERVER) {
			// Server Mode
			try {
				ServerSocket srv_soc = new ServerSocket(info.port);
				m_thread = new ServerObserverThread(srv_soc);
				m_thread.start();
			} catch (IOException ex) {
				throw ex;
			}
			m_mode = Mode.MODE_SERVER;
		} else {
			m_thread = null;
			MainWindow.setErr("Connection Failed.");
			System.out.println("Connection Failed.");
			return;
		}
	}

//	protected void finalize() throws Throwable {
//		try {
//			super.finalize();
//		} finally {
//			clear();
//		}
//	}

	public void clear() {
		m_mode = Mode.MODE_VOID;
		m_thread.shutdown();
		m_thread = null;
	}
	
	public void sendMessage(Message msg) {
		if (m_mode == Mode.MODE_SERVER) {
			((ServerObserverThread)m_thread).sendMessage(msg);
		}
	}
	
	public Mode getMode() {
		return m_mode;
	}
	
	public static String getIp() throws SocketException {
	    return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
	            .flatMap(i -> Collections.list(i.getInetAddresses()).stream())
	            .filter(ip -> ip instanceof Inet4Address && ip.isSiteLocalAddress())
	            .findFirst().orElseThrow(RuntimeException::new)
	            .getHostAddress();
	}
};

abstract class CommThread extends Thread {
	public abstract void shutdown();
}

class ServerObserverThread extends CommThread {
	private volatile boolean m_done_flag = false;
	ServerSocket m_socket;
	LinkedList<ServerThread> m_thread_list = new LinkedList<ServerThread>();
	
	public ServerObserverThread(ServerSocket soc) {
		m_done_flag = false;
		m_socket = soc;
		try {
			MainWindow.setInfo("Server Started at " + ConnectionManager.getIp());
			System.out.println("Server Started at " + ConnectionManager.getIp());
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
	}
	
	public void run() {
		while (!m_done_flag) {
			try {
				Socket socket = m_socket.accept();
				if (!socket.getInetAddress().equals(InetAddress.getLocalHost())) {
					ServerThread thread = new ServerThread(socket, new GetMousePos());
					m_thread_list.add(thread);
					thread.start();
				} else {
					m_done_flag = true;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		for (ServerThread thread : m_thread_list) {
			thread.shutdown();
		}
	}

	public void shutdown() {
		System.out.println("ServerObserver shutdown signal\n");
		m_done_flag = true;

		// 自分自身に接続してacceptを抜ける
		interrupt();
        try {
            Socket socket = new Socket(m_socket.getInetAddress(), m_socket.getLocalPort());
			PrintWriter sendout = new PrintWriter(socket.getOutputStream(), true);
            sendout.write("shutdown command");
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
	public void sendMessage(Message msg) {
		
	}
}

class ServerThread extends CommThread {
	private volatile boolean m_done_flag = false;
	private Socket m_socket;
	private GetData m_gd;
	
	private LinkedList<Message> m_msg_buffer = new LinkedList<Message>();
	
	public ServerThread (Socket soc, GetData gd) {
		m_socket = soc;
		m_gd = gd;
		MainWindow.setInfo("New Client at " + soc.getInetAddress());
		System.out.println("New Client at " + soc.getInetAddress());
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			super.finalize();
		} finally {
			try {
				if (m_socket != null) {
					m_socket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void run() {
		while (!m_done_flag) {
			try {
				PrintWriter sendout = new PrintWriter(m_socket.getOutputStream(), true);
				
				for (Message str : m_msg_buffer) {
					sendout.println(str.toString());
				}
				
				Message msg = m_gd.func();
				if (msg != null) {
					sendout.println(msg.toString());
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				try {
					if (m_socket != null) {
						m_socket.close();
					}
				} catch (IOException ex2) {
					ex2.printStackTrace();
				}
			}
		}
		try {
			if (m_socket != null) {
				m_socket.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void shutdown() {
		m_done_flag = true;
	}
	
	public synchronized void sendMessage(Message msg) {
		m_msg_buffer.add(msg);
	}
};

class ClientThread extends CommThread {
	private volatile boolean m_done_flag;
	private Socket m_socket;
	private Callback m_cb;
	
	public ClientThread (Socket soc, Callback cb) {
		m_socket = soc;
		m_cb = cb;
		System.out.println("Client Thread Connecting to " + soc.getInetAddress());
	}
	
	public void run() {
		while (!m_done_flag) {
			try {
				BufferedReader reader =
						new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
				String line;
				line = reader.readLine();
				if (line != "" && line != null) {
					System.out.println("Message from server: " + line);
					
					String[] msg_str = line.trim().split(":");
					if (msg_str.length == 0) {
						System.out.println("Invalid Message.");
						continue;
					}
					
					Message msg = new Message(Message.Type.valueOf(msg_str[0]));
					if (msg.getType() == null) {
						System.out.println("Invalid Message.");
						continue;						
					}
					
					if (msg_str.length > 1) {
						String[] data = msg_str[1].trim().split(",");
						msg.data = data;
					}
					m_cb.func(msg);	
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				try {
					if (m_socket != null) {
						m_socket.close();
					}
				} catch (IOException ex2) {
					ex2.printStackTrace();
				}
			}
		}
		try {
			if (m_socket != null) {
				m_socket.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void shutdown() {
		m_done_flag = true;
	}
};