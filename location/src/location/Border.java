package location;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Представляет границу области локации.
 * 
 * @author Pokrovskaya Oksana
 */
public class Border extends Polygon {

	/** Флаг того, что происходит перетаскивание участка границы. */
	private boolean dragging = false;

	/**
	 * Координаты первой точки, зафиксированной на границе (для отметки участка
	 * для перетаскивания)
	 */
	Point2D.Double firstCheckPoint = null;

	/**
	 * Координаты второй точки, зафиксированной на границе (для отметки участка
	 * для перетаскивания)
	 */
	Point2D.Double secondCheckPoint = null;

	/**
	 * Координаты первой точки, зафиксированной на границе (для перетаскивания)
	 */
	Point2D.Double firstDraggingPoint = null;

	/**
	 * Координаты второй точки, зафиксированной на границе (для перетаскивания)
	 */
	Point2D.Double secondDraggingPoint = null;

	/**
	 * @param x
	 *            массив абсцисс точек в порядке их соединения
	 * @param y
	 *            массив ординат точек в порядке их соединения
	 */
	public Border(int[] x, int[] y) {
		super(x, y, x.length);
		firstCheckPoint = null;
		secondCheckPoint = null;
		firstDraggingPoint = null;
		secondDraggingPoint = null;
	}

	/**
	 * Устанавливает в null все точки, связанные с перетаскиванием участка
	 * границы. (firstCheckPoint, secondCheckPoint, firstDraggingPoint,
	 * secondDraggingPoint)
	 */
	public void setNullPoints() {
		firstCheckPoint = null;
		secondCheckPoint = null;
		firstDraggingPoint = null;
		secondDraggingPoint = null;
	}

	/**
	 * Осуществляет перетаскивание участка границы.
	 * 
	 * @param x
	 *            абсцисса точки, к которой нужно перетащить
	 * @param y
	 *            ордината точки, к которой нужно перетащить
	 */
	public void drag(int x, int y) {
		if (dragging) {
			boolean f = false;
			if ((x >= 0) && (y >= 0)) {
				// вертикальный отрезок
				if (firstCheckPoint.getX() == secondCheckPoint.getX()) {
					firstDraggingPoint.setLocation(x, firstCheckPoint.getY());
					secondDraggingPoint.setLocation(x, secondCheckPoint.getY());
					if ((x) != firstCheckPoint.getX()) {
						Line2D.Float line = checkLine(firstCheckPoint,
								secondCheckPoint);
						if (firstCheckPoint.getY() > secondCheckPoint.getY()) {
							if (line.getY1() > line.getY2()) {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP1(),
										line.getP2());
							} else {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP2(),
										line.getP1());
							}
						} else {
							if (line.getY1() > line.getY2()) {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP2(),
										line.getP1());
							} else {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP1(),
										line.getP2());
							}
						}
						if (f)
							f = addPoints(firstDraggingPoint,
									secondDraggingPoint, firstCheckPoint,
									secondCheckPoint);
						else {
							dragging = false;
							firstCheckPoint = null;
							secondCheckPoint = null;
							firstDraggingPoint = null;
							secondDraggingPoint = null;
							return;
						}
						if (f) {
							firstCheckPoint = (Point2D.Double) firstDraggingPoint
									.clone();
							secondCheckPoint = (Point2D.Double) secondDraggingPoint
									.clone();
						} else {
							dragging = false;
							deletePoint(firstCheckPoint);
							deletePoint(secondCheckPoint);
							firstCheckPoint = null;
							secondCheckPoint = null;
							firstDraggingPoint = null;
							secondDraggingPoint = null;
							return;
						}
					}
				}
				// горизонтальный отрезок
				if (firstCheckPoint.getY() == secondCheckPoint.getY()) {
					firstDraggingPoint.setLocation(firstCheckPoint.getX(), y);
					secondDraggingPoint.setLocation(secondCheckPoint.getX(), y);
					if ((y) != firstCheckPoint.getY()) {
						Line2D.Float line = checkLine(firstCheckPoint,
								secondCheckPoint);
						if (firstCheckPoint.getX() > secondCheckPoint.getX()) {
							if (line.getX1() > line.getX2()) {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP1(),
										line.getP2());
							} else {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP2(),
										line.getP1());
							}
						} else {
							if (line.getX1() > line.getX2()) {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP2(),
										line.getP1());
							} else {
								f = addPoints(firstCheckPoint,
										secondCheckPoint, line.getP1(),
										line.getP2());
							}
						}
						if (f)
							f = addPoints(firstDraggingPoint,
									secondDraggingPoint, firstCheckPoint,
									secondCheckPoint);
						else {
							dragging = false;
							firstCheckPoint = null;
							secondCheckPoint = null;
							firstDraggingPoint = null;
							secondDraggingPoint = null;
							return;
						}
						if (f) {
							firstCheckPoint = (Point2D.Double) firstDraggingPoint
									.clone();
							secondCheckPoint = (Point2D.Double) secondDraggingPoint
									.clone();
						} else {
							dragging = false;
							deletePoint(firstCheckPoint);
							deletePoint(secondCheckPoint);
							firstCheckPoint = null;
							secondCheckPoint = null;
							firstDraggingPoint = null;
							secondDraggingPoint = null;
							return;
						}
					}
				}
				if (f)
					deleteWrongPoints();
			}
		}
	}

	/**
	 * Прекращает перетаскивание участка границы.
	 */
	public void stopDragging() {
		if (dragging) {
			firstCheckPoint = null;
			secondCheckPoint = null;
			firstDraggingPoint = null;
			secondDraggingPoint = null;
			dragging = false;
			deleteWrongPoints();
		}
	}

	/**
	 * Пытается установить firstDraggingPoint или SecondDraggingPoint, в
	 * зависимости от текущего состояния и параметров.
	 * 
	 * @param x
	 *            абсцисса точки, претендующей на то, чтобы быть
	 *            firstDraggingPoint или SecondDraggingPoint
	 * @param y
	 *            ордината точки, претендующей на то, чтобы быть
	 *            firstDraggingPoint или SecondDraggingPoint
	 */
	public void setDraggingPoints(int x, int y) {
		if ((firstCheckPoint != null) && (secondCheckPoint != null)) {
			Point2D.Double p = new Point2D.Double(x, y);
			Line2D.Float l = new Line2D.Float(firstCheckPoint, secondCheckPoint);
			if (l.intersectsLine(new Line2D.Float(p, p))) {
				dragging = true;
				firstDraggingPoint = (Point2D.Double) firstCheckPoint.clone();
				secondDraggingPoint = (Point2D.Double) secondCheckPoint.clone();
			}
		}
	}

	/**
	 * Пытается установить firstCheckPoint или SecondCheckPoint, в зависимости
	 * от текущего состояния и параметров.
	 * 
	 * @param x
	 *            абсцисса точки, претендующей на то, чтобы быть firstCheckPoint
	 *            или SecondCheckPoint
	 * @param y
	 *            ордината точки, претендующей на то, чтобы быть firstCheckPoint
	 *            или SecondCheckPoint
	 */
	public void setCheckPoints(int x, int y) {
		if (!dragging) {
			Point2D.Double p = new Point2D.Double(x, y);
			if (containingLine(p) != null) {
				if (firstCheckPoint == null)
					firstCheckPoint = p;
				else {
					if (checkLine(firstCheckPoint, p) != null)
						secondCheckPoint = p;
				}
			} else {
				firstCheckPoint = null;
				secondCheckPoint = null;
			}
		}
	}

	/**
	 * Сообщает, лежит ли фрейм внутри границы.
	 * 
	 * @param f
	 *            фрейм, принадлежность которого границе надо проверить
	 * @return true - фрейм лежит внутри границы, false иначе
	 */
	public boolean isInternal(Frame f) {
		PathIterator pi = this.getPathIterator(null);
		int i = 0;
		float coords[] = new float[6];
		float prev[] = new float[2];
		float first[] = new float[2];
		Line2D.Float l = new Line2D.Float((float) f.getX(), (float) f.getY(),
				java.lang.Float.MAX_VALUE, (float) f.getY());
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
				if (!isDote(line))
					if (line.intersectsLine(l)) {
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
				if (!isDote(line1))
					if (line1.intersectsLine(l)) {
						i++;
					}
				break;
			}
			pi.next();
		}
		if (i % 2 == 0)
			return false;
		else
			return true;
	}

	/**
	 * Сообщает, каким образом ограничена ячейка, определяемая координатами x1
	 * x2 y1 y2
	 * 
	 * @param x1
	 *            абсцисса левого верхнего угла ячейки
	 * @param y1
	 *            ордината левого верхнего угла ячейки
	 * @param x2
	 *            абсцисса правого нижнего угла ячейки
	 * @param y2
	 *            ордината правого нижнего угла ячейки
	 * @return каким образом ограничена ячейка ([0] - сверху; [1] - снизу; [2] -
	 *         справа; [3] - слева)
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
	 * @return true точка принадлежит контуру, false иначе
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
				if (!isDote(line))
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
				if (!isDote(line1))
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
	 * @return true, если точки были вставлены, false иначе
	 */
	public boolean addPoints(Point2D.Double point1, Point2D.Double point2,
			Point2D point2d, Point2D point2d2) {
		if (!check(point1, point2, point2d, point2d2)) {
			return false;
		}
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
						return true;
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
							return true;
						}
						return false;
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
		return true;
	}

	/**
	 * Проверяет, явяется ли корректным добавление в грницу двух точек, между
	 * указанными двумя.
	 * 
	 * @param point1
	 *            первая точка для вставки
	 * @param point2
	 *            вторая точка для вставки
	 * @param point2d
	 *            первая точка, между которыми надо вставить
	 * @param point2d2
	 *            вторая точка, между которыми надо вставить
	 * @return false - некорректно, true - корректно
	 */
	private boolean check(Point2D.Double point1, Point2D.Double point2,
			Point2D point2d, Point2D point2d2) {
		PathIterator pi = this.getPathIterator(null);
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
				if (!isDote(line)) {
					Line2D.Float l = new Line2D.Float(point2d, point2d2);
					if (!l.intersectsLine(new Line2D.Float(point1, point2))) {
						if (line.intersectsLine(new Line2D.Float(point1, point1))
								&& line.intersectsLine(new Line2D.Float(point2,
										point2)))
							return false;
						if (point1.getX() == point2.getX())
							if (line.intersectsLine(new Line2D.Float(point1,
									point2))
									&& (line.getY1() == line.getY2())
									&& (line.getY1() != point1.getY())
									&& (line.getY1() != point2.getY()))
								return false;
						if (point1.getY() == point2.getY())
							if (line.intersectsLine(new Line2D.Float(point1,
									point2))
									&& (line.getX1() == line.getX2())
									&& (line.getX1() != point1.getX())
									&& (line.getX1() != point2.getX()))
								return false;
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
				if (!isDote(line1)) {
					Line2D.Float l = new Line2D.Float(point2d, point2d2);
					if (!l.intersectsLine(new Line2D.Float(point1, point2))) {
						if (line1.intersectsLine(new Line2D.Float(point1,
								point2)))
							return false;
						if (point1.getX() == point2.getX())
							if (line1.intersectsLine(new Line2D.Float(point1,
									point2))
									&& (line1.getY1() == line1.getY2())
									&& (line1.getY1() != point1.getY())
									&& (line1.getY1() != point2.getY()))
								return false;
						if (point1.getY() == point2.getY())
							if (line1.intersectsLine(new Line2D.Float(point1,
									point2))
									&& (line1.getX1() == line1.getX2())
									&& (line1.getX1() != point1.getX())
									&& (line1.getX1() != point2.getX()))
								return false;
					}
				}
				break;
			}
			pi.next();
		}
		return true;
	}

	/**
	 * Убирает ненужные точки (те, что лежат не в прямых углах границы)
	 */
	public void deleteWrongPoints() {
		ArrayList<Point2D.Double> p = new ArrayList<Point2D.Double>();
		int k = 1;
		for (int i = 0; i < xpoints.length - k; i++) {
			if ((xpoints[i] == xpoints[i + 1])
					&& (ypoints[i] == ypoints[i + 1])) {
				for (int j = i + 1; j < xpoints.length - 1; j++) {
					xpoints[j] = xpoints[j + 1];
					ypoints[j] = ypoints[j + 1];
				}
				k++;
			}
		}
		if ((xpoints[xpoints.length - k] == xpoints[0])
				&& (ypoints[xpoints.length - k] == ypoints[0]))
			k++;
		int[] x = new int[xpoints.length - k + 1];
		int[] y = new int[xpoints.length - k + 1];
		for (int i = 0; i < x.length; i++) {
			x[i] = xpoints[i];
			y[i] = ypoints[i];
		}
		xpoints = x;
		ypoints = y;
		npoints = xpoints.length;
		PathIterator pi = this.getPathIterator(null);
		float coords[] = new float[6];
		float prev[] = new float[2];
		float first[] = new float[2];
		Line2D.Float prevLine = null;
		Line2D.Float firstLine = null;
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
				if (prevLine != null) {
					if ((new Line2D.Float(prevLine.getP1(), line.getP2())
							.intersectsLine(new Line2D.Float(prevLine.getP2(),
									prevLine.getP2())))
							|| (line.intersectsLine(new Line2D.Float(prevLine
									.getP1(), prevLine.getP1())))
							|| (prevLine.intersectsLine(new Line2D.Float(line
									.getP2(), line.getP2()))))
						p.add(new Point2D.Double(prevLine.getX2(), prevLine
								.getY2()));
				} else
					firstLine = line;
				prev[0] = coords[0];
				prev[1] = coords[1];
				prevLine = (Float) line.clone();
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
				if (prevLine != null) {
					if ((new Line2D.Float(prevLine.getP1(), line1.getP2())
							.intersectsLine(new Line2D.Float(prevLine.getP2(),
									prevLine.getP2())))
							|| (line1.intersectsLine(new Line2D.Float(prevLine
									.getP1(), prevLine.getP1())))
							|| (prevLine.intersectsLine(new Line2D.Float(line1
									.getP2(), line1.getP2()))))
						p.add(new Point2D.Double(prevLine.getX2(), prevLine
								.getY2()));
					prevLine = line1;
				}
				break;
			}
			pi.next();
		}
		if ((new Line2D.Float(prevLine.getP1(), firstLine.getP2())
				.intersectsLine(new Line2D.Float(prevLine.getP2(), prevLine
						.getP2())))
				|| (firstLine.intersectsLine(new Line2D.Float(prevLine.getP1(),
						prevLine.getP1())))
				|| (prevLine.intersectsLine(new Line2D.Float(firstLine.getP2(),
						firstLine.getP2()))))
			p.add(new Point2D.Double(prevLine.getX2(), prevLine.getY2()));
		if (p.size() == 0)
			return;
		else {
			for (int i = 0; i < p.size(); i++) {
				deletePoint(p.get(i));
			}
			deleteWrongPoints();
		}
	}

	/**
	 * Удаляет точку границы
	 * 
	 * @param p
	 *            точка, которую нужно удалить
	 */
	public void deletePoint(Point2D.Double p) {
		boolean f = false;
		for (int i = 0; i < npoints - 1; i++) {
			if (f) {
				xpoints[i] = xpoints[i + 1];
				ypoints[i] = ypoints[i + 1];
			} else {
				if ((xpoints[i] == p.getX()) && (ypoints[i] == p.getY())) {
					f = true;
					i--;
				}
			}
		}
		if ((xpoints[npoints - 1] == p.getX())
				&& (ypoints[npoints - 1] == p.getY()))
			f = true;
		if (f) {
			int[] x = new int[npoints - 1];
			int[] y = new int[npoints - 1];
			for (int i = 0; i < x.length; i++) {
				x[i] = xpoints[i];
				y[i] = ypoints[i];
			}
			xpoints = x;
			ypoints = y;
			npoints--;
		}
	}

	/**
	 * Получить линию, которой одновременно принадлежат обе точки.
	 * 
	 * @param p1
	 *            первая точка
	 * @param p2
	 *            вторая точка
	 * @return линия, которой принадлежат обе точки, или null
	 */
	public Line2D.Float checkLine(Point2D.Double p1, Point2D.Double p2) {
		Line2D.Float l = null;
		Line2D.Float[] l1 = containingLine(p1);
		Line2D.Float[] l2 = containingLine(p2);
		if ((l1 == null) || (l2 == null))
			return null;
		if ((l1[0] != null) && !isDote(l1[0]))
			if ((l1[0].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l1[0].intersectsLine(new Line2D.Float(p2, p2))))
				l = l1[0];
		if ((l2[0] != null) && !isDote(l2[0]))
			if ((l2[0].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l2[0].intersectsLine(new Line2D.Float(p2, p2))))
				l = l2[0];
		if ((l2[1] != null) && !isDote(l2[1]))
			if ((l2[1].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l2[1].intersectsLine(new Line2D.Float(p2, p2))))
				l = l2[1];
		if ((l1[1] != null) && !isDote(l1[1]))
			if ((l1[1].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l1[1].intersectsLine(new Line2D.Float(p2, p2))))
				l = l1[1];
		return l;
	}

	private boolean isDote(Line2D.Float l1) {
		if ((l1.getX1() == l1.getX2()) && (l1.getY1() == l1.getY2()))
			return true;
		else
			return false;
	}

	/**
	 * Получить первую точку, зафиксированную на границе (для перетаскивания)
	 * 
	 * @return первая точка
	 */
	public Point2D.Double getFirstCheckPoint() {
		return firstCheckPoint;
	}

	/**
	 * Получить вторую точку, зафиксированную на границе (для перетаскивания)
	 * 
	 * @return вторая точка
	 */
	public Point2D.Double getSecondCheckPoint() {
		return secondCheckPoint;
	}
}
