package location;

import java.util.ArrayList;

/** 
* Представляет позиционируемый объект. 
* @author Pokrovskaya Oksana
*/
public class PosObject {

	/** Абсцисса */
	private double x;
	/** Ордината */
	private double y;

	/** Вектор сил сигнала */
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
