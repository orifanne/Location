package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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
			Map m = plan.getStation(i).getMap(
					plan.getStation(i).getActiveMapNumber());
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
				if ((!s.isTaught()) || (s.getMaps().size() == 0))
					continue;
				if ((getVector(k) > 0)
						&& (s.getMap(s.getActiveMapNumber()).fp(k,
								getVector(k), plan.getTails().get(j)) > 0))
					psx *= s.getMap(s.getActiveMapNumber()).fp(k, getVector(k),
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
