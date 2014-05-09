package location;

/** 
* Представляет ячейку. 
* @author Pokrovskaya Oksana
*/
public class Tail extends AbstractTail {

	/** Абсцисса левого верхнего угла */
	protected double x1;
	/** Ордината левого верхнего угла */
	protected double y1;
	/** Абсцисса правого нижнего угла */
	protected double x2;
	/** Ордината правого нижнего угла */
	protected double y2;

	/** Список законов распределения */
	private Law laws;

	public Tail() {
		x1 = 0;
		x2 = 0;
		y1 = 0;
		y2 = 0;
		laws = null;
	}

	/** 
	* @param x11 абсцисса левого верхнего угла (< x21)
	* @param y11 ордината левого верхнего угла (> y21)
	* @param x21 абсцисса правого нижнего угла
	* @param y21 ордината правого нижнего угла
	*/
	public Tail(int x11, int y11, int x21, int y21) {
		//если перепутаны углы
		if ((x21 <= x11) || (y11 <= y21))
		{
			//add exeption
		}
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;
		
		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}

	public Tail(double x11, double y11, double x21, double y21) {
		// TODO Auto-generated constructor stub
		x1 = x11;
		x2 = x21;
		y1 = y11;
		y2 = y21;
		
		x = (x1 + x2) / 2;
		y = (y1 + y2) / 2;
	}

	/** 
	* Получить абсциссу левого верхнего угла.
	*/
	public double getX1() {
		return x1;
	}

	/** 
	* Получить абсциссу правого нижнего угла.
	*/
	public double getX2() {
		return x2;
	}

	/** 
	* Получить ординату левого верхнего угла.
	*/
	public double getY1() {
		return y1;
	}

	/** 
	* Получить ординату правого нижнего угла.
	*/
	public double getY2() {
		return y2;
	}

	/** 
	 * Получить ординату центра.
	 */
	public double getY() {
			return y;
	}
	
	/** 
	 * Получить абсциссу центра.
	 */
	public double getX() {
			return x;
	}

}
