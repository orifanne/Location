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
 * ��������� �������� ���������������� ������� �������, ���������� ���������.
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

	/** ����� ��������� �����. */
	private int mapNumber = 0;

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

	/** ������ ��� ���������������� */
	PosObject object = null;

	/**
	 * �������������� ����, �������� ������� ������ ��� ����������� �����
	 * ������.
	 */
	JScrollPane scrollPane = new JScrollPane(panel);

	/** ������ �������� ������. */
	private int tailSize = 1;

	/** ������ � ������� ��� ������� */
	JComboBox<String> stationsComboBox;

	/** ������ � ������� ��� ��������� */
	JComboBox<String> mapComboBox;

	/** ���� ����, ��� ���������� �������� ������� �����. */
	boolean deleting = false;

	/** ���� ����, ��� � ���� ���� ������� ���������. */
	boolean canged = false;

	/**
	 * ���������� �����, ������� � ������� ��������� ����� �����.
	 */
	Point2D.Double deletePoint = null;

	public Location() {
		// ��������� ����
		super("Location");
		// ���������� ������ �������� ���� + ������ � ������
		setBounds(0, 0, width, height);
		// ������ �������� ������ ����
		setResizable(false);
		createMenu();
		setJMenuBar(menu);
		// ��������� ��������� ��� �������� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		stationsComboBox = new JComboBox<String>();
		stationsComboBox.addActionListener(new StationsChooseListener());

		mapComboBox = new JComboBox<String>();
		mapComboBox.addActionListener(new MapChooseListener());

		// ����� ������������ ������� ����
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

		// ���������� ������ ������������
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

		mapComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		mapComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(new JLabel("Base station:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(stationsComboBox);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		stationsComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		instrumentsPanel.add(new JLabel("Display map:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(mapComboBox);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		mapComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

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
		JMenu fileMenu = new JMenu("File");
		fileMenu.setFont(font);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.setFont(font);
		openItem.setActionCommand("open");
		fileMenu.add(openItem);

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

		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setFont(font);

		JMenuItem importItem = new JMenuItem("Import map");
		importItem.setFont(font);
		importItem.setActionCommand("import");
		toolsMenu.add(importItem);

		JMenuItem cmpItem = new JMenuItem("Compare maps");
		cmpItem.setFont(font);
		cmpItem.setActionCommand("cmp");
		toolsMenu.add(cmpItem);

		toolsMenu.insertSeparator(1);

		menu.add(toolsMenu);

		setJMenuBar(menu);

		ActionListener actionListener = new NewMenuListener();
		openItem.addActionListener(actionListener);
		createItem.addActionListener(actionListener);
		saveItem.addActionListener(actionListener);
		saveAsItem.addActionListener(actionListener);
		importItem.addActionListener(actionListener);
		cmpItem.addActionListener(actionListener);
	}

	/**
	 * �������������� ������� ����.
	 */
	private class NewMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("open".equals(command)) {
				if (canged) {
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
					panel.repaint();

					stationsComboBox.removeAllItems();
					for (int i = 0; i < plan.getStations().size(); i++)
						stationsComboBox.addItem(plan.getStation(i).getName());

					mapComboBox.removeAllItems();
					if (plan.getStations().size() > 0)
						for (int i = 0; i < plan.getStation(stationNumber)
								.getMaps().size(); i++)
							mapComboBox.addItem(plan.getStation(stationNumber)
									.getMap(i).getName());

					canged = false;
					object = new PosObject();
					stationNumber = 0;
				}
			}
			if ("create".equals(command)) {

				if (canged) {
					if (!Dialogs.saveChanged(plan, openedFile))
						return;
				}

				openedFile = null;
				plan = new Plan();
				plan.devide(tailSize);
				stationsComboBox.removeAllItems();
				mapComboBox.removeAllItems();
				panel.setPlan(plan);
				panel.repaint();
				canged = false;
				object = new PosObject();
				stationNumber = 0;
			}
			if ("save".equals(command)) {
				if (plan != null) {
					if (openedFile != null) {
						plan.save(openedFile);
						canged = false;
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
							canged = false;
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
						canged = false;
					}
				}
			}
			if ("import".equals(command)) {
				if ((plan != null) && (plan.getStations().size() > 0)) {
					openedFile = Dialogs.showOpenDialog();
					if (openedFile != null) {
						plan.getStation(stationNumber).importMap(openedFile, plan.sigma);
						mapComboBox.removeAllItems();
						for (int i = 0; i < plan.getStation(stationNumber)
								.getMaps().size(); i++)
							mapComboBox.addItem(plan.getStation(stationNumber)
									.getMap(i).getName());
					}
				}
			}
			if ("cmp".equals(command)) {
				if ((plan != null)
						&& (plan.getStations().size() > 0)
						&& (plan.getStation(stationNumber).getMaps().size() > 0)) {
					Dialogs d = new Dialogs();
					d.showCompareMapsDialog(plan, stationNumber, object);
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

				mapComboBox.removeAllItems();
				for (int i = 0; i < plan.getStation(stationNumber).getMaps()
						.size(); i++)
					mapComboBox.addItem(plan.getStation(stationNumber)
							.getMap(i).getName());
			}

			panel.repaint();
		}
	};

	/**
	 * �������������� ������� ������ ������� ��� ����������� �����.
	 */
	private class MapChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			mapNumber = c.getSelectedIndex();
			if ((mapNumber == 1)
					&& ((plan != null) && (object != null) && (stationNumber >= 0)))
				plan.getStation(stationNumber).teach(object, plan, 100);
			panel.repaint();
		}
	};

	/**
	 * �������������� ����������� ����.
	 */
	private class NewMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
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
					if (deleting) {
						deleting = false;
						plan.devide(tailSize);
					}
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
					double n;
					try {
						if ((i = plan.findStation(
								x / panel.getBar() / panel.getM(),
								y / panel.getBar() / panel.getM())) != -1) {

							n = Dialogs.showStationDialog(name,
									plan.getStation(i));
							if (name[0] != "") {
								plan.setStationName(i, name[0]);
								stationsComboBox.removeAllItems();
								for (int j = 0; j < plan.getStations().size(); j++)
									stationsComboBox.addItem(plan.getStation(j)
											.getName());
							}
							plan.setStationS(i, n);

						} else {
							n = Dialogs.showStationDialog(name);
							if (name[0] != "") {
								plan.addStation(
										x / panel.getBar() / panel.getM(),
										y / panel.getBar() / panel.getM(),
										name[0], n);
								stationsComboBox.addItem(name[0]);
								panel.repaint();
							}
						}
						canged = true;
					} catch (NullPointerException | NumberFormatException e1) {
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
					canged = true;
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

	/**
	 * �������� ��������������� ������.
	 * 
	 * @return ��������������� ������
	 */
	public PosObject getPosObject() {
		return object;
	}

	/**
	 * �������� ����� ��������� ��� ����������� �����.
	 * 
	 * @return ����� �����
	 */
	public int getMapNumber() {
		return mapNumber;
	}

}
