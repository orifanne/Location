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
 * ���������� ���������.
 * 
 * @author Pokrovskaya Oksana
 */
public class Location extends JFrame {

	/** ������������� ��� ������ �� ������ ���������. */
	final int WALL = 0;
	/** ������������� ��� ������ �� ������ ���������. */
	final int BORDER = 1;
	/** ������������� ��� ������ �� ������ ���������. */
	final int STATION = 2;
	/** ������������� ��� ������ �� ������ ���������. */
	final int DELETE = 3;

	/** ������ ����. */
	private JMenuBar menu = null;
	/** ������ ����. */
	private int width = 900;
	/** ������ ����. */
	private int height = 500;

	/** ����� ��������� �������. */
	private int stationNumber = 0;

	/** ����� ���������� ����������� ��� ���������. */
	private int instrumentNumber = WALL;

	/**
	 * ������� �����������, �������� �� �������� ������������ ������
	 * ������������ � ���� ��� ���������.
	 */
	private JPanel mainpanel = new JPanel();

	/** �������� �� �������� ������������ ������������ �� ������. */
	private JPanel instrumentsPanel = new JPanel();

	/** ������� ��� ����������� ��������. */
	private JSlider scale;

	/** ������� ��� ����������� ������� �����. */
	private JSlider scaleTail;

	/** ������ ������������. */
	private JToolBar toolBar;

	/** ������, �������������� ���� ������. */
	private ImagePanel panel = new ImagePanel(width * 5, height * 5, this);

	/** ���� ������. */
	private Plan plan = null;
	/** �������� ����. */
	private File openedFile = null;
	
	/** �������������� ������� ����. */
	NewMenuListener menuListener;
	/** �������������� ������� �������� ��������. */
	NewChangeListener changeListener;
	/** �������������� ������� �������� ������� ����. */
	NewTailChangeListener tailChangeListener;
	/** �������������� ������� ������ �� ������ ������� �������. */
	StationsChooseListener stationsChooseListener;
	/** �������������� ������� ������ �� ������ ���� ������� �������. */
	MapChooseListener mapChooseListener;
	/** �������������� ����������� ����. */
	NewMouseMotionListener mouseMotionListener;
	/** �������������� ������� ����. */
	NewMouseListener mouseListener;

	/**
	 * �������������� ����, �������� ������� ������ ��� ����������� �����
	 * ������.
	 */
	JScrollPane scrollPane = new JScrollPane(panel);

	/** ������ �������� ������. */
	private int tailSize = 1;

	/** ������ � ������� ��� ����������� ������ ������� ������� */
	JComboBox<String> stationsComboBox;

	/** ������ � ������� ��� ����������� ������ ���� ������� ������� */
	JComboBox<String> mapComboBox;

	/** ���� ����, ��� ���������� �������� ������� �����. */
	boolean deleting = false;

	/** ���� ����, ��� � ���� ���� ������� ���������. */
	boolean changed = false;
	
	/** ���� ����, ��� ������������ ����� �������� ����� ��� �����������. */
	boolean selectMap = true;

	/**
	 * ���������� �����, ������� � ������� ��������� ����� �����.
	 */
	Point2D.Double deletePoint = null;

	public Location() {
		// ��������� ����
		super("������� ���������� ����������������");
		// ���������� ������ �������� ���� + ������ � ������
		setBounds(0, 0, width, height);
		// ������ �������� ������ ����
		setResizable(false);
		createMenu();
		setJMenuBar(menu);
		// ��������� ��������� ��� �������� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		stationsChooseListener = new StationsChooseListener();
		stationsComboBox = new JComboBox<String>();
		stationsComboBox.addActionListener(stationsChooseListener);

		mapChooseListener = new MapChooseListener();
		mapComboBox = new JComboBox<String>();
		mapComboBox.addActionListener(mapChooseListener);

		mouseListener = new NewMouseListener();
		mouseMotionListener = new NewMouseMotionListener();
		// ����� ������������ ������� ����
		panel.addMouseListener(mouseListener);
		panel.addMouseMotionListener(mouseMotionListener);
		panel.setDoubleBuffered(true);

		panel.setPreferredSize(new Dimension(width * 5, height * 5));
		panel.setMinimumSize(new Dimension(width * 5, height * 5));

		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollPane.setPreferredSize(new Dimension(width - 200, height));

		scale = new JSlider(JSlider.HORIZONTAL, 1, 10, panel.getM());
		changeListener = new NewChangeListener();
		scale.addChangeListener(changeListener);

		tailChangeListener = new NewTailChangeListener();
		scaleTail = new JSlider(JSlider.HORIZONTAL, 1, 10, tailSize);
		scaleTail.addChangeListener(tailChangeListener);

		// ���������� ������ ������������
		instrumentsPanel.setLayout(new BoxLayout(instrumentsPanel,
				BoxLayout.Y_AXIS));
		instrumentsPanel.setPreferredSize(new Dimension(width / 5, height));
		instrumentsPanel.setMinimumSize(new Dimension(width / 5, height));

		instrumentsPanel.add(new JLabel("�������:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(scale);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		scale.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		instrumentsPanel.add(new JLabel("������ ����:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(scaleTail);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		scaleTail.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		stationsComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		stationsComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		mapComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		mapComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(new JLabel("������� �������:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(stationsComboBox);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		stationsComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		instrumentsPanel.add(new JLabel("����� ������� ��������:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(mapComboBox);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		mapComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		instrumentsPanel.add(new JLabel("Edit:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		DemoAction wallsAction = new DemoAction("�����",
				createImageIcon("wall.png"), "������������� �����", 'W');
		DemoAction borderAction = new DemoAction("������� ������� �������",
				createImageIcon("border.png"), "������������� ������� ������� �������", 'B');
		DemoAction stationsAction = new DemoAction("�������",
				createImageIcon("station.png"), "������������� ������� �������", 'S');
		DemoAction deleteAction = new DemoAction("�������",
				createImageIcon("delete.png"), "�������", 'D');
		toolBar.add(wallsAction);
		toolBar.add(borderAction);
		toolBar.add(stationsAction);
		toolBar.add(deleteAction);
		instrumentsPanel.add(toolBar);
		toolBar.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		// ���������� ������� ������
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.X_AXIS));
		mainpanel.add(scrollPane);
		mainpanel.add(instrumentsPanel);

		mainpanel.setDoubleBuffered(true);
		Container container = getContentPane();
		container.add(mainpanel);

	}

	/**
	 * ������� ������.
	 * 
	 * @param path
	 *            ���� � �����
	 * @return ������, ��� null, ���� ������ ����� ����
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
	 * ������� ����.
	 */
	private void createMenu() {

		Font font = new Font("Verdana", Font.PLAIN, 11);

		menu = new JMenuBar();
		JMenu fileMenu = new JMenu("����");
		fileMenu.setFont(font);

		JMenuItem openItem = new JMenuItem("�������");
		openItem.setFont(font);
		openItem.setActionCommand("open");
		fileMenu.add(openItem);

		JMenuItem createItem = new JMenuItem("�������");
		createItem.setFont(font);
		createItem.setActionCommand("create");
		fileMenu.add(createItem);

		JMenuItem saveItem = new JMenuItem("���������");
		saveItem.setFont(font);
		saveItem.setActionCommand("save");
		fileMenu.add(saveItem);

		JMenuItem saveAsItem = new JMenuItem("��������� ���");
		saveAsItem.setFont(font);
		saveAsItem.setActionCommand("saveas");
		fileMenu.add(saveAsItem);

		fileMenu.insertSeparator(1);
		fileMenu.insertSeparator(3);
		fileMenu.insertSeparator(5);
		//fileMenu.insertSeparator(7);

		menu.add(fileMenu);

		JMenu toolsMenu = new JMenu("�����������");
		toolsMenu.setFont(font);

		JMenuItem importItem = new JMenuItem("������������� �����");
		importItem.setFont(font);
		importItem.setActionCommand("import");
		toolsMenu.add(importItem);

		JMenuItem cmpItem = new JMenuItem("�������� �����");
		cmpItem.setFont(font);
		cmpItem.setActionCommand("cmp");
		toolsMenu.add(cmpItem);

		JMenuItem modelItem = new JMenuItem("������������� �����");
		modelItem.setFont(font);
		modelItem.setActionCommand("model");
		toolsMenu.add(modelItem);

		JMenuItem teachItem = new JMenuItem("������� �������");
		teachItem.setFont(font);
		teachItem.setActionCommand("teach");
		toolsMenu.add(teachItem);
		
		JMenuItem placeItem = new JMenuItem("���������� �������");
		placeItem.setFont(font);
		placeItem.setActionCommand("place");
		toolsMenu.add(placeItem);
		
		JMenuItem evaluateItem = new JMenuItem("���������� ������");
		evaluateItem.setFont(font);
		evaluateItem.setActionCommand("evaluate");
		toolsMenu.add(evaluateItem);

		toolsMenu.insertSeparator(1);
		toolsMenu.insertSeparator(3);
		toolsMenu.insertSeparator(5);
		toolsMenu.insertSeparator(7);
		toolsMenu.insertSeparator(9);

		menu.add(toolsMenu);

		setJMenuBar(menu);

		menuListener = new NewMenuListener();
		openItem.addActionListener(menuListener);
		createItem.addActionListener(menuListener);
		saveItem.addActionListener(menuListener);
		saveAsItem.addActionListener(menuListener);
		importItem.addActionListener(menuListener);
		cmpItem.addActionListener(menuListener);
		modelItem.addActionListener(menuListener);
		teachItem.addActionListener(menuListener);
		placeItem.addActionListener(menuListener);
		evaluateItem.addActionListener(menuListener);
	}

	/**
	 * �������������� ������� ����.
	 */
	private class NewMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("open".equals(command)) {
				if (changed) {
					if (!Dialogs.saveChanged(plan, openedFile))
						return;
				}
				openedFile = Dialogs.showOpenDialog();
				if (openedFile != null) {
					plan = new Plan(openedFile);
					// ?
					// if (plan.getWalls().size() > 0)
					plan.devide(tailSize);
					panel.setPlan(plan);

					stationsComboBox.removeAllItems();
					for (int i = 0; i < plan.getStations().size(); i++)
						stationsComboBox.addItem(plan.getStation(i).getName());

					mapComboBox.removeAllItems();
					if (plan.getStations().size() > 0)
						for (int i = 0; i < plan.getStation(stationNumber)
								.getMaps().size(); i++)
							mapComboBox.addItem(plan.getStation(stationNumber)
									.getMap(i).getName());

					changed = false;
					stationNumber = 0;
					panel.repaint();
				}
			}
			if ("create".equals(command)) {

				if (changed) {
					if (!Dialogs.saveChanged(plan, openedFile))
						return;
				}

				openedFile = null;
				plan = new Plan();
				plan.devide(tailSize);
				stationsComboBox.removeAllItems();
				mapComboBox.removeAllItems();
				panel.setPlan(plan);
				changed = false;
				stationNumber = 0;
				panel.repaint();
			}
			if ("save".equals(command)) {
				if (plan != null) {
					if (openedFile != null) {
						plan.save(openedFile);
						changed = false;
					} else {
						File f = Dialogs.showSaveDialog();
						if (f != null) {
							String s = f.getAbsolutePath();
							String s1 = null;
							int dotPos = s.lastIndexOf(".");
							if (dotPos > 0) {
								s1 = s.substring(dotPos);
								// System.out.println(s1);
								if (!s1.equals("xml")) {
									s += ".xml";
									f.renameTo(new File(s));
								}
							} else {
								s += ".xml";
								f.renameTo(new File(s));
							}
							plan.save(f);
							changed = false;
						}
					}
				}
			}
			if ("saveas".equals(command)) {
				if (plan != null) {
					File f = Dialogs.showSaveDialog();
					if (f != null) {
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
						changed = false;
					}
				}
			}
			if ("import".equals(command)) {
				if ((plan != null) && (plan.getStations().size() > 0)) {
					openedFile = Dialogs.showOpenDialog();
					if (openedFile != null) {
						plan.getStation(stationNumber).importMap(openedFile,
								plan.sigma);
						selectMap = false;
						mapComboBox.removeAllItems();
						for (int i = 0; i < plan.getStation(stationNumber)
								.getMaps().size(); i++)
							mapComboBox.addItem(plan.getStation(stationNumber)
									.getMap(i).getName());
						selectMap = true;
						changed = true;
					}
				}
			}
			if ("cmp".equals(command)) {
				if ((plan != null)
						&& (plan.getStations().size() > 0)
						&& (plan.getStation(stationNumber).getMaps().size() > 0)) {
					Dialogs d = new Dialogs();
					d.showCompareMapsDialog(plan, stationNumber);
				}
			}
			if ("model".equals(command)) {
				if ((plan != null) && (plan.getStations().size() > 0)) {
					String[] name = new String[1];
					try {
						double s = Dialogs.showMapModelDialog(name);
						plan.getStation(stationNumber)
								.explode(plan, name[0], s);
						mapComboBox.addItem(name[0]);
						changed = true;
					} catch (NumberFormatException e1) {
						// ignoge
					}
				}
			}
			if ("teach".equals(command)) {
				if ((plan != null) && (plan.getStations().size() > 0)) {
					String[] name = new String[1];
					try {
						int s = Dialogs.showTeachStationDialog(name);
						plan.getStation(stationNumber).teach(plan, s,
								name[0]);
						mapComboBox.addItem(name[0]);
						changed = true;
					} catch (NumberFormatException e1) {
						// ignoge
					}
				}
			}
			if ("place".equals(command)) {
				if (plan != null) {
					String n = "";
					try {
						n = Dialogs.showPlaceStationsDialog();
						plan.placeStations(Integer.valueOf(n));
						stationsComboBox.removeAllItems();
						for (int j = 0; j < plan.getStations().size(); j++)
							stationsComboBox.addItem(plan.getStation(j)
									.getName());
						panel.repaint();
					} catch (NumberFormatException e1) {
						// ignoge
					}
					
				}
			}
			if ("evaluate".equals(command)) {
				if ((plan != null)
						&& (plan.getStations().size() > 0)
						&& (plan.getStation(stationNumber).getMaps().size() > 0)) {
					Dialogs.showEvaluateMapDialog(plan, stationNumber, tailSize);
				}
			}
		}
	}

	/**
	 * �������������� ������� �������� ��������.
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
	 * �������������� ������� �������� ������� ������.
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
	 * �������������� ������� ������ ������� ��� ����������� �������.
	 */
	private class StationsChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			if (c.getSelectedIndex() >= 0) {
				stationNumber = c.getSelectedIndex();
				selectMap = false;
				mapComboBox.removeAllItems();
				for (int i = 0; i < plan.getStation(stationNumber).getMaps()
						.size(); i++)
					mapComboBox.addItem(plan.getStation(stationNumber)
							.getMap(i).getName());
				if (plan.getStation(stationNumber).getMaps().size() > 0)
					mapComboBox.setSelectedIndex(plan.getStation(stationNumber)
							.getActiveMapNumber());
				selectMap = true;
			}
			panel.repaint();
		}
	};

	/**
	 * �������������� ������� ������ ����� ��� �����������.
	 */
	private class MapChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			if ((plan != null) && (selectMap)) {
				Station s = plan.getStation(stationNumber);
				s.setActiveMapNumber(c.getSelectedIndex());
				panel.repaint();
			}
		}
	};

	/**
	 * �������������� ����������� ����.
	 */
	private class NewMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			changed = true;
			// �������� ���������� �������������� � ������ ���������
			int x = e.getX();
			x -= x % (panel.getM() * panel.getBar());
			int y = e.getY();
			y -= y % (panel.getM() * panel.getBar());
			switch (instrumentNumber) {
			case BORDER:
				plan.getBorder().drag(x / panel.getBar() / panel.getM(),
						y / panel.getBar() / panel.getM());
				plan.devide(tailSize);
				panel.repaint();
				break;
			case DELETE:
				Point2D.Double p = new Point2D.Double(x / panel.getBar()
						/ panel.getM(), y / panel.getBar() / panel.getM());
				plan.deleteWall(p);
				panel.repaint();
				break;
			}

		}

		/** ������ ����������. */
		public void mouseMoved(MouseEvent e) {
		}
	}

	/**
	 * �������������� ������� ����.
	 */
	private class NewMouseListener implements MouseListener {

		// ���������� ������ � ����� �����
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		public void mouseReleased(MouseEvent e) {
			if (plan != null) {
				changed = true;
				switch (instrumentNumber) {
				case WALL:
					// ���������� ���������� �����, ������� � ������ ���������
					x2 = e.getX();
					x2 -= x2 % (panel.getM() * panel.getBar());
					y2 = e.getY();
					y2 -= y2 % (panel.getM() * panel.getBar());
					// �������� ����� �����
					plan.addWall(x1 / panel.getBar() / panel.getM(),
							y1 / panel.getBar() / panel.getM(),
							x2 / panel.getBar() / panel.getM(),
							y2 / panel.getBar() / panel.getM());
					plan.devide(tailSize);
					panel.repaint();
					break;
				case BORDER:
					plan.getBorder().stopDragging();
					plan.devide(tailSize);
					panel.repaint();
					break;
				case DELETE:
					plan.setDeleting(false);
					plan.devide(tailSize);
					break;
				}
			}
		}

		/** ������ ����������. */
		public void mouseEntered(MouseEvent e) {
		}

		/** ������ ����������. */
		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (plan != null) {
				changed = true;
				// ���������� ���������� ������, ������� � ������ ���������
				x1 = e.getX();
				x1 -= x1 % (panel.getM() * panel.getBar());
				y1 = e.getY();
				y1 -= y1 % (panel.getM() * panel.getBar());
				switch (instrumentNumber) {
				case BORDER:
					plan.getBorder().setDraggingPoints(
							x1 / panel.getBar() / panel.getM(),
							y1 / panel.getBar() / panel.getM());
					break;
				case DELETE:
					Point2D.Double p = new Point2D.Double(x1 / panel.getBar()
							/ panel.getM(), y1 / panel.getBar() / panel.getM());
					plan.startDeleting(p);
					break;
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (plan != null) {
				changed = true;
				// �������� ���������� ����� � ������ ���������
				int x = e.getX();
				x -= x % (panel.getM() * panel.getBar());
				int y = e.getY();
				y -= y % (panel.getM() * panel.getBar());
				switch (instrumentNumber) {
				case BORDER:
					plan.getBorder().setCheckPoints(
							x / panel.getBar() / panel.getM(),
							y / panel.getBar() / panel.getM());
					panel.repaint();
					break;
				case STATION:
					int i;
					String[] name = new String[1];
					String n;
					try {
						if ((i = plan.findStation(
								x / panel.getBar() / panel.getM(),
								y / panel.getBar() / panel.getM())) != -1) {

							n = Dialogs.showStationDialog(plan.getStation(i));
							if (n != "") {
								plan.setStationName(i, name[0]);
								stationsComboBox.removeAllItems();
								for (int j = 0; j < plan.getStations().size(); j++)
									stationsComboBox.addItem(plan.getStation(j)
											.getName());
							}

						} else {
							n = Dialogs.showStationDialog();
							if (n != "") {
								plan.addStation(
										x / panel.getBar() / panel.getM(),
										y / panel.getBar() / panel.getM(), n);
								stationsComboBox.addItem(name[0]);
								panel.repaint();
							}
						}
					} catch (NullPointerException e1) {
						// ��������, ����� ������ �� ���� ������
					}
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
					break;
				}

			}
		}
	}

	/**
	 * �������� ����� ��������� �������.
	 * 
	 * @return ����� ��������� �������
	 */
	public int getStationNumber() {
		return stationNumber;
	}

	/**
	 * ��������, ������ �� �����-������ ����.
	 * 
	 * @return true, ���� ������ ����, false �����
	 */
	public boolean hasOpenFile() {
		if (openedFile != null)
			return true;
		else
			return false;
	}

	/**
	 * �������� ����.
	 * 
	 * @return ����
	 */
	public Plan getPlan() {
		return plan;
	}

	/** ������������ ������ � ������� ������������ ��� ���������. */
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
				plan.getBorder().setNullPoints();
				panel.repaint();
				break;
			case "Border":
				instrumentNumber = BORDER;
				break;
			case "Stations":
				instrumentNumber = STATION;
				plan.getBorder().setNullPoints();
				panel.repaint();
				break;
			case "Delete":
				instrumentNumber = DELETE;
				plan.getBorder().setNullPoints();
				panel.repaint();
				break;
			}
		}
	}
}
