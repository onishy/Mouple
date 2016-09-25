package jp.mouple.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import jp.mouple.core.GetData;
import jp.mouple.core.User;
import jp.mouple.gui.GlobalKeyObserver;
import jp.mouple.gui.MainWindow;

public class ServerThread extends CommThread {
    private volatile boolean m_done_flag = false;
    private Socket m_socket;
    private ObjectOutputStream m_output_stream;
    private ObjectInputStream m_input_stream;
    private User m_user;
    
    private LinkedList<Message> m_msg_buffer = new LinkedList<Message>();
    
    public ServerThread (Socket soc, GetData gd) throws IOException, ClassNotFoundException {
        m_socket = soc;
    	m_input_stream = new ObjectInputStream(new BufferedInputStream(m_socket.getInputStream()));
    	Message msg = (Message) m_input_stream.readObject();
    	System.out.println(msg.toString());
        MainWindow.setInfo("New Client at " + soc.getInetAddress());
        System.out.println("New Client at " + soc.getInetAddress());
        
        m_user = new User(this, msg.data[0], soc.getInetAddress().toString(), GlobalKeyObserver.Key.CMD);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
        	System.out.println("Finalize...");
            try {
                if (m_socket != null) {
                    m_socket.close();
                    shutdown();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void run() {
    	try {
			m_output_stream = new ObjectOutputStream(new BufferedOutputStream(m_socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
            shutdown();
		}
        while (!m_done_flag) {
            try {
                synchronized (m_msg_buffer) {
                	Message msg = m_msg_buffer.pollFirst();
                	while(msg != null){
	                    m_output_stream.writeObject(msg);
	                	msg = m_msg_buffer.pollFirst();
                	}
                    m_output_stream.flush();
                }
                
//                Message msg = m_gd.func();
//                if (msg != null) {
//                    sendout.println(msg.toString());
//                }
            } catch (IOException ex) {
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
    	System.out.println("shutdown now");
        m_done_flag = true;
        m_user.destroy();
    }
    
    public synchronized void sendMessage(Message msg) {
        m_msg_buffer.addFirst(msg);
    }
};