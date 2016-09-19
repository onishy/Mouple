package jp.mouple.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

public class MouseObserver implements MouseListener, MouseMotionListener, MouseWheelListener {
	private Dimension m_window_size;
	
	MouseObserver (Dimension window_size) {
		System.out.println("Window Size: " + window_size.width + " x " + window_size.height);
		m_window_size = window_size;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		MainWindow.notifyPress(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		MainWindow.notifyRelease(e);		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		MainWindow.notifyWheelMove(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D.Float ratio = new Point2D.Float((float)e.getX() / m_window_size.width, (float)e.getY() / m_window_size.height);
		MainWindow.notifyMouseMove(ratio);		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point2D.Float ratio = new Point2D.Float((float)e.getX() / m_window_size.width, (float)e.getY() / m_window_size.height);
		MainWindow.notifyMouseMove(ratio);
	}	
}