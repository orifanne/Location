package location;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Представляет карту уровней сигналов.
 * 
 * @author Pokrovskaya Oksana
 */

public class Map {

	/** Карта уровней сигнала */
	private HashMap<Tail, Law> map;

	/** Набор точек измерений */
	private ArrayList<HashMap<Point2D.Double, Law>> points;

	/** Имя */
	private String name;

	public Map(HashMap<Tail, Law> map, ArrayList<HashMap<Double, Law>> points,
			String name) {
		super();
		this.map = map;
		this.points = points;
		this.name = name;
	}

	/**
	 * Построить карту уровней сигналов, основываясь на наборе точек с
	 * измерениями.
	 * 
	 * @param t
	 *            ячейки, для которых нужно построить карту
	 * @param sigma
	 *            дисперсия
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
	 * Подсчет вероятности того, что k-ая компонента вектора равна num в ячейке
	 * t
	 * 
	 * @param k
	 *            номер компоненты
	 * @param num
	 *            вероятное значение компоненты
	 * @param t
	 *            ячейка
	 * @return вероятность
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
	 * Получить карту
	 * 
	 * @return карта
	 */
	public HashMap<Tail, Law> getMap() {
		return map;
	}

	/**
	 * Установить карту
	 * 
	 * @param map
	 *            карта
	 */
	public void setMap(HashMap<Tail, Law> map) {
		this.map = map;
	}

	/**
	 * Получить имя карты
	 * 
	 * @return имя
	 */
	public String getName() {
		return name;
	}

	/**
	 * Установить имя карты
	 * 
	 * @param name
	 *            имя
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Сбросить карту на нулевую
	 * 
	 * @param sigma
	 *            среднеквадратическое отклонения для закона распределения
	 * @param tails
	 *            ячейки карты
	 */
	public void newMap(int sigma, ArrayList<Tail> tails) {
		map = new HashMap<Tail, Law>();
		for (int i = 0; i < tails.size(); i++)
			map.put(tails.get(i), new Law(0, sigma));
	}

	/**
	 * Получить набор точек измерений
	 * 
	 * @return набор точек измерений
	 */
	public ArrayList<HashMap<Point2D.Double, Law>> getPoints() {
		return points;
	}
}
