package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

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
			if (plan.getStation(i).getMaps().size() == 0)
				continue;
			Map m = plan.getStation(i).getActiveMap();
			double alpha = m.getMap().get(t).getA();
			double sigma = m.getMap().get(t).getQ();
			s.add(Math.max(0,
					(java.lang.Double) (rand.nextGaussian() * sigma + alpha)));
		}
	}

	/**
	 * ���������� ��������������
	 * 
	 * @param plan
	 *            ���� ������
	 */
	public void locate(Plan plan) {
		double ps = 0, psx;
		Tail t1 = null;

		for (int j = 0; j < plan.getTails().size(); j++) {
			psx = 1;
			for (int k = 0; k < plan.getStations().size(); k++) {
				Station s = plan.getStation(k);
				if (s.getMaps().size() == 0)
					continue;
				if ((getVector(k) > 0)
						&& (s.getActiveMap().fp(k, getVector(k),
								plan.getTails().get(j)) > 0))
					psx *= s.getActiveMap().fp(k, getVector(k),
							plan.getTails().get(j));
			}
			if (psx > ps) {
				ps = psx;
				t1 = plan.getTails().get(j);
			}
		}
		probT = t1;
	}

	/**
	 * ���������� �������������� (��� �������� ������)
	 * 
	 * @param plan
	 *            ���� ������
	 * @param eps
	 *            ��������� �������� �����������
	 * @return ����������� ������, result[0] - ������� �� �����������, result[1]
	 *         - ������� �� �������
	 */
	public double[] locate(Plan plan, double eps) {
		double[] result = new double[2];
		double psx;
		HashMap<java.lang.Double, Tail> tmp = new HashMap<java.lang.Double, Tail>();
		for (int j = 0; j < plan.getTails().size(); j++) {
			psx = 1;
			for (int k = 0; k < plan.getStations().size(); k++) {
				Station s = plan.getStation(k);
				if (s.getMaps().size() == 0)
					continue;
				if ((getVector(k) > 0)
						&& (s.getActiveMap().fp(k, getVector(k),
								plan.getTails().get(j)) > 0))
					psx *= s.getActiveMap().fp(k, getVector(k),
							plan.getTails().get(j));
			}
			tmp.put(psx, plan.getTails().get(j));
		}
		TreeMap<java.lang.Double, Tail> tmp_ = new TreeMap<java.lang.Double, Tail>(
				tmp);
		int i = 0;
		TreeMap<java.lang.Double, Tail> tmp__ = new TreeMap<java.lang.Double, Tail>();
		double x = 0, y = 0;
		for (i = 0; i < tmp_.size(); i++) {
			if ((java.lang.Double) tmp_.keySet().toArray()[i] > eps) {
				tmp__.put((java.lang.Double) tmp_.keySet().toArray()[i],
						tmp_.get((java.lang.Double) tmp_.keySet().toArray()[i]));
				x += tmp_.get((java.lang.Double) tmp_.keySet().toArray()[i])
						.getX();
				y += tmp_.get((java.lang.Double) tmp_.keySet().toArray()[i])
						.getY();
			}
		}
		result[0] = tmp__.size();
		x /= tmp__.size();
		y /= tmp__.size();
		double d = 0;
		for (i = 0; i < tmp__.size(); i++) {
			d += Point2D.Double.distance(x, y,
					tmp__.get((java.lang.Double) tmp__.keySet().toArray()[i])
							.getX(),
					tmp__.get((java.lang.Double) tmp__.keySet().toArray()[i])
							.getY());
		}
		result[1] = d;
		probT = tmp_.get((java.lang.Double) tmp_.keySet().toArray()[tmp_.size() - 1]);
		return result;
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
