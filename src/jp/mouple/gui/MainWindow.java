package jp.mouple.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

import jp.mouple.net.*;
import javax.swing.JSpinner;
import java.awt.Color;

public class MainWindow {

	private static MainWindow window;
	private static boolean initialized = false;

	private JFrame frame;
	private static JTextField strAddress;
	private static JSpinner portSpinner;
	private static ButtonGroup selectMode = new ButtonGroup();
	private static JRadioButton rdbtnServer;
	private static JRadioButton rdbtnClient;
	private static JButton btnConnect;
	private static JButton btnDisconnect;
	private static JLabel lblInfo;
	private static JLabel lblErr;
	
	private static ConnectionManager connectionManager = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MainWindow();
					window.frame.setVisible(true);
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblRunAs = new JLabel("Run As");
		lblRunAs.setBounds(6, 6, 61, 16);
		frame.getContentPane().add(lblRunAs);
		
		rdbtnServer = new JRadioButton("Server");
		rdbtnServer.setSelected(true);
		selectMode.add(rdbtnServer);
		rdbtnServer.setBounds(16, 34, 141, 23);
		frame.getContentPane().add(rdbtnServer);
		
		rdbtnClient = new JRadioButton("Client");
		selectMode.add(rdbtnClient);
		rdbtnClient.setBounds(16, 70, 141, 23);
		frame.getContentPane().add(rdbtnClient);
		
		portSpinner = new JSpinner();
		portSpinner.setBounds(247, 32, 74, 28);
		portSpinner.setValue(8080);
		portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, "#"));
		frame.getContentPane().add(portSpinner);

		strAddress = new JTextField();
		strAddress.setBounds(247, 68, 134, 28);
		frame.getContentPane().add(strAddress);
		strAddress.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(169, 38, 61, 16);
		frame.getContentPane().add(lblPort);
		
		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(169, 74, 61, 16);
		frame.getContentPane().add(lblAddress);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setErr("");
				ConnectionInfo info = new ConnectionInfo();
				info.address = strAddress.getText();
				info.port =  (Integer)(portSpinner.getValue());
				info.timeout = 10000;
				if (selectMode.getSelection() == rdbtnClient.getModel()) {
					info.mode = ConnectionManager.Mode.MODE_CLIENT;
					try {
						connectionManager = new ConnectionManager(info);
					} catch (IOException ex) {
						ex.printStackTrace();
						setErr(ex.getMessage());
						return;
					}
				} else {
					info.mode = ConnectionManager.Mode.MODE_SERVER;
					try {
						connectionManager = new ConnectionManager(info);
					} catch (IOException ex) {
						ex.printStackTrace();
						setErr(ex.getMessage());
						return;
					}
				}
				setEnabledAll(false);
				btnDisconnect.setEnabled(true);
			}
		});
		btnConnect.setBounds(204, 243, 117, 29);
		frame.getContentPane().add(btnConnect);		
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setErr("");
				if (connectionManager == null) {
					System.out.println("Error: connectionManager is null");
					return;
				}
				connectionManager.clear();
				connectionManager = null;
				setEnabledAll(true);
				btnDisconnect.setEnabled(false);
				setInfo("Disconnected.");
			}
		});
		btnDisconnect.setEnabled(false);
		btnDisconnect.setBounds(327, 243, 117, 29);
		frame.getContentPane().add(btnDisconnect);
		
		lblInfo = new JLabel("Select Mode");
		lblInfo.setBounds(16, 157, 365, 16);
		frame.getContentPane().add(lblInfo);
		
		lblErr = new JLabel("");
		lblErr.setForeground(Color.RED);
		lblErr.setBounds(16, 199, 365, 16);
		frame.getContentPane().add(lblErr);
		
		initialized = true;
	}
	
	private void setEnabledAll(boolean value) {
		if (!initialized) return;
		strAddress.setEnabled(value);
		portSpinner.setEnabled(value);
		rdbtnServer.setEnabled(value);
		rdbtnClient.setEnabled(value);
		btnConnect.setEnabled(value);
		btnDisconnect.setEnabled(value);
	}
	
	public static void setInfo(String msg) {
		if (!initialized) return;
		lblInfo.setText(msg);
	}
	
	public static void setErr(String msg) {
		if (!initialized) return;
		lblErr.setText(msg);
	}
}