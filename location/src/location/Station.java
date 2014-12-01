package location;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Представляет базовую станцию.
 * 
 * @author Pokrovskaya Oksana
 */

public class Station extends AbstractStation {

	/** Список карт уровней сигнала */
	private ArrayList<Map> maps;

	public Station() {
		super();

		maps = new ArrayList<Map>();
		maps.add(new Map(new HashMap<Tail, Law>(), null, "modelled"));
		maps.add(new Map(new HashMap<Tail, Law>(), null, "taught"));
	}

	/**
	 * @param x1
	 *            абсцисса
	 * @param y1
	 *            ордината
	 * @param s1
	 *            базовая сила сигнала
	 */
	public Station(int x1, int y1, int s1) {
		super(x1, y1, s1);

		maps = new ArrayList<Map>();
		maps.add(new Map(new HashMap<Tail, Law>(), null, "modelled"));
		maps.add(new Map(new HashMap<Tail, Law>(), null, "taught"));
	}

	/**
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 */
	public Station(double x, double y) {
		super(x, y);

		maps = new ArrayList<Map>();
		maps.add(new Map(new HashMap<Tail, Law>(), null, "modelled"));
		maps.add(new Map(new HashMap<Tail, Law>(), null, "taught"));
	}

	/**
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 * @param name
	 *            имя станции
	 */
	public Station(double x, double y, String name) {
		super(x, y, name);

		maps = new ArrayList<Map>();
		maps.add(new Map(new HashMap<Tail, Law>(), null, "modelled"));
		maps.add(new Map(new HashMap<Tail, Law>(), null, "taught"));
	}

	/**
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 * @param name
	 *            имя станции
	 * @param s
	 *            базовый уровень сигнала
	 */
	public Station(double x, double y, String name, double s) {
		super(x, y, name, s);

		maps = new ArrayList<Map>();
		maps.add(new Map(new HashMap<Tail, Law>(), null, "modelled"));
		maps.add(new Map(new HashMap<Tail, Law>(), null, "taught"));
	}

	/**
	 * Импортировать карту уровней сигналов.
	 * 
	 * @param file
	 *            файл, из которого нужно импортировать карту
	 * @param sigma
	 *            дисперсия
	 */
	public void importMap(File file, int sigma) {
		DocumentBuilderFactory f = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			builder = f.newDocumentBuilder();
		}

		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} // заглушка

		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} // заглушка
		catch (IOException e) {
			e.printStackTrace();
		} // заглушка

		NodeList n = doc.getElementsByTagName("map");
		NamedNodeMap k = null;
		String name;
		k = n.item(0).getAttributes();
		name = k.getNamedItem("name").getNodeValue();

		n = doc.getElementsByTagName("point");
		k = null;
		ArrayList<HashMap<Point2D.Double, Law>> points = new ArrayList<HashMap<Point2D.Double, Law>>();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			Point2D.Double p = new Point2D.Double(
					java.lang.Double.parseDouble(k.getNamedItem("x")
							.getNodeValue()), java.lang.Double.parseDouble(k
							.getNamedItem("y").getNodeValue()));
			HashMap<Point2D.Double, Law> h = new HashMap<Point2D.Double, Law>();
			h.put(p,
					new Law(java.lang.Double.parseDouble(k.getNamedItem(
							"signal").getNodeValue()), sigma));
			points.add(h);
		}

		maps.add(new Map(null, points, name));
	}

	@Override
	public void explode(Tail tail, int sigma) {
		double d = Point2D.Double.distance(x, y, tail.getX(), tail.getY());
		Law l = new Law(s - countFSL(d), sigma);

		for (int i = 0; i < maps.size(); i++) {
			if (maps.get(i).getName().equals("modelled"))
				maps.get(i).getMap().put(tail, l);
			if (maps.get(i).getName().equals("taught"))
				maps.get(i).getMap().put(tail, new Law(0, sigma));
		}
	}

	@Override
	public void explode(ArrayList<Tail> tails, int sigma) {
		for (int i = 0; i < tails.size(); i++)
			explode(tails.get(i), sigma);
	}

	@Override
	public void explode(Tail tail, Plan plan, int sigma) {
		ArrayList<Point2D.Double> p = new ArrayList<Point2D.Double>();
		Point2D.Double n = null;
		for (int i = 0; i < plan.getWalls().size(); i++) {
			if (plan.getWalls().get(i)
					.intersectsLine(x, y, tail.getX(), tail.getY())
					&& !plan.getWalls().get(i).intersectsLine(x, y, x, y)) {
				if (plan.getWalls().get(i).getX1() == plan.getWalls().get(i)
						.getX2()) {
					if (y == tail.getY()) {
						n = new Point2D.Double(plan.getWalls().get(i).getX1(),
								y);
						if (!p.contains(n))
							p.add(n);
					} else {
						double k = (y - tail.getY()) / (x - tail.getX());
						double b = tail.getY() - k * tail.getX();
						n = new Point2D.Double(plan.getWalls().get(i).getX1(),
								k * plan.getWalls().get(i).getX1() + b);
						if (!p.contains(n))
							p.add(n);
					}
				}
				if (plan.getWalls().get(i).getY1() == plan.getWalls().get(i)
						.getY2()) {
					if (x == tail.getX()) {
						n = new Point2D.Double(x, plan.getWalls().get(i)
								.getY1());
						if (!p.contains(n))
							p.add(n);
					} else {
						double k = (y - tail.getY()) / (x - tail.getX());
						double b = tail.getY() - k * tail.getX();
						n = new Point2D.Double(
								(plan.getWalls().get(i).getY1() - b) / k, plan
										.getWalls().get(i).getY1());
						if (!p.contains(n))
							p.add(n);
					}
				}
			}
		}

		double d = Point2D.Double.distance(x, y, tail.getX(), tail.getY());
		Law l = new Law(s - countFSL(d) - countExtraPL(p.size()), sigma);

		for (int i = 0; i < maps.size(); i++) {
			if (maps.get(i).getName().equals("modelled"))
				maps.get(i).getMap().put(tail, l);
			if (maps.get(i).getName().equals("taught"))
				maps.get(i).getMap().put(tail, new Law(0, sigma));
		}
	}

	@Override
	public void explode(ArrayList<Tail> tails, Plan plan, int sigma) {
		for (int i = 0; i < tails.size(); i++)
			explode(tails.get(i), plan, sigma);
	}

	@Override
	public void teach(PosObject object, Plan plan, int num) {
		taught = false;

		maps.get(1).newMap(plan.getSigma(), plan.getTails());
		double[] probx = new double[plan.getTails().size()];
		double[] probsx = new double[plan.getTails().size()];
		double[] del = new double[plan.getTails().size()];
		double ps, psx;
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);

			// debug

			/*
			 * System.out.println(plan.getTails().indexOf(object.getT()) + 1);
			 * for (int k = 0; k < plan.getStations().size() - 1; k++) {
			 * System.out.println(object.getVector(k) + " "); }
			 * System.out.println();
			 */

			ps = 0;
			// рассчитать вероятность принять его в каждой площадке (psx)
			// и просто вероятность принять его (ненормализованную) (ps)
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
			// рассчитать вероятности находиться в каждой площадке
			for (int j = 0; j < plan.getTails().size(); j++) {
				probx[j] = probsx[j] / ps;
				int n = plan.getStations().indexOf(this);
				maps.get(1).getMap().get(plan.getTails().get(j)).a += (object
						.getVector(n) * probx[j]);
				del[j] += probx[j];
			}
		}
		for (int i = 0; i < plan.getTails().size(); i++)
			if (del[i] != 0) {
				maps.get(1).getMap().get(plan.getTails().get(i)).a /= del[i];
				// System.out.println(tMap.get(plan.getTails().get(i)).a);
			}
		taught = true;
	}

	/**
	 * Вычисляет ошибку позиционирования определенной карты уровней сигналов.
	 * 
	 * @param object
	 *            позиционируемый объект
	 * @param plan
	 *            план здания
	 * @param num
	 *            количество точек
	 * @param m
	 *            номер карты
	 * @param result
	 *            вычисленные ошбики; result[0] содержит ошибку позиционирования
	 *            как расстояние между реальной и определенной позицией, а
	 *            result[1] - процент угадываний
	 */
	public void cmpMapsPos(PosObject object, Plan plan, int num, int m,
			double[] result) {
		if (m > 1)
			maps.get(m).buildMap(plan.getTails(), plan.getSigma());
		double d = 0;
		int n = 0;
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);
			object.locate(plan, plan.getStations().indexOf(this), m);
			double diff = Math.sqrt(Math.pow((object.getT().getX() - object
					.getProbT().getX()), 2)
					+ Math.pow(
							(object.getT().getY() - object.getProbT().getY()),
							2));
			if ((object.getT().getX() == object.getProbT().getX())
					&& (object.getT().getY() == object.getProbT().getY()))
				n++;
			d += diff;
		}
		result[0] = d / num;
		result[1] = n / num;
	}

	/**
	 * Вычисляет относительное отличие двух карт уровней сигналов.
	 * 
	 * @param object
	 *            позиционируемый объект
	 * @param plan
	 *            план здания
	 * @param num
	 *            количество точек
	 * @param m1
	 *            номер первой карты
	 * @param m2
	 *            номер второй карты
	 * @return относительное отличие двух карт
	 */
	public double cmpMaps(PosObject object, Plan plan, int num, int m1, int m2) {
		if (m1 > 1)
			maps.get(m1).buildMap(plan.getTails(), plan.getSigma());
		if (m2 > 1)
			maps.get(m2).buildMap(plan.getTails(), plan.getSigma());
		double d = 0;
		for (int i = 0; i < num; i++) {
			double f = 0;
			for (int j = 0; j < plan.getTails().size(); j++) {
				f += maps.get(m1).getMap().get(plan.getTails().get(j)).getA()
						/ maps.get(m2).getMap().get(plan.getTails().get(j))
								.getA();
			}
			f /= plan.getTails().size();
			d += f;
		}
		return Math.abs(1 - (d / num));
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
	 * @param plan
	 *            план здания
	 * @param s
	 *            станция, для которой нужно использовать карту номер m
	 * @param m
	 *            номер карты, которую нужно использовать
	 * @return вероятность
	 */
	public static double fp(int k, double num, Tail t, Plan plan, int s, int m) {
		double a;
		double q;
		if (k != s)
			m = 0;
		a = plan.getStation(k).getMap(m).getMap().get(t).getA();
		q = plan.getStation(k).getMap(m).getMap().get(t).getQ();

		double cons = 1 / (Math.sqrt(2 * Math.PI) * q);
		double step = -1 * Math.pow((num - a), 2) / (2 * Math.pow(q, 2));

		double p = cons * Math.pow(Math.E, step);
		return p;
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
	 * @param plan
	 *            план здания
	 * @return вероятность
	 */
	public static double fp(int k, double num, Tail t, Plan plan) {
		double a;
		double q;
		a = plan.getStation(k).getMap(0).getMap().get(t).getA();
		q = plan.getStation(k).getMap(0).getMap().get(t).getQ();

		double cons = 1 / (Math.sqrt(2 * Math.PI) * q);
		double step = -1 * Math.pow((num - a), 2) / (2 * Math.pow(q, 2));

		double p = cons * Math.pow(Math.E, step);
		return p;
	}

	/**
	 * Подсчет FreeSpaceLoss на заданном расстоянии
	 * 
	 * @param d
	 *            расстояние
	 * @returns FreeSpaceLoss
	 */
	private double countFSL(double d) {
		// -27,55 + 20·log10F+20·log10d
		return -27.55 + 20 * Math.log10(2440) + 20 * Math.log10(d);
	}

	/**
	 * Подсчет потери сигнала на стенах на пути его распространения
	 * 
	 * @param d
	 *            количество стен
	 * @returns потеря сигнала
	 */
	private double countExtraPL(int d) {
		// -27,55 + 20·log10F+20·log10d
		return 7 * d;
	}

	/**
	 * Получить список карт
	 * 
	 * @return список карт
	 */
	public ArrayList<Map> getMaps() {
		return maps;
	}

	/**
	 * Получить карту
	 * 
	 * @param i
	 *            номер карты
	 * @return
	 */
	public Map getMap(int i) {
		return maps.get(i);
	}
}
