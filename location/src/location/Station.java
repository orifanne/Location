package location;

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
 * ������������ ������� �������.
 * 
 * @author Pokrovskaya Oksana
 */

public class Station extends AbstractStation {

	/** ������ ���� ������� ������� */
	private ArrayList<Map> maps;

	/** ����� �������� ����� */
	private int activeMapNumber = 0;

	public Station() {
		super();

		maps = new ArrayList<Map>();
	}

	/**
	 * @param x
	 *            ��������
	 * @param y
	 *            ��������
	 */
	public Station(double x, double y) {
		super(x, y);

		maps = new ArrayList<Map>();
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

		maps = new ArrayList<Map>();
	}

	/**
	 * ������������� ����� ������� ��������.
	 * 
	 * @param file
	 *            ����, �� �������� ����� ������������� �����
	 * @param sigma
	 *            ���������
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
		} // ��������

		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} // ��������
		catch (IOException e) {
			e.printStackTrace();
		} // ��������

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
	public void explode(Tail tail, int sigma, Map m, double s) {
		double d = Point2D.Double.distance(x, y, tail.getX(), tail.getY());
		Law l = new Law(s - countFSL(d), sigma);
		m.getMap().put(tail, l);
		HashMap<Point2D.Double, Law> h = new HashMap<Point2D.Double, Law>();
		h.put(tail.getLocation(), l);
		m.getPoints().add(h);
	}

	@Override
	public void explode(ArrayList<Tail> tails, int sigma, String name, double s) {
		Map m = new Map(new HashMap<Tail, Law>(),
				new ArrayList<HashMap<Point2D.Double, Law>>(), name);
		for (int i = 0; i < tails.size(); i++)
			explode(tails.get(i), sigma, m, s);
		maps.add(m);
	}

	@Override
	public void explode(Tail tail, Plan plan, Map m, double s) {
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
		Law l = new Law(s - countFSL(d) - countExtraPL(p.size()),
				plan.getSigma());

		m.getMap().put(tail, l);

		HashMap<Point2D.Double, Law> h = new HashMap<Point2D.Double, Law>();
		h.put(tail.getLocation(), l);
		m.getPoints().add(h);
	}

	@Override
	public void explode(Plan plan, String name, double s) {
		Map m = new Map(new HashMap<Tail, Law>(),
				new ArrayList<HashMap<Point2D.Double, Law>>(), name);
		for (int i = 0; i < plan.getTails().size(); i++)
			explode(plan.getTails().get(i), plan, m, s);
		maps.add(m);
	}

	@Override
	public void teach(PosObject object, Plan plan, int num, String name) {
		for (int i = 0; i < plan.getStations().size(); i++) {
			plan.getStation(i).getActiveMap().buildMap(plan.getTails(), plan.getSigma());
		}
		Map m = new Map(new HashMap<Tail, Law>(),
				new ArrayList<HashMap<Point2D.Double, Law>>(), name);
		for (int i = 0; i < plan.getTails().size(); i++)
			m.getMap().put(plan.getTails().get(i), new Law(0, plan.getSigma()));

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
					Station s = plan.getStation(k);
					if (s.getMaps().size() == 0)
						continue;
					if ((object.getVector(k) > 0)
							&& (s.getActiveMap().fp(k, object.getVector(k),
									plan.getTails().get(j)) > 0))
						psx *= s.getActiveMap().fp(k, object.getVector(k),
								plan.getTails().get(j));
				}
				probsx[j] = psx;
				ps += psx;
			}
			// ���������� ����������� ���������� � ������ ��������
			for (int j = 0; j < plan.getTails().size(); j++) {
				probx[j] = probsx[j] / ps;
				int n = plan.getStations().indexOf(this);
				m.getMap().get(plan.getTails().get(j)).a += (object
						.getVector(n) * probx[j]);
				del[j] += probx[j];
			}
		}
		for (int i = 0; i < plan.getTails().size(); i++)
			if (del[i] != 0) {
				m.getMap().get(plan.getTails().get(i)).a /= del[i];

				HashMap<Point2D.Double, Law> h = new HashMap<Point2D.Double, Law>();
				h.put(plan.getTails().get(i).getLocation(), new Law(m.getMap()
						.get(plan.getTails().get(i)).a, plan.getSigma()));
				m.getPoints().add(h);
			}
		maps.add(m);
	}

	/**
	 * ��������� ������ ��� �������� ����� ������� ��������.
	 * 
	 * @param object
	 *            ��������������� ������
	 * @param plan
	 *            ���� ������
	 * @param num
	 *            ���������� �����
	 * @param result
	 *            ����������� ������; result[0] �������� ������ ����������������
	 *            ��� ����������� ���������� ����� �������� � ������������
	 *            ��������, result[1] - ������� ����������, result[2] - �������
	 *            �� �����������, result[3] - ������� �� �������, result[4] - ��������
	 */
	public void evaluateMap(PosObject object, Plan plan, int num, double eps, double[] result) {
		for (int i = 0; i < plan.getStations().size(); i++) {
			plan.getStation(i).getActiveMap().buildMap(plan.getTails(), plan.getSigma());
		}
		double d = 0;
		double dist = 0;
		double k = 0;
		double ent = 0;
		int n = 0;
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);
			//ent += plan.countEnt(object);
			double[] res = object.locate(plan, eps);
			k += res[0];
			dist += res[1];
			double diff = Point2D.Double.distance(object.getT().getX(), object
					.getT().getY(), object.getProbT().getX(), object.getProbT()
					.getY());
			if (diff <= ((double) Location.tailSize / 2.0))
				n++;
			d += diff;
		}
		result[0] = d / num;
		result[1] = (double) n / (double) num;
		result[2] = k / num;
		result[3] = dist / num;
		result[4] = ent / num;
	}

	/**
	 * ��������� ������� ������������� ������� ���� ���� ������� ��������.
	 * 
	 * @param object
	 *            ��������������� ������
	 * @param plan
	 *            ���� ������
	 * @param num
	 *            ���������� �����
	 * @param m1
	 *            ����� ������ �����
	 * @param m2
	 *            ����� ������ �����
	 * @return ������������� ������� ���� ����
	 */
	public double cmpMapsRel(PosObject object, Plan plan, int num, int m1,
			int m2) {
		maps.get(m1).buildMap(plan.getTails(), plan.getSigma());
		maps.get(m2).buildMap(plan.getTails(), plan.getSigma());
		double d = 0;
		for (int i = 0; i < num; i++) {
			java.lang.Double f = 0.0;
			for (int j = 0; j < plan.getTails().size(); j++) {
				double f_ = (maps.get(m1).getMap().get(plan.getTails().get(j))
						.getA() - maps.get(m2).getMap()
						.get(plan.getTails().get(j)).getA())
						/ maps.get(m2).getMap().get(plan.getTails().get(j))
								.getA();
				if (!f.isInfinite() && !f.isNaN()) {
					f += f_;
				}
			}
			f /= plan.getTails().size();
			d += f;
		}

		return Math.abs(d / num);
	}

	/**
	 * ������� FreeSpaceLoss �� �������� ����������
	 * 
	 * @param d
	 *            ����������
	 * @return FreeSpaceLoss
	 */
	private double countFSL(double d) {
		// -27,55 + 20�log10F+20�log10d
		java.lang.Double t = -27.55 + 20 * Math.log10(2440) + 20
				* Math.log10(d);
		if (t.isInfinite())
			return 0;
		return t;
	}

	/**
	 * ������� ������ ������� �� ������ �� ���� ��� ���������������
	 * 
	 * @param d
	 *            ���������� ����
	 * @return ������ �������
	 */
	private double countExtraPL(int d) {
		// -27,55 + 20�log10F+20�log10d
		return 7 * d;
	}

	/**
	 * �������� ������ ����
	 * 
	 * @return ������ ����
	 */
	public ArrayList<Map> getMaps() {
		return maps;
	}

	/**
	 * �������� �����
	 * 
	 * @param i
	 *            ����� �����
	 * @return ����� � ������� i
	 */
	public Map getMap(int i) {
		return maps.get(i);
	}

	/**
	 * �������� ����� �������� �����
	 * 
	 * @return ����� �������� �����
	 */
	public int getActiveMapNumber() {
		return activeMapNumber;
	}

	/**
	 * ���������� ����� �������� �����
	 * 
	 * @param activeMapNumber
	 *            ����� �������� �����
	 */
	public void setActiveMapNumber(int activeMapNumber) {
		this.activeMapNumber = activeMapNumber;
	}

	/**
	 * �������� �������� �����
	 * 
	 * @return �������� �����
	 */
	public Map getActiveMap() {
		return this.getMap(activeMapNumber);
	}
}
