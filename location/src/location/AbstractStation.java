package location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ������������ ������� �������.
 * 
 * @author Pokrovskaya Oksana
 */
public abstract class AbstractStation {

	/** �������� */
	protected double x;
	/** �������� */
	protected double y;

	/** ������� ���� ������� */
	protected double s = 150;

	/** ��� ������� */
	protected String name = "station";

	/**
	 * ���� ����, ��� ��� ������� ��������� ����� ��� �������� (������� �������)
	 */
	protected boolean taught = false;

	public AbstractStation() {
		x = 0;
		y = 0;
	}

	
	
	
	
	/**
	 * @param x1
	 *            ��������
	 * @param y1
	 *            ��������
	 * @param s1
	 *            ������� ���� �������
	 */
	public AbstractStation(double x1, double y1, double s1) {
		x = x1;
		y = y1;
		s = s1;
	}

	/**
	 * @param x1
	 *            ��������
	 * @param y1
	 *            ��������
	 */
	public AbstractStation(double x1, double y1) {
		x = x1;
		y = y1;
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
		x = x2;
		y = y2;
		name = name2;
	}

	
	
	
	
	/**
	 * ������������ ������� �������.
	 * 
	 * @param tail
	 *            ������, � ������� ���������� ���������� ������� �������
	 */
	public abstract void explode(Tail tail);

	/**
	 * ������ ����� ������� �������.
	 * 
	 * @param tails
	 *            ������, ��� ������� ���������� ��������� ����� ������� �������
	 */
	public abstract void explode(ArrayList<Tail> tails);

	/**
	 * ������������ ������� ������� � ������ ����� ���������.
	 * 
	 * @param tail
	 *            ������, � ������� ���������� ���������� ������� �������
	 * @param plan
	 *            ���� ���������
	 */
	public abstract void explode(Tail tail, Plan plan);

	/**
	 * ������ ����� ������� ������� � ������ ����� ���������.
	 * 
	 * @param tails
	 *            ������, ��� ������� ���������� ��������� ����� ������� �������
	 * @param plan
	 *            ���� ���������
	 */
	public abstract void explode(ArrayList<Tail> tails, Plan plan);

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

	
	
	
	
	
	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}

	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}

	/** ������, ������� �� ������� */
	public boolean isTaught() {
		return taught;
	}

	/** ���������� ���� �������� */
	public void setTaught(boolean taught) {
		this.taught = taught;
	}

	@Override
	public String toString() {
		return name;
	}

	/** ���������� ��� */
	public String getName() {
		return name;
	}

	/** �������� ��� */
	public void setName(String name) {
		this.name = name;
	}

}
