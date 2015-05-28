package location;

import java.awt.geom.Point2D;

/**
 * ������������ ����������� ������. �� ����� ����������������, ������ ���
 * ������� ������ �������. (������ �����, �������� ���������������� � ���� �����
 * ����� ������������� � �������)
 * 
 * @author Pokrovskaya Oksana
 */

public class AbstractTail extends Point2D.Double {
	
	public Point2D.Double getLocation() {
		return new Point2D.Double(this.x, this.y);
	}
}
