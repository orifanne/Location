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
 * 
 * @author Pokrovskaya Oksana
 */
public class Location extends JFrame {

	/** Панель меню. */
	private JMenuBar menu = null;
	/** Ширина окна. */
	private int width = 900;
	/** Высота окна. */
	private int height = 500;

	/** Номер выбранной станции. */
	private int stationNumber = 0;

	/** Номер выбранного инструмента для рисования. */
	private int instrumentNumber = 0;

	/** Переключатели вида карты для отображения. */
	private JRadioButton orign, taught;
	private ButtonGroup bg;
	/** Флаг того, что нужно показывать карту, полученную обучением. */
	boolean displayTaught = false;

	/** Флаг того, что создается файл. */
	boolean creating = false;

	/**
	 * Главный компоновщик, отвечает за взаимное расположение панели
	 * инструментов и поля для рисования.
	 */
	private JPanel mainpanel = new JPanel();

	/** Отвечает за взаимное расположение инструментов на панели. */
	private JPanel instrumentsPanel = new JPanel();

	/** Слайдер для регулировки масштаба. */
	private JSlider scale;

	/** Слайдер для регулировки размера ячеек. */
	private JSlider scaleTail;

	/** Панель, отрисовывающая план здания. */
	private ImagePanel panel = new ImagePanel(width * 5, height * 5, this);

	/** План здания. */
	private Plan plan = null;
	/** Открытый файл. */
	private File openedFile = null;

	// объект для позиционирования
	PosObject object;

	JScrollPane scrollPane = new JScrollPane(panel);

	/** Размер конечной ячейки. */
	private int tailSize = 1;

	/** Список с выбором для станций */
	JComboBox<String> stationsComboBox;

	/** Список с выбором для рисования */
	JComboBox<String> paintComboBox;

	public Location() {
		// заголовок окна
		super("Location");
		// координаты левого верхнего угла + ширина и высота
		setBounds(0, 0, width, height);
		// нельзя изменять размер окна
		setResizable(false);
		createMenu();
		setJMenuBar(menu);
		// завершить программу при закрытии окна
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		taught = new JRadioButton("Построенная алгоритмом обучения");
		orign = new JRadioButton("Со смоделированным распространением сигнала");
		bg = new ButtonGroup();

		bg.add(orign);
		bg.add(taught);

		stationsComboBox = new JComboBox<String>();
		stationsComboBox.addActionListener(new StationsChooseListener());

		paintComboBox = new JComboBox<String>();
		paintComboBox.addActionListener(new PaintChooseListener());
		paintComboBox.addItem("Стены");

		// будем прослушивать события мыши
		panel.addMouseListener(new NewMouseListener());
		panel.addMouseMotionListener(new NewMouseMotionListener());
		panel.setDoubleBuffered(true);
		// panel.setOpaque(true);

		panel.setPreferredSize(new Dimension(width * 5, height * 5));
		panel.setMinimumSize(new Dimension(width * 5, height * 5));

		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollPane.setPreferredSize(new Dimension(width - 200, height));

		scale = new JSlider(JSlider.HORIZONTAL, 1, 10, panel.getM());
		scale.addChangeListener(new NewChangeListener());

		scaleTail = new JSlider(JSlider.HORIZONTAL, 1, 10, tailSize);
		scaleTail.addChangeListener(new NewTailChangeListener());

		// компановка панели инструментов
		instrumentsPanel.setLayout(new BoxLayout(instrumentsPanel,
				BoxLayout.Y_AXIS));
		instrumentsPanel.setPreferredSize(new Dimension(width / 5, height));
		instrumentsPanel.setMinimumSize(new Dimension(width / 5, height));

		instrumentsPanel.add(new JLabel("Масштаб:"));
		instrumentsPanel.add(scale);

		instrumentsPanel.add(new JLabel("Размер ячейки:"));
		instrumentsPanel.add(scaleTail);

		stationsComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		stationsComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(new JLabel("Выбор базовой станции:"));
		instrumentsPanel.add(stationsComboBox);

		instrumentsPanel.add(new JLabel("Выбор карты уровней сигналов:"));
		instrumentsPanel.add(orign);
		instrumentsPanel.add(taught);

		orign.addActionListener(new RadioListener());
		taught.addActionListener(new RadioListener());

		instrumentsPanel.add(new JLabel("Рисование:"));

		paintComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		paintComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(paintComboBox);

		// компановка главной панели
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.X_AXIS));
		mainpanel.add(scrollPane);
		mainpanel.add(instrumentsPanel);

		mainpanel.setDoubleBuffered(true);
		Container container = getContentPane();
		container.add(mainpanel);

		object = new PosObject();
	}

	/**
	 * Создает меню.
	 */
	private void createMenu() {

		Font font = new Font("Verdana", Font.PLAIN, 11);

		menu = new JMenuBar();
		JMenu fileMenu = new JMenu("Файл");
		fileMenu.setFont(font);

		JMenuItem openItem = new JMenuItem("Открыть");
		openItem.setFont(font);
		openItem.setActionCommand("open");
		fileMenu.add(openItem);

		JMenuItem closeItem = new JMenuItem("Закрыть");
		closeItem.setFont(font);
		closeItem.setActionCommand("close");
		fileMenu.add(closeItem);

		JMenuItem createItem = new JMenuItem("Создать");
		closeItem.setFont(font);
		closeItem.setActionCommand("create");
		fileMenu.add(createItem);

		fileMenu.insertSeparator(1);

		menu.add(fileMenu);

		setJMenuBar(menu);

		ActionListener actionListener = new NewMenuListener();
		openItem.addActionListener(actionListener);
		closeItem.addActionListener(actionListener);
	}

	/**
	 * Прослушиватель событий меню. При нажатии на кнопку "Open" вызывает диалог
	 * выбора файла. При нажатии на кнопку "Close" закрывает файл.
	 */
	private class NewMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			// if ("exit".equals(command)) {
			// System.exit(0);
			// }
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
					stationsComboBox.removeAllItems();
					for (int i = 0; i < plan.getStations().size(); i++)
						stationsComboBox.addItem(plan.getStation(i).getName());
				}
			}
			if ("close".equals(command)) {
				openedFile = null;
				plan = null;
				panel.repaint();
				stationsComboBox.removeAllItems();
			}
			if ("create".equals(command)) {
				openedFile = null;
				plan = new Plan();
				panel.repaint();
				stationsComboBox.removeAllItems();
				creating = true;
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
			// scrollPane.revalidate();
			scrollPane.getViewport().revalidate();
		}
	}

	/**
	 * Прослушиватель событий слайдера размера ячейки.
	 */
	private class NewTailChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider js = (JSlider) e.getSource();
			tailSize = js.getValue();
			// System.out.println(tailSize);
			if (plan != null)
				plan.devide(tailSize);
			panel.repaint();
		}
	}

	/**
	 * Прослушиватель событий выбора станции для отображения карты.
	 */
	private class StationsChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			stationNumber = c.getSelectedIndex();
			// System.out.println(c.getSelectedIndex());
			panel.repaint();
		}
	};

	/**
	 * Прослушиватель событий выбора инструментов для рисования.
	 */
	private class PaintChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			instrumentNumber = c.getSelectedIndex();
		}
	};

	/**
	 * Прослушиватель событий выбора типа карты для отбражения (смоделированная
	 * или обученная).
	 */
	private class RadioListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if ((plan != null) && (object != null) && (stationNumber > 0)) {
				if (orign.isSelected())
					displayTaught = false;
				else {
					plan.getStation(stationNumber).teach(object, plan, 1000);
					displayTaught = true;
				}
				panel.repaint();
			}
		}
	}

	/**
	 * Прослушиватель перемещений мыши.
	 */
	private class NewMouseMotionListener implements MouseMotionListener {

		/** Пустой обработчик. */
		public void mouseDragged(MouseEvent e) {
		}

		/** Пустой обработчик. */
		public void mouseMoved(MouseEvent e) {
		}
	}

	/**
	 * Прослушиватель событий мыши.
	 */
	private class NewMouseListener implements MouseListener {

		// флаг того, что происходит рисование
		private boolean drawing = false;
		// координаты начала и конца стены
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		public void mouseReleased(MouseEvent e) {
			drawing = false;
			//запоминаем координаты конца, приводя к нужной кратности
			x2 = e.getX();
			x2 -= x2 % (panel.getM() * panel.getBar());
			y2 = e.getY();
			y2 -= y2 % (panel.getM() * panel.getBar());
			
			//проверяем, должна ли там появиться стена
			switch (instrumentNumber) {
				case 0: 
					//если отрезок горизонтальный или вертикальный
					if ((x1 == x2) || (y1 == y2))
						//и при этом не точка
						if (y1 != x1)
						{
							//добавить новую стену
							plan.addWall(x1 / panel.getBar() / panel.getM(), 
									y1 / panel.getBar() / panel.getM(), 
									x2 / panel.getBar() / panel.getM(), 
									y2 / panel.getBar() / panel.getM());
							System.out.println(x1 + " " + y1 + " : " + x2 + " " + y2);
							panel.repaint();
						}
			}
		}

		/** Пустой обработчик. */
		public void mouseEntered(MouseEvent e) {
		}

		/** Пустой обработчик. */
		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			drawing = true;
			// запоминаем координаты начала, приводя к нужной кратности
			x1 = e.getX();
			x1 -= x1 % (panel.getM() * panel.getBar());
			y1 = e.getY();
			y1 -= y1 % (panel.getM() * panel.getBar());
		}

		/** Пустой обработчик. */
		public void mouseClicked(MouseEvent e) {
		}
	}

	/**
	 * Получить номер выбранной станции.
	 */
	public int getStationNumber() {
		return stationNumber;
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
}
