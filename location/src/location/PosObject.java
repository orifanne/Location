package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Представляет позиционируемый объект.
 * 
 * @author Pokrovskaya Oksana
 */
public class PosObject {

	/** Вектор уровней сигнала */
	private ArrayList<java.lang.Double> s;

	/** Ячейка, в которой находится объект */
	private Tail t;

	/** Ячейка, в которой вероятно находится объект */
	private Tail probT;

	/** Объект для генерации случайных чисел */
	Random rand;

	public PosObject() {
		t = null;
		s = new ArrayList<java.lang.Double>();
		rand = new Random(new Date().getTime());
	}

	/**
	 * @param t
	 *            ячейка расположения
	 */
	public PosObject(Tail t) {
		this.t = t;
		s = new ArrayList<java.lang.Double>();
	}

	/**
	 * Объект позиционирования случайным образом перемещается в следующую ячейку
	 * 
	 * @param plan
	 *            план здания
	 */
	public void nextStep(Plan plan) {
		int p = rand.nextInt(plan.getTails().size());
		t = plan.getTails().get(p);
		getVector(plan);
	}

	/**
	 * Объект позиционирования регистрирует вектор уровней сигналов от базовых
	 * станций
	 * 
	 * @param plan
	 *            план здания
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
	 * Рассчитать местоположение
	 * 
	 * @param plan
	 *            план здания
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
	 * Рассчитать местоположение (для рассчета оценок)
	 * 
	 * @param plan
	 *            план здания
	 * @param eps
	 *            пороговое значение вероятности
	 * @return рассчитаные оценки, result[0] - разброс по вероятности, result[1]
	 *         - разброс по площади
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
	 * Получить k-ую комноненту вектора уровней сигналов.
	 * 
	 * @param k
	 *            номер компоненты
	 * @return значение компоненты
	 */
	public double getVector(int k) {
		return s.get(k);
	}

	/**
	 * Получать ячейку расположения
	 * 
	 * @return ячейка расположения
	 */
	public Tail getT() {
		return t;
	}

	/**
	 * Получать ячейку вероятного расположения
	 * 
	 * @return ячейка вероятного расположения
	 */
	public Tail getProbT() {
		return probT;
	}

}
