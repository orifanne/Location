package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * ������������ ��������������� ������.
 * 
 * @author Pokrovskaya Oksana
 */
public class PosObject {

	/** ������ ������� ������� */
	private ArrayList<java.lang.Double> s;

	/** ������, � ������� ��������� ������ */
	private Tail t;

	/** ������, � ������� �������� ��������� ������ */
	private Tail probT;

	/** ������ ��� ��������� ��������� ����� */
	Random rand;

	public PosObject() {
		t = null;
		s = new ArrayList<java.lang.Double>();
		rand = new Random(new Date().getTime());
	}

	/**
	 * @param t
	 *            ������ ������������
	 */
	public PosObject(Tail t) {
		this.t = t;
		s = new ArrayList<java.lang.Double>();
	}

	/**
	 * ������ ���������������� ��������� ������� ������������ � ��������� ������
	 * 
	 * @param plan
	 *            ���� ������
	 */
	public void nextStep(Plan plan) {
		int p = rand.nextInt(plan.getTails().size());
		t = plan.getTails().get(p);
		getVector(plan);
	}

	/**
	 * ������ ���������������� ������������ ������ ������� �������� �� �������
	 * �������
	 * 
	 * @param plan
	 *            ���� ������
	 */
	public void getVector(Plan plan) {
		s = new ArrayList<java.lang.Double>();
		for (int i = 0; i < plan.getStations().size(); i++) {
			double alpha = plan.getStation(i).getMap(0).getMap().get(t).getA();
			double sigma = plan.getStation(i).getMap(0).getMap().get(t).getQ();
			s.add(Math.max(0,
					(java.lang.Double) (rand.nextGaussian() * sigma + alpha)));
		}
	}

	/**
	 * ���������� ��������������
	 * 
	 * @param plan
	 *            ���� ������
	 * @param s
	 *            �������, ��� ������� ����� ������������ ����� ����� m
	 * @param m
	 *            ����� �����, ������� ����� ������������
	 */
	public void locate(Plan plan, int s, int m) {
		double ps = 0, psx;
		Tail t1 = null;

		for (int j = 0; j < plan.getTails().size(); j++) {
			psx = 1;
			for (int k = 0; k < plan.getStations().size(); k++) {
				if (!plan.getStation(k).isTaught())
					continue;
				if ((getVector(k) > 0)
						&& (Station.fp(k, getVector(k), plan.getTails().get(j),
								plan, s, m) > 0))
					psx *= Station.fp(k, getVector(k), plan.getTails().get(j),
							plan, s, m);
			}
			if (psx > ps) {
				ps = psx;
				t1 = plan.getTails().get(j);
			}
		}

		probT = t1;
	}

	/**
	 * �������� k-�� ���������� ������� ������� ��������.
	 * 
	 * @param k
	 *            ����� ����������
	 * @return �������� ����������
	 */
	public double getVector(int k) {
		return s.get(k);
	}

	/**
	 * �������� ������ ������������
	 * 
	 * @return ������ ������������
	 */
	public Tail getT() {
		return t;
	}

	/**
	 * �������� ������ ���������� ������������
	 * 
	 * @return ������ ���������� ������������
	 */
	public Tail getProbT() {
		return probT;
	}

}
