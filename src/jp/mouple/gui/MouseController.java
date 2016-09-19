package jp.mouple.gui;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseController {
    private static MouseController m_instance = new MouseController();
    private Robot m_robot = null;
    
    private MouseController() {
        try{
            m_robot = new Robot();
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }
    
    public static MouseController getInstance() {
        return m_instance;
    }
    
    public void mouseMove(Point pt) {
        m_robot.mouseMove(pt.x, pt.y);
    }
    
    public Point mousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
    
    public void mousePress(int button) {
        m_robot.mousePress(InputEvent.getMaskForButton(button));
    }
    
    public void mouseRelease(int button) {
    	m_robot.mouseRelease(InputEvent.getMaskForButton(button));
    }
    
    public void mouseWheel(int amt) {
    	m_robot.mouseWheel(amt);
    }
}
