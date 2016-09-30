package jp.mouple.net;

import java.util.LinkedList;

import jp.mouple.gui.KeyBinder;
import jp.mouple.gui.MainWindow;

public class User {
	private int m_id;
	private String m_name;
	private String m_address;
	private KeyBinder m_key;
	private ServerThread m_thread;
	
    private static int userId = 0;
	private static LinkedList<User> users = new LinkedList<User>();
	private static LinkedList<User> key_active_users = new LinkedList<User>();
	
	public User(ServerThread thread, String name, String address, KeyBinder key) {
		m_id = userId++;
		m_name = name;
		m_address = address;
		m_key = key;
		m_key.setUser(this);
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
	
	public KeyBinder getKey() {
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
	
	public void keyPressed() {
		addActiveUser(this);
	}
	
	public void keyReleased() {
		removeActiveUser(this);
	}
	
	// static members
	private static boolean addUser(User u) {
		boolean res = users.add(u);
		MainWindow.updateClientList();
		return res;
	}
		
	private static boolean removeUser(User u) {
		boolean res = users.remove(u);
		removeActiveUser(u);
		MainWindow.updateClientList();
		return res;
	}
	
	private static boolean addActiveUser(User u) {
		if (key_active_users.contains(u)) {
			return false;
		} else {
			key_active_users.add(u);
			updateActiveWindow();
			return true;
		}
	}
	
	private static boolean removeActiveUser(User u) {
		if (key_active_users.remove(u)) {
			updateActiveWindow();
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean updateActiveWindow() {
		if (key_active_users.size() > 0) {
			MainWindow.showClickCapturer();
			return true;
		} else {
			MainWindow.hideClickCapturer();
			return false;
		}
	}

	public static final LinkedList<User> getUsers() {
		return users;
	}
	
	public static void scanKeyBinds() {
		for (User u : users) {
			u.getKey().notifyStatus();
		}
	}
}
