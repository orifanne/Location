package location;

import java.util.ArrayList;
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
	protected double s = 150;

	public AbstractStation() {
		x = 0;
		y = 0;
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
	}
	
	/** 
	* @param x1 �������� 
	* @param y1 �������� 
	*/
	public AbstractStation(double x1, double y1) {
		x = x1;
		y = y1;
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
	public abstract void explode(ArrayList<Tail> tails);

	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}
	
	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}

}
