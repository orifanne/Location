package location;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ������������ ����� ������� ��������.
 * 
 * @author Pokrovskaya Oksana
 */

public class Map {

	/** ����� ������� ������� */
	private HashMap<Tail, Law> map;

	/** ����� ����� ��������� */
	private ArrayList<HashMap<Point2D.Double, Law>> points;

	/** ��� */
	private String name;

	public Map(HashMap<Tail, Law> map, ArrayList<HashMap<Double, Law>> points,
			String name) {
		super();
		this.map = map;
		this.points = points;
		this.name = name;
	}

	/**
	 * ��������� ����� ������� ��������, ����������� �� ������ ����� �
	 * �����������.
	 * 
	 * @param t
	 *            ������, ��� ������� ����� ��������� �����
	 * @param sigma
	 *            ���������
	 */
	public void buildMap(ArrayList<Tail> t, int sigma) {
		if ((points == null) || (points.size() == 0))
			return;
		map = new HashMap<Tail, Law>();
		for (int i = 0; i < t.size(); i++) {
			double a = 0;
			int d = 0;
			Point2D.Double[] p = new Point2D.Double[1];
			Law[] l = new Law[1];
			for (int j = 0; j < points.size(); j++) {
				if (t.get(i).contains(points.get(j).keySet().toArray(p)[0])) {
					a += points.get(j).values().toArray(l)[0].getA();
					d++;
				}
			}
			if (d > 0)
				a /= (double) d;
			map.put(t.get(i), new Law(a, sigma));
		}
	}

	/**
	 * ������� ����������� ����, ��� k-�� ���������� ������� ����� num � ������
	 * t
	 * 
	 * @param k
	 *            ����� ����������
	 * @param num
	 *            ��������� �������� ����������
	 * @param t
	 *            ������
	 * @return �����������
	 */
	public double fp(int k, double num, Tail t) {
		double a = map.get(t).getA();
		double q = map.get(t).getQ();

		double cons = 1 / (Math.sqrt(2 * Math.PI) * q);
		double step = -1 * Math.pow((num - a), 2) / (2 * Math.pow(q, 2));

		double p = cons * Math.pow(Math.E, step);
		return p;
	}

	/**
	 * �������� �����
	 * 
	 * @return �����
	 */
	public HashMap<Tail, Law> getMap() {
		return map;
	}

	/**
	 * ���������� �����
	 * 
	 * @param map
	 *            �����
	 */
	public void setMap(HashMap<Tail, Law> map) {
		this.map = map;
	}

	/**
	 * �������� ��� �����
	 * 
	 * @return ���
	 */
	public String getName() {
		return name;
	}

	/**
	 * ���������� ��� �����
	 * 
	 * @param name
	 *            ���
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * �������� ����� �� �������
	 * 
	 * @param sigma
	 *            �������������������� ���������� ��� ������ �������������
	 * @param tails
	 *            ������ �����
	 */
	public void newMap(int sigma, ArrayList<Tail> tails) {
		map = new HashMap<Tail, Law>();
		for (int i = 0; i < tails.size(); i++)
			map.put(tails.get(i), new Law(0, sigma));
	}

	/**
	 * �������� ����� ����� ���������
	 * 
	 * @return ����� ����� ���������
	 */
	public ArrayList<HashMap<Point2D.Double, Law>> getPoints() {
		return points;
	}
}