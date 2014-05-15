package location;

import java.util.ArrayList;
import java.util.HashMap;

/** 
* Представляет базовую станцию. 
* @author Pokrovskaya Oksana
*/
public abstract class AbstractStation {

	/** Абсцисса */
	protected double x;
	/** Ордината */
	protected double y;

	/** Базовая сила сигнала */
	protected double s = 150;

	public AbstractStation() {
		x = 0;
		y = 0;
	}

	/** 
	* @param x1 абсцисса 
	* @param y1 ордината 
	* @param s1 базовая сила сигнала
	*/
	public AbstractStation(double x1, double y1, double s1) {
		x = x1;
		y = y1;
		s = s1;
	}
	
	/** 
	* @param x1 абсцисса 
	* @param y1 ордината 
	*/
	public AbstractStation(double x1, double y1) {
		x = x1;
		y = y1;
	}

	/** 
	* Рассчитывает уровень сигнала.
	* @param tail ячейка, в которой необходимо рассчитать уровень сигнала 
	*/
	public abstract void explode(Tail tail);
	
	/** 
	* Строит карту уровней сигнала.
	* @param tails ячейки, для которых необходимо построить карту уровней сигнала
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
