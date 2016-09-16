package jp.mouple.gui;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

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
    
    public void mouseClick(int button) {
        m_robot.mousePress(button);
    }
}
