package location;

import java.util.HashMap;

/** 
* ������������ ������� �������. 
* @author Pokrovskaya Oksana
*/
public abstract class AbstractStation {

	/** �������� */
	protected double x;
	/** �������� */
	protected double y;

	/** ������� ���� ������� */
	private double s;
	
	/** ����� ������� ������� */
	HashMap<Tail, Law> map;

	public AbstractStation() {
		x = 0;
		y = 0;
		s = 0;
		map = new HashMap<Tail, Law>();
	}

	/** 
	* @param x1 �������� 
	* @param y1 �������� 
	* @param s1 ������� ���� �������
	*/
	public AbstractStation(double x1, double y1, double s1) {
		x = x1;
		y = y1;
		s = s1;
		map = new HashMap<Tail, Law>();
	}
	
	/** 
	* @param x1 �������� 
	* @param y1 �������� 
	*/
	public AbstractStation(double x1, double y1) {
		x = x1;
		y = y1;
		s = 0;
		map = new HashMap<Tail, Law>();
	}

	/** 
	* ������������ ������� �������.
	* @param tail ������, � ������� ���������� ���������� ������� ������� 
	*/
	public abstract void explode(Tail tail);
	
	/** 
	* ������ ����� ������� �������.
	* @param tails ������, ��� ������� ���������� ��������� ����� ������� �������
	*/
	public abstract void explode(Tail[] tails);

	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}
	
	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}

}
