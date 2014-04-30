package location;

/** 
* Вспомогательный класс, представляет область для разметки на ячейки. 
* @author Pokrovskaya Oksana
*/
public class Frame extends Tail{
	
	/** Абсцисса левого верхнего угла */
	private int x1;
	/** Ордината левого верхнего угла */
	private int y1;
	/** Абсцисса правого нижнего угла */
	private int x2;
	/** Ордината правого нижнего угла */
	private int y2;

	/** Флаг использования */
	private boolean used;

	/** Флаг ограниченности сверху */
	public  boolean up = false;

	/** Флаг ограниченности снизу */
	public boolean down = false;

	/** Флаг ограниченности справа */
	public boolean right = false;

	/** Флаг ограниченности слева */
	public boolean left = false;

	public Frame() {
		super();
		used = false;
	}

	/** 
	* @param x11 абсцисса левого верхнего угла (< x21)
	* @param y11 ордината левого верхнего угла (> y21)
	* @param x21 абсцисса правого нижнего угла
	* @param y21 ордината правого нижнего угла
	*/
	public Frame(int x11, int y11, int x21, int y21) {
		super(x11, y11, x21, y21);
		used = false;
	}


	public Frame(double x11, double y11, double x21, double y21) {
		// TODO Auto-generated constructor stub
		super(x11, y11, x21, y21);
		used = false;
	}

	/** Получить флаг использования */
	public boolean isUsed() {
		return used;
	}

	/** Установить флаг использования */
	public void used(boolean a) {
		used = a;
	}
}
