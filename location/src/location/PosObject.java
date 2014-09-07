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
public class PosObject extends Point2D.Double {

	/** ������������ �������� */
	private double probX;
	/** ������������ �������� */
	private double probY;

	/** ������ ������� ������� */
	private ArrayList<java.lang.Double> s;

	/** ������, � ������� ��������� ������ */
	private Tail t;

	public PosObject() {
		x = 0;
		y = 0;
		t = null;
		probX = 0;
		probY = 0;
		s = new ArrayList<java.lang.Double>();
	}

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 * @param t
	 *            ������ ������������
	 */
	public PosObject(double x, double y, Tail t) {
		super(x, y);
		probX = 0;
		probY = 0;
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
		Random rand = new Random(new Date().getTime());
		int p = rand.nextInt(plan.getTails().size());
		x = plan.getTails().get(p).getX();
		y = plan.getTails().get(p).getY();
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
		Random rand = new Random(new Date().getTime());
		for (int i = 0; i < plan.getStations().size(); i++) {
			double alpha = plan.getStation(i).getMap().get(t).getA();
			double sigma = plan.getStation(i).getMap().get(t).getQ();
			s.add((java.lang.Double) (rand.nextGaussian() * sigma + alpha));
		}
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

}
