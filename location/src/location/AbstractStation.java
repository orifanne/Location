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

	/** ������� ���� ������� */
	protected double s = 150;

	/** ��� ������� */
	protected String name = "station";

	/**
	 * ���� ����, ��� ��� ������� ��������� ����� ��� �������� (������� �������)
	 */
	protected boolean taught = false;

	/**
	 * @param x1
	 *            ��������
	 * @param y1
	 *            ��������
	 * @param s1
	 *            ������� ���� �������
	 */
	public AbstractStation(double x1, double y1, double s1) {
		super(x1, y1);
		s = s1;
	}

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

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 * @param name2
	 *            ��� �������
	 * @param s2
	 *            ������� ������� �������
	 */
	public AbstractStation(double x, double y, String name2, double s2) {
		super(x, y);
		name = name2;
		s = s2;
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
	 * ������������ ������� �������.
	 * 
	 * @param tail
	 *            ������, � ������� ���������� ���������� ������� �������
	 * @param sigma
	 *            ��������� ������ �������
	 */
	public abstract void explode(Tail tail, int sigma);

	/**
	 * ������ ����� ������� �������.
	 * 
	 * @param tails
	 *            ������, ��� ������� ���������� ��������� ����� ������� �������
	 * @param sigma
	 *            ��������� ������ �������
	 */
	public abstract void explode(ArrayList<Tail> tails, int sigma);

	/**
	 * ������������ ������� ������� � ������ ����� ���������.
	 * 
	 * @param tail
	 *            ������, � ������� ���������� ���������� ������� �������
	 * @param plan
	 *            ���� ���������
	 * @param sigma
	 *            ��������� ������ �������
	 */
	public abstract void explode(Tail tail, Plan plan, int sigma);

	/**
	 * ������ ����� ������� ������� � ������ ����� ���������.
	 * 
	 * @param tails
	 *            ������, ��� ������� ���������� ��������� ����� ������� �������
	 * @param plan
	 *            ���� ���������
	 * @param sigma
	 *            ��������� ������ �������
	 */
	public abstract void explode(ArrayList<Tail> tails, Plan plan, int sigma);

	/**
	 * ������� �������.
	 * 
	 * @param object
	 *            ��������������� ������
	 * @param plan
	 *            ���� ������
	 * @param num
	 *            ���������� ����� ���������������� (���������� ����� �������)
	 */
	public abstract void teach(PosObject object, Plan plan, int num);

	/**
	 * ������, ������� �� �������
	 * 
	 * @return true - ������� �������, false - �� �������
	 */
	public boolean isTaught() {
		return taught;
	}

	/**
	 * ���������� ���� ��������
	 * 
	 * @param taught
	 *            �������� �����
	 */
	public void setTaught(boolean taught) {
		this.taught = taught;
	}

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

	/**
	 * �������� ������� ������� �������
	 * 
	 * @return ������� ������� �������
	 */
	public double getS() {
		return s;
	}

	/**
	 * ���������� ������� ������� �������
	 * 
	 * @param s
	 *            ������� ������� �������
	 */
	public void setS(double s) {
		this.s = s;
	}

}
