package location;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

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
	 * @throws Exception
	 */
	public Tail(int x11, int y11, int x21, int y21) throws Exception {
		// ���� ���������� ����
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
	 *            �������� ������ �������� ���� (< x21)
	 * @param y11
	 *            �������� ������ �������� ���� (> y21)
	 * @param x21
	 *            �������� ������� ������� ����
	 * @param y21
	 *            �������� ������� ������� ����
	 */
	public Tail(double x11, double y11, double x21, double y21) {
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;

		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}

	/**
	 * ����������, �������� �� ������ �����.
	 * 
	 * @param p
	 *            �����
	 * @return true, ���� ��������, false �����
	 */
	public boolean contains(Point2D.Double p) {
		if ((x1 <= p.getX()) && (x2 >= p.getX()) && (y1 <= p.getY())
				&& (y2 >= p.getY()))
			return true;
		return false;
	}

	/**
	 * �������� �������� ������ �������� ����.
	 * 
	 * @return �������� ������ �������� ����
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * �������� �������� ������� ������� ����.
	 * 
	 * @return �������� ������� ������� ����
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * �������� �������� ������ �������� ����.
	 * 
	 * @return �������� ������ �������� ����
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * �������� �������� ������� ������� ����.
	 * 
	 * @return �������� ������� ������� ����
	 */
	public double getY2() {
		return y2;
	}

}
