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

   	/** План здания. */
   	private Plan plan = null;
   	/** Открытый файл. */
   	private File openedFile = null;
   	
   	//объект для позиционирования
   	PosObject object;

	JScrollPane scrollPane = new JScrollPane(panel);
	
	/** Размер конечной ячейки. */
	private int tailSize = 1;
	


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

		//будем прослушивать события мыши 
		panel.addMouseListener(new NewMouseListener());
		panel.addMouseMotionListener(new NewMouseMotionListener());
		panel.setDoubleBuffered(true);
		//panel.setOpaque(true);
		
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
        
        object = new PosObject();
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
	
	/*public void teach(Station st, PosObject object, int num) {
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);
			
		}
	}*/

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
						plan.devide(tailSize);
						panel.repaint();
					}
		    	}
			if ("close".equals(command)) {
				openedFile = null;
				plan = null;
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
                //scrollPane.revalidate();
                scrollPane.getViewport().revalidate();
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
                //System.out.println(tailSize);
                plan.devide(tailSize);
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
