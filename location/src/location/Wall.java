package location;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ������������ �����.
 * 
 * @author Pokrovskaya Oksana
 */
public class Wall extends Line2D.Double {

	public Wall(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

}
