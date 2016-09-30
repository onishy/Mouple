package jp.mouple.gui;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D.Float;
import java.io.IOException;
import java.util.LinkedList;
import java.awt.event.ActionEvent;

import jp.mouple.core.Def;
import jp.mouple.net.*;
import jp.mouple.net.ConnectionManager.Mode;

import javax.swing.JSpinner;

import java.awt.AWTException;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class MainWindow {

    private static MainWindow window;
    private static boolean initialized = false;

    private JFrame frame;
    private static JFrame translucent_frame;
    private static JTabbedPane tabbedPane;
    private static JTextField strAddressClient;
    private static JSpinner spinnerPortServer;
    private static JSpinner spinnerPortClient;
    private static JButton btnConnectServer;
    private static JButton btnDisconnectServer;
    private static JButton btnConnectClient;
    private static JButton btnDisconnectClient;
    private static JLabel lblErr;
    private static JLabel lblInfo;
    
    private static Image iconImage = null;
    private static PopupMenu taskTrayMenu;
    private static TrayIcon taskTrayIcon;
    private static MenuItem itemConnectClient;
    private static MenuItem itemConnectServer;
    
    private static ConnectionManager connectionManager = null;
    private static JTable tableClientList;
    private static DefaultTableModel tableModel;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	GlobalScreen.addNativeKeyListener(new GlobalKeyObserver());		

                	window = new MainWindow();
                    window.frame.setVisible(true);
                    translucent_frame = new TranslucentWindow();
                    
                    initializeTaskTray();
                    new GlobalKeyObserver();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 294);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Mouple");
		try {
			iconImage = ImageIO.read(Thread.currentThread()
			        .getContextClassLoader()
			        .getResourceAsStream("icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (iconImage != null) {
	        frame.setIconImage(iconImage);
		}
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(6, 6, 438, 214);
        frame.getContentPane().add(tabbedPane);
        
        JPanel tabServer = new JPanel();
        tabbedPane.addTab("Server", null, tabServer, null);
        tabServer.setLayout(null);
        
        btnConnectServer = new JButton("Connect");
        btnConnectServer.setBounds(169, 133, 115, 29);
        tabServer.add(btnConnectServer);
        btnConnectServer.addActionListener(new ConnectServerActionListener());
        
        btnDisconnectServer = new JButton("Disconnect");
        btnDisconnectServer.setEnabled(false);
        btnDisconnectServer.setBounds(296, 133, 115, 29);
        tabServer.add(btnDisconnectServer);
        btnDisconnectServer.addActionListener(new DisconnectActionListener());
        
        spinnerPortServer = new JSpinner();
        spinnerPortServer.setBounds(56, 6, 88, 29);
        spinnerPortServer.setValue(Def.NetworkDeafult.default_port);
        spinnerPortServer.setEditor(new JSpinner.NumberEditor(spinnerPortServer, "#"));
        tabServer.add(spinnerPortServer);
        
        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(6, 6, 38, 29);
        tabServer.add(lblPort);
        
        String[] columnNames = {"No", "Key", "Name", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0);
        
        JLabel lblClientList = new JLabel("Client List");
        lblClientList.setBounds(169, 6, 115, 16);
        tabServer.add(lblClientList);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(169, 28, 229, 93);
        tabServer.add(scrollPane);
        tableClientList = new JTable(tableModel);
        tableClientList.setEnabled(false);
        scrollPane.setColumnHeaderView(tableClientList);
        tableClientList.setShowGrid(true);

        JPanel tabClient = new JPanel();
        tabbedPane.addTab("Client", null, tabClient, null);
        tabClient.setLayout(null);
        
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setBounds(6, 11, 55, 16);
        tabClient.add(lblAddress);
        
        strAddressClient = new JTextField();
        strAddressClient.setBounds(73, 5, 134, 28);
        strAddressClient.setText(Def.NetworkDeafult.default_ip);
        tabClient.add(strAddressClient);
        strAddressClient.setColumns(10);
        
        JLabel label = new JLabel("Port:");
        label.setBounds(6, 51, 29, 16);
        tabClient.add(label);
        
        spinnerPortClient = new JSpinner();
        spinnerPortClient.setBounds(73, 45, 83, 28);
        spinnerPortClient.setValue(Def.NetworkDeafult.default_port);
        spinnerPortClient.setEditor(new JSpinner.NumberEditor(spinnerPortClient, "#"));
        tabClient.add(spinnerPortClient);
        
        btnConnectClient = new JButton("Connect");
        btnConnectClient.setBounds(169, 133, 115, 29);
        tabClient.add(btnConnectClient);
        
        btnConnectClient.addActionListener(new ConnectClientActionListener());
        
        btnDisconnectClient = new JButton("Disconnect");
        btnDisconnectClient.setEnabled(false);
        btnDisconnectClient.setBounds(296, 133, 115, 29);
        tabClient.add(btnDisconnectClient);
        btnDisconnectClient.addActionListener(new DisconnectActionListener());        
        lblErr = new JLabel("");
        lblErr.setBounds(16, 242, 417, 24);
        frame.getContentPane().add(lblErr);
        lblErr.setForeground(Color.RED);
        
        lblInfo = new JLabel("Status:");
        lblInfo.setBounds(16, 213, 417, 24);
        frame.getContentPane().add(lblInfo);

        taskTrayMenu = new PopupMenu();
        itemConnectClient = new MenuItem("Connect as Client");
        itemConnectClient.addActionListener(new ConnectClientActionListener());
        taskTrayMenu.add(itemConnectClient);
        itemConnectServer = new MenuItem("Connect as Server");
        itemConnectServer.addActionListener(new ConnectServerActionListener());
        taskTrayMenu.add(itemConnectServer);

//        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        manager.addKeyEventDispatcher(new CommandDispatcher());
        
        
        initialized = true;
    }
    
    private static void initializeTaskTray() {
        // トレイアイコン生成
    	if (iconImage != null) {
    		taskTrayIcon = new TrayIcon(iconImage);
            // イベント登録
            taskTrayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            taskTrayIcon.setPopupMenu(taskTrayMenu);
            taskTrayIcon.setImageAutoSize(true);
            
            try {
				SystemTray.getSystemTray().add(taskTrayIcon);
			} catch (AWTException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
    	}

    }

//    private class CommandDispatcher implements KeyEventDispatcher {
//        @Override
//        public boolean dispatchKeyEvent(KeyEvent e) {
//        	if (e.getKeyCode() == KeyEvent.VK_META) {
//	            if (e.getID() == KeyEvent.KEY_PRESSED) {
//	                System.out.println("Pressed");
//	                showClickCapturer();
//	            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
//	                System.out.println("Released");
//	                hideClickCapturer();
//	            } else if (e.getID() == KeyEvent.KEY_TYPED) {
//	                System.out.println("Typed");
//	            }
//        	}
//            return false;
//        }
//    }
    
    private void setEnabledAll(boolean value) {
        if (!initialized) return;
        tabbedPane.setEnabled(value);
        strAddressClient.setEnabled(value);
        spinnerPortServer.setEnabled(value);
        spinnerPortClient.setEnabled(value);
        btnConnectServer.setEnabled(value);
        btnDisconnectServer.setEnabled(value);
        btnConnectClient.setEnabled(value);
        btnDisconnectClient.setEnabled(value);
    }
    
    public static void setInfo(String msg) {
        if (!initialized) return;
        lblInfo.setText("Status: " + msg);        	
    }
    
    public static void setErr(String msg) {
        if (!initialized) return;
        if (msg == "") {
        	lblErr.setText("");
        } else {
            lblErr.setText("Error: " + msg);        	
        }
    }
    
    public static void showClickCapturer() {
    	translucent_frame.setVisible(true);
    }

    public static void hideClickCapturer() {
    	translucent_frame.setVisible(false);
    }
    
    public static boolean isCapturing() {
    	return translucent_frame.isVisible();
    }

    public static void notifyPress(MouseEvent e) {
    	System.out.println("clicked!");
    	if (connectionManager != null && connectionManager.getMode() == Mode.MODE_SERVER) {
    		Message click_msg = new Message(Message.Type.c);
    		click_msg.data = new String[1];
    		click_msg.data[0] = "" + e.getButton();
    		connectionManager.sendMessage(click_msg);
    	}
    }

    public static void notifyRelease(MouseEvent e) {
    	System.out.println("released!");
    	if (connectionManager != null && connectionManager.getMode() == Mode.MODE_SERVER) {
    		Message release_msg = new Message(Message.Type.r);
    		release_msg.data = new String[1];
    		release_msg.data[0] = "" + e.getButton();
    		connectionManager.sendMessage(release_msg);
    	}
    }
    
    public static void notifyWheelMove(MouseWheelEvent e) {
    	System.out.println("wheel!");
    	if (connectionManager != null && connectionManager.getMode() == Mode.MODE_SERVER) {
    		Message release_msg = new Message(Message.Type.w);
    		release_msg.data = new String[1];
    		release_msg.data[0] = "" + e.getWheelRotation();
    		connectionManager.sendMessage(release_msg);
    	}    	
    }

	public static void notifyMouseMove(Float pt) {
    	System.out.println("move!");
    	if (connectionManager != null && connectionManager.getMode() == Mode.MODE_SERVER) {
    		Message move_msg = new Message(Message.Type.p);
    		move_msg.data = new String[2];
    		move_msg.data[0] = "" + pt.x;
    		move_msg.data[1] = "" + pt.y;
    		connectionManager.sendMessage(move_msg);
    	}		
	}
	
	public static void updateClientList(){
		tableClientList.removeAll();
		
		LinkedList<User> users = User.getUsers();
		int cnt = 0;
		for (User u : users) {
			String[] rawdata = {Integer.toString(cnt), u.getName(), u.getKey().getText(), u.getAddress()};
			tableModel.addRow(rawdata);
			cnt++;
		}
	}
	
    private class ConnectClientActionListener implements ActionListener {
    	@Override
    	public void actionPerformed(ActionEvent e) {
        	setErr("");

        	ConnectionInfo info = new ConnectionInfo();
            info.address = strAddressClient.getText();
            info.port =  (Integer)(spinnerPortClient.getValue());
            info.timeout = 10000;
            info.mode = ConnectionManager.Mode.MODE_CLIENT;
            try {
                connectionManager = new ConnectionManager(info);
            } catch (IOException ex) {
                ex.printStackTrace();
                setErr(ex.getMessage());
                return;
            }
            setEnabledAll(false);
            btnDisconnectClient.setEnabled(true);
        }
    }
    
    private class ConnectServerActionListener implements ActionListener {
    	@Override
    	public void actionPerformed(ActionEvent e) {
        	setErr("");

        	ConnectionInfo info = new ConnectionInfo();
            info.address = "";
            info.port =  (Integer)(spinnerPortServer.getValue());
            info.timeout = 10000;
            info.mode = ConnectionManager.Mode.MODE_SERVER;
            try {
                connectionManager = new ConnectionManager(info);
            } catch (IOException ex) {
                ex.printStackTrace();
                setErr(ex.getMessage());
                return;
            }
            setEnabledAll(false);
            btnDisconnectServer.setEnabled(true);
        }
    }
    
    private class DisconnectActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            setErr("");
            if (connectionManager == null) {
                System.out.println("Error: connectionManager is null");
                return;
            }
            connectionManager.clear();
            connectionManager = null;
            setEnabledAll(true);
            btnDisconnectClient.setEnabled(false);
            btnDisconnectServer.setEnabled(false);
            setInfo("Disconnected.");
        }    	
    }
}
