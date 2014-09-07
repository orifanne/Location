package location;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Представляет стену.
 * 
 * @author Pokrovskaya Oksana
 */
public class Wall extends Line2D.Double {

	/**
	 * @param x1
	 *            абсцисса начала
	 * @param y1
	 *            ордината начала
	 * @param x2
	 *            абсцисса конца
	 * @param y2
	 *            ордината конца
	 */
	public Wall(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

}
