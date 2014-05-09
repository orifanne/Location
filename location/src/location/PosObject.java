package location;

import java.util.ArrayList;

/** 
* ������������ ��������������� ������. 
* @author Pokrovskaya Oksana
*/
public class PosObject {

	/** �������� */
	private double x;
	/** �������� */
	private double y;

	/** ������ ��� ������� */
	private ArrayList<Integer> s;

	public PosObject() {
		x = 0;
		y = 0;
		s = new ArrayList<Integer>();
	}
	
	public PosObject(double x, double y) {
		this.x = x;
		this.y = y;
		s = new ArrayList<Integer>();
	}

}
