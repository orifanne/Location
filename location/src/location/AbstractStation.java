package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ������������ ������� �������.
 * 
 * @author Pokrovskaya Oksana
 */
public abstract class AbstractStation extends Point2D.Double {

	/** ��� ������� */
	protected String name = "station";

	/**
	 * @param x2
	 *            ��������
	 * @param y2
	 *            ��������
	 * @param name2
	 *            ��� �������
	 */
	public AbstractStation(double x2, double y2, String name2) {
		super(x2, y2);
		name = name2;
	}

	public AbstractStation() {
		super(0, 0);
	}

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 */
	public AbstractStation(double x, double y) {
		super(x, y);
	}

	/**
	 * ������������ ������� �������, ��������� ��������������� �������.
	 * 
	 * @param tail
	 *            ������, � ������� ���������� ���������� ������� �������
	 * @param sigma
	 *            ��������� ������ �������
	 * @param m
	 *            �����, � ������� ����� �������� ���������
	 * @param s
	 *            ������� ���� �������
	 */
	public abstract void explode(Tail tail, int sigma, Map m, double s);

	/**
	 * ������ ����� ������� �������, ��������� ��������������� �������, �
	 * ��������� �� � ������ ���� ������ ������� �������.
	 * 
	 * @param tails
	 *            ������, ��� ������� ���������� ��������� ����� ������� �������
	 * @param sigma
	 *            ��������� ������ �������
	 * @param name
	 *            ��� ����� �����
	 * @param s
	 *            ������� ���� �������
	 */
	public abstract void explode(ArrayList<Tail> tails, int sigma, String name,
			double s);

	/**
	 * ������������ ������� ������� � ������ ����� ���������, ���������
	 * ��������������� �������.
	 * 
	 * @param tail
	 *            ������, � ������� ���������� ���������� ������� �������
	 * @param plan
	 *            ���� ���������
	 * @param m
	 *            �����, � ������� ����� �������� ���������
	 * @param s
	 *            ������� ���� �������
	 */
	public abstract void explode(Tail tail, Plan plan, Map m, double s);

	/**
	 * ������ ����� ������� ������� � ������ ����� ���������, ���������
	 * ��������������� �������, � ��������� �� � ������ ���� ������ �������
	 * �������.
	 * 
	 * @param plan
	 *            ���� ���������
	 * @param name
	 *            ��� ����� �����
	 * @param s
	 *            ������� ���� �������
	 */
	public abstract void explode(Plan plan, String name, double s);

	/**
	 * ������ ����� ������� ������� � ������ ����� ���������, ������ �������, �
	 * ��������� �� � ������ ���� ������ ������� �������.
	 * 
	 * @param object
	 *            ��������������� ������
	 * @param plan
	 *            ���� ������
	 * @param num
	 *            ���������� ����� ���������������� (���������� ����� �������)
	 * @param name
	 *            ��� ����� �����
	 */
	public abstract void teach(PosObject object, Plan plan, int num, String name);

	/**
	 * �������� ���
	 * 
	 * @return ���
	 */
	public String getName() {
		return name;
	}

	/**
	 * ���������� ���
	 * 
	 * @param name
	 *            ���
	 */
	public void setName(String name) {
		this.name = name;
	}

}
