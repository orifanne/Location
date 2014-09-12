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
 * Реализует основную функциональность системы локации, организует интерфейс.
 * 
 * @author Pokrovskaya Oksana
 */
public class Location extends JFrame {

	/** Идентификатор для кнопки на панели рисования. */
	final int WALL = 0;
	/** Идентификатор для кнопки на панели рисования. */
	final int BORDER = 1;
	/** Идентификатор для кнопки на панели рисования. */
	final int STATION = 2;
	/** Идентификатор для кнопки на панели рисования. */
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

	/** Объект для позиционирования */
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

	/** Флаг того, что происходит удаление участка стены. */
	boolean deleting = false;

	/** Флаг того, что в файл были внесены изменения. */
	boolean canged = false;

	/**
	 * Координаты точки, начиная с которой удаляется часть стены.
	 */
	Point2D.Double deletePoint = null;

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

		taught = new JRadioButton("taught");
		orign = new JRadioButton("modeled");
		bg = new ButtonGroup();

		bg.add(orign);
		bg.add(taught);

		stationsComboBox = new JComboBox<String>();
		stationsComboBox.addActionListener(new StationsChooseListener());

		// будем прослушивать события мыши
		panel.addMouseListener(new NewMouseListener());
		panel.addMouseMotionListener(new NewMouseMotionListener());
		panel.setDoubleBuffered(true);

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

		instrumentsPanel.add(new JLabel("Scale:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(scale);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		scale.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		instrumentsPanel.add(new JLabel("Tail size:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(scaleTail);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		scaleTail.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		stationsComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		stationsComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(new JLabel("Base station:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(stationsComboBox);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		stationsComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		instrumentsPanel.add(new JLabel("Map type:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(orign);
		orign.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		instrumentsPanel.add(taught);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		taught.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		orign.addActionListener(new RadioListener());
		taught.addActionListener(new RadioListener());

		instrumentsPanel.add(new JLabel("Edit:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		DemoAction wallsAction = new DemoAction("Walls",
				createImageIcon("wall.png"), "Edit walls", 'W');
		DemoAction borderAction = new DemoAction("Border",
				createImageIcon("border.png"), "Edit border", 'B');
		DemoAction stationsAction = new DemoAction("Stations",
				createImageIcon("station.png"), "Edit base stations", 'S');
		DemoAction deleteAction = new DemoAction("Delete",
				createImageIcon("delete.png"), "Delete", 'D');
		toolBar.add(wallsAction);
		toolBar.add(borderAction);
		toolBar.add(stationsAction);
		toolBar.add(deleteAction);
		instrumentsPanel.add(toolBar);
		toolBar.setAlignmentX(JComponent.LEFT_ALIGNMENT);

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

		JMenuItem createItem = new JMenuItem("Create");
		createItem.setFont(font);
		createItem.setActionCommand("create");
		fileMenu.add(createItem);

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setFont(font);
		saveItem.setActionCommand("save");
		fileMenu.add(saveItem);

		JMenuItem saveAsItem = new JMenuItem("Save as");
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

	/**
	 * Получить первую точку, зафиксированную на границе (для перетаскивания)
	 * 
	 * @return первая точка
	 */
	public Point2D.Double getFirstCheckPoint() {
		return firstCheckPoint;
	}

	/**
	 * Получить вторую точку, зафиксированную на границе (для перетаскивания)
	 * 
	 * @return вторая точка
	 */
	public Point2D.Double getSecondCheckPoint() {
		return secondCheckPoint;
	}

	/**
	 * Прослушиватель событий меню.
	 */
	private class NewMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("open".equals(command)) {
				if (canged) {
					if (!saveChanged())
						return;
				}
				JFileChooser fileopen = new JFileChooser();
				FileFilter filter = new ExtensionFileFilter("xml", "xml");
				fileopen.setFileFilter(filter);
				int ret = fileopen.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					openedFile = fileopen.getSelectedFile();
					plan = new Plan(openedFile);
					if (plan.getWalls().size() > 0)
						plan.devide(tailSize);
					panel.setPlan(plan);
					panel.repaint();
					stationsComboBox.removeAllItems();
					for (int i = 0; i < plan.getStations().size(); i++)
						stationsComboBox.addItem(plan.getStation(i).getName());
					canged = false;
				}
			}
			if ("close".equals(command)) {
				if (canged) {
					if (!saveChanged())
						return;
				}
				canged = false;
				openedFile = null;
				plan = null;
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				stationsComboBox.removeAllItems();
				panel.repaint();
			}
			if ("create".equals(command)) {

				if (canged) {
					if (!saveChanged())
						return;
				}

				openedFile = null;
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				plan = new Plan();
				plan.devide(tailSize);
				stationsComboBox.removeAllItems();
				panel.setPlan(plan);
				panel.repaint();
				canged = false;
			}
			if ("save".equals(command)) {
				if (plan != null) {
					if (openedFile != null) {
						plan.save(openedFile);
						canged = false;
					} else {
						JFileChooser filesave = new JFileChooser();
						FileFilter filter = new ExtensionFileFilter("xml",
								"xml");
						filesave.setFileFilter(filter);
						int ret = filesave.showSaveDialog(null);
						if (ret == JFileChooser.APPROVE_OPTION) {
							File f = filesave.getSelectedFile();
							String s = f.getAbsolutePath();
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
							canged = false;
						}
					}
				}
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
						String s1 = null;
						int dotPos = s.lastIndexOf(".");
						if (dotPos > 0) {
							s1 = s.substring(dotPos);
							if (!s1.equals("xml")) {
								s += ".xml";
								f.renameTo(new File(s));
							}
						} else {
							s += ".xml";
							f.renameTo(new File(s));
						}
						plan.save(f);
						canged = false;
					}
				}
			}
		}
	}

	/**
	 * Предлагает сохранить файл, в случае если в него были внесены изменения.
	 * (Да, Нет, Отмена)
	 * 
	 * @return true, если пользователь выбрал Да или Нет, false, если Отмена
	 */
	private boolean saveChanged() {
		Object[] options = { "Yes", "No", "Cancel" };
		int n = JOptionPane.showOptionDialog(null,
				"Do you want to save this file?", "Save",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[2]);
		switch (n) {
		case 0:
			if (plan != null) {
				if (openedFile != null) {
					plan.save(openedFile);
					canged = false;
				} else {
					JFileChooser filesave = new JFileChooser();
					FileFilter filter = new ExtensionFileFilter("xml", "xml");
					filesave.setFileFilter(filter);
					int ret = filesave.showSaveDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {
						File f = filesave.getSelectedFile();
						String s = f.getAbsolutePath();
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
						canged = false;
					}
				}
			}
			break;
		case 1:
			break;
		case 2:
			return false;
		}
		return true;
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
		public void mouseDragged(MouseEvent e) {
			// приводим координаты перетаскивания к нужной кратности
			int x = e.getX();
			x -= x % (panel.getM() * panel.getBar());
			int y = e.getY();
			y -= y % (panel.getM() * panel.getBar());
			switch (instrumentNumber) {
			case BORDER:
				if (dragging) {
					boolean f = false;
					if ((x >= 0) && (y >= 0)) {
						// вертикальный отрезок
						if (firstCheckPoint.getX() == secondCheckPoint.getX()) {
							firstDraggingPoint.setLocation(x / panel.getBar()
									/ panel.getM(), firstCheckPoint.getY());
							secondDraggingPoint.setLocation(x / panel.getBar()
									/ panel.getM(), secondCheckPoint.getY());
							if ((x / panel.getBar() / panel.getM()) != firstCheckPoint
									.getX()) {
								Line2D.Float line = plan.getBorder().checkLine(
										firstCheckPoint, secondCheckPoint);
								if (firstCheckPoint.getY() > secondCheckPoint
										.getY()) {
									if (line.getY1() > line.getY2()) {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP1(),
												line.getP2());
									} else {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP2(),
												line.getP1());
									}
								} else {
									if (line.getY1() > line.getY2()) {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP2(),
												line.getP1());
									} else {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP1(),
												line.getP2());
									}
								}
								if (f)
									f = plan.addBorderPoints(
											firstDraggingPoint,
											secondDraggingPoint,
											firstCheckPoint, secondCheckPoint);
								else {
									dragging = false;
									plan.devide(tailSize);
									firstCheckPoint = null;
									secondCheckPoint = null;
									firstDraggingPoint = null;
									secondDraggingPoint = null;
									panel.repaint();
									return;
								}
								if (f) {
									firstCheckPoint = (Point2D.Double) firstDraggingPoint
											.clone();
									secondCheckPoint = (Point2D.Double) secondDraggingPoint
											.clone();
								} else {
									dragging = false;
									plan.devide(tailSize);
									plan.deleteBorderPoint(firstCheckPoint);
									plan.deleteBorderPoint(secondCheckPoint);
									firstCheckPoint = null;
									secondCheckPoint = null;
									firstDraggingPoint = null;
									secondDraggingPoint = null;
									panel.repaint();
									return;
								}
							}
						}
						// горизонтальный отрезок
						if (firstCheckPoint.getY() == secondCheckPoint.getY()) {
							firstDraggingPoint.setLocation(
									firstCheckPoint.getX(), y / panel.getBar()
											/ panel.getM());
							secondDraggingPoint.setLocation(
									secondCheckPoint.getX(), y / panel.getBar()
											/ panel.getM());
							if ((y / panel.getBar() / panel.getM()) != firstCheckPoint
									.getY()) {
								Line2D.Float line = plan.getBorder().checkLine(
										firstCheckPoint, secondCheckPoint);
								if (firstCheckPoint.getX() > secondCheckPoint
										.getX()) {
									if (line.getX1() > line.getX2()) {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP1(),
												line.getP2());
									} else {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP2(),
												line.getP1());
									}
								} else {
									if (line.getX1() > line.getX2()) {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP2(),
												line.getP1());
									} else {
										f = plan.addBorderPoints(
												firstCheckPoint,
												secondCheckPoint, line.getP1(),
												line.getP2());
									}
								}
								if (f)
									f = plan.addBorderPoints(
											firstDraggingPoint,
											secondDraggingPoint,
											firstCheckPoint, secondCheckPoint);
								else {
									dragging = false;
									plan.devide(tailSize);
									firstCheckPoint = null;
									secondCheckPoint = null;
									firstDraggingPoint = null;
									secondDraggingPoint = null;
									panel.repaint();
									return;
								}
								if (f) {
									firstCheckPoint = (Point2D.Double) firstDraggingPoint
											.clone();
									secondCheckPoint = (Point2D.Double) secondDraggingPoint
											.clone();
								} else {
									dragging = false;
									plan.devide(tailSize);
									plan.deleteBorderPoint(firstCheckPoint);
									plan.deleteBorderPoint(secondCheckPoint);
									firstCheckPoint = null;
									secondCheckPoint = null;
									firstDraggingPoint = null;
									secondDraggingPoint = null;
									panel.repaint();
									return;
								}
							}
						}
						if (f)
							plan.deleteWrongBorderPoints();
						panel.repaint();
					}
				}
				canged = true;
				break;
			case DELETE:
				if (deleting) {
					Point2D.Double p = new Point2D.Double(x / panel.getBar()
							/ panel.getM(), y / panel.getBar() / panel.getM());
					if (((deletePoint.getX() != p.getX()) && (deletePoint
							.getY() != p.getY()))
							|| ((deletePoint.getX() == p.getX()) && (deletePoint
									.getY() == p.getY())))
						return;

					ArrayList<Wall> w1 = plan.findWallsForPoint(deletePoint);
					ArrayList<Wall> w2 = plan.findWallsForPoint(p);
					Wall del = null;
					for (int i = 0; i < w1.size(); i++)
						if (w2.contains(w1.get(i)))
							del = w1.get(i);
					if (del != null) {
						plan.deleteWallPart(deletePoint, p, del);
						deletePoint = (Point2D.Double) p.clone();
						panel.repaint();
					}
				}
				canged = true;
				break;
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
				switch (instrumentNumber) {
				case WALL:
					// запоминаем координаты конца, приводя к нужной кратности
					x2 = e.getX();
					x2 -= x2 % (panel.getM() * panel.getBar());
					y2 = e.getY();
					y2 -= y2 % (panel.getM() * panel.getBar());
					// добавить новую стену
					plan.addWall(x1 / panel.getBar() / panel.getM(),
							y1 / panel.getBar() / panel.getM(),
							x2 / panel.getBar() / panel.getM(),
							y2 / panel.getBar() / panel.getM());
					plan.devide(tailSize);
					panel.repaint();
					break;
				case BORDER:
					if (dragging) {
						firstCheckPoint = null;
						secondCheckPoint = null;
						firstDraggingPoint = null;
						secondDraggingPoint = null;
						dragging = false;
						plan.deleteWrongBorderPoints();
						plan.devide(tailSize);
						panel.repaint();
					}
					break;
				case STATION:
					break;
				case DELETE:
					if (deleting) {
						deleting = false;
						plan.devide(tailSize);
					}
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
				switch (instrumentNumber) {
				case BORDER:
					if ((firstCheckPoint != null) && (secondCheckPoint != null)) {
						Point2D.Double p = new Point2D.Double(x1
								/ panel.getBar() / panel.getM(), y1
								/ panel.getBar() / panel.getM());
						Line2D.Float l = new Line2D.Float(firstCheckPoint,
								secondCheckPoint);
						if (l.intersectsLine(new Line2D.Float(p, p))) {
							dragging = true;
							firstDraggingPoint = (Point2D.Double) firstCheckPoint
									.clone();
							secondDraggingPoint = (Point2D.Double) secondCheckPoint
									.clone();
						}
					}
					break;
				case DELETE:
					Point2D.Double p = new Point2D.Double(x1 / panel.getBar()
							/ panel.getM(), y1 / panel.getBar() / panel.getM());
					if (plan.findWallsForPoint(p).size() != 0) {
						deletePoint = p;
						deleting = true;
					}
					break;
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (plan != null) {
				// приводим координаты клика к нужной кратности
				int x = e.getX();
				x -= x % (panel.getM() * panel.getBar());
				int y = e.getY();
				y -= y % (panel.getM() * panel.getBar());
				switch (instrumentNumber) {
				case BORDER:
					if (!dragging) {
						Point2D.Double p = new Point2D.Double(x
								/ panel.getBar() / panel.getM(), y
								/ panel.getBar() / panel.getM());
						if ((plan.getBorder().containingLine(p)) != null) {
							if (firstCheckPoint == null)
								firstCheckPoint = p;
							else {
								if (plan.getBorder().checkLine(firstCheckPoint,
										p) != null)
									secondCheckPoint = p;
							}
							panel.repaint();
						} else {
							firstCheckPoint = null;
							secondCheckPoint = null;
							panel.repaint();
						}
					}
					break;
				case STATION:
					int i;
					JTextField name = new JTextField();
					JTextField power = new JTextField();
					if ((i = plan.findStation(
							x / panel.getBar() / panel.getM(),
							y / panel.getBar() / panel.getM())) != -1) {
						name.setText(plan.getStation(i).getName());
						power.setText(java.lang.Double.toString(plan
								.getStation(i).getS()));
						final JComponent[] inputs = new JComponent[] {
								new JLabel("Имя:"), name,
								new JLabel("Базовый уровень сигнала:"), power, };
						JOptionPane.showMessageDialog(null, inputs,
								"Edit station data", JOptionPane.PLAIN_MESSAGE);
						if (name.getText() != "")
							plan.setStationName(i, name.getText());
						stationsComboBox.removeAllItems();
						for (int j = 0; j < plan.getStations().size(); j++)
							stationsComboBox.addItem(plan.getStation(j)
									.getName());
						try {
							plan.setStationS(i, java.lang.Double
									.parseDouble(power.getText()));
						} catch (NullPointerException | NumberFormatException e1) {
							// заглушка, здесь ничего не надо делать
						}
					} else {
						final JComponent[] inputs = new JComponent[] {
								new JLabel("Имя:"), name,
								new JLabel("Базовый уровень сигнала:"), power, };
						JOptionPane.showMessageDialog(null, inputs,
								"Edit station data", JOptionPane.PLAIN_MESSAGE);
						try {
							if (name.getText() != "") {
								plan.addStation(
										x / panel.getBar() / panel.getM(),
										y / panel.getBar() / panel.getM(), name
												.getText(), java.lang.Double
												.parseDouble(power.getText()));
								stationsComboBox.addItem(name.getText());
								panel.repaint();
							}
						} catch (NullPointerException | NumberFormatException e1) {
							// заглушка, здесь ничего не надо делать
						}
					}
					canged = true;
					break;
				case DELETE:
					if ((i = plan.findStation(
							x / panel.getBar() / panel.getM(),
							y / panel.getBar() / panel.getM())) != -1) {
						plan.deleteStation(i);
						stationsComboBox.removeAllItems();
						for (int j = 0; j < plan.getStations().size(); j++)
							stationsComboBox.addItem(plan.getStation(j)
									.getName());
						panel.repaint();
					}
					canged = true;
					break;
				}

			}
		}
	}

	/**
	 * Получить номер выбранной станции.
	 * 
	 * @return номер выбранной станции
	 */
	public int getStationNumber() {
		return stationNumber;
	}

	/**
	 * Сообщает, открыт ли какой-нибудь файл.
	 * 
	 * @return true, если открыт файл, false иначе
	 */
	public boolean hasOpenFile() {
		if (openedFile != null)
			return true;
		else
			return false;
	}

	/**
	 * Получить план.
	 * 
	 * @return план
	 */
	public Plan getPlan() {
		return plan;
	}

	/** Обеспечивает работу с выбором инструментов для рисования. */
	class DemoAction extends AbstractAction {

		public DemoAction(String text, Icon icon, String description,
				char accelerator) {
			super(text, icon);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(SHORT_DESCRIPTION, description);
		}

		public void actionPerformed(ActionEvent e) {
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
