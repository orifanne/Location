package location;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.ArrayList;

/** 
* ѕредставл€ет план здани€. 
* @author Pokrovskaya Oksana
*/
public class Plan {
	
	/** Ѕазовые станции */
	ArrayList<Station> stations;
	
	/** ћассив стен. */
	private Wall[] walls = null;

	/** ¬нешний контур. */
	private Border border = null;

	/** ћассив вертикальных стен, упор€доченных слева направо.
	* v[i][0] - координата x
	* v[i][1] - меньша€ координата y
	* v[i][2] - больша€ координата y
	*/
	private double[][] v;

	/** ћассив горизонтальных стен, упор€доченных сверху вниз.
	* h[i][0] - координата y
	* h[i][1] - меньша€ координата x
	* h[i][2] - больша€ координата x
	*/
	private double[][] h;

	/**  оличество вертикальных стен. */
	private int vNum;

	/**  оличество горизонтальных стен. */
	private int hNum;

	/**
	* ѕолучить массив стен.
	*/
	public Wall[] getWalls() {
		return walls;
	}
	
	/**
	* ƒобавить базовую станцию.
	*/
	//private void addStation(double x, double y) {
		//stations.add(new Station(x, y));
	//}
	
	/**
	* ѕолучить список базовых станций.
	*/
	public ArrayList<Station> getStations() {
		return stations;
	}
	
	/**
	* ѕолучить список базовых станций.
	*/
	public Station getStation(int i) {
		return stations.get(i);
	}

    /** 
    * ѕолучить стену с номером i.
	* @param i номер стены
    */
	public Wall getWall(int i) {
		return walls[i];
	}

	/**
	* ѕолучить внешний контур.
	*/
	public Border getBorder() {
		return border;
	}

	/** 
	* @param w массив стен
	* @param b внешний контур
	*/
	public Plan(Wall[] w, Border b) {
		walls = w;
		border = b;
		doArrays();
	}

	/** 
  	* ќрганизует массивы вертикальных и горизонтальных стен.
  	*/
	private void doArrays() {
		vNum = 0;
		hNum = 0;
		v = new double[walls.length][3];
		h = new double[walls.length][3];
		//заполн€ем массивы вертикальных и горизонтальных стен
		for (int i = 0; i < walls.length; i++) {
			//если стена вертикальна€
			if (walls[i].getX1() == walls[i].getX2()) {
				v[vNum][0] = walls[i].getX1();
				if (walls[i].getY1() < walls[i].getY2()) {
					v[vNum][1] = walls[i].getY1();
					v[vNum][2] = walls[i].getY2();
				}
				else {
					v[vNum][1] = walls[i].getY2();
					v[vNum][2] = walls[i].getY1();
				}
				vNum++;
			}
			//если стена горизонтальна€
			else {
				h[hNum][0] = walls[i].getY1();
				if (walls[i].getX1() < walls[i].getX2()) {
					h[hNum][1] = walls[i].getX1();
					h[hNum][2] = walls[i].getX2();
				}
				else {
					h[hNum][1] = walls[i].getX2();
					h[hNum][2] = walls[i].getX1();
				}
				hNum++;
			}
		}
	}

	/** 
  	* @param file xml-файл с описанием плана
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

		catch (ParserConfigurationException e) {} //заглушка

		try {
			doc = builder.parse(file);
		}
		catch (SAXException e) {} //заглушка
		catch (IOException e) {} //заглушка

		//заполн€ем внешний контур
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

		//заполн€ем массив стен
		n = doc.getElementsByTagName("wall");
		k = null;
		double x1, x2, y1, y2;
		walls = new Wall[n.getLength()];
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			x1 = Double.parseDouble(k.getNamedItem("x1").getNodeValue());
			x2 = Double.parseDouble(k.getNamedItem("x2").getNodeValue());
			y1 = Double.parseDouble(k.getNamedItem("y1").getNodeValue());
			y2 = Double.parseDouble(k.getNamedItem("y2").getNodeValue());
			walls[i] = new Wall(x1, y1, x2, y2);
		}
		doArrays();
		
		n = doc.getElementsByTagName("station");
		k = null;
		double x, y;
		stations = new ArrayList<Station>();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			x = Double.parseDouble(k.getNamedItem("x").getNodeValue());
			y = Double.parseDouble(k.getNamedItem("y").getNodeValue());
			System.out.println(x + " " + y);
			stations.add(new Station(x, y));
		}
	}

    	/** 
    	* —ообщает, ограничен ли фрейм.
    	*/
	public boolean[] isBordered(Frame f) {
		return isBordered(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}

    	/** 
    	* —ообщает, каким образом ограничена €чейка, определ€ема€ координатами x1 x2 y1 y2.
    	* [0] - сверху;
    	* [1] - снизу;
    	* [2] - справа;
    	* [3] - слева.
    	*/
	public boolean[] isBordered(double x1, double y1, double x2, double y2) {
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		for (int i = 0; i < vNum; i++)
			//если эта стена на одном уровне по вертикали с точкой
			//if ((y < v[i][1]) && (y > v[i][2]) || (y > v[i][1]) && (y < v[i][2])) {
			if ((y > v[i][1]) && (y < v[i][2])) {
				if (right && left)
					break;
				//и примыкает к правому краю
				if (x2 == v[i][0]) 
					right = true;
				//и примыкает к левому краю
				if (x1 == v[i][0]) 
					left = true;
			}
		for (int i = 0; i < hNum; i++)
			//если эта стена на одном уровне по горизонтали с точкой
			//if ((x < h[i][1]) && (x > h[i][2]) || (x > h[i][1]) && (x < h[i][2])) {
			if ((x > h[i][1]) && (x < h[i][2])) {
				if (up && down)
					break;
				//и примыкает к нижнему краю
				if (y2 == h[i][0]) 
					down = true;
				//и примыкает к верхнему краю
				if (y1 == h[i][0]) 
					up = true;
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
}
