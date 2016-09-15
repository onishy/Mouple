package jp.mouple.core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

public class Display {
    public Display() {
    	m_size = Toolkit.getDefaultToolkit().getScreenSize();
    }

    public Dimension size() {
        return m_size;
    }

    public Point2D.Float toRatio(final Point pt) {
        Point2D.Float res = new Point2D.Float();

        res.x = (float)pt.x / m_size.width;
        res.y = (float)pt.y / m_size.height;

        return res;
    }
    private Dimension m_size;
};