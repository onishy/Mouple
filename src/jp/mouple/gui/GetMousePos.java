package jp.mouple.gui;

import java.awt.Point;
import java.awt.geom.Point2D;

import jp.mouple.core.Config;
import jp.mouple.core.GetData;
import jp.mouple.net.Message;

public class GetMousePos implements GetData {
    private Point m_old = null;
    
    @Override
    public Message func() {
    	if (!MainWindow.isCapturing()) {
    		return null;
    	}
    	
        Point pt_int = MouseController.getInstance().mousePosition();
        Point2D.Float pt_ratio = 
                Config.getInstance().getDisplay().toRatio(pt_int);
        Message res = new Message(Message.Type.p);
        String[] data = new String[2];
        if (m_old == null) {
            data[0] = "" + pt_ratio.x;
            data[1] = "" + pt_ratio.y;
            m_old = pt_int;
        } else if ((m_old.x != pt_int.x) || (m_old.y != pt_int.y)) {
            data[0] = "" + pt_ratio.x;
            data[1] = "" + pt_ratio.y;
            m_old = pt_int;
        } else {
            return null;
        }
        res.data = data;
        return res;
    }
}
