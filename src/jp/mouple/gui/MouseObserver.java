package jp.mouple.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseObserver implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		MainWindow.notifyClick(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}