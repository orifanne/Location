package location;

/** 
* Представляет базовую станцию. 
* @author Pokrovskaya Oksana
*/
public class Station {

	/** Абсцисса */
	private int x;
	/** Ордината */
	private int y;

	/** Базовая сила сигнала */
	private int s;

	public Station() {
		x = 0;
		y = 0;
		s = 0;
	}

	/** 
	* @param x1 абсцисса 
	* @param y1 ордината 
	* @param s1 базовая сила сигнала
	*/
	public Station(int x1, int y1, int s1) {
		x = x1;
		y = y1;
		s = s1;
	}

	/** 
	* Рассчитывает силу сигнала.
	* @param tail ячейка, в которой необходимо рассчитать силу сигнала 
	*/
	public void explode(Tail tail) {
		//...
	}

}
