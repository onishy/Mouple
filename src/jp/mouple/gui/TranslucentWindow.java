package jp.mouple.gui;

import java.awt.*;

import javax.swing.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class TranslucentWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	public TranslucentWindow() {
        super("ShapedWindow");
        setLayout(new GridBagLayout());

        GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setVisible(true);
        
        MouseObserver observer = new MouseObserver(getSize());
        addMouseListener(observer);
        addMouseMotionListener(observer);
        addMouseWheelListener(observer);
        setVisible(false);

        if (gd.isWindowTranslucencySupported(TRANSLUCENT)) {
            setOpacity(0.3f);
        }
    }
}