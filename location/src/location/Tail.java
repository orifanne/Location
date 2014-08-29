package location;

/**
 * ������������ ������.
 * 
 * @author Pokrovskaya Oksana
 */
public class Tail extends AbstractTail {

	/** �������� ������ �������� ���� */
	protected double x1;
	/** �������� ������ �������� ���� */
	protected double y1;
	/** �������� ������� ������� ���� */
	protected double x2;
	/** �������� ������� ������� ���� */
	protected double y2;

	
	
	
	
	public Tail() {
		x1 = 0;
		x2 = 0;
		y1 = 0;
		y2 = 0;
	}
	
	/**
	 * @param x11
	 *            �������� ������ �������� ���� (< x21)
	 * @param y11
	 *            �������� ������ �������� ���� (> y21)
	 * @param x21
	 *            �������� ������� ������� ����
	 * @param y21
	 *            �������� ������� ������� ����
	 */
	public Tail(int x11, int y11, int x21, int y21) {
		// ���� ���������� ����
		if ((x21 <= x11) || (y11 <= y21)) {
			// add exeption
		}
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;

		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}

	public Tail(double x11, double y11, double x21, double y21) {
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;

		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}
	
	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tail other = (Tail) obj;
		if (Double.doubleToLongBits(x1) != Double.doubleToLongBits(other.x1))
			return false;
		if (Double.doubleToLongBits(x2) != Double.doubleToLongBits(other.x2))
			return false;
		if (Double.doubleToLongBits(y1) != Double.doubleToLongBits(other.y1))
			return false;
		if (Double.doubleToLongBits(y2) != Double.doubleToLongBits(other.y2))
			return false;
		return true;
	}

	/**
	 * �������� �������� ������ �������� ����.
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * �������� �������� ������� ������� ����.
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * �������� �������� ������ �������� ����.
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * �������� �������� ������� ������� ����.
	 */
	public double getY2() {
		return y2;
	}

	/**
	 * �������� �������� ������.
	 */
	public double getY() {
		return y;
	}

	/**
	 * �������� �������� ������.
	 */
	public double getX() {
		return x;
	}

}
