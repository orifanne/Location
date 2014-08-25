package location;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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

	final int WALL = 0;
	final int BORDER = 1;
	final int STATION = 2;
	final int DELETE = 3;

	/** Панель меню. */
	private JMenuBar menu = null;
	/** Ширина окна. */
	private int width = 900;
	/** Высота окна. */
	private int height = 500;

	/** Номер выбранной станции. */
	private int stationNumber = 0;

	/** Номер выбранного инструмента для рисования. */
	private int instrumentNumber = WALL;

	/** Переключатели вида карты для отображения. */
	private JRadioButton orign, taught;
	/** Группа переключателей вида карты для отображения. */
	private ButtonGroup bg;
	/** Флаг того, что нужно показывать карту, полученную обучением. */
	boolean displayTaught = false;

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

	/** Панель инструментов. */
	private JToolBar toolBar;

	/** Панель, отрисовывающая план здания. */
	private ImagePanel panel = new ImagePanel(width * 5, height * 5, this);

	/** План здания. */
	private Plan plan = null;
	/** Открытый файл. */
	private File openedFile = null;

	// объект для позиционирования
	PosObject object;

	/**
	 * Прокручиваемое поле, котоорое вмещает панель для отображения плана
	 * здания.
	 */
	JScrollPane scrollPane = new JScrollPane(panel);

	/** Размер конечной ячейки. */
	private int tailSize = 1;

	/** Список с выбором для станций */
	JComboBox<String> stationsComboBox;

	/** Список с выбором для рисования */
	JComboBox<String> paintComboBox;

	/** Флаг того, что происходит перетаскивание участка границы. */
	boolean dragging = false;

	/**
	 * Координаты первой точки, зафиксированной на границе (для отметки участка
	 * для перетаскивания)
	 */
	Point2D.Double firstCheckPoint = null;

	/**
	 * Координаты второй точки, зафиксированной на границе (для отметки участка
	 * для перетаскивания)
	 */
	Point2D.Double secondCheckPoint = null;

	/**
	 * Координаты первой точки, зафиксированной на границе (для перетаскивания)
	 */
	Point2D.Double firstDraggingPoint = null;

	/**
	 * Координаты второй точки, зафиксированной на границе (для перетаскивания)
	 */
	Point2D.Double secondDraggingPoint = null;

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

		/*
		 * paintComboBox = new JComboBox<String>();
		 * paintComboBox.addActionListener(new PaintChooseListener());
		 * paintComboBox.addItem("Стены");
		 */

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

		// instrumentsPanel.add(new JLabel("Рисование:"));

		/*
		 * paintComboBox.setMaximumSize(new Dimension(instrumentsPanel
		 * .getPreferredSize().width, 25)); paintComboBox.setMinimumSize(new
		 * Dimension(instrumentsPanel .getPreferredSize().width, 25));
		 * 
		 * instrumentsPanel.add(paintComboBox);
		 */

		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		DemoAction wallsAction = new DemoAction("Walls",
				createImageIcon("wall.gif"), "Редактировать стены", 'W');
		DemoAction borderAction = new DemoAction("Border",
				createImageIcon("border.gif"),
				"Редактировать границу области локации", 'B');
		DemoAction stationsAction = new DemoAction("Stations",
				createImageIcon("station.gif"),
				"Редактировать базовые станции", 'S');
		DemoAction deleteAction = new DemoAction("Delete",
				createImageIcon("delete.gif"), "Удалить", 'D');
		toolBar.add(wallsAction);
		toolBar.add(borderAction);
		toolBar.add(stationsAction);
		toolBar.add(deleteAction);
		instrumentsPanel.add(toolBar);

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
	 * Создает иконку.
	 * 
	 * @param path
	 *            путь к файлу
	 * @return иконка, или null, если нельзя найти файл
	 */
	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.out.println("Couldn't find file: " + path);
			return null;
		}
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
		createItem.setFont(font);
		createItem.setActionCommand("create");
		fileMenu.add(createItem);

		JMenuItem saveItem = new JMenuItem("Сохранить");
		saveItem.setFont(font);
		saveItem.setActionCommand("save");
		fileMenu.add(saveItem);

		JMenuItem saveAsItem = new JMenuItem("Сохранить как");
		saveAsItem.setFont(font);
		saveAsItem.setActionCommand("saveas");
		fileMenu.add(saveAsItem);

		fileMenu.insertSeparator(1);
		fileMenu.insertSeparator(3);
		fileMenu.insertSeparator(5);
		fileMenu.insertSeparator(7);

		menu.add(fileMenu);

		setJMenuBar(menu);

		ActionListener actionListener = new NewMenuListener();
		openItem.addActionListener(actionListener);
		closeItem.addActionListener(actionListener);
		createItem.addActionListener(actionListener);
		saveItem.addActionListener(actionListener);
		saveAsItem.addActionListener(actionListener);
	}

	public Point2D.Double getFirstCheckPoint() {
		return firstCheckPoint;
	}

	public Point2D.Double getSecondCheckPoint() {
		return secondCheckPoint;
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
				int ret = fileopen.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					openedFile = fileopen.getSelectedFile();
					plan = new Plan(openedFile);
					if (plan.getWalls().size() > 0)
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
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				plan = new Plan();
				panel.repaint();
				stationsComboBox.removeAllItems();
			}
			if ("save".equals(command)) {
				if ((plan != null) && (openedFile != null))
					plan.save(openedFile);
			}
			if ("saveas".equals(command)) {
				if (plan != null) {
					JFileChooser filesave = new JFileChooser();
					FileFilter filter = new ExtensionFileFilter("xml", "xml");
					filesave.setFileFilter(filter);
					int ret = filesave.showSaveDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {
						File f = filesave.getSelectedFile();
						String s = f.getAbsolutePath();
						System.out.println(s);
						String s1 = null;
						int dotPos = s.lastIndexOf(".");
						if (dotPos > 0) {
							s1 = s.substring(dotPos);
							System.out.println(s1);
							if (!s1.equals("xml")) {
								s += ".xml";
								f.renameTo(new File(s));
							}
						} else {
							s += ".xml";
							f.renameTo(new File(s));
						}
						plan.save(f);
					}
				}
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
		boolean changed = false;

		public void mouseDragged(MouseEvent e) {
			if ((instrumentNumber == BORDER) && (dragging)) {
				// приводим координаты перетаскивания к нужной кратности
				int x = e.getX();
				x -= x % (panel.getM() * panel.getBar());
				int y = e.getY();
				y -= y % (panel.getM() * panel.getBar());
				if ((x >= 0) && (y >= 0)) {
					// System.out.println(x / panel.getBar() / panel.getM() +
					// " "
					// + y / panel.getBar() / panel.getM());
					// вертикальный отрезок
					if (firstCheckPoint.getX() == secondCheckPoint.getX()) {
						// System.out.println("vertical");
						firstDraggingPoint.setLocation(x / panel.getBar()
								/ panel.getM(), firstCheckPoint.getY());
						secondDraggingPoint.setLocation(x / panel.getBar()
								/ panel.getM(), secondCheckPoint.getY());
						// System.out.println(firstDraggingPoint.getX() + " " +
						// firstDraggingPoint.getY());
						if (!changed) {
							if ((x / panel.getBar() / panel.getM()) != firstCheckPoint
									.getX()) {
								System.out.println("changed");
								Line2D.Float[] l = plan.getBorder()
										.containingLine(firstCheckPoint);
								Line2D.Float[] l1 = plan.getBorder()
										.containingLine(secondCheckPoint);
								Line2D.Float line = checkLine(firstCheckPoint,
										secondCheckPoint, l, l1);

								if (firstCheckPoint.getY() > secondCheckPoint
										.getY()) {
									if (line.getY1() > line.getY2()) {
										plan.addBorderPoints(firstCheckPoint,
												secondCheckPoint, line.getP1(),
												line.getP2());
										System.out.println("1");
									} else {
										plan.addBorderPoints(firstCheckPoint,
												secondCheckPoint, line.getP2(),
												line.getP1());
										System.out.println("2");
									}
								} else {
									if (line.getY1() > line.getY2()) {
										plan.addBorderPoints(firstCheckPoint,
												secondCheckPoint, line.getP2(),
												line.getP1());
										System.out.println("3");
									} else {
										plan.addBorderPoints(firstCheckPoint,
												secondCheckPoint, line.getP1(),
												line.getP2());
										System.out.println("4");
									}
								}
								plan.addBorderPoints(firstDraggingPoint,
										secondDraggingPoint, firstCheckPoint,
										secondCheckPoint);
								changed = true;
							}
						} else {

						}
						panel.repaint();
					}
					// горизонтальный отрезок
					if (firstCheckPoint.getX() == secondCheckPoint.getX()) {
						firstDraggingPoint.setLocation(
								firstDraggingPoint.getX(), y);
						secondDraggingPoint.setLocation(
								secondDraggingPoint.getX(), y);
					}
				}
			}
		}

		/** Пустой обработчик. */
		public void mouseMoved(MouseEvent e) {
		}
	}

	/**
	 * Прослушиватель событий мыши.
	 */
	private class NewMouseListener implements MouseListener {

		// координаты начала и конца стены
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		public void mouseReleased(MouseEvent e) {
			if (plan != null) {
				if (dragging) {
					firstCheckPoint = null;
					secondCheckPoint = null;
					firstDraggingPoint = null;
					secondDraggingPoint = null;
					panel.repaint();
					dragging = false;
				}
				// запоминаем координаты конца, приводя к нужной кратности
				x2 = e.getX();
				x2 -= x2 % (panel.getM() * panel.getBar());
				y2 = e.getY();
				y2 -= y2 % (panel.getM() * panel.getBar());

				switch (instrumentNumber) {
				// проверяем, должна ли там появиться стена
				case WALL:
					// если отрезок горизонтальный или вертикальный
					if ((x1 == x2) || (y1 == y2))
						// и при этом не точка
						if (!((x1 == x2) && (y1 == y2))) {
							// добавить новую стену
							plan.addWall(x1 / panel.getBar() / panel.getM(), y1
									/ panel.getBar() / panel.getM(),
									x2 / panel.getBar() / panel.getM(), y2
											/ panel.getBar() / panel.getM());
							System.out.println(x1 + " " + y1 + " : " + x2 + " "
									+ y2);
							panel.repaint();
						}
					break;
				// проверяем, должна ли быть перетащена граница
				case BORDER:
					// если это точка
					if ((x1 == x2) && (y1 == y2))

						break;
				case STATION:
					break;
				case DELETE:
					break;
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
			if (plan != null) {
				// запоминаем координаты начала, приводя к нужной кратности
				x1 = e.getX();
				x1 -= x1 % (panel.getM() * panel.getBar());
				y1 = e.getY();
				y1 -= y1 % (panel.getM() * panel.getBar());
				if ((instrumentNumber == BORDER) && (firstCheckPoint != null)
						&& (secondCheckPoint != null)) {
					Point2D.Double p = new Point2D.Double(x1 / panel.getBar()
							/ panel.getM(), y1 / panel.getBar() / panel.getM());
					Line2D.Float l = new Line2D.Float(firstCheckPoint,
							secondCheckPoint);
					if (l.intersectsLine(new Line2D.Float(p, p))) {
						dragging = true;
						firstDraggingPoint = (Double) firstCheckPoint.clone();
						secondDraggingPoint = (Double) secondCheckPoint.clone();
					}
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if ((plan != null) && (instrumentNumber == BORDER) && (!dragging)) {
				// приводим координаты клика к нужной кратности
				int x = e.getX();
				x -= x % (panel.getM() * panel.getBar());
				int y = e.getY();
				y -= y % (panel.getM() * panel.getBar());
				// System.out.println(x + " " + y);
				Point2D.Double p = new Point2D.Double(x / panel.getBar()
						/ panel.getM(), y / panel.getBar() / panel.getM());
				Line2D.Float l[] = null;
				if ((l = plan.getBorder().containingLine(p)) != null) {
					if (firstCheckPoint == null)
						firstCheckPoint = p;
					else {
						Line2D.Float l1[] = plan.getBorder().containingLine(
								firstCheckPoint);
						if (checkLine(firstCheckPoint, p, l, l1) != null)
							secondCheckPoint = p;
					}
					panel.repaint();
				} else {
					firstCheckPoint = null;
					secondCheckPoint = null;
					panel.repaint();
				}
			}
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

	/**
	 * Получить линию, которой одновременно принадлежат обе точки.
	 * 
	 * @param p1
	 *            первая точка
	 * @param p2
	 *            вторая точка
	 * @param l1
	 *            первая линия
	 * @param l2
	 *            вторая линия
	 * @return линию, которой принадлежат обе точки, или null
	 */
	private Line2D.Float checkLine(Point2D.Double p1, Point2D.Double p2,
			Line2D.Float[] l1, Line2D.Float[] l2) {
		Line2D.Float l = null;
		if (l1[0] != null)
			if ((l1[0].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l1[0].intersectsLine(new Line2D.Float(p2, p2))))
				l = l1[0];
		if (l2[0] != null)
			if ((l2[0].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l2[0].intersectsLine(new Line2D.Float(p2, p2))))
				l = l2[0];
		if (l2[1] != null)
			if ((l2[1].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l2[1].intersectsLine(new Line2D.Float(p2, p2))))
				l = l2[1];
		if (l1[1] != null)
			if ((l1[1].intersectsLine(new Line2D.Float(p1, p1)))
					&& (l1[1].intersectsLine(new Line2D.Float(p2, p2))))
				l = l1[1];
		return l;
	}

	class DemoAction extends AbstractAction {

		public DemoAction(String text, Icon icon, String description,
				char accelerator) {
			super(text, icon);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(SHORT_DESCRIPTION, description);
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println(getValue(NAME).toString());
			switch (getValue(NAME).toString()) {
			case "Walls":
				instrumentNumber = WALL;
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				panel.repaint();
				break;
			case "Border":
				instrumentNumber = BORDER;
				break;
			case "Stations":
				instrumentNumber = STATION;
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				panel.repaint();
				break;
			case "Delete":
				instrumentNumber = DELETE;
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				panel.repaint();
				break;
			}
		}
	}
}
