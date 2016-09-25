package jp.mouple.core;

import java.util.LinkedList;

import jp.mouple.net.Message;
import jp.mouple.net.ServerThread;
import jp.mouple.gui.GlobalKeyObserver;
import jp.mouple.gui.MainWindow;

public class User {
	private int m_id;
	private String m_name;
	private String m_address;
	private GlobalKeyObserver.Key m_key;
	private ServerThread m_thread;
	
    private static int userId = 0;
	private static LinkedList<User> users = new LinkedList<User>();
	
	public User(ServerThread thread, String name, String address, GlobalKeyObserver.Key key) {
		m_id = userId++;
		m_name = name;
		m_address = address;
		m_key = key;
		m_thread = thread;
		
		addUser(this);
	}
	
	public int getId() {
		return m_id;
	}
	
	public String getName() {
		return m_name;
	}
	
	public String getAddress() {
		return m_address;
	}
	
	public GlobalKeyObserver.Key getKey() {
		return m_key;
	}
	
	public ServerThread getThread() {
		return m_thread;
	}
	
	public void sendMessage(Message msg) {
		m_thread.sendMessage(msg);
	}
	
	public void destroy() {
		removeUser(this);
	}

	public void shutdown() {
		m_thread.shutdown();
		destroy();
	}
	
	private static boolean addUser(User u) {
		boolean res = users.add(u);
		MainWindow.updateClientList();
		return res;
	}
		
	private static boolean removeUser(User u) {
		boolean res = users.remove(u);
		MainWindow.updateClientList();
		return res;
	}

	public static final LinkedList<User> getUsers() {
		return users;
	}	
}
