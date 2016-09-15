package jp.mouple.gui;

import java.awt.Point;
import java.awt.geom.Point2D;

import jp.mouple.core.Config;
import jp.mouple.core.GetData;

public class GetMousePos implements GetData {
	private Point m_old = null;
	
	@Override
	public String func() {
		Point pt_int = MouseController.getInstance().mousePosition();
		Point2D.Float pt_ratio = 
				Config.getInstance().getDisplay().toRatio(pt_int);
		String res = "";
		if (m_old == null) {
			res = pt_ratio.x + "," + pt_ratio.y;
			m_old = pt_int;
		} else if ((m_old.x != pt_int.x) || (m_old.y != pt_int.y)) {
			res = pt_ratio.x + "," + pt_ratio.y;
			m_old = pt_int;
		}
		return res;
	}
}
