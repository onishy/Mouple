package jp.mouple.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import jp.mouple.core.Callback;
import jp.mouple.core.Config;
import jp.mouple.net.Message;;

public class InterpretMessage implements Callback {

    @Override
    public void func(Message msg) {
        if (msg.getType() == Message.Type.p) {
            String[] data = msg.data;
            if (data.length != 2) {
                System.out.println("data size is invalid");
            }
            Point2D.Float pt_ratio = new Point2D.Float();
            pt_ratio.x = Float.parseFloat(data[0]);
            pt_ratio.y = Float.parseFloat(data[1]);
            
            Point pt_int = new Point();
            Dimension disp_dim = Config.getInstance().getDisplay().size();
            pt_int.x = (int)(pt_ratio.x * disp_dim.width);
            pt_int.y = (int)(pt_ratio.y * disp_dim.height);
            
            MouseController.getInstance().mouseMove(pt_int);
            System.out.println("TargetPt: " + pt_int.x + "," + pt_int.y + "\n");
        } else if (msg.getType() == Message.Type.c) {
            // Click
            if (msg.data.length != 1) {
                System.out.println("data size is invalid");
            }
            int button = Integer.parseInt(msg.data[0]);
            MouseController.getInstance().mouseClick(button);
        } else if (msg.getType() == Message.Type.m) {
            // Message
        }
    }
}
