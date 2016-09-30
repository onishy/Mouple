package jp.mouple.gui;

import java.util.ArrayList;
import java.util.Arrays;

import org.jnativehook.keyboard.NativeKeyEvent;

import jp.mouple.net.User;

public class SingleKeyBinder implements KeyBinder {
	private User m_super_user;
	private String m_name;
	private ArrayList<Integer> m_keys;
	private boolean m_status;
	private boolean m_status_changed;
	
	SingleKeyBinder(String name, ArrayList<Integer> keys) {
		m_name = name;
		m_keys = keys;
		m_status = false;
		m_status_changed = false;
		m_super_user = null;
	}
	
	@Override
	public void setUser(User u) {
		m_super_user = u;
	}

	@Override
	public boolean isActive() {
		updateStatus();
		return m_status;
	}

	@Override
	public synchronized void notifyStatus() {
		updateStatus();
		if (!m_status_changed) return;
		
		if (m_status) {
			m_super_user.keyPressed();
		} else {
			m_super_user.keyReleased();
		}
		m_status_changed = false;
	}
	
	@Override
	public String getText() {
		return m_name;
	}
	
	private synchronized void updateStatus() {
		// if any of the keys is pressed, the status will be true
		// if all keys are released, the status will be false
		boolean flag = false;
		for (Integer i : m_keys) {
			if (GlobalKeyObserver.getKeyStatus(i)) {
				flag = true;
				break;
			}
		}
		if (m_status != flag) {
			m_status = flag;
			m_status_changed = true;
		}
	}
	
	public static SingleKeyBinder CmdBinder = new SingleKeyBinder("Cmd", new ArrayList<Integer>(Arrays.asList(NativeKeyEvent.VC_META_L, NativeKeyEvent.VC_META_R)));
	public static SingleKeyBinder CtrlBinder = new SingleKeyBinder("Ctrl", new ArrayList<Integer>(Arrays.asList(NativeKeyEvent.VC_CONTROL_L, NativeKeyEvent.VC_CONTROL_R)));
	public static SingleKeyBinder ShiftBinder = new SingleKeyBinder("Shift", new ArrayList<Integer>(Arrays.asList(NativeKeyEvent.VC_SHIFT_L, NativeKeyEvent.VC_SHIFT_R)));
}
