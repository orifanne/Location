package location;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * ������, �������������� ���� ������.
 * 
 * @author Pokrovskaya Oksana
 */
public class ImagePanel extends JPanel {

	/** ������� ������� ��� ��������� */
	private Location location = null;
	/** ������� ����� ��� ��������� ���� */
	private int wPen = 5;
	/** ������� ����� ��� ��������� ������� ����� */
	private int bPen = 1;
	/** ������� ����� ��� ��������� �����, ����������� ����� */
	private int oneMeterPen = 2;
	/** ������� ����� ��� ��������� �������� ������� */
	private int borderPen = 8;
	/** ���������� ��������, ��������� ��� ��������� ������� ������ */
	private int bar = 10;
	/** ������� - ������� ����� ������� ����� � ����� ����� */
	private int m = 3;

	/** ���������� �����, ����������� �� ����������� */
	private int hBars;
	/** ���������� �����, ����������� �� ��������� */
	private int vBars;
	/** ������ ������ */
	private int width;
	/** ������ ������ */
	private int height;

	/** ���������� �������� ��� ����������� ����� */
	private int range = 5;
	/** ����������� �������� ������ ������� */
	private double minA = 5;
	/** ������������ �������� ������ ������� */
	private double maxA = 150;

	/** ��� ������ ������� */
	private double d;
	/** ������� ������������ ����� */
	private double[] r;
	/** ������� ������ ������� */
	private double[] f;

	/** ������������ ������� */
	private int maxM = 10;
	/** ����������� ������� */
	private int minM = 1;

	/**
	 * ������ �����, ������� ����������� ������� ������� (� ������� �������
	 * �����)
	 */
	private double radSt = 0.5;

	/**
	 * ������ �����, ������� ����������� �����, �������������� �������
	 * �������������� ����� ������� (� ������� ������� �����)
	 */
	private double radCheckPont = 0.7;

	/** ���� ������ ��� ��������� */
	Plan plan = null;

	/**
	 * @param width
	 *            ������ ���� ��� ���������
	 * @param height
	 *            ������ ���� ��� ���������
	 * @param l
	 *            ������� ������� ��� ���������
	 */
	ImagePanel(int w, int h, Location l) {
		super();
		width = w;
		height = h;

		// ������ ���������� ������
		Dimension size = new Dimension(width, height);
		setMaximumSize(size);
		setMinimumSize(size);

		location = l;
		plan = location.getPlan();

		// ������������ ���������� �����, ����������� �� ����������� � ���������
		hBars = width / bar;
		vBars = height / bar;

		// ������������ ��� � ������� ��� ���������� ���� �������
		d = (maxA - minA) / range;
		r = new double[range];
		f = new double[range];
		for (int i = 0; i < range; i++) {
			f[i] = (0.5 / (double) range) * i;
			r[i] = d * i;
		}
	}

	/**
	 * ��������� ��������� ����� ������.
	 * 
	 * @param gr
	 *            ����������� ����� ��� ���������
	 */
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		// ������������� ������� �����
		drawBaseLines(g);

		if (plan != null) {

			// ������������ ����� ��� ��������
			drawMap(g);

			// ������������ �����
			drawWalls(g);

			// ������������ ������� ������
			drawBorder(g);

			// ������������ �������� ������
			drawTails(g);

			// drawFinalFrames(g);

			// ������������ ������� �������
			drawStations(g);

			// ������������ �����, �������������� ������� �������������� �����
			// �������
			drawCheckPonts(g);
		}
	}

	/**
	 * ������������ ���������� ������ (������ ������� ������), ������� ��
	 * ������.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ ����� ��� ��������.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ ������, ����������� ������ �������� ������ ������� � ���.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
	 * @param t
	 *            ������
	 * @param a
	 *            ������� ������� � ������
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
	 * ������������ ������� �����.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ ������� ������� �������.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ ������ ���������.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ �����.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ ��������� ������ (��������������, ������� ����������� ��
	 * �������� ������).
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ ������� �������.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ������������ �����, �������������� ������� �������������� �������.
	 * 
	 * @param g
	 *            ����������� ����� ��� ���������
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
	 * ��������� ������ �������������.
	 * 
	 * @param d
	 *            �������� ������ �������� ����
	 * @param e
	 *            �������� ������ �������� ����
	 * @param f
	 *            ������
	 * @param h
	 *            ������
	 */
	public void fillColor(double d, double e, double f, double h) {
		Color c = new Color((float) 0.0, (float) 0.0, (float) 0.1, (float) 0.5);
		Graphics2D g = (Graphics2D) this.getGraphics();
		g.setColor(c);
		g.fillRect((int) d, (int) e, (int) (f - d), (int) (h - e));
	}

	/**
	 * �������� �������.
	 * 
	 * @return �������
	 */
	public int getM() {
		return m;
	}

	/**
	 * ���������� �������.
	 * 
	 * @param m1
	 *            ����� �������� ��������
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
	 * �������� ���������� ��������, ��������� ��� ��������� ������� ������
	 * 
	 * @return ���������� ��������, ��������� ��� ��������� ������� ������
	 */
	public int getBar() {
		return bar;
	}

	/**
	 * ���������� ���������� ��������, ��������� ��� ��������� ������� ������
	 * 
	 * @param ����������
	 *            ��������, ��������� ��� ��������� ������� ������
	 */
	public void setBar(int bar) {
		this.bar = bar;
	}

	/**
	 * ���������� ���� ������
	 * 
	 * @param ����
	 *            ������
	 */
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
}
