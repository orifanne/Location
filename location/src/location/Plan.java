package location;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * ������������ ���� ������.
 * 
 * @author Pokrovskaya Oksana
 */
public class Plan {

	// ������ ����� �� �����������
	private ArrayList<Double> vDotes = null;
	// ������ ����� �� ���������
	private ArrayList<Double> hDotes = null;

	// ��������������� ������
	private Frame[][] frames = null;

	// ��������� ������
	Frame[] finalFrames = null;
	// ���������� ��������� ��������
	int finalFramesNum = 0;

	/** �������� ������. */
	private ArrayList<Tail> tails = null;
	/** ���������� �������� �����. */
	private int tailsNum = 0;

	/** ������� ������� */
	ArrayList<Station> stations = null;

	/** ������ ����. */
	private ArrayList<Wall> walls = null;

	/** ������� ������. */
	private Border border = null;
	
	
	
	
	
	/**
	 * ������ ���� ��������� �� xml-�����.
	 * 
	 * @param file
	 *            xml-���� � ��������� �����
	 */
	public Plan(File file) {

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

		// ��������� ������� ������
		NodeList n = doc.getElementsByTagName("dote");
		NamedNodeMap k = null;
		int[] xDotes = new int[n.getLength()];
		int[] yDotes = new int[n.getLength()];
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			xDotes[i] = Integer.parseInt(k.getNamedItem("x").getNodeValue());
			yDotes[i] = Integer.parseInt(k.getNamedItem("y").getNodeValue());
		}
		border = new Border(xDotes, yDotes);

		// ��������� ������ ����
		n = doc.getElementsByTagName("wall");
		k = null;
		double x1, x2, y1, y2;
		walls = new ArrayList<Wall>();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			x1 = Double.parseDouble(k.getNamedItem("x1").getNodeValue());
			x2 = Double.parseDouble(k.getNamedItem("x2").getNodeValue());
			y1 = Double.parseDouble(k.getNamedItem("y1").getNodeValue());
			y2 = Double.parseDouble(k.getNamedItem("y2").getNodeValue());
			walls.add(new Wall(x1, y1, x2, y2));
		}

		n = doc.getElementsByTagName("station");
		k = null;
		double x, y;
		String name;
		stations = new ArrayList<Station>();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			x = Double.parseDouble(k.getNamedItem("x").getNodeValue());
			y = Double.parseDouble(k.getNamedItem("y").getNodeValue());
			name = k.getNamedItem("name").getNodeValue();
			// System.out.println(x + " " + y);
			stations.add(new Station(x, y, name));
		}
	}

	/**
	 * ������� ������ ���� ��������� � �������� ������� ��������������� (1,1) (10,10)
	 */
	public Plan() {
		walls = new ArrayList<Wall>();
		stations = new ArrayList<Station>();
		int[] x = {3, 13, 13, 3};
		int[] y = {3, 3, 13, 13};
		border = new Border(x, y);
	}






	/**
	 * ��������� ������� ������� �� ������.
	 * 
	 * @param tailSize
	 *            �������� ������ ������
	 */
	public void devide(int tailSize) {

		double x1, y1, x2, y2;
		vDotes = new ArrayList<Double>();
		hDotes = new ArrayList<Double>();

		for (int i = 0; i < walls.size(); i++) {

			x1 = walls.get(i).getX1();
			x2 = walls.get(i).getX2();
			y1 = walls.get(i).getY1();
			y2 = walls.get(i).getY2();

			if (!vDotes.contains(y1))
				vDotes.add(y1);
			if (!vDotes.contains(y2))
				vDotes.add(y2);
			if (!hDotes.contains(x1))
				hDotes.add(x1);
			if (!hDotes.contains(x2))
				hDotes.add(x2);

		}

		// ��������� ������� �����
		for (int i = 0; i < border.xpoints.length; i++) {
			x1 = border.xpoints[i];
			y1 = border.ypoints[i];
			if (!vDotes.contains(y1))
				vDotes.add(y1);
			if (!hDotes.contains(x1))
				hDotes.add(x1);
		}

		Collections.sort(hDotes);
		Collections.sort(vDotes);

		// System.out.println(vDotesNum + " " + hDotesNum);

		// ���������� ������ �� �������� �����
		frames = new Frame[vDotes.size() - 1][hDotes.size() - 1];
		for (int i = 0; i < vDotes.size() - 1; i++) {
			for (int j = 0; j < hDotes.size() - 1; j++) {
				frames[i][j] = new Frame(hDotes.get(j), vDotes.get(i),
						hDotes.get(j + 1), vDotes.get(i + 1));
				// ���� ���� ����� �� ������ �������
				if (!border.isInternal(frames[i][j])) {
					// ��������� ��� �� ����������� ������������
					frames[i][j].used(true);
					// System.out.println("external");
				}
				// ����� ��������� �� ��������������
				else {
					boolean[] b = new boolean[4];
					b = isBordered(frames[i][j]);
					if (b[0])
						frames[i][j].up = true;
					if (b[1])
						frames[i][j].down = true;
					if (b[2])
						frames[i][j].right = true;
					if (b[3])
						frames[i][j].left = true;
				}
			}
		}

		// ���������� ��������� ������
		finalFrames();

		// ��������� �� �� �������� ������
		doTails(tailSize);

		explodeAllStations();
	}

	/**
	 * ��������� �� �������� ������.
	 * 
	 * @param tailSize
	 *            �������� ������ ������
	 */
	private void doTails(int tailSize) {
		tailsNum = 0;
		tails = new ArrayList<Tail>();
		for (int i = 0; i < finalFramesNum; i++) {
			double a = finalFrames[i].getX2() - finalFrames[i].getX1();
			double b = finalFrames[i].getY2() - finalFrames[i].getY1();

			int finalSizeA = tailSize;
			if (a >= tailSize) {
				if ((a % tailSize) < ((double) tailSize / 2.0))
					while ((a % finalSizeA) != 0)
						finalSizeA++;
				else
					while ((a % finalSizeA) != 0)
						finalSizeA--;
			}

			int finalSizeB = tailSize;
			if (b >= tailSize) {
				if ((b % tailSize) < ((double) tailSize / 2.0))
					while ((b % finalSizeB) != 0)
						finalSizeB++;
				else
					while ((b % finalSizeB) != 0)
						finalSizeB--;
			}

			if ((a >= tailSize) && (b >= tailSize)) {
				for (double u = finalFrames[i].getX1(); u < finalFrames[i]
						.getX2(); u += finalSizeA)
					for (double v = finalFrames[i].getY1(); v < finalFrames[i]
							.getY2(); v += finalSizeB) {
						tails.add(new Tail(u, v, u + finalSizeA, v + finalSizeB));
						tailsNum++;
					}
				continue;
			}

			if (a >= tailSize) {
				for (double u = finalFrames[i].getX1(); u < finalFrames[i]
						.getX2(); u += finalSizeA) {
					tails.add(new Tail(u, finalFrames[i].getY1(), u
							+ finalSizeA, finalFrames[i].getY2()));
					tailsNum++;
				}
				continue;
			}

			if (b >= tailSize) {
				for (double v = finalFrames[i].getY1(); v < finalFrames[i]
						.getY2(); v += finalSizeB) {
					tails.add(new Tail(finalFrames[i].getX1(), v,
							finalFrames[i].getX2(), v + finalSizeB));
					tailsNum++;
				}
				continue;
			}

			tails.add(new Tail(finalFrames[i].getX1(), finalFrames[i].getY1(),
					finalFrames[i].getX2(), finalFrames[i].getY2()));
			tailsNum++;
		}
	}

	/**
	 * ���������� ����� ��� ������� ��� ���� �������, ��������� �������������
	 * �������.
	 */
	public void explodeAllStations() {
		for (int i = 0; i < stations.size(); i++) {
			stations.get(i).explode(tails, this);
			stations.get(i).setTaught(true);
		}
	}

	/**
	 * ���������� ��������� ������. � ������� ����� �������� �� ��������� ��
	 * �������� ������� �� ����������� �� ������� � ��������.
	 */
	private void finalFrames() {
		finalFrames = new Frame[(vDotes.size() - 1) * (hDotes.size() - 1)];
		finalFramesNum = 0;
		for (int i = 0; i < vDotes.size() - 1; i++) {
			for (int j = 0; j < hDotes.size() - 1; j++) {
				if (!frames[i][j].isUsed()) {
					int minW = hDotes.size();
					int k;
					for (k = i; k < vDotes.size() - 1; k++) {
						int t;
						if (frames[k][j].down == true) {
							for (t = j; t < hDotes.size() - 1; t++) {
								if (frames[k][t].right == true)
									break;
							}
							if ((t - j) < minW)
								minW = (t - j);
							break;
						} else {
							for (t = j; t < hDotes.size() - 2; t++) {
								if ((frames[k][t].right == true)
										|| (frames[k][t + 1].down == true))
									break;
							}
							if ((t - j) < minW) {
								minW = (t - j);
								// f = true;
							}
						}
						// System.out.println(t);
					}
					// System.out.println(i + " " + j + "     " + k + " " + (j +
					// minW));
					finalFrames[finalFramesNum] = new Frame(
							frames[i][j].getX1(), frames[i][j].getY1(),
							frames[k][j + minW].getX2(),
							frames[k][j + minW].getY2());
					finalFramesNum++;
					for (int u = i; u <= k; u++)
						for (int v = j; v <= j + minW; v++) {
							frames[u][v].used(true);
							if (u > 0)
								frames[u - 1][v].down = true;
							if (v > 0)
								frames[u][v - 1].right = true;
							if (u < vDotes.size() - 2)
								frames[u + 1][v].up = true;
							if (v < hDotes.size() - 2)
								frames[u][v + 1].left = true;
						}
				}
			}
		}
	}

	/**
	 * ��������, ��������� �� �����.
	 */
	public boolean[] isBordered(Frame f) {
		return isBordered(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}

	/**
	 * ��������, ����� ������� ���������� ������, ������������ ������������ x1
	 * x2 y1 y2. [0] - ������; [1] - �����; [2] - ������; [3] - �����.
	 */
	public boolean[] isBordered(double x1, double y1, double x2, double y2) {
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		for (int i = 0; i < walls.size(); i++) {
			// System.out.println(i);
			if (walls.get(i).intersectsLine((x1 + x2) / 2, y1, (x1 + x2) / 2, y2)) {
				if (walls.get(i).getY1() == walls.get(i).getY2()) {
					if (walls.get(i).getY1() == y1)
						up = true;
					if (walls.get(i).getY2() == y2)
						down = true;
					// continue;
				}
			}
			if (walls.get(i).intersectsLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2)) {
				if (walls.get(i).getX1() == walls.get(i).getX2()) {
					if (walls.get(i).getX1() == x1)
						left = true;
					if (walls.get(i).getX2() == x2)
						right = true;
					// continue;
				}
			}
		}

		boolean[] b1 = new boolean[4];
		if (!(up && down && left && right))
			b1 = border.isBordered(x1, y1, x2, y2);
		boolean[] b = new boolean[4];
		b[0] = up | b1[0];
		b[1] = down | b1[1];
		b[2] = right | b1[2];
		b[3] = left | b1[3];
		return b;
	}

	
	
	
	
	
	/**
	 * �������� ������ ������� �������.
	 */
	public ArrayList<Station> getStations() {
		return stations;
	}

	/**
	 * �������� ������� �������.
	 */
	public Station getStation(int i) {
		return stations.get(i);
	}

	/**
	 * �������� ����� � ������� i.
	 * 
	 * @param i
	 *            ����� �����
	 */
	public Wall getWall(int i) {
		return walls.get(i);
	}

	/**
	 * �������� ������� ������.
	 */
	public Border getBorder() {
		return border;
	}

	/**
	 * �������� ������ ����.
	 */
	public ArrayList<Wall> getWalls() {
		return walls;
	}

	/**
	 * �������� �������� ������.
	 */
	public ArrayList<Tail> getTails() {
		return tails;
	}

	/**
	 * �������� ��������� ������.
	 */
	public Frame[][] getStartFrames() {
		return frames;
	}

	/**
	 * �������� �������� ������.
	 */
	public int getTailsNum() {
		return tailsNum;
	}
	
	/**
	 * �������� �����.
	 * @param x1 �������� ������
	 * @param y1 �������� ������
	 * @param x2 �������� �����
	 * @param y2 �������� �����
	 */
	public void addWall(int x1, int y1, int x2, int y2) {
		Wall w = new Wall(x1, y1, x2, y2);
		if (!walls.contains(w))
			walls.add(w);
	}

	/**
	 * ��������� ���� � ��������� ����.
	 * 
	 * @param file
	 *            ���� ��� ����������
	 */
	public void save(File file) {
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

		doc = builder.newDocument();
		
		Element locEl = doc.createElement("location");
		Element planEl = doc.createElement("plan");
		
		for (int i = 0; i < border.npoints; i++) {
			Element doteEl = doc.createElement("dote");
			doteEl.setAttribute("x", Integer.toString(border.xpoints[i], 10));
			doteEl.setAttribute("y", Integer.toString(border.ypoints[i], 10));
			planEl.appendChild(doteEl);
		}
		for (int i = 0; i < walls.size(); i++) {
			Element wallEl = doc.createElement("wall");
			wallEl.setAttribute("x1", Integer.toString((int) walls.get(i).getX1(), 10));
			wallEl.setAttribute("y1", Integer.toString((int) walls.get(i).getY1(), 10));
			wallEl.setAttribute("x2", Integer.toString((int) walls.get(i).getX2(), 10));
			wallEl.setAttribute("y2", Integer.toString((int) walls.get(i).getY2(), 10));
			planEl.appendChild(wallEl);
		}
		for (int i = 0; i < stations.size(); i++) {
			Element stEl = doc.createElement("station");
			stEl.setAttribute("x", Integer.toString((int) stations.get(i).getX(), 10));
			stEl.setAttribute("y", Integer.toString((int) stations.get(i).getY(), 10));
			stEl.setAttribute("name", stations.get(i).getName());
			planEl.appendChild(stEl);
		}
		
		locEl.appendChild(planEl);
		doc.appendChild(locEl);
		
		String path = null;
		if (file != null)
			path = file.getAbsolutePath();
		else
		{//����� ���� �������� ����� ����� ��� ����������
			
		}
		
		Transformer t = null;
		try {
			t = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e1) {
			e1.printStackTrace(); // ��������
		}
        try {
			t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(path)));
		} catch (FileNotFoundException | TransformerException e) {
			e.printStackTrace(); // ��������
		}
	}
	
	/**
	 * ��������� � ������� 2 �����, �� �������, ����� ���������� �����
	 * 
	 * @param point1
	 *            ������ ����� ��� �������
	 * @param point2
	 *            ������ ����� ��� �������
	 * @param point2d
	 *            ������ �����, ����� �������� ���� ��������
	 * @param point2d2
	 *            ������ �����, ����� �������� ���� ��������
	 */
	public void addBorderPoints(Point2D.Double point1, Point2D.Double point2,
			Point2D point2d, Point2D point2d2) {
		border.addPoints(point1, point2, point2d, point2d2);
	}
}
