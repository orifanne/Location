package location;

public class AbstractTail {

	/** �������� ������ */
	protected double x;
	/** �������� ������ */
	protected double y;

	public AbstractTail() {
	}

	/**
	 * @param x1
	 *            ��������
	 * @param y1
	 *            ��������
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
