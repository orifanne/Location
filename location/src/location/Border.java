package location;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

/** 
* ������������ ������� ������. 
* @author Pokrovskaya Oksana
*/
public class Border extends Polygon {
	
	/** ������ ����� � ������� �� ����������. */
	private int[][] dotes;

	/** ������ ������������ ��������, ������������� ����� �������. 
	* v[i][0] - ���������� x
	* v[i][1] - ������� ���������� y
	* v[i][2] - ������� ���������� y
	*/
	private int[][] v;

	/** ������ �������������� ��������, ������������� ������ ����.
	* h[i][0] - ���������� y
	* h[i][1] - ������� ���������� x
	* h[i][2] - ������� ���������� x
	*/
	private int[][] h;

	/** ���������� ������������ ��������. */
	private int vNum;

	/** ���������� �������������� ��������. */
	private int hNum;

	public Border() {
		dotes = null;
	}

	/** 
	* @param d ������ ����� � ������� �� ����������
	*/
	public Border(int[][] d, int[] x, int[] y) {
		//���� ���� �� �������������� ��� �� ������������ �������
		//if (...) {
			//add exeption
		//}
		
		super(x, y, x.length);

		dotes = d;

		vNum = 0;
		hNum = 0;
		v = new int[dotes.length - 1][3];
		h = new int[dotes.length - 1][3];
		//��������� ������� ������������ � �������������� ��������
		for (int i = 0; i < dotes.length - 1; i++) {
			//���� ���� ����� �������� ������������ �������
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
			//���� ���� ����� �������� �������������� �������
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
    	* �������� �����.
    	*/
	public int[][] getDotes() {
		return dotes;
	}

    	/** 
    	* �������� ����� � ������� i.
	* @param i ����� �����
    	*/
	public int[] getDote(int i) {
		return dotes[i];
	}

    	/** 
    	* ��������, ����� �� ����� ������ �������.
    	*/
	public boolean isInternal(Frame f) {
		return isInternal(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}
		

    	/** 
    	* ��������, ����� �� ������, ������������ ������������ x1 y1 x2 y2, ������ �������.
    	*/
	public boolean isInternal(double x1, double y1, double x2, double y2) {
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		
		return super.contains(x, y);
		
		//������������ ���������� ������������ �������� ����� ����� (x, y)
		/*int n = 0;
		for (int i = 0; i < vNum; i++)
			//���� ���� ������� �� ����� ������ �� ��������� � ������
			if ((y < v[i][1]) && (y > v[i][2]) || (y > v[i][1]) && (y < v[i][2]))
				//� ���� �� �����
				if (x > v[i][0])
					n++;
		if (n % 2 != 0)
			return true;
		else 
			return false;*/
	}

    	/** 
    	* ��������, ����� ������� ���������� ������, ������������ ������������ x1 x2 y1 y2.
    	* [0] - ������;
    	* [1] - �����;
    	* [2] - ������;
    	* [3] - �����.
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
