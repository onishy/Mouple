package jp.mouple.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

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

//    protected void finalize() throws Throwable {
//        try {
//            super.finalize();
//        } finally {
//            clear();
//        }
//    }

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
    
    public static String getIp() throws SocketException, UnknownHostException {
    	ArrayList<NetworkInterface> c = Collections.list(NetworkInterface.getNetworkInterfaces());
    	ArrayList<InetAddress> i_list = new ArrayList<InetAddress>();
    	i_list.add(InetAddress.getLocalHost());
    	for (NetworkInterface i : c) {
//    		for (InetAddress ia : Collections.list(i.getInetAddresses())) {
//    			System.out.println(ia.getHostAddress());
//    		}
    		i_list.addAll(Collections.list(i.getInetAddresses()));
    	}
    	return i_list.stream()
    			.filter(ip -> ip instanceof Inet4Address && ip.isSiteLocalAddress())
                .findFirst().orElseThrow(RuntimeException::new)
                .getHostAddress();
    }
};

class ServerObserverThread extends CommThread {
    private volatile boolean m_done_flag = false;
    ServerSocket m_socket;
    
    public ServerObserverThread(ServerSocket soc) {
        m_done_flag = false;
        m_socket = soc;
        try {
            MainWindow.setInfo("Server Started at " + ConnectionManager.getIp());
            System.out.println("Server Started at " + ConnectionManager.getIp());
        } catch (SocketException | UnknownHostException ex) {
            ex.printStackTrace();
        }
    }
    
    public void run() {
        while (!m_done_flag) {
            try {
                Socket socket = m_socket.accept();
                if (!socket.getInetAddress().equals(InetAddress.getLocalHost())) {
                    ServerThread thread = new ServerThread(socket, new GetMousePos());
                    thread.start();
                } else {
                    m_done_flag = true;
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Server halts now\n");
        for (User u : User.getUsers()) {
            u.shutdown();
        }
    }

    public void shutdown() {
        System.out.println("ServerObserver shutdown signal\n");
        m_done_flag = true;

        // 自分自身に接続してacceptを抜ける
        interrupt();
        try {
            Socket socket = new Socket(m_socket.getInetAddress(), m_socket.getLocalPort());
//            PrintWriter sendout = new PrintWriter(socket.getOutputStream(), true);
//            sendout.write("shutdown command");
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendMessage(Message msg) {
        for (User u : User.getUsers()) {
        	u.sendMessage(msg);
        }
    }
}

class ClientThread extends CommThread {
    private volatile boolean m_done_flag;
    private Socket m_socket;
    private ObjectOutputStream m_output_stream;
    private ObjectInputStream m_input_stream;

    private Callback m_cb;
    
    public ClientThread (Socket soc, Callback cb) throws IOException {
        m_socket = soc;
        m_cb = cb;
        
    	m_output_stream = new ObjectOutputStream(new BufferedOutputStream(m_socket.getOutputStream()));
        Message msg = new Message(Message.Type.i);
        msg.data = new String[1];
        msg.data[0] = "hemi";
        m_output_stream.writeObject(msg);
        m_output_stream.flush();
        
        System.out.println("Client Thread Connecting to " + soc.getInetAddress());
    }
    
    public void run() {
    	try {
			m_input_stream = new ObjectInputStream(new BufferedInputStream(m_socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
        while (!m_done_flag) {
            try {
                Message msg = (Message)m_input_stream.readObject();
                if (msg != null) {
                    System.out.println("Message from server: " + msg.toString());
                    
                    if (msg.getType() == null) {
                        System.out.println("Invalid Message.");
                        continue;                        
                    }                    
                    m_cb.func(msg);    
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                try {
                    if (m_socket != null) {
                        m_socket.close();
                    }
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                } finally {
                	shutdown();
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
    
    public synchronized void shutdown() {
        m_done_flag = true;
    }
};