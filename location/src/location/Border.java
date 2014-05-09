package location;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/** 
* ѕредставл€ет внешний контур. 
* @author Pokrovskaya Oksana
*/
public class Border extends Polygon {

	/** 
	* @param d массив точек в пор€дке их соединени€
	*/
	public Border(int[] x, int[] y) {
		//если есть не горизонтальные или не вертикальные отрезки
		//if (...) {
			//add exeption
		//}
		
		super(x, y, x.length);
	}
    	/** 
    	* —ообщает, лежит ли фрейм внутри контура.
    	*/
	public boolean isInternal(Frame f) {
		return isInternal(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}
		

    	/** 
    	* —ообщает, лежит ли €чейка, определ€ема€ координатами x1 y1 x2 y2, внутри контура.
    	*/
	public boolean isInternal(double x1, double y1, double x2, double y2) {
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		return super.contains(x, y);
	}

    	/** 
    	* —ообщает, каким образом ограничена €чейка, определ€ема€ координатами x1 x2 y1 y2.
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
