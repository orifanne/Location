package location;

/** 
* Представляет внешний контур. 
* @author Pokrovskaya Oksana
*/
public class Border {
	
	/** Массив точек в порядке их соединения. */
	private int[][] dotes;

	/** Массив вертикальных отрезков, упорядоченных слева направо. 
	* v[i][0] - координата x
	* v[i][1] - меньшая координата y
	* v[i][2] - большая координата y
	*/
	private int[][] v;

	/** Массив горизонтальных отрезков, упорядоченных сверху вниз.
	* h[i][0] - координата y
	* h[i][1] - меньшая координата x
	* h[i][2] - большая координата x
	*/
	private int[][] h;

	/** Количество вертикальных отрезков. */
	private int vNum;

	/** Количество горизонтальных отрезков. */
	private int hNum;

	public Border() {
		dotes = null;
	}

	/** 
	* @param d массив точек в порядке их соединения
	*/
	public Border(int[][] d) {
		//если есть не горизонтальные или не вертикальные отрезки
		//if (...) {
			//add exeption
		//}

		dotes = d;

		vNum = 0;
		hNum = 0;
		v = new int[dotes.length - 1][3];
		h = new int[dotes.length - 1][3];
		//заполняем массивы вертикальных и горизонтальных отрезков
		for (int i = 0; i < dotes.length - 1; i++) {
			//если пара точек образует вертикальный отрезок
			if (dotes[i][0] == dotes[i + 1][0]) {
				v[vNum][0] = dotes[i][0];
				if (dotes[i][1] < dotes[i + 1][1]) {
					v[vNum][1] = dotes[i][1];
					v[vNum][2] = dotes[i + 1][1];
				}
				else {
					v[vNum][1] = dotes[i + 1][1];
					v[vNum][2] = dotes[i][1];
				}
				vNum++;
			}
			//если пара точек образует горизонтальный отрезок
			else {
				h[hNum][0] = dotes[i][1];
				if (dotes[i][0] < dotes[i + 1][0]) {
					h[hNum][1] = dotes[i][0];
					h[hNum][2] = dotes[i + 1][0];
				}
				else {
					h[hNum][1] = dotes[i + 1][0];
					h[hNum][2] = dotes[i][0];
				}
				hNum++;
			}
		}
	}

    	/** 
    	* Получить точки.
    	*/
	public int[][] getDotes() {
		return dotes;
	}

    	/** 
    	* Получить точку с номером i.
	* @param i номер точки
    	*/
	public int[] getDote(int i) {
		return dotes[i];
	}

    	/** 
    	* Сообщает, лежит ли фрейм внутри контура.
    	*/
	public boolean isInternal(Frame f) {
		return isInternal(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}
		

    	/** 
    	* Сообщает, лежит ли ячейка, определяемая координатами x1 y1 x2 y2, внутри контура.
    	*/
	public boolean isInternal(int x1, int y1, int x2, int y2) {
		float x = ((float) x1 + (float) x2) / 2;
		float y = ((float) y1 + (float) y2) / 2;
		
		//подсчитываем количество вертикальных отрезков левее точки (x, y)
		int n = 0;
		for (int i = 0; i < vNum; i++)
			//если этот отрезок на одном уровне по вертикали с точкой
			if ((y < v[i][1]) && (y > v[i][2]) || (y > v[i][1]) && (y < v[i][2]))
				//и если он левее
				if (x > v[i][0])
					n++;
		if (n % 2 != 0)
			return true;
		else 
			return false;
	}

    	/** 
    	* Сообщает, каким образом ограничена ячейка, определяемая координатами x1 x2 y1 y2.
    	* [0] - сверху;
    	* [1] - снизу;
    	* [2] - справа;
    	* [3] - слева.
    	*/
	public boolean[] isBordered(int x1, int y1, int x2, int y2) {
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		float x = ((float) x1 + (float) x2) / 2;
		float y = ((float) y1 + (float) y2) / 2;
		for (int i = 0; i < vNum; i++)
			//если этот отрезок на одном уровне по вертикали с точкой
			//if ((y < v[i][1]) && (y > v[i][2]) || (y > v[i][1]) && (y < v[i][2])) {
			if ((y > v[i][1]) && (y < v[i][2])) {
				//и примыкает к правому краю
				if (x2 == v[i][0]) {
					right = true;
					break;
				}
				//и примыкает к левому краю
				if (x1 == v[i][0]) {
					left = true;
					break;
				}
			}
		for (int i = 0; i < hNum; i++)
			//если этот отрезок на одном уровне по горизонтали с точкой
			//if ((x < h[i][1]) && (x > h[i][2]) || (x > h[i][1]) && (x < h[i][2])) {
			if ((x > h[i][1]) && (x < h[i][2])) {
				//и примыкает к нижнему краю
				if (y2 == h[i][0]) {
					down = true;
					break;
				}
				//и примыкает к верхнему краю
				if (y1 == h[i][0]) {
					up = true;
					break;
				}
			}
		boolean[] b = new boolean[4];
		b[0] = up;
		b[1] = down;
		b[2] = right;
		b[3] = left;
		return b;
	}

}
