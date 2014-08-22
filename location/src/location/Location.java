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

	JScrollPane scrollPane = new JScrollPane(panel);

	/** ������ �������� ������. */
	private int tailSize = 1;

	/** ������ � ������� ��� ������� */
	JComboBox<String> stationsComboBox;

	/** ������ � ������� ��� ��������� */
	JComboBox<String> paintComboBox;

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

		taught = new JRadioButton("����������� ���������� ��������");
		orign = new JRadioButton("�� ��������������� ���������������� �������");
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
		instrumentsPanel.add(scale);

		instrumentsPanel.add(new JLabel("������ ������:"));
		instrumentsPanel.add(scaleTail);

		stationsComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		stationsComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(new JLabel("����� ������� �������:"));
		instrumentsPanel.add(stationsComboBox);

		instrumentsPanel.add(new JLabel("����� ����� ������� ��������:"));
		instrumentsPanel.add(orign);
		instrumentsPanel.add(taught);

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
				panel.repaint();
				stationsComboBox.removeAllItems();
			}
			if ("create".equals(command)) {
				openedFile = null;
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

		/** ������ ����������. */
		public void mouseDragged(MouseEvent e) {
		}

		/** ������ ����������. */
		public void mouseMoved(MouseEvent e) {
		}
	}

	/**
	 * �������������� ������� ����.
	 */
	private class NewMouseListener implements MouseListener {

		// ���� ����, ��� ���������� ���������
		private boolean drawing = false;
		// ���������� ������ � ����� �����
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		public void mouseReleased(MouseEvent e) {
			if (plan != null) {
				drawing = false;
				// ���������� ���������� �����, ������� � ������ ���������
				x2 = e.getX();
				x2 -= x2 % (panel.getM() * panel.getBar());
				y2 = e.getY();
				y2 -= y2 % (panel.getM() * panel.getBar());

				// ���������, ������ �� ��� ��������� �����
				switch (instrumentNumber) {
				case WALL:
					// ���� ������� �������������� ��� ������������
					if ((x1 == x2) || (y1 == y2))
						// � ��� ���� �� �����
						if (!((x1 == x2) && (y1 == y2))) {
							// �������� ����� �����
							plan.addWall(x1 / panel.getBar() / panel.getM(), y1
									/ panel.getBar() / panel.getM(),
									x2 / panel.getBar() / panel.getM(), y2
											/ panel.getBar() / panel.getM());
							System.out.println(x1 + " " + y1 + " : " + x2 + " "
									+ y2);
							panel.repaint();
						}
					break;
				case BORDER:
				case STATION:
				case DELETE:
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
				drawing = true;
				// ���������� ���������� ������, ������� � ������ ���������
				x1 = e.getX();
				x1 -= x1 % (panel.getM() * panel.getBar());
				y1 = e.getY();
				y1 -= y1 % (panel.getM() * panel.getBar());
			}
		}

		/** ������ ����������. */
		public void mouseClicked(MouseEvent e) {
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
			//System.out.println(getValue(NAME).toString());
			switch (getValue(NAME).toString()) {
				case "Walls": instrumentNumber = WALL; break;
				case "Border": instrumentNumber = BORDER; break;
				case "Stations": instrumentNumber = STATION; break;
				case "Delete": instrumentNumber = DELETE; break;
			}
		}
	}
}
