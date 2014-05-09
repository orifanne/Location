package location;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/** 
* Представляет внешний контур. 
* @author Pokrovskaya Oksana
*/
public class Border extends Polygon {
	
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
	public Border(int[][] d, int[] x, int[] y) {
		//если есть не горизонтальные или не вертикальные отрезки
		//if (...) {
			//add exeption
		//}
		
		super(x, y, x.length);

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
	public boolean isInternal(double x1, double y1, double x2, double y2) {
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		
		return super.contains(x, y);
		
		//подсчитываем количество вертикальных отрезков левее точки (x, y)
		/*int n = 0;
		for (int i = 0; i < vNum; i++)
			//если этот отрезок на одном уровне по вертикали с точкой
			if ((y < v[i][1]) && (y > v[i][2]) || (y > v[i][1]) && (y < v[i][2]))
				//и если он левее
				if (x > v[i][0])
					n++;
		if (n % 2 != 0)
			return true;
		else 
			return false;*/
	}

    	/** 
    	* Сообщает, каким образом ограничена ячейка, определяемая координатами x1 x2 y1 y2.
    	* [0] - сверху;
    	* [1] - снизу;
    	* [2] - справа;
    	* [3] - слева.
    	*/
	public boolean[] isBordered(double x1, double y1, double x2, double y2) {
		PathIterator pi = this.getPathIterator(null);
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		float coords[] = new float[6];
		float prev[] = new float[2];
		float first[] = new float[2];
		while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                    case PathIterator.SEG_MOVETO:
                    	prev[0] = coords[0];
                    	prev[1] = coords[1];
                    	first[0] = coords[0];
                    	first[1] = coords[1];
                        break;
                    case PathIterator.SEG_LINETO:
                    	Line2D.Float line = new Line2D.Float(prev[0], prev[1],
                    			coords[0], coords[1]);
                    	//System.out.println(prev[0] + " " + prev[1] + " " +
                    			//coords[0] + " " + coords[1]);
                    	if (line.intersectsLine(x1, y1, x2, y1))
                    		up = true;
                    	if (line.intersectsLine(x2, y2, x1, y2))
                    		down = true;
                    	if (line.intersectsLine(x1, y1, x1, y2))
                    		left = true;
                    	if (line.intersectsLine(x2, y2, x2, y1))
                    		right = true;
                    	prev[0] = coords[0];
                    	prev[1] = coords[1];
                        break;
                    case PathIterator.SEG_QUADTO:
                    	//ignored
                            break;
                    case PathIterator.SEG_CUBICTO:
                    	//ignored
                            break;
                    case PathIterator.SEG_CLOSE:
                    	Line2D.Float line1 = new Line2D.Float(prev[0], prev[1],
                    			first[0], first[1]);
                    	//System.out.println("close " + prev[0] + " " + prev[1] + " " +
                    			//first[0] + " " + first[1]);
                    	if (line1.intersectsLine(x1, y1, x2, y1))
                    		up = true;
                    	if (line1.intersectsLine(x2, y2, x1, y2))
                    		down = true;
                    	if (line1.intersectsLine(x1, y1, x1, y2))
                    		left = true;
                    	if (line1.intersectsLine(x2, y2, x2, y1))
                    		right = true;
                        break;
            }
            pi.next();
		}

		boolean[] b = new boolean[4];
		b[0] = up;
		b[1] = down;
		b[2] = right;
		b[3] = left;
		return b;
	}

}
