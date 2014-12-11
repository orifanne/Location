package location;

import java.awt.geom.Point2D;

/**
 * Представляет абстрактную ячейку. Не несет функциональности, введен для
 * ясности модели классов. (Скореевсего, внесение функциональности в этот класс
 * может потребоваться в будущем)
 * 
 * @author Pokrovskaya Oksana
 */

public class AbstractTail extends Point2D.Double {
	public Point2D.Double getLocation() {
		return new Point2D.Double(this.x, this.y);
	}
}
