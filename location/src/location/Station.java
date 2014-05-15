package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Station extends AbstractStation {
	
	/** Карта уровней сигнала */
	private HashMap<Tail, Law> map;
	
	public HashMap<Tail, Law> getMap() {
		return map;
	}

	public Station() {
		super();
		map = new HashMap<Tail, Law>();
	}

	public Station(int x1, int y1, int s1) {
		super(x1, y1, s1);
		map = new HashMap<Tail, Law>();
	}

	public Station(double x, double y) {
		super(x, y);
		map = new HashMap<Tail, Law>();
	}
	
	public Station(double x, double y, String name) {
		super(x, y, name);
		map = new HashMap<Tail, Law>();
	}

	private double countFSL(double d) {
		//-27,55 + 20·log10F+20·log10d
		return -27.55 + 20 * Math.log10(2440) + 20 * Math.log10(d);
	}

	@Override
	public void explode(Tail tail) {
		double d = Point2D.Double.distance(x, y, tail.getX(), tail.getY());
		Law l = new Law(s - countFSL(d), 0);
		map.put(tail, l);
		//System.out.println(countFSL(d) + " " + s);
	}

	@Override
	public void explode(ArrayList<Tail> tails) {
		for (int i = 0; i < tails.size(); i++)
			explode(tails.get(i));
	}
}
