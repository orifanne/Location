package location;

/**
 * ѕредставл€ет €чейку.
 * 
 * @author Pokrovskaya Oksana
 */
public class Tail extends AbstractTail {

	/** јбсцисса левого верхнего угла */
	protected double x1;
	/** ќрдината левого верхнего угла */
	protected double y1;
	/** јбсцисса правого нижнего угла */
	protected double x2;
	/** ќрдината правого нижнего угла */
	protected double y2;

	public Tail() {
		x1 = 0;
		x2 = 0;
		y1 = 0;
		y2 = 0;
	}

	/**
	 * @param x11
	 *            абсцисса левого верхнего угла (< x21)
	 * @param y11
	 *            ордината левого верхнего угла (> y21)
	 * @param x21
	 *            абсцисса правого нижнего угла
	 * @param y21
	 *            ордината правого нижнего угла
	 * @throws Exception
	 */
	public Tail(int x11, int y11, int x21, int y21) throws Exception {
		// если перепутаны углы
		if ((x21 <= x11) || (y11 <= y21)) {
			throw new Exception("Wrong points order");
		}
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;

		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}

	/**
	 * @param x11
	 *            абсцисса левого верхнего угла (< x21)
	 * @param y11
	 *            ордината левого верхнего угла (> y21)
	 * @param x21
	 *            абсцисса правого нижнего угла
	 * @param y21
	 *            ордината правого нижнего угла
	 * @throws Exception
	 */
	public Tail(double x11, double y11, double x21, double y21)
			throws Exception {
		// если перепутаны углы
		if ((x21 <= x11) || (y11 <= y21)) {
			throw new Exception("Wrong points order");
		}
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;

		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}

	/**
	 * ѕолучить абсциссу левого верхнего угла.
	 * 
	 * @return абсцисса левого верхнего угла
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * ѕолучить абсциссу правого нижнего угла.
	 * 
	 * @return абсцисса правого нижнего угла
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * ѕолучить ординату левого верхнего угла.
	 * 
	 * @return ордината левого верхнего угла
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * ѕолучить ординату правого нижнего угла.
	 * 
	 * @return ордината правого нижнего угла
	 */
	public double getY2() {
		return y2;
	}

}
