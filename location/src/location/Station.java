package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ������������ ������� �������.
 * 
 * @author Pokrovskaya Oksana
 */

public class Station extends AbstractStation {

	/** ����� ������� �������, ���������� �������������� ������� */
	private HashMap<Tail, Law> map;

	/** ����� ������� �������, ���������� ��������� ������� */
	private HashMap<Tail, Law> tMap;

	public Station() {
		super();
		map = new HashMap<Tail, Law>();
		tMap = new HashMap<Tail, Law>();
	}

	/**
	 * @param x1
	 *            ��������
	 * @param y1
	 *            ��������
	 * @param s1
	 *            ������� ���� �������
	 */
	public Station(int x1, int y1, int s1) {
		super(x1, y1, s1);
		map = new HashMap<Tail, Law>();
		tMap = new HashMap<Tail, Law>();
	}

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 */
	public Station(double x, double y) {
		super(x, y);
		map = new HashMap<Tail, Law>();
		tMap = new HashMap<Tail, Law>();
	}

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 * @param name
	 *            ��� �������
	 */
	public Station(double x, double y, String name) {
		super(x, y, name);
		map = new HashMap<Tail, Law>();
		tMap = new HashMap<Tail, Law>();
	}

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 * @param name
	 *            ��� �������
	 * @param s
	 *            ������� ������� �������
	 */
	public Station(int x, int y, String name, double s) {
		super(x, y, name, s);
		map = new HashMap<Tail, Law>();
		tMap = new HashMap<Tail, Law>();
	}

	@Override
	public void explode(Tail tail) {
		double d = Point2D.Double.distance(x, y, tail.getX(), tail.getY());
		Law l = new Law(s - countFSL(d), 5);
		map.put(tail, l);
		tMap.put(tail, new Law(0, 5));
	}

	@Override
	public void explode(ArrayList<Tail> tails) {
		for (int i = 0; i < tails.size(); i++)
			explode(tails.get(i));
	}

	@Override
	public void explode(Tail tail, Plan plan) {
		int count = 0;
		for (int i = 0; i < plan.getWalls().size(); i++) {
			if (plan.getWalls().get(i)
					.intersectsLine(x, y, tail.getX(), tail.getY()))
				count++;
		}
		double d = Point2D.Double.distance(x, y, tail.getX(), tail.getY());
		Law l = new Law(s - countFSL(d) - countExtraPL(count), 5);
		map.put(tail, l);
		tMap.put(tail, new Law(0, 5));
	}

	@Override
	public void explode(ArrayList<Tail> tails, Plan plan) {
		for (int i = 0; i < tails.size(); i++)
			explode(tails.get(i), plan);
	}

	@Override
	public void teach(PosObject object, Plan plan, int num) {
		taught = false;
		tMap = new HashMap<Tail, Law>();
		for (int i = 0; i < plan.getTails().size(); i++)
			tMap.put(plan.getTails().get(i), new Law(0, 5));
		double[] probx = new double[plan.getTails().size()];
		double[] probsx = new double[plan.getTails().size()];
		double[] del = new double[plan.getTails().size()];
		double ps, psx;
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);
			ps = 0;
			// ���������� ����������� ������� ��� � ������ �������� (psx)
			// � ������ ����������� ������� ��� (�����������������) (ps)
			for (int j = 0; j < plan.getTails().size(); j++) {
				psx = 1;
				for (int k = 0; k < plan.getStations().size(); k++) {
					if (!plan.getStation(k).isTaught())
						continue;
					if ((object.getVector(k) > 0)
							&& (fp(k, object.getVector(k),
									plan.getTails().get(j), plan) > 0))
						psx *= fp(k, object.getVector(k), plan.getTails()
								.get(j), plan);
				}
				probsx[j] = psx;
				ps += psx;
			}
			// ���������� ����������� ���������� � ������ ��������
			for (int j = 0; j < plan.getTails().size(); j++) {
				probx[j] = probsx[j] / ps;
				int n = plan.getStations().indexOf(this);
				tMap.get(plan.getTails().get(j)).a += (object.getVector(n) * probx[j]);
				del[j] += probx[j];
			}
		}
		for (int i = 0; i < plan.getTails().size(); i++)
			if (del[i] != 0) {
				tMap.get(plan.getTails().get(i)).a /= del[i];
				System.out.println(tMap.get(plan.getTails().get(i)).a);
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
	 * @param plan
	 *            ���� ������
	 * @return �����������
	 */
	double fp(int k, double num, Tail t, Plan plan) {
		double a;
		double q;
		a = plan.getStation(k).getMap().get(t).getA();
		q = plan.getStation(k).getMap().get(t).getQ();

		double cons = 1 / (Math.sqrt(2 * Math.PI) * q);
		double step = -1 * Math.pow((num - a), 2) / (2 * Math.pow(q, 2));

		double p = cons * Math.pow(Math.E, step);
		return p;
	}

	/**
	 * ������� FreeSpaceLoss �� �������� ����������
	 * 
	 * @param d
	 *            ����������
	 * @returns FreeSpaceLoss
	 */
	private double countFSL(double d) {
		// -27,55 + 20�log10F+20�log10d
		return -27.55 + 20 * Math.log10(2440) + 20 * Math.log10(d);
	}

	/**
	 * ������� ������ ������� �� ������ �� ���� ��� ���������������
	 * 
	 * @param d
	 *            ���������� ����
	 * @returns ������ �������
	 */
	private double countExtraPL(int d) {
		// -27,55 + 20�log10F+20�log10d
		return 7 * d;
	}

	/**
	 * �������� ����� ��� ��������, ���������� ��������������.
	 * 
	 * @return ����� ��� ��������, ���������� ��������������
	 */
	public HashMap<Tail, Law> getMap() {
		return map;
	}

	/**
	 * �������� ����� ��� ��������, ���������� ���������.
	 * 
	 * @return ����� ��� ��������, ���������� ���������
	 */
	public HashMap<Tail, Law> getTMap() {
		return tMap;
	}
}
