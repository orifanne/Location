package location;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import javax.swing.event.*;

import java.lang.Math;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javax.swing.filechooser.FileFilter;

/** 
* Реализует основную функциональность . 
* @author Pokrovskaya Oksana
*/
public class Location extends JFrame {

	/** Панель меню. */
    private JMenuBar menu = null;
   	/** Ширина окна. */
   	private int width = 900;
   	/** Высота окна. */
   	private int height = 500;

   	/** Главный компоновщик, отвечает за взаимное расположение панели инструментов и поля для рисования. */
   	private JPanel mainpanel = new JPanel();
   	
   	/** Отвечает за взаимное расположение инструментов на панели. */
   	private JPanel instrumentsPanel = new JPanel();
   	
   	/** Слайдер для регулировки масштаба. */
   	private JSlider scale;
   	
   	/** Слайдер для регулировки размера ячеек. */
   	private JSlider scaleTail;

   	/** Панель, отрисовывающая план здания. */
   	private ImagePanel panel = new ImagePanel(width , height, this);

   	/** Запоминает предыдущие координаты мыши для отрисовки перетаскивания. */
   	private int x, y;

   	/** План здания. */
   	private Plan plan = null;
   	/** Открытый файл. */
   	private File openedFile = null;

	//массив точек по горизонтали
	private ArrayList <Integer> vDotes = null;
	//массив точек по вертикали
	private ArrayList <Integer> hDotes = null;
	//количество точек по горизонтали
	private int hDotesNum;
	//количество точек по вертикали
	private int vDotesNum;

	//вспомогательные фреймы
	private Frame[][] frames;

	//финальные фреймы
	private Frame[] finalFrames;
	//количество финальных фрейймов
	private int finalFramesNum;
	
	JScrollPane scrollPane = new JScrollPane(panel);
	
	/** Размер конечной ячейки. */
	private int tailSize = 1;
	
	/** Конечные ячейки. */
	private Tail[] tails;
	/** Количество конечных ячеек. */
	private int tailsNum;
	//оценка максимального количества конечных ячеек
	private int maxTailsNum;

   	public Location() {
		//заголовок окна
	   	super("Location");
		//координаты левого верхнего угла + ширина и высота
	   	setBounds(0, 0, width, height);
		//нельзя изменять размер окна
	   	setResizable(false);
	   	createMenu();
	   	setJMenuBar(menu);
		//завершить программу при закрытии окна
	   	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	   	init();

		//будем прослушивать события мыши 
		panel.addMouseListener(new NewMouseListener());
		panel.addMouseMotionListener(new NewMouseMotionListener());
		panel.setDoubleBuffered(true);
		
		panel.setPreferredSize(new Dimension(width, height));
		panel.setMinimumSize(new Dimension(width, height));
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollPane.setPreferredSize(new Dimension(width - 200, height));
		
		scale = new JSlider(JSlider.HORIZONTAL, 1, 10, panel.getM());
		scale.addChangeListener(new NewChangeListener());
		
		scaleTail = new JSlider(JSlider.HORIZONTAL, 1, 10, tailSize);
		scaleTail.addChangeListener(new NewTailChangeListener());
		
		//компановка панели инструментов
		instrumentsPanel.setLayout(new BoxLayout(instrumentsPanel, BoxLayout.Y_AXIS));
		instrumentsPanel.add(scale);
		instrumentsPanel.add(scaleTail);

		//компановка главной панели
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.X_AXIS));
		mainpanel.add(scrollPane);
		mainpanel.add(instrumentsPanel);

        mainpanel.setDoubleBuffered(true);
		Container container = getContentPane();
        container.add(mainpanel);
    }

	/** 
 	* Инициализирует данные для начала  .
 	*/
    	public void init() {

    	}

	/**
	* Сообщает, открыт ли какой-нибудь файл.
	*/
	public boolean hasOpenFile() {
		if (openedFile != null)
			return true;
		else
			return false;
	}

	/**
	* Получить план.
	*/
	public Plan getPlan() {
		return plan;
	}
	
	/**
	* Получить конечные ячейки.
	*/
	public Tail[] getTails() {
		return tails;
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
     	public void devide() {
    		if (openedFile == null)
    			return;

		int x1, y1, x2, y2;
		vDotes = new ArrayList<Integer>();
		hDotes = new ArrayList<Integer>();
		vDotesNum = 0;
		hDotesNum = 0;

		for (int i = 0; i < plan.getWalls().length; i++) {
			
			x1 = plan.getWall(i).getX1();
			x2 = plan.getWall(i).getX2();
			y1 = plan.getWall(i).getY1();
			y2 = plan.getWall(i).getY2();

			if (!vDotes.contains(y1)) {
				vDotes.add(y1);
				vDotesNum++;
			}
			if (!vDotes.contains(y2)) {
				vDotes.add(y2);
				vDotesNum++;
			}
			if (!hDotes.contains(x1)) {
				hDotes.add(x1);
				hDotesNum++;
			}
			if (!hDotes.contains(x2)) {
				hDotes.add(x2);
				hDotesNum++;
			}
			
		}

		//заполняем массивы точек
		for (int i = 0; i < plan.getBorder().getDotes().length; i++) {
			x1 = plan.getBorder().getDote(i)[0];
			y1 = plan.getBorder().getDote(i)[1];
			if (!vDotes.contains(y1)) {
				vDotes.add(y1);
				vDotesNum++;
			}
			if (!hDotes.contains(x1)) {
				hDotes.add(x1);
				hDotesNum++;
			}
			
		}
		Collections.sort(hDotes);
		Collections.sort(vDotes);

		//составляем фреймы по массивам точек
		frames = new Frame[vDotesNum - 1][hDotesNum - 1];
		maxTailsNum = 0;
		for (int i = 0; i < vDotesNum - 1; i++) {
			for (int j = 0; j < hDotesNum - 1; j++) {
				frames[i][j] = new Frame(hDotes.get(j), vDotes.get(i), 
						hDotes.get(j + 1), vDotes.get(i + 1));
				//если этот фрейм не внутри контура
				if (!plan.getBorder().isInternal(frames[i][j])) {
					//исключаем его из дальнейшего рассмотрения
					frames[i][j].used(true);
				}
				//иначе проверяем на ограниченность
				else {
					boolean[] b = new boolean[4];
					b = plan.isBordered(frames[i][j]);
					if (b[0])
						frames[i][j].up = true;
					if (b[1])
						frames[i][j].down = true;
					if (b[2])
						frames[i][j].right = true;
					if (b[3])
						frames[i][j].left = true;
					//оцениваем максимальное количество конечных ячеек
					maxTailsNum += (frames[i][j].getX2() - frames[i][j].getX1()) * (frames[i][j].getY2() - frames[i][j].getY1());
				}
			}
		}

		//составляем финальные фреймы
		finalFrames();
		
		//разбиваеи их на конечные ячейки
		doTails();
	}
     	
    /** 
    * Разбивает на конечные ячейки.
    */
    private void doTails() {
		if (openedFile == null)
			return;
    	tailsNum = 0;
    	tails = new Tail[maxTailsNum];
    	for (int i = 0; i < finalFramesNum; i++) {
    		int a = finalFrames[i].getX2() - finalFrames[i].getX1();
    		int b = finalFrames[i].getY2() - finalFrames[i].getY1();
    		
    		int finalSizeA = tailSize;
    		if (a >= tailSize) {
	    		if (((float) a % tailSize) < ((float) tailSize / 2.0))
	    			while ((a % finalSizeA) != 0)
	    				finalSizeA++;
	    		else
	    			while ((a % finalSizeA) != 0)
						finalSizeA--;
    		}
    		
    		int finalSizeB = tailSize;
    		if (b >= tailSize) {
	    		if (((float) b % tailSize) < ((float) tailSize / 2.0))
	    			while ((b % finalSizeB) != 0) 
	    				finalSizeB++;
	    		else
	    			while ((b % finalSizeB) != 0)
	    				finalSizeB--;
    		}
    		
    		if ((a >= tailSize) && (b >= tailSize)) {
	    		for (int u = finalFrames[i].getX1(); u < finalFrames[i].getX2(); u += finalSizeA)
	    			for (int v = finalFrames[i].getY1(); v < finalFrames[i].getY2(); v += finalSizeB) {
	    				tails[tailsNum] = new Tail(u, v, u + finalSizeA, v + finalSizeB);
	    				tailsNum++;
	    			}
	    		continue;
    		}
    		
    		if (a >= tailSize) {
	    		for (int u = finalFrames[i].getX1(); u < finalFrames[i].getX2(); u += finalSizeA) {
	    				tails[tailsNum] = new Tail(u, finalFrames[i].getY1(), u + finalSizeA, finalFrames[i].getY2());
	    				tailsNum++;
	    			}
	    		continue;
    		}
    		
    		if (b >= tailSize) {
	    		for (int v = finalFrames[i].getY1(); v < finalFrames[i].getY2(); v += finalSizeB) {
	    			tails[tailsNum] = new Tail(finalFrames[i].getX1(), v, finalFrames[i].getX2(), v + finalSizeB);
	    			tailsNum++;
	    		}
	    		continue;
    		}
    		
    		tails[tailsNum] = new Tail(finalFrames[i].getX1(), finalFrames[i].getY1(), finalFrames[i].getX2(), finalFrames[i].getY2());
			tailsNum++;
    	}
    }

    /** 
    * Составляет финальные фреймы.
    */
	private void finalFrames() {
		if (openedFile == null)
			return;
		finalFrames = new Frame[(vDotesNum - 1) * (hDotesNum - 1)];
		finalFramesNum = 0;
		for (int i = 0; i < vDotesNum - 1; i++) {
			for (int j = 0; j < hDotesNum - 1; j++) {
				if (!frames[i][j].isUsed()) {
					int minW = hDotesNum;
					int k;
					for (k = i; k < vDotesNum - 1; k++) {
						int t;
						if (frames[k][j].down == true) {
							for (t = j; t < hDotesNum - 1; t++) {
								if (frames[k][t].right == true) 
									break;
							}
							if ((t - j) < minW)
								minW = (t - j);
							break;
						}
						else {
							for (t = j; t < hDotesNum - 2; t++) {
								if ((frames[k][t].right == true) || (frames[k][t + 1].down == true)) 
									break;
							}
							if ((frames[k][t].right == false) && (frames[k][t + 1].down == false))
								t++;
							if ((t - j) < minW) {
								minW = (t - j);
								//f = true;
							}
						}
					}
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
							if (u < vDotesNum - 2)
								frames[u + 1][v].up = true;
							if (v < hDotesNum - 2)
								frames[u][v + 1].left = true;
						}
				}
			}
		}
	}

    	/** 
    	* Создает меню.
    	*/
    	private void createMenu() {

		Font font = new Font("Verdana", Font.PLAIN, 11);
	
		menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
        	fileMenu.setFont(font);
	
		JMenuItem openItem = new JMenuItem("Open");
        	openItem.setFont(font);
		openItem.setActionCommand("open");
        	fileMenu.add(openItem);
        	 
        	JMenuItem closeItem = new JMenuItem("Close");
        	closeItem.setFont(font);
		closeItem.setActionCommand("close");
        	fileMenu.add(closeItem);
	
		fileMenu.insertSeparator(1);
	
		menu.add(fileMenu);
	
		setJMenuBar(menu);

		ActionListener actionListener = new NewMenuListener();
		openItem.addActionListener(actionListener);
		closeItem.addActionListener(actionListener);
    	}

    	/** 
    	* Прослушиватель событий меню.
    	* При нажатии на кнопку "Open" вызывает диалог выбора файла.
    	* При нажатии на кнопку "Close" закрывает файл.
    	*/
    	private class NewMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		    	String command = e.getActionCommand();
		    	//if ("exit".equals(command)) {
			//	System.exit(0);
		    	//}
		    	if ("open".equals(command)) {
				JFileChooser fileopen = new JFileChooser();    
				FileFilter filter = new ExtensionFileFilter("xml", "xml");
    				fileopen.setFileFilter(filter);        
				int ret = fileopen.showDialog(null, "Открыть файл");
				if (ret == JFileChooser.APPROVE_OPTION) {
					openedFile = fileopen.getSelectedFile();
					plan = new Plan(openedFile);
					devide();
					panel.repaint();
				}
		    	}
			if ("close".equals(command)) {
				openedFile = null;
				plan = null;
				vDotes = null;
				hDotes = null;
				panel.repaint();
		    }
		}
    }

    	/** 
    	* Прослушиватель событий слайдера масштаба.
    	* 
    	*/
    	private class NewChangeListener implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				JSlider js = (JSlider) e.getSource();
                panel.setM(js.getValue());
                panel.repaint();
			}
    	}
    	
    	/** 
    	* Прослушиватель событий слайдера размера ячейки.
    	* 
    	*/
    	private class NewTailChangeListener implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				JSlider js = (JSlider) e.getSource();
                tailSize = js.getValue();
                System.out.println(tailSize);
                devide();
                panel.repaint();
			}
    	}

    	/** 
    	* Прослушиватель перемещений мыши.
    	*/
    	private class NewMouseMotionListener implements MouseMotionListener {
	
		/** Пустой обработчик. */
		public void mouseDragged(MouseEvent e) {}
		/** Пустой обработчик. */
		public void mouseMoved(MouseEvent e) {}
    	}

    	/** 
    	* Прослушиватель событий мыши.
    	*/
    	private class NewMouseListener implements MouseListener {

		/** Пустой обработчик. */
		public void mouseReleased(MouseEvent e) {}
		/** Пустой обработчик. */
		public void mouseEntered(MouseEvent e) {}
		/** Пустой обработчик. */
		public void mouseExited(MouseEvent e) {}
		/** Пустой обработчик. */
		public void mousePressed(MouseEvent e) {}
		/** Пустой обработчик. */
		public void mouseClicked(MouseEvent e) {}
    	}

}
