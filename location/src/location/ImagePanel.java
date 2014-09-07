package location;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Панель, отрисовывающая план здания.
 * 
 * @author Pokrovskaya Oksana
 */
public class ImagePanel extends JPanel {

	/** Система локации для отрисовки */
	private Location location = null;
	/** толщина линии для отрисовки стен */
	private int wPen = 5;
	/** толщина линии для отрисовки базовой сетки */
	private int bPen = 1;
	/** толщина линии для отрисовки сетки, размечающей метры */
	private int oneMeterPen = 2;
	/** толщина линии для отрисовки внешнего контура */
	private int borderPen = 8;
	/** Количество пикселов, отводимое для отрисовки базовой ячейки */
	private int bar = 10;
	/** Масштаб - сколько ячеек базовой сетки в одном метре */
	private int m = 3;

	/** Количество ячеек, умещающихся по горизонтали */
	private int hBars;
	/** Количество ячеек, умещающихся по вертикали */
	private int vBars;
	/** Ширина панели */
	private int width;
	/** Высота панели */
	private int height;

	/** Количество оттенков для отображения карты */
	private int range = 5;
	/** Минимальное значение уровня сигнала */
	private double minA = 5;
	/** Максимальное значение уровня сигнала */
	private double maxA = 150;

	/** шаг уровня сигнала */
	private double d;
	/** границы прозрачности цвета */
	private double[] r;
	/** границы уровня сигнала */
	private double[] f;

	/** Максимальный масштаб */
	private int maxM = 10;
	/** Минимальный масштаб */
	private int minM = 1;

	/**
	 * Радиус круга, которым отбражается базовая станция (в ячейках базовой
	 * сетки)
	 */
	private double radSt = 0.5;

	/**
	 * Радиус круга, которым отбражаются точки, ограничивающие отрезок
	 * перетаскивания части границы (в ячейках базовой сетки)
	 */
	private double radCheckPont = 0.7;

	/** План здания для отрисовки */
	Plan plan = null;

	/**
	 * @param width
	 *            ширина поля для рисования
	 * @param height
	 *            высота поля для рисования
	 * @param l
	 *            система локации для отрисовки
	 */
	ImagePanel(int w, int h, Location l) {
		super();
		width = w;
		height = h;

		// задаем постоянный размер
		Dimension size = new Dimension(width, height);
		setMaximumSize(size);
		setMinimumSize(size);

		location = l;
		plan = location.getPlan();

		// рассчитываем количество ячеек, умещающихся по горизонтали и вертикали
		hBars = width / bar;
		vBars = height / bar;

		// рассчитываем шаг и границы для тображения карт сигнала
		d = (maxA - minA) / range;
		r = new double[range];
		f = new double[range];
		for (int i = 0; i < range; i++) {
			f[i] = (0.5 / (double) range) * i;
			r[i] = d * i;
		}
	}

	/**
	 * Выполняет рисование плана здания.
	 * 
	 * @param gr
	 *            графический объет для рисования
	 */
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		// отрисоываваем базовую сетку
		drawBaseLines(g);

		if (plan != null) {

			// отрисовываем карту сил сигналов
			drawMap(g);

			// отрисовываем стены
			drawWalls(g);

			// отрисовываем внешний контур
			drawBorder(g);

			// отрисовываем конечные ячейки
			drawTails(g);

			// drawFinalFrames(g);

			// отрисовываем базовые станции
			drawStations(g);

			// отрисовываем точки, ограничивающие отрезок перетаскивания части
			// границы
			drawCheckPonts(g);
		}
	}

	/**
	 * Отрисовывает внутренние ячейки (внутри области локаци), выделяя их
	 * цветом.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawInnerFrames(Graphics2D g) {

		Frame[][] frames = plan.getStartFrames();
		for (int i = 0; i < frames.length; i++)
			for (int j = 0; j < frames[0].length; j++)
				if (!frames[i][j].isUsed()) {
					fillColor(frames[i][j].getX1() * m * bar,
							frames[i][j].getY1() * m * bar,
							frames[i][j].getX2() * m * bar,
							frames[i][j].getY2() * m * bar);
				}

	}

	/**
	 * Отрисовывает катру сил сигналов.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawMap(Graphics2D g) {
		if (plan.getStations().size() > 0) {
			ArrayList<Tail> t = plan.getTails();
			Station s = plan.getStation(location.getStationNumber());
			for (int i = 0; i < t.size(); i++) {
				if (location.displayTaught) {
					if (s.getTMap().containsKey(t.get(i))) {
						Law l = s.getTMap().get(t.get(i));
						if (l != null)
							drawMapTail(g, t.get(i), l.getA());
					}
				} else {
					if (s.getMap().containsKey(t.get(i))) {
						Law l = s.getMap().get(t.get(i));
						if (l != null)
							drawMapTail(g, t.get(i), l.getA());
					}
				}
			}
		}
	}

	/**
	 * Отрисовывает ячейку, заполненную цветом согласно уровню сигнала в ней.
	 * 
	 * @param g
	 *            графический объет для рисования
	 * @param t
	 *            ячейка
	 * @param a
	 *            уровень сигнала в ячейке
	 */
	private void drawMapTail(Graphics2D g, Tail t, double a) {
		int i;
		for (i = 0; i < range; i++)
			if (a < r[i])
				break;
		if (i == range)
			i--;

		Color c = new Color((float) 0.0, (float) 0.0, (float) 0.01,
				(float) f[i]);
		g.setColor(c);
		g.fillRect((int) t.getX1() * m * bar, (int) t.getY1() * m * bar,
				(int) (t.getX2() - t.getX1()) * m * bar,
				(int) (t.getY2() - t.getY1()) * m * bar);
	}

	/**
	 * Отрисовывает базовую сетку.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawBaseLines(Graphics2D g) {
		BasicStroke b = new BasicStroke(bPen);
		BasicStroke b1 = new BasicStroke(oneMeterPen);
		g.setColor(Color.GRAY);
		for (int i = 0; i < vBars; i++) {
			if (i % m == 0)
				g.setStroke(b1);
			else
				g.setStroke(b);
			g.drawLine(0, bar * i, width, bar * i);
		}
		for (int i = 0; i < hBars; i++) {
			if (i % m == 0)
				g.setStroke(b1);
			else
				g.setStroke(b);
			g.drawLine(bar * i, 0, bar * i, height);
		}
	}

	/*
	 * public void drawHelpLines(Graphics2D g) { BasicStroke b = new
	 * BasicStroke(bPen); g.setStroke(b); g.setColor(Color.RED); if
	 * (location.hasOpenFile()) { for (int i = 0; i < location.hDotesNum; i++)
	 * g.drawLine(location.h Dotes.get(i) * m * bar, 0, location.hDotes.get(i) *
	 * m * bar, height); for (int i = 0; i < location.vDotesNum; i++)
	 * g.drawLine(0, location.vDotes.get(i) * m * bar, width,
	 * location.vDotes.get(i) * m * bar); }
	 */

	/**
	 * Отрисовывает границу области локации.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawBorder(Graphics2D g) {

		Color green = new Color((float) 0.0, (float) 1.0, (float) 0.0,
				(float) 0.5);
		g.setColor(green);
		BasicStroke brd = new BasicStroke(borderPen);
		g.setStroke(brd);

		Border border = plan.getBorder();
		for (int i = 0; i < border.npoints - 1; i++) {
			g.drawLine(border.xpoints[i] * m * bar,
					border.ypoints[i] * m * bar, border.xpoints[i + 1] * m
							* bar, border.ypoints[i + 1] * m * bar);
		}
		if (border.npoints > 0)
			g.drawLine(border.xpoints[border.npoints - 1] * m * bar,
					border.ypoints[border.npoints - 1] * m * bar,
					border.xpoints[0] * m * bar, border.ypoints[0] * m * bar);

	}

	/**
	 * Отрисовывает ячейки разбиения.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawTails(Graphics2D g) {

		BasicStroke b = new BasicStroke(bPen);
		g.setStroke(b);
		g.setColor(Color.red);
		ArrayList<Tail> t = plan.getTails();
		for (int i = 0; i < t.size(); i++)
			g.drawRect((int) t.get(i).getX1() * m * bar, (int) t.get(i).getY1()
					* m * bar, (int) (t.get(i).getX2() - t.get(i).getX1()) * m
					* bar, (int) (t.get(i).getY2() - t.get(i).getY1()) * m
					* bar);

	}

	/**
	 * Отрисовывает стены.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	void drawWalls(Graphics2D g) {

		g.setColor(Color.BLACK);
		BasicStroke w = new BasicStroke(wPen);
		g.setStroke(w);

		for (int i = 0; i < plan.getWalls().size(); i++) {
			g.drawLine((int) plan.getWall(i).getX1() * m * bar, (int) plan
					.getWall(i).getY1() * m * bar, (int) plan.getWall(i)
					.getX2() * m * bar, (int) plan.getWall(i).getY2() * m * bar);
		}

	}

	/**
	 * Отрисовывает финальные фреймы (прямоугольники, которые разбиваются на
	 * конечные ячейки).
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawFinalFrames(Graphics2D g) {

		g.setColor(Color.BLUE);
		BasicStroke b = new BasicStroke(bPen);
		g.setStroke(b);
		for (int i = 0; i < plan.getFinalFrames().size(); i++)
			g.drawRect(
					(int) (plan.getFinalFrames().get(i).getX1() * m * bar + 5),
					(int) (plan.getFinalFrames().get(i).getY1() * m * bar + 5),
					(int) ((plan.getFinalFrames().get(i).getX2() - plan
							.getFinalFrames().get(i).getX1())
							* m * bar) - 10,
					(int) ((plan.getFinalFrames().get(i).getY2() - plan
							.getFinalFrames().get(i).getY1())
							* m * bar) - 10);

	}

	/**
	 * Отрисовывает базовые станции.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawStations(Graphics2D g) {

		if (plan.getStations().size() > 0) {
			BasicStroke b = new BasicStroke(borderPen);
			g.setStroke(b);
			g.setColor(Color.BLUE);
			for (int i = 0; i < plan.getStations().size(); i++)
				g.drawOval((int) (plan.getStation(i).getX() * m * bar - radSt
						* bar),
						(int) (plan.getStation(i).getY() * m * bar - radSt
								* bar), (int) (radSt * bar * 2), (int) (radSt
								* bar * 2));
			g.setColor(Color.red);
			g.drawOval((int) (plan.getStation(location.getStationNumber())
					.getX() * m * bar - radSt * bar),
					(int) (plan.getStation(location.getStationNumber()).getY()
							* m * bar - radSt * bar), (int) (radSt * bar * 2),
					(int) (radSt * bar * 2));
		}

	}

	/**
	 * Отрисовывает точки, ограничивающие отрезок перетаскивания границы.
	 * 
	 * @param g
	 *            графический объет для рисования
	 */
	public void drawCheckPonts(Graphics2D g) {
		BasicStroke b = new BasicStroke(bPen);
		g.setStroke(b);
		g.setColor(Color.green);
		if (location.getFirstCheckPoint() != null)
			g.drawOval(
					(int) (location.getFirstCheckPoint().getX() * m * bar - radCheckPont
							* bar), (int) (location.getFirstCheckPoint().getY()
							* m * bar - radCheckPont * bar),
					(int) (radCheckPont * bar * 2),
					(int) (radCheckPont * bar * 2));
		if (location.getSecondCheckPoint() != null)
			g.drawOval(
					(int) (location.getSecondCheckPoint().getX() * m * bar - radCheckPont
							* bar), (int) (location.getSecondCheckPoint()
							.getY() * m * bar - radCheckPont * bar),
					(int) (radCheckPont * bar * 2),
					(int) (radCheckPont * bar * 2));
	}

	/**
	 * Заполняет цветом прямоугольник.
	 * 
	 * @param d
	 *            абсцисса левого верхнего угла
	 * @param e
	 *            ордината левого верхнего угла
	 * @param f
	 *            ширина
	 * @param h
	 *            высота
	 */
	public void fillColor(double d, double e, double f, double h) {
		Color c = new Color((float) 0.0, (float) 0.0, (float) 0.1, (float) 0.5);
		Graphics2D g = (Graphics2D) this.getGraphics();
		g.setColor(c);
		g.fillRect((int) d, (int) e, (int) (f - d), (int) (h - e));
	}

	/**
	 * Получить масштаб.
	 * 
	 * @return масштаб
	 */
	public int getM() {
		return m;
	}

	/**
	 * Установить масштаб.
	 * 
	 * @param m1
	 *            новое значение масштаба
	 */
	public void setM(int m1) {
		width += (hBars / m) * (m1 - m) * bar;
		height += (vBars / m) * (m1 - m) * bar;
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		m = m1;
		hBars = width / bar;
		vBars = height / bar;
	}

	/**
	 * Получить количество пикселов, отводимое для отрисовки базовой ячейки
	 * 
	 * @return количество пикселов, отводимое для отрисовки базовой ячейки
	 */
	public int getBar() {
		return bar;
	}

	/**
	 * Установить количество пикселов, отводимое для отрисовки базовой ячейки
	 * 
	 * @param Количество
	 *            пикселов, отводимое для отрисовки базовой ячейки
	 */
	public void setBar(int bar) {
		this.bar = bar;
	}

	/**
	 * Установить план здания
	 * 
	 * @param план
	 *            здания
	 */
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
}
