package location;

public class AbstractTail {

	/** Абсцисса центра */
	protected double x;
	/** Ордината центра */
	protected double y;

	public AbstractTail() {
	}

	/**
	 * @param x1
	 *            абсцисса
	 * @param y1
	 *            ордината
	 */
	public AbstractTail(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
