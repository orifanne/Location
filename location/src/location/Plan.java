package location;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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
import java.util.Arrays;
import java.util.Collections;

/**
 * Представляет план здания.
 * 
 * @author Pokrovskaya Oksana
 */
public class Plan {

	/** Дисперсия по умолчанию. */
	int sigma = 3;

	// массив точек по горизонтали
	private ArrayList<java.lang.Double> vDotes = null;
	// массив точек по вертикали
	private ArrayList<java.lang.Double> hDotes = null;

	// вспомогательные фреймы
	private Frame[][] frames = null;

	/** финальные фреймы */
	private ArrayList<Frame> finalFrames = null;

	/** Конечные ячейки. */
	private ArrayList<Tail> tails = null;
	/** Количество конечных ячеек. */
	private int tailsNum = 0;

	/** Базовые станции */
	ArrayList<Station> stations = null;

	/** Массив стен. */
	private ArrayList<Wall> walls = null;

	/** Внешний контур. */
	private Border border = null;

	/**
	 * Строит план помещения по xml-файлу.
	 * 
	 * @param file
	 *            xml-файл с описанием плана
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
		} // заглушка

		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} // заглушка
		catch (IOException e) {
			e.printStackTrace();
		} // заглушка

		// заполняем внешний контур
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

		// заполняем массив стен
		n = doc.getElementsByTagName("wall");
		k = null;
		double x1, x2, y1, y2;
		walls = new ArrayList<Wall>();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			x1 = java.lang.Double.parseDouble(k.getNamedItem("x1")
					.getNodeValue());
			x2 = java.lang.Double.parseDouble(k.getNamedItem("x2")
					.getNodeValue());
			y1 = java.lang.Double.parseDouble(k.getNamedItem("y1")
					.getNodeValue());
			y2 = java.lang.Double.parseDouble(k.getNamedItem("y2")
					.getNodeValue());
			walls.add(new Wall(x1, y1, x2, y2));
		}

		n = doc.getElementsByTagName("station");
		k = null;
		double x, y, s;
		String name;
		stations = new ArrayList<Station>();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			x = java.lang.Double
					.parseDouble(k.getNamedItem("x").getNodeValue());
			y = java.lang.Double
					.parseDouble(k.getNamedItem("y").getNodeValue());
			name = k.getNamedItem("name").getNodeValue();
			s = java.lang.Double
					.parseDouble(k.getNamedItem("s").getNodeValue());
			stations.add(new Station(x, y, name, s));
		}
	}

	/**
	 * Создает пустой план помещения с областью локации прямоугольником (1,1)
	 * (10,10)
	 */
	public Plan() {
		walls = new ArrayList<Wall>();
		stations = new ArrayList<Station>();
		int[] x = { 3, 13, 13, 3 };
		int[] y = { 3, 3, 13, 13 };
		border = new Border(x, y);
	}

	/**
	 * Разбивает область локации на ячейки.
	 * 
	 * @param tailSize
	 *            желаемый размер ячейки
	 */
	public void devide(int tailSize) {

		double x1, y1, x2, y2;
		vDotes = new ArrayList<java.lang.Double>();
		hDotes = new ArrayList<java.lang.Double>();

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

		// заполняем массивы точек
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

		// составляем фреймы по массивам точек
		frames = new Frame[vDotes.size() - 1][hDotes.size() - 1];
		for (int i = 0; i < vDotes.size() - 1; i++) {
			for (int j = 0; j < hDotes.size() - 1; j++) {
				frames[i][j] = new Frame(hDotes.get(j), vDotes.get(i),
						hDotes.get(j + 1), vDotes.get(i + 1));
				// если этот фрейм не внутри контура
				if (!border.isInternal(frames[i][j])) {
					// исключаем его из дальнейшего рассмотрения
					frames[i][j].used(true);
					// System.out.println("external");
				}
				// иначе проверяем на ограниченность
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

		// составляем финальные фреймы
		finalFrames();

		// разбиваеи их на конечные ячейки
		doTails(tailSize);

		explodeAllStations();
	}

	/**
	 * Разбивает на конечные ячейки.
	 * 
	 * @param tailSize
	 *            желаемый размер ячейки
	 */
	private void doTails(int tailSize) {
		tailsNum = 0;
		tails = new ArrayList<Tail>();
		for (int i = 0; i < finalFrames.size(); i++) {
			double a = finalFrames.get(i).getX2() - finalFrames.get(i).getX1();
			double b = finalFrames.get(i).getY2() - finalFrames.get(i).getY1();

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
				for (double u = finalFrames.get(i).getX1(); u < finalFrames
						.get(i).getX2(); u += finalSizeA)
					for (double v = finalFrames.get(i).getY1(); v < finalFrames
							.get(i).getY2(); v += finalSizeB) {
						tails.add(new Tail(u, v, u + finalSizeA, v + finalSizeB));
						tailsNum++;
					}
				continue;
			}

			if (a >= tailSize) {
				for (double u = finalFrames.get(i).getX1(); u < finalFrames
						.get(i).getX2(); u += finalSizeA) {
					tails.add(new Tail(u, finalFrames.get(i).getY1(), u
							+ finalSizeA, finalFrames.get(i).getY2()));
					tailsNum++;
				}
				continue;
			}

			if (b >= tailSize) {
				for (double v = finalFrames.get(i).getY1(); v < finalFrames
						.get(i).getY2(); v += finalSizeB) {
					tails.add(new Tail(finalFrames.get(i).getX1(), v,
							finalFrames.get(i).getX2(), v + finalSizeB));
					tailsNum++;
				}
				continue;
			}

			tails.add(new Tail(finalFrames.get(i).getX1(), finalFrames.get(i)
					.getY1(), finalFrames.get(i).getX2(), finalFrames.get(i)
					.getY2()));
			tailsNum++;
		}
	}

	/**
	 * Составляет карты сил сигнала для всех станций, моделируя рспрострнение
	 * сигнала.
	 */
	public void explodeAllStations() {
		for (int i = 0; i < stations.size(); i++) {
			stations.get(i).explode(tails, this, sigma);
			stations.get(i).setTaught(true);
		}
	}

	/**
	 * Составляет финальные фреймы. В будущем лучше заменить на разбиение по
	 * принципу запрета на пересечение со стенами и границей.
	 */
	private void finalFrames() {
		finalFrames = new ArrayList<Frame>();
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
							}
						}
					}
					finalFrames.add(new Frame(frames[i][j].getX1(),
							frames[i][j].getY1(), frames[k][j + minW].getX2(),
							frames[k][j + minW].getY2()));
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
	 * Сообщает, каким образом ограничен фрейм.
	 * 
	 * @param f
	 *            фрейм
	 * @return каким образом ограничен фрейм ([0] - сверху; [1] - снизу; [2] -
	 *         справа; [3] - слева)
	 */
	public boolean[] isBordered(Frame f) {
		return isBordered(f.getX1(), f.getY1(), f.getX2(), f.getY2());
	}

	/**
	 * Сообщает, каким образом ограничена ячейка, определяемая координатами x1
	 * x2 y1 y2.
	 * 
	 * @param x1
	 *            абсцисса левого верхнего угла ячейки
	 * @param y1
	 *            ордината левого верхнего угла ячейки
	 * @param x2
	 *            абсцисса правого нижнего угла ячейки
	 * @param y2
	 *            ордината правого нижнего угла ячейки
	 * @return каким образом ограничена ячейка ([0] - сверху; [1] - снизу; [2] -
	 *         справа; [3] - слева)
	 */
	public boolean[] isBordered(double x1, double y1, double x2, double y2) {
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		for (int i = 0; i < walls.size(); i++) {
			// System.out.println(i);
			if (walls.get(i).intersectsLine((x1 + x2) / 2, y1, (x1 + x2) / 2,
					y2)) {
				if (walls.get(i).getY1() == walls.get(i).getY2()) {
					if (walls.get(i).getY1() == y1)
						up = true;
					if (walls.get(i).getY2() == y2)
						down = true;
					// continue;
				}
			}
			if (walls.get(i).intersectsLine(x1, (y1 + y2) / 2, x2,
					(y1 + y2) / 2)) {
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
	 * Получить список базовых станций.
	 * 
	 * @return список базовых станций
	 */
	public ArrayList<Station> getStations() {
		return stations;
	}

	/**
	 * Получить базовую станцию с номером i.
	 * 
	 * @param i
	 *            номер базовой станции
	 * @return базовая станция с номером i
	 */
	public Station getStation(int i) {
		return stations.get(i);
	}

	/**
	 * Получить стену с номером i.
	 * 
	 * @param i
	 *            номер стены
	 * @return стена с номером i
	 */
	public Wall getWall(int i) {
		return walls.get(i);
	}

	/**
	 * Получить границу.
	 * 
	 * @return граница
	 */
	public Border getBorder() {
		return border;
	}

	/**
	 * Получить массив стен.
	 * 
	 * @return массив стен
	 */
	public ArrayList<Wall> getWalls() {
		return walls;
	}

	/**
	 * Получить конечные ячейки.
	 * 
	 * @return конечные ячейки
	 */
	public ArrayList<Tail> getTails() {
		return tails;
	}

	/**
	 * Получить начальные фреймы.
	 * 
	 * @return начальные фреймы
	 */
	public Frame[][] getStartFrames() {
		return frames;
	}

	/**
	 * Добавить стену. Если стена не является горизонтальной или вертикальной,
	 * то она не будет добавлена. Если в результате добавления какие-то отрезки
	 * стен станут дублироваться, то такие стены будут слиты в одну. Если стена
	 * явяется точкой, то она не будет добавлена.
	 * 
	 * @param x1
	 *            абсцисса начала
	 * @param y1
	 *            ордината начала
	 * @param x2
	 *            абсцисса конца
	 * @param y2
	 *            ордината конца
	 */
	public void addWall(int x1, int y1, int x2, int y2) {
		// точки не добавляем
		if ((x1 == x2) && (y1 == y2))
			return;
		Wall w = new Wall(x1, y1, x2, y2);
		ArrayList<Wall> s = new ArrayList<Wall>();
		s.add(w);
		// вертикальная стена
		if (x1 == x2) {
			// перебираем все стены
			for (int i = 0; i < walls.size(); i++) {
				// если очередная стена не вертикальная, пропускаем ее
				if (walls.get(i).getX1() != walls.get(i).getX2())
					continue;
				// иначе проверяем на пересечение с нашей новой стеной
				if (walls.get(i).intersectsLine(w))
					// если пересекается, запоминаем ее
					s.add(walls.get(i));
			}
			// находим максимум и минимум по y
			int maxY = 0;
			int minY = Integer.MAX_VALUE;
			for (int i = 0; i < s.size(); i++) {
				if (s.get(i).getY1() > maxY)
					maxY = (int) s.get(i).getY1();
				if (s.get(i).getY2() > maxY)
					maxY = (int) s.get(i).getY2();
				if (s.get(i).getY1() < minY)
					minY = (int) s.get(i).getY1();
				if (s.get(i).getY2() < minY)
					minY = (int) s.get(i).getY2();
			}
			// удаляем все лишние стены
			for (int i = 0; i < s.size(); i++)
				deleteWall(s.get(i));
			// добавляем одну итоговую
			walls.add(new Wall(x1, minY, x1, maxY));
		}
		// горизонтальная стена
		if (y1 == y2) {
			// перебираем все стены
			for (int i = 0; i < walls.size(); i++) {
				// если очередная стена не горизонтальная, пропускаем ее
				if (walls.get(i).getY1() != walls.get(i).getY2())
					continue;
				// иначе проверяем на пересечение с нашей новой стеной
				if (walls.get(i).intersectsLine(w))
					// если пересекается, запоминаем ее
					s.add(walls.get(i));
			}
			// находим максимум и минимум по x
			int maxX = 0;
			int minX = Integer.MAX_VALUE;
			for (int i = 0; i < s.size(); i++) {
				if (s.get(i).getX1() > maxX)
					maxX = (int) s.get(i).getX1();
				if (s.get(i).getX2() > maxX)
					maxX = (int) s.get(i).getX2();
				if (s.get(i).getX1() < minX)
					minX = (int) s.get(i).getX1();
				if (s.get(i).getX2() < minX)
					minX = (int) s.get(i).getX2();
			}
			// удаляем все лишние стены
			for (int i = 0; i < s.size(); i++)
				deleteWall(s.get(i));
			// добавляем одну итоговую
			walls.add(new Wall(minX, y1, maxX, y1));
		}
	}

	/**
	 * Удалить стену.
	 * 
	 * @param w
	 *            стена, которую нужно удалить
	 */
	public void deleteWall(Wall w) {
		walls.remove(w);
	}

	/**
	 * Сохраняет план в указанный файл.
	 * 
	 * @param file
	 *            файл для сохранения
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
		} // заглушка

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
			wallEl.setAttribute("x1",
					Integer.toString((int) walls.get(i).getX1(), 10));
			wallEl.setAttribute("y1",
					Integer.toString((int) walls.get(i).getY1(), 10));
			wallEl.setAttribute("x2",
					Integer.toString((int) walls.get(i).getX2(), 10));
			wallEl.setAttribute("y2",
					Integer.toString((int) walls.get(i).getY2(), 10));
			planEl.appendChild(wallEl);
		}
		for (int i = 0; i < stations.size(); i++) {
			Element stEl = doc.createElement("station");
			stEl.setAttribute("x",
					Integer.toString((int) stations.get(i).getX(), 10));
			stEl.setAttribute("y",
					Integer.toString((int) stations.get(i).getY(), 10));
			stEl.setAttribute("name", stations.get(i).getName());
			stEl.setAttribute("s",
					java.lang.Double.toString(stations.get(i).getS()));
			// TODO сохранение карт
			/**
			for (int j = 0; j < stations.get(i).getMaps().size(); j++) {
				Element mapEl = doc.createElement("map");
				mapEl.setAttribute("name", stations.get(i).getMap(j).getName());
				
			}
			*/
			planEl.appendChild(stEl);
		}

		locEl.appendChild(planEl);
		doc.appendChild(locEl);

		String path = null;
		if (file != null)
			path = file.getAbsolutePath();
		else {// здесь надо добавить выбор имени для сохранения

		}

		Transformer t = null;
		try {
			t = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException
				| TransformerFactoryConfigurationError e1) {
			e1.printStackTrace(); // заглушка
		}
		try {
			t.transform(new DOMSource(doc), new StreamResult(
					new FileOutputStream(path)));
		} catch (FileNotFoundException | TransformerException e) {
			e.printStackTrace(); // заглушка
		}
	}

	/**
	 * Добавляет в границу 2 точки, по порядку, между указанными двумя
	 * 
	 * @param point1
	 *            первая точка для вставки
	 * @param point2
	 *            вторая точка для вставки
	 * @param point2d
	 *            первая точка, между которыми надо вставить
	 * @param point2d2
	 *            вторая точка, между которыми надо вставить
	 * @return true, если точки были добавлены, false иначе
	 */
	public boolean addBorderPoints(Point2D.Double point1,
			Point2D.Double point2, Point2D point2d, Point2D point2d2) {
		return border.addPoints(point1, point2, point2d, point2d2);
	}

	/**
	 * Убирает ненужные точки границы (те, что лежат не в прямых углах границы)
	 */
	public void deleteWrongBorderPoints() {
		border.deleteWrongPoints();
	}

	/**
	 * Удаляет точку границы
	 * 
	 * @param point
	 *            точка, которую нужно удалить
	 */
	public void deleteBorderPoint(Point2D.Double point) {
		border.deletePoint(point);
	}

	/**
	 * Находит станцию с заданными координатами
	 * 
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 * 
	 * @return номер найденной станции, или -1, если станции с такими
	 *         координатами не существует
	 */
	public int findStation(int x, int y) {
		for (int i = 0; i < stations.size(); i++)
			if ((stations.get(i).getX() == x) && (stations.get(i).getY() == y))
				return i;
		return -1;
	}

	/**
	 * Установить имя базовой станции
	 * 
	 * @param i
	 *            номер базовой станции
	 * @param text
	 *            имя
	 */
	public void setStationName(int i, String text) {
		stations.get(i).setName(text);
	}

	/**
	 * Установить базовый уровень сигнала станции
	 * 
	 * @param i
	 *            номер базовой станции
	 * @param s
	 *            базовый уровень сигнала станции
	 */
	public void setStationS(int i, double s) {
		stations.get(i).setS(s);
		stations.get(i).explode(tails, this, 3);
	}

	/**
	 * Добавить базовую станцию.
	 * 
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 * @param text
	 *            имя
	 * @param s
	 *            базовый уровень сигнала станции
	 */
	public void addStation(int x, int y, String text, double s) {
		Station s1 = new Station(x, y, text, s);
		stations.add(s1);
		stations.get(stations.indexOf(s1)).explode(tails, this, 3);
	}

	/**
	 * Удалить базовую станцию с указанным номером. Если станции с таким номером
	 * нет, то никакая станция не будет удалена.
	 * 
	 * @param i
	 *            номер станции для удаления
	 */
	public void deleteStation(int i) {
		try {
			stations.remove(i);
		} catch (IndexOutOfBoundsException e) {
			// ignore
		}
	}

	/**
	 * Находит стены, содержащие заданную точку.
	 * 
	 * @param p
	 *            точка
	 * 
	 * @return список стен (если нет ни одной стены, содержащей данную точку, то
	 *         список будет пуст)
	 */
	public ArrayList<Wall> findWallsForPoint(Point2D.Double p) {
		ArrayList<Wall> w = new ArrayList<Wall>();
		for (int i = 0; i < walls.size(); i++)
			if (walls.get(i).intersectsLine(new Line2D.Double(p, p)))
				w.add(walls.get(i));
		return w;
	}

	/**
	 * Удаляет часть стены, заданную точками. Если какая-либо из точек не лежит
	 * на стене, то ничего не удаляется.
	 * 
	 * @param p1
	 *            первая точка на стене
	 * @param p2
	 *            вторая точка на стене
	 * @param w
	 *            стена
	 */
	public void deleteWallPart(Point2D.Double p1, Point2D.Double p2, Wall w) {
		deleteWall(w);
		// точки не удаляем
		if (((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))
				|| (!w.intersectsLine(new Line2D.Double(p1, p1)))
				|| (!w.intersectsLine(new Line2D.Double(p2, p2))))
			return;
		double[] s = new double[4];
		// вертикальная стена
		if (p1.getX() == p2.getX()) {
			s[0] = p1.getY();
			s[1] = p2.getY();
			s[2] = w.getY1();
			s[3] = w.getY2();
			Arrays.sort(s);
			if (s[0] != s[1])
				walls.add(new Wall(p1.getX(), s[0], p1.getX(), s[1]));
			if (s[2] != s[3])
				walls.add(new Wall(p1.getX(), s[2], p1.getX(), s[3]));
		}
		// горизонтальная стена
		if (p1.getY() == p2.getY()) {
			s[0] = p1.getX();
			s[1] = p2.getX();
			s[2] = w.getX1();
			s[3] = w.getX2();
			Arrays.sort(s);
			if (s[0] != s[1])
				walls.add(new Wall(s[0], p1.getY(), s[1], p1.getY()));
			if (s[2] != s[3])
				walls.add(new Wall(s[2], p1.getY(), s[3], p1.getY()));
		}
	}

	/**
	 * Получить финальные фреймы.
	 * 
	 * @return финальные фреймы
	 */
	public ArrayList<Frame> getFinalFrames() {
		return finalFrames;
	}

	/**
	 * Получить среднеквадратическое отклонение
	 * 
	 * @return среднеквадратическое отклонение
	 */
	public int getSigma() {
		return sigma;
	}

	/**
	 * Задать среднеквадратическое отклонение
	 * 
	 * @param sigma
	 *            среднеквадратическое отклонение
	 */
	public void setSigma(int sigma) {
		this.sigma = sigma;
	}
}
