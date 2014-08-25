package location;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * Представляет границу области локации.
 * 
 * @author Pokrovskaya Oksana
 */
public class Border extends Polygon {

	/**
	 * @param d
	 *            массив точек в порядке их соединения
	 */
	public Border(int[] x, int[] y) {
		// если есть не горизонтальные или не вертикальные отрезки
		// if (...) {
		// add exeption
		// }

		super(x, y, x.length);
	}

	public Border() {
		super();
	}

	/**
	 * Сообщает, лежит ли фрейм внутри контура.
	 */
	public boolean isInternal(Frame f) {
		return super.contains(f.getX(), f.getY());
	}

	/**
	 * Сообщает, лежит ли ячейка, определяемая координатами x1 y1 x2 y2, внутри
	 * контура.
	 */
	public boolean isInternal(double x1, double y1, double x2, double y2) {
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		return super.contains(x, y);
	}

	/**
	 * Сообщает, каким образом ограничена ячейка, определяемая координатами x1
	 * x2 y1 y2. [0] - сверху; [1] - снизу; [2] - справа; [3] - слева.
	 */
	public boolean[] isBordered(double x1, double y1, double x2, double y2) {
		PathIterator pi = this.getPathIterator(null);
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		float coords[] = new float[6];
		float prev[] = new float[2];
		float first[] = new float[2];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				prev[0] = coords[0];
				prev[1] = coords[1];
				first[0] = coords[0];
				first[1] = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				Line2D.Float line = new Line2D.Float(prev[0], prev[1],
						coords[0], coords[1]);
				// System.out.println(prev[0] + " " + prev[1] + " " +
				// coords[0] + " " + coords[1]);
				if (line.intersectsLine((x1 + x2) / 2, y1, (x1 + x2) / 2, y2)) {
					if (line.getY1() == line.getY2()) {
						if (line.getY1() == y1)
							up = true;
						if (line.getY2() == y2)
							down = true;
						// continue;
					}
				}
				if (line.intersectsLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2)) {
					if (line.getX1() == line.getX2()) {
						if (line.getX1() == x1)
							left = true;
						if (line.getX2() == x2)
							right = true;
						// continue;
					}
				}
				prev[0] = coords[0];
				prev[1] = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				// ignored
				break;
			case PathIterator.SEG_CUBICTO:
				// ignored
				break;
			case PathIterator.SEG_CLOSE:
				Line2D.Float line1 = new Line2D.Float(prev[0], prev[1],
						first[0], first[1]);
				// System.out.println("close " + prev[0] + " " + prev[1] + " " +
				// first[0] + " " + first[1]);
				if (line1.intersectsLine((x1 + x2) / 2, y1, (x1 + x2) / 2, y2)) {
					if (line1.getY1() == line1.getY2()) {
						if (line1.getY1() == y1)
							up = true;
						if (line1.getY2() == y2)
							down = true;
						// continue;
					}
				}
				if (line1.intersectsLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2)) {
					if (line1.getX1() == line1.getX2()) {
						if (line1.getX1() == x1)
							left = true;
						if (line1.getX2() == x2)
							right = true;
						// continue;
					}
				}
				break;
			}
			pi.next();
		}

		boolean[] b = new boolean[4];
		b[0] = up;
		b[1] = down;
		b[2] = right;
		b[3] = left;
		return b;
	}

	/**
	 * Сообщает,содержится ли данная точка в контуре
	 * 
	 * @param point
	 *            точка, принадлежность которой к контуру нужно проверить
	 * @return принадлежит ли точка контуру
	 */
	public boolean containsPoint(Point2D.Double point) {
		PathIterator pi = this.getPathIterator(null);
		boolean f = false;
		float coords[] = new float[6];
		float prev[] = new float[2];
		float first[] = new float[2];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				prev[0] = coords[0];
				prev[1] = coords[1];
				first[0] = coords[0];
				first[1] = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				Line2D.Float line = new Line2D.Float(prev[0], prev[1],
						coords[0], coords[1]);
				if (line.intersectsLine(new Line2D.Double(point, point))) {
					f = true;
				}
				prev[0] = coords[0];
				prev[1] = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				// ignored
				break;
			case PathIterator.SEG_CUBICTO:
				// ignored
				break;
			case PathIterator.SEG_CLOSE:
				Line2D.Float line1 = new Line2D.Float(prev[0], prev[1],
						first[0], first[1]);
				if (line1.intersectsLine(new Line2D.Double(point, point))) {
					f = true;
				}
				break;
			}
			pi.next();
		}
		return f;
	}

	/**
	 * Сообщает,каким линиям контура принадлежит точка
	 * 
	 * @param point
	 *            точка, принадлежность которой к определенной линии нужно
	 *            выяснить
	 * @return линии, которым принадлежит данная точка, или null, если таких
	 *         линий нет
	 */
	public Line2D.Float[] containingLine(Point2D.Double point) {
		PathIterator pi = this.getPathIterator(null);
		Line2D.Float[] l = new Line2D.Float[2];
		float coords[] = new float[6];
		float prev[] = new float[2];
		float first[] = new float[2];
		int i = 0;
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				prev[0] = coords[0];
				prev[1] = coords[1];
				first[0] = coords[0];
				first[1] = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				Line2D.Float line = new Line2D.Float(prev[0], prev[1],
						coords[0], coords[1]);
				if (line.intersectsLine(new Line2D.Double(point, point))) {
					l[i] = line;
					i++;
				}
				prev[0] = coords[0];
				prev[1] = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				// ignored
				break;
			case PathIterator.SEG_CUBICTO:
				// ignored
				break;
			case PathIterator.SEG_CLOSE:
				Line2D.Float line1 = new Line2D.Float(prev[0], prev[1],
						first[0], first[1]);
				if (line1.intersectsLine(new Line2D.Double(point, point))) {
					l[i] = line1;
					i++;
				}
				break;
			}
			pi.next();
		}
		if (i > 0)
			return l;
		else
			return null;
	}

	/**
	 * Добавляет в границу 2 точки, по порядку, между указанными двумя
	 * 
	 * @param point1
	 *            первая точка для вставки
	 * @param point2
	 *            вторая точка для вставки
	 * @param point2d
	 *            первая точка, между которыми надо вставить
	 * @param point2d2
	 *            вторая точка, между которыми надо вставить
	 */
	public void addPoints(Point2D.Double point1, Point2D.Double point2,
			Point2D point2d, Point2D point2d2) {
		int[] newXpoints = new int[npoints + 2];
		int[] newYpoints = new int[npoints + 2];
		boolean f = false;
		for (int i = 0; i < newXpoints.length; i++) {
			if (f) {
				newXpoints[i] = xpoints[i - 2];
				newYpoints[i] = ypoints[i - 2];
			} else {
				if (i == (xpoints.length - 1)) {
					if ((xpoints[i] == point2d.getX())
							&& (ypoints[i] == point2d.getY())
							&& (xpoints[0] == point2d2.getX())
							&& (ypoints[0] == point2d2.getY())) {
						newXpoints[i] = xpoints[i];
						newYpoints[i] = ypoints[i];
						i++;
						newXpoints[i] = (int) point1.getX();
						newYpoints[i] = (int) point1.getY();
						i++;
						newXpoints[i] = (int) point2.getX();
						newYpoints[i] = (int) point2.getY();
						xpoints = newXpoints;
						ypoints = newYpoints;
						npoints += 2;
						return;
					}
					break;
				} else if ((xpoints[i] == point2d.getX())
						&& (ypoints[i] == point2d.getY())
						&& (xpoints[i + 1] == point2d2.getX())
						&& (ypoints[i + 1] == point2d2.getY())) {
					newXpoints[i] = xpoints[i];
					newYpoints[i] = ypoints[i];
					i++;
					newXpoints[i] = (int) point1.getX();
					newYpoints[i] = (int) point1.getY();
					i++;
					newXpoints[i] = (int) point2.getX();
					newYpoints[i] = (int) point2.getY();
					f = true;
				} else {
					newXpoints[i] = xpoints[i];
					newYpoints[i] = ypoints[i];
				}
			}
		}
		if (!f) {
			int[] xp = new int[xpoints.length];
			int[] yp = new int[ypoints.length];
			for (int i = 0; i < xp.length; i++) {
				xp[i] = xpoints[xpoints.length - i - 1];
				yp[i] = ypoints[ypoints.length - i - 1];
			}
			xpoints = xp;
			ypoints = yp;
			for (int i = 0; i < newXpoints.length; i++) {
				if (f) {
					newXpoints[i] = xpoints[i - 2];
					newYpoints[i] = ypoints[i - 2];
				} else {
					if (i == (xpoints.length - 1)) {
						if ((xpoints[i] == point2d.getX())
								&& (ypoints[i] == point2d.getY())
								&& (xpoints[0] == point2d2.getX())
								&& (ypoints[0] == point2d2.getY())) {
							newXpoints[i] = xpoints[i];
							newYpoints[i] = ypoints[i];
							i++;
							newXpoints[i] = (int) point1.getX();
							newYpoints[i] = (int) point1.getY();
							i++;
							newXpoints[i] = (int) point2.getX();
							newYpoints[i] = (int) point2.getY();
							xpoints = newXpoints;
							ypoints = newYpoints;
							npoints += 2;
							return;
						}
						return;
					} else if ((xpoints[i] == point2d.getX())
							&& (ypoints[i] == point2d.getY())
							&& (xpoints[i + 1] == point2d2.getX())
							&& (ypoints[i + 1] == point2d2.getY())) {
						newXpoints[i] = xpoints[i];
						newYpoints[i] = ypoints[i];
						i++;
						newXpoints[i] = (int) point1.getX();
						newYpoints[i] = (int) point1.getY();
						i++;
						newXpoints[i] = (int) point2.getX();
						newYpoints[i] = (int) point2.getY();
						f = true;
					} else {
						newXpoints[i] = xpoints[i];
						newYpoints[i] = ypoints[i];
					}
				}
			}
		}
		xpoints = newXpoints;
		ypoints = newYpoints;
		npoints += 2;
	}
}
