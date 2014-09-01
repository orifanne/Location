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
 * ��������� �������� ���������������� .
 * 
 * @author Pokrovskaya Oksana
 */
public class Location extends JFrame {

	final int WALL = 0;
	final int BORDER = 1;
	final int STATION = 2;
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

	/** ������������� ���� ����� ��� �����������. */
	private JRadioButton orign, taught;
	/** ������ �������������� ���� ����� ��� �����������. */
	private ButtonGroup bg;
	/** ���� ����, ��� ����� ���������� �����, ���������� ���������. */
	boolean displayTaught = false;

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

	// ������ ��� ����������������
	PosObject object;

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
	JComboBox<String> paintComboBox;

	/** ���� ����, ��� ���������� �������������� ������� �������. */
	boolean dragging = false;

	/**
	 * ���������� ������ �����, ��������������� �� ������� (��� ������� �������
	 * ��� ��������������)
	 */
	Point2D.Double firstCheckPoint = null;

	/**
	 * ���������� ������ �����, ��������������� �� ������� (��� ������� �������
	 * ��� ��������������)
	 */
	Point2D.Double secondCheckPoint = null;

	/**
	 * ���������� ������ �����, ��������������� �� ������� (��� ��������������)
	 */
	Point2D.Double firstDraggingPoint = null;

	/**
	 * ���������� ������ �����, ��������������� �� ������� (��� ��������������)
	 */
	Point2D.Double secondDraggingPoint = null;

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

		taught = new JRadioButton("���������");
		orign = new JRadioButton("���������������");
		bg = new ButtonGroup();

		bg.add(orign);
		bg.add(taught);

		stationsComboBox = new JComboBox<String>();
		stationsComboBox.addActionListener(new StationsChooseListener());

		/*
		 * paintComboBox = new JComboBox<String>();
		 * paintComboBox.addActionListener(new PaintChooseListener());
		 * paintComboBox.addItem("�����");
		 */

		// ����� ������������ ������� ����
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

		instrumentsPanel.add(new JLabel("������ ������:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(scaleTail);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		scaleTail.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		stationsComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		stationsComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		// instrumentsPanel.add(new JTextArea("����� ������� �������:"));
		instrumentsPanel.add(new JLabel("����� ������� �������:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(stationsComboBox);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		stationsComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		// instrumentsPanel.add(new JTextArea("����� ����� ������� ��������:"));
		instrumentsPanel.add(new JLabel("��� �����:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		instrumentsPanel.add(orign);
		orign.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		instrumentsPanel.add(taught);
		instrumentsPanel.add(Box.createVerticalStrut(10));
		taught.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		orign.addActionListener(new RadioListener());
		taught.addActionListener(new RadioListener());

		// instrumentsPanel.add(new JLabel("���������:"));

		/*
		 * paintComboBox.setMaximumSize(new Dimension(instrumentsPanel
		 * .getPreferredSize().width, 25)); paintComboBox.setMinimumSize(new
		 * Dimension(instrumentsPanel .getPreferredSize().width, 25));
		 * 
		 * instrumentsPanel.add(paintComboBox);
		 */

		instrumentsPanel.add(new JLabel("���������:"));
		instrumentsPanel.add(Box.createVerticalStrut(10));
		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		DemoAction wallsAction = new DemoAction("Walls",
				createImageIcon("wall.gif"), "������������� �����", 'W');
		DemoAction borderAction = new DemoAction("Border",
				createImageIcon("border.gif"),
				"������������� ������� ������� �������", 'B');
		DemoAction stationsAction = new DemoAction("Stations",
				createImageIcon("station.gif"),
				"������������� ������� �������", 'S');
		DemoAction deleteAction = new DemoAction("Delete",
				createImageIcon("delete.gif"), "�������", 'D');
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

		object = new PosObject();
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

		JMenuItem closeItem = new JMenuItem("�������");
		closeItem.setFont(font);
		closeItem.setActionCommand("close");
		fileMenu.add(closeItem);

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
	 * �������������� ������� ����. ��� ������� �� ������ "Open" �������� ������
	 * ������ �����. ��� ������� �� ������ "Close" ��������� ����.
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
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				stationsComboBox.removeAllItems();
				panel.repaint();
			}
			if ("create".equals(command)) {
				openedFile = null;
				firstCheckPoint = null;
				secondCheckPoint = null;
				firstDraggingPoint = null;
				secondDraggingPoint = null;
				plan = new Plan();
				plan.devide(tailSize);
				stationsComboBox.removeAllItems();
				panel.repaint();
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
	 * �������������� ������� ������ ������� ��� ����������� �����.
	 */
	private class StationsChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			stationNumber = c.getSelectedIndex();
			panel.repaint();
		}
	};

	/**
	 * �������������� ������� ������ ������������ ��� ���������.
	 */
	private class PaintChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c = (JComboBox<String>) e.getSource();
			instrumentNumber = c.getSelectedIndex();
		}
	};

	/**
	 * �������������� ������� ������ ���� ����� ��� ���������� (���������������
	 * ��� ���������).
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
	 * �������������� ����������� ����.
	 */
	private class NewMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			if ((instrumentNumber == BORDER) && (dragging)) {
				// �������� ���������� �������������� � ������ ���������
				int x = e.getX();
				x -= x % (panel.getM() * panel.getBar());
				int y = e.getY();
				y -= y % (panel.getM() * panel.getBar());
				boolean f = false;
				if ((x >= 0) && (y >= 0)) {
					// ������������ �������
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
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP1(),
											line.getP2());
								} else {
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP2(),
											line.getP1());
								}
							} else {
								if (line.getY1() > line.getY2()) {
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP2(),
											line.getP1());
								} else {
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP1(),
											line.getP2());
								}
							}
							if (f)
								f = plan.addBorderPoints(firstDraggingPoint,
										secondDraggingPoint, firstCheckPoint,
										secondCheckPoint);
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
					// �������������� �������
					if (firstCheckPoint.getY() == secondCheckPoint.getY()) {
						firstDraggingPoint.setLocation(firstCheckPoint.getX(),
								y / panel.getBar() / panel.getM());
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
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP1(),
											line.getP2());
								} else {
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP2(),
											line.getP1());
								}
							} else {
								if (line.getX1() > line.getX2()) {
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP2(),
											line.getP1());
								} else {
									f = plan.addBorderPoints(firstCheckPoint,
											secondCheckPoint, line.getP1(),
											line.getP2());
								}
							}
							if (f)
								f = plan.addBorderPoints(firstDraggingPoint,
										secondDraggingPoint, firstCheckPoint,
										secondCheckPoint);
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
				if ((instrumentNumber == BORDER) && (firstCheckPoint != null)
						&& (secondCheckPoint != null)) {
					Point2D.Double p = new Point2D.Double(x1 / panel.getBar()
							/ panel.getM(), y1 / panel.getBar() / panel.getM());
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
								new JLabel("���:"), name,
								new JLabel("������� ������� �������:"), power, };
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
							// ��������, ����� ������ �� ���� ������
						}
					} else {
						final JComponent[] inputs = new JComponent[] {
								new JLabel("���:"), name,
								new JLabel("������� ������� �������:"), power, };
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
							// ��������, ����� ������ �� ���� ������
						}
					}
					break;
				case DELETE:
					break;
				}

			}
		}
	}

	/**
	 * �������� ����� ��������� �������.
	 */
	public int getStationNumber() {
		return stationNumber;
	}

	/**
	 * ��������, ������ �� �����-������ ����.
	 */
	public boolean hasOpenFile() {
		if (openedFile != null)
			return true;
		else
			return false;
	}

	/**
	 * �������� ����.
	 */
	public Plan getPlan() {
		return plan;
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
