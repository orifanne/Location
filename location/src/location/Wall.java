package location;

/** 
* Представляет стену. 
* @author Pokrovskaya Oksana
*/
public class Wall {
	/** Абсцисса первого конца */
	private int x1;
	/** Ордината первого конца */
	private int y1;
	/** Абсцисса второго конца */
	private int x2;
	/** Ордината второго конца */
	private int y2;

	public Wall() {
		x1 = 0;
		x2 = 0;
		y1 = 0;
		y2 = 0;
	}

	/** 
	* @param x11 абсцисса первого конца
	* @param y11 ордината первого конца
	* @param x21 абсцисса второго конца
	* @param y21 ордината второго конца
	*/
	public Wall(int x11, int y11, int x21, int y21) {
		//если отрезок не параллелен одной из осей
		if (!((x11 == x21) || (y11 == y21))) {
			//add exeption
		}
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;
	}

    	/** 
    	* Получить абсциссу первого конца.
    	*/
	public int getX1() {
		return x1;
	}

    	/** 
    	* Получить абсциссу второго конца.
    	*/
	public int getX2() {
		return x2;
	}

    	/** 
    	* Получить ординату первого конца.
    	*/
	public int getY1() {
		return y1;
	}

    	/** 
    	* Получить ординату второго конца.
    	*/
	public int getY2() {
		return y2;
	}
}
