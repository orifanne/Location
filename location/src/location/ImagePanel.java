package location;

import javax.swing.*;

import java.awt.*;

/** 
* Панель, отрисовывающая план здания.
* @author Pokrovskaya Oksana
*/
public class ImagePanel extends JPanel {

	private Location location = null;
	//толщина линии для отрисовки стен
    private int wPen = 5;
	//толщина линии для отрисовки базовой сетки
    private int bPen = 1;
	//толщина линии для отрисовки внешнего контура
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
	
	/** Максимальный масштаб */
	private int maxM = 10;
	/** Минимальный масштаб */
	private int minM = 1;
	
	/** Радиус круга, которым отбражается базовая станция (в ячейках базовой сетки) */
	private double rad = 0.5;
	
	Plan plan;
	
	/**
	 * @return масштаб
	 */
	public int getM() {
		return m;
	}
	
	/**
	 * Установить масштаб.
	 * @param m1 новое значение масштаба
	 */
	public void setM(int m1) {
		width += (hBars / m) * (m1 - m) * bar;
		height += (vBars / m) * (m1 - m) * bar;
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		m = m1;
		hBars = width / bar;
		vBars = height / bar;
		
		System.out.println(width + " " + height);
	}


    /** 
    * @param width ширина поля для рисования
    * @param height высота поля для рисования
    */
    ImagePanel(int w, int h, Location l) {
		super();
		width = w;
		height = h;
	
		//задаем постоянный размер
		Dimension size = new Dimension(width, height);
		setMaximumSize(size);
		setMinimumSize(size);
			
		location = l;
	
		//рассчитываем количество ячеек, умещающихся по горизонтали и вертикали
		hBars = width / bar;
		vBars = height / bar;
   	}

   	public void update(Graphics gr) {
   		paintComponent(gr);
   	}

    	public void paintComponent(Graphics gr) {
        	super.paintComponent(gr);
			Graphics2D g = (Graphics2D) gr;
			plan = location.getPlan();
			
			//отрисоываваем базовую сетку
			drawBaseLines(g);
			
			//отрисовываем стены
			drawWalls(g);

			//отрисовываем внешний контур
			drawBorder(g);
			
			drawInnerFrames(g);
			
			//отрисовываем конечные ячейки
			drawTails(g);
			
			//отрисовываем базовые станции
			drawStations(g);
 	}
    	
    public void drawInnerFrames(Graphics2D g) {
    	if (location.hasOpenFile()) {
    		Frame[][] frames = location.getStartFrames();
			for (int i = 0; i < frames.length; i++)
				for (int j = 0; j < frames[0].length; j++)
					if (!frames[i][j].isUsed()) {
						fillColor(frames[i][j].getX1() * m * bar, 
						frames[i][j].getY1() * m * bar,
						frames[i][j].getX2() * m * bar, 
						frames[i][j].getY2() * m * bar);
						System.out.println(i + " " + j);
					}
    	}
    }
    	
    public void drawBaseLines(Graphics2D g) {
       	
    	BasicStroke b = new BasicStroke(bPen); 
		g.setStroke(b);
		g.setColor(Color.GRAY);
		for (int i = 0; i < vBars; i++)
			g.drawLine(0, bar * i, width, bar * i);
		for (int i = 0; i < hBars; i++)
			g.drawLine(bar * i, 0, bar * i, height);
   }
    
    /*public void drawHelpLines(Graphics2D g) {
    	BasicStroke b = new BasicStroke(bPen);
    	g.setStroke(b);
		g.setColor(Color.RED);
		if (location.hasOpenFile()) {
			for (int i = 0; i < location.hDotesNum; i++)
				g.drawLine(location.hDotes.get(i) * m * bar, 0, 
				location.hDotes.get(i) * m * bar, height);
			for (int i = 0; i < location.vDotesNum; i++)
				g.drawLine(0, location.vDotes.get(i) * m * bar, 
				width, location.vDotes.get(i) * m * bar);
    }*/
    
    public void drawBorder(Graphics2D g) {
    	
    	Color green = new Color((float) 0.0, (float) 1.0, (float) 0.0, (float) 0.5);
		g.setColor(green);
		BasicStroke brd = new BasicStroke(borderPen); 
		g.setStroke(brd);
		if (location.hasOpenFile()) {
			Border border = plan.getBorder();
			for (int i = 0; i < border.getDotes().length - 1; i++) {
				g.drawLine(border.getDote(i)[0] * m * bar, 
				border.getDote(i)[1] * m * bar, 
				border.getDote(i + 1)[0] * m * bar, 
				border.getDote(i + 1)[1] * m * bar);
			}
			//g.drawPolygon(border);
		}
    }

    public void drawTails(Graphics2D g) {
    	
    	if (location.hasOpenFile()) {
    		BasicStroke b = new BasicStroke(bPen); 
			g.setStroke(b);
			g.setColor(Color.red);
			Tail[] t = location.getTails();
			for (int i = 0; i < location.getTailsNum(); i++)
				g.drawRect((int) t[i].getX1() * m * bar,
						(int) t[i].getY1() * m * bar,
						(int) (t[i].getX2() - t[i].getX1()) * m * bar,
						(int) (t[i].getY2() - t[i].getY1()) * m * bar);
		}
    }
    
    void drawWalls(Graphics2D g) {
    	
    	g.setColor(Color.BLACK);
		BasicStroke w = new BasicStroke(wPen); 
		g.setStroke(w);
		if (location.hasOpenFile())
			for (int i = 0; i < plan.getWalls().length; i++) {
				g.drawLine((int) plan.getWall(i).getX1() * m * bar, 
						(int) plan.getWall(i).getY1() * m * bar, 
						(int) plan.getWall(i).getX2() * m * bar, 
						(int) plan.getWall(i).getY2() * m * bar);
			}
    }
    
    /*public void drawFinalFrames(Graphics2D g) {
    	
    	if (location.hasOpenFile()) {
			g.setColor(Color.BLUE);
			//g.setStroke(brd);
			for (int i = 0; i < location.finalFramesNum; i++)
				g.drawRect(location.finalFrames[i].getX1() * m * bar + 5,
					location.finalFrames[i].getY1() * m * bar + 5,
				((location.finalFrames[i].getX2() - location.finalFrames[i].getX1()) * m * bar) - 10,
 				((location.finalFrames[i].getY2() - location.finalFrames[i].getY1()) * m * bar) - 10);
    }*/
    	
    public void drawStations(Graphics2D g) {
    	
		if (location.hasOpenFile()) {
			BasicStroke b = new BasicStroke(borderPen); 
			g.setStroke(b);
			g.setColor(Color.BLUE);
			for (int i = 0; i < plan.getStations().size(); i++)
				g.drawOval((int) (plan.getStation(i).getX() * m * bar - rad * bar), 
						(int) (plan.getStation(i).getY() * m * bar - rad * bar), 
						(int) (rad * bar * 2), (int) (rad * bar * 2));
		}
    }

    /** 
    * Заполняет цветом прямоугольник.
    */
	public void fillColor(double d, double e, double f, double h) {
		Color c = new Color((float) 0.0, (float) 0.0, (float) 0.1, (float) 0.5);
		Graphics2D g = (Graphics2D) this.getGraphics();
		g.setColor(c);
		g.fillRect((int) d, (int) e, (int) (f - d), (int) (h - e));
	}
}
