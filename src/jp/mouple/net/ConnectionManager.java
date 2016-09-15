package jp.mouple.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.LinkedList;

import jp.mouple.core.*;
import jp.mouple.gui.GetMousePos;
import jp.mouple.gui.MainWindow;
import jp.mouple.gui.MoveMousePos;

public class ConnectionManager {
	public enum Mode {
		MODE_VOID,
		MODE_CLIENT,
		MODE_SERVER
	};
		
	private StoppableThread m_thread;
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
				m_thread = new ClientThread(socket, new MoveMousePos());
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
	
	public Mode getMode() {
		return m_mode;
	}
};

abstract class StoppableThread extends Thread {
	public abstract void shutdown();
}

class ServerObserverThread extends StoppableThread {
	private volatile boolean m_done_flag = false;
	ServerSocket m_socket;
	LinkedList<ServerThread> m_thread_list = new LinkedList<ServerThread>();
	
	public ServerObserverThread(ServerSocket soc) {
		m_done_flag = false;
		m_socket = soc;
		try {
			MainWindow.setInfo("Server Started at " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("Server Started at " + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException ex) {
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
}

class ServerThread extends StoppableThread {
	private volatile boolean m_done_flag = false;
	private Socket m_socket;
	private GetData m_gd;
	
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
				String msg = m_gd.func();
				if (msg != "") {
					sendout.println(msg);				
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

class ClientThread extends StoppableThread {
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
				System.out.println("Message from server: " + line);
				if (line != "") {
					String[] data = line.trim().split(",");
					if (data.length > 0) {
						m_cb.func(data);				
					}
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