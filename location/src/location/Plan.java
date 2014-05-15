package location;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/** 
* Представляет план здания. 
* @author Pokrovskaya Oksana
*/
public class Plan {
	

	//массив точек по горизонтали
	private ArrayList <Double> vDotes = null;
	//массив точек по вертикали
	private ArrayList <Double> hDotes = null;

	//вспомогательные фреймы
	private Frame[][] frames;

	//финальные фреймы
	Frame[] finalFrames;
	//количество финальных фрейймов
	int finalFramesNum;
	
	/** Конечные ячейки. */
	private  ArrayList <Tail> tails;
	/** Количество конечных ячеек. */
	private int tailsNum;
	//оценка максимального количества конечных ячеек
	private int maxTailsNum;
	
	/** Базовые станции */
	ArrayList<Station> stations;
	
	/** Массив стен. */
	private Wall[] walls = null;

	/** Внешний контур. */
	private Border border = null;

	/**
	* Получить массив стен.
	*/
	public Wall[] getWalls() {
		return walls;
	}	
	
	/**
	* Получить конечные ячейки.
	*/
	public ArrayList<Tail> getTails() {
		return tails;
	}
	
	/**
	* Получить начальные фреймы.
	*/
	public Frame[][] getStartFrames() {
		return frames;
	}
	
	/**
	* Получить конечные ячейки.
	*/
	public int getTailsNum() {
		return tailsNum;
	}
		

	/** 
  	* Разбивает область локации на ячейки.
  	*/
     	public void devide(int tailSize) {

		double x1, y1, x2, y2;
		vDotes = new ArrayList<Double>();
		hDotes = new ArrayList<Double>();

		for (int i = 0; i < walls.length; i++) {
			
			x1 = walls[i].getX1();
			x2 = walls[i].getX2();
			y1 = walls[i].getY1();
			y2 = walls[i].getY2();

			if (!vDotes.contains(y1))
				vDotes.add(y1);
			if (!vDotes.contains(y2))
				vDotes.add(y2);
			if (!hDotes.contains(x1))
				hDotes.add(x1);
			if (!hDotes.contains(x2))
				hDotes.add(x2);
			
		}

		//заполняем массивы точек
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
		
		//System.out.println(vDotesNum + " " + hDotesNum);

		//составляем фреймы по массивам точек
		frames = new Frame[vDotes.size() - 1][hDotes.size() - 1];
		maxTailsNum = 0;
		for (int i = 0; i < vDotes.size() - 1; i++) {
			for (int j = 0; j < hDotes.size() - 1; j++) {
				frames[i][j] = new Frame(hDotes.get(j), vDotes.get(i), 
						hDotes.get(j + 1), vDotes.get(i + 1));
				//если этот фрейм не внутри контура
				if (!border.isInternal(frames[i][j])) {
					//исключаем его из дальнейшего рассмотрения
					frames[i][j].used(true);
					//System.out.println("external");
				}
				//иначе проверяем на ограниченность
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
					//System.out.println(b[0] + " " + " " + b[1] + " " + b[2] + " " + b[3]);
					//оцениваем максимальное количество конечных ячеек
					maxTailsNum += (frames[i][j].getX2() - frames[i][j].getX1()) * (frames[i][j].getY2() - frames[i][j].getY1());
				}
			}
		}

		//составляем финальные фреймы
		finalFrames();
		
		//разбиваеи их на конечные ячейки
		doTails(tailSize);
		
		explodeAllStations();
	}
     	
    	
    /** 
    * Разбивает на конечные ячейки.
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
	    		for (double u = finalFrames[i].getX1(); u < finalFrames[i].getX2(); u += finalSizeA)
	    			for (double v = finalFrames[i].getY1(); v < finalFrames[i].getY2(); v += finalSizeB) {
	    				tails.add(new Tail(u, v, u + finalSizeA, v + finalSizeB));
	    				tailsNum++;
	    			}
	    		continue;
    		}
    		
    		if (a >= tailSize) {
	    		for (double u = finalFrames[i].getX1(); u < finalFrames[i].getX2(); u += finalSizeA) {
	    			tails.add(new Tail(u, finalFrames[i].getY1(), u + finalSizeA, finalFrames[i].getY2()));
	    				tailsNum++;
	    			}
	    		continue;
    		}
    		
    		if (b >= tailSize) {
	    		for (double v = finalFrames[i].getY1(); v < finalFrames[i].getY2(); v += finalSizeB) {
	    			tails.add(new Tail(finalFrames[i].getX1(), v, finalFrames[i].getX2(), v + finalSizeB));
	    			tailsNum++;
	    		}
	    		continue;
    		}
    		
    		tails.add(new Tail(finalFrames[i].getX1(), finalFrames[i].getY1(), finalFrames[i].getX2(), finalFrames[i].getY2()));
			tailsNum++;
    	}
    }
    

	public void explodeAllStations() {
		for (int i = 0; i < stations.size(); i++) {
			stations.get(i).explode(tails, this);
		}
	}

    /** 
    * Составляет финальные фреймы.
    * В будущем лучше заменить на разбиение по принципу запрета на пересечение со стенами и границей.
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
						}
						else {
							for (t = j; t < hDotes.size() - 2; t++) {
								if ((frames[k][t].right == true) || (frames[k][t + 1].down == true)) 
									break;
							}
							if ((t - j) < minW) {
								minW = (t - j);
								//f = true;
							}
						}
						//System.out.println(t);
					}
					//System.out.println(i + " " + j + "     " + k + " " + (j + minW));
					finalFrames[finalFramesNum] = new Frame(frames[i][j].getX1(),
					frames[i][j].getY1(), frames[k][j + minW].getX2(),
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
	* Добавить базовую станцию.
	*/
	//private void addStation(double x, double y) {
		//stations.add(new Station(x, y));
	//}
	
	/**
	* Получить список базовых станций.
	*/
	public ArrayList<Station> getStations() {
		return stations;
	}
	
	/**
	* Получить базовую станцию.
	*/
	public Station getStation(int i) {
		return stations.get(i);
	}

    /** 
    * Получить стену с номером i.
	* @param i номер стены
    */
	public Wall getWall(int i) {
		return walls[i];
	}

	/**
	* Получить внешний контур.
	*/
	public Border getBorder() {
		return border;
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

		//заполняем внешний контур
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

		//заполняем массив стен
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
			//System.out.println(x + " " + y);
			stations.add(new Station(x, y, name));
		}
	}

    	/** 
    	* Сообщает, ограничен ли фрейм.
    	*/
	public boolean[] isBordered(Frame f) {
		return isBordered(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}

    	/** 
    	* Сообщает, каким образом ограничена ячейка, определяемая координатами x1 x2 y1 y2.
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
		for(int i = 0; i < walls.length; i++) {
			//System.out.println(i);
			if (walls[i].intersectsLine((x1+ x2) / 2, y1, (x1+ x2) / 2, y2)) {
				if (walls[i].getY1() == walls[i].getY2()) {
					if (walls[i].getY1() == y1)
						up = true;
					if (walls[i].getY2() == y2)
						down = true;
					//continue;
				}
			}
			if (walls[i].intersectsLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2)) {
				if (walls[i].getX1() == walls[i].getX2()) {
					if (walls[i].getX1() == x1)
						left = true;
					if (walls[i].getX2() == x2)
						right = true;
					//continue;
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
}
