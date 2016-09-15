package jp.mouple.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyObserver implements KeyListener {
	
	private boolean cmd_pressed = false;

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		if (keycode == KeyEvent.VK_META) {
			cmd_pressed = true;
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		int keycode = e.getKeyCode();
		if (keycode == KeyEvent.VK_META) {
			cmd_pressed = false;
		}
	}
	
	public synchronized boolean isPressed() {
		return cmd_pressed;
	}

}
