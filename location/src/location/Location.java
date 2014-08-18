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

	/** ������ ����. */
	private JMenuBar menu = null;
	/** ������ ����. */
	private int width = 900;
	/** ������ ����. */
	private int height = 500;

	/** ����� ��������� �������. */
	private int stationNumber = 0;

	/** ����� ���������� ����������� ��� ���������. */
	private int instrumentNumber = 0;

	/** ������������� ���� ����� ��� �����������. */
	private JRadioButton orign, taught;
	private ButtonGroup bg;
	/** ���� ����, ��� ����� ���������� �����, ���������� ���������. */
	boolean displayTaught = false;

	/** ���� ����, ��� ��������� ����. */
	boolean creating = false;

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

		paintComboBox = new JComboBox<String>();
		paintComboBox.addActionListener(new PaintChooseListener());
		paintComboBox.addItem("�����");

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

		instrumentsPanel.add(new JLabel("���������:"));

		paintComboBox.setMaximumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));
		paintComboBox.setMinimumSize(new Dimension(instrumentsPanel
				.getPreferredSize().width, 25));

		instrumentsPanel.add(paintComboBox);

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
				int ret = fileopen.showDialog(null, "������� ����");
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
	 * �������������� ������� �������� ��������.
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
	 * �������������� ������� �������� ������� ������.
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
	 * �������������� ������� ������ ������� ��� ����������� �����.
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
			drawing = false;
			//���������� ���������� �����, ������� � ������ ���������
			x2 = e.getX();
			x2 -= x2 % (panel.getM() * panel.getBar());
			y2 = e.getY();
			y2 -= y2 % (panel.getM() * panel.getBar());
			
			//���������, ������ �� ��� ��������� �����
			switch (instrumentNumber) {
				case 0: 
					//���� ������� �������������� ��� ������������
					if ((x1 == x2) || (y1 == y2))
						//� ��� ���� �� �����
						if (y1 != x1)
						{
							//�������� ����� �����
							plan.addWall(x1 / panel.getBar() / panel.getM(), 
									y1 / panel.getBar() / panel.getM(), 
									x2 / panel.getBar() / panel.getM(), 
									y2 / panel.getBar() / panel.getM());
							System.out.println(x1 + " " + y1 + " : " + x2 + " " + y2);
							panel.repaint();
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
			drawing = true;
			// ���������� ���������� ������, ������� � ������ ���������
			x1 = e.getX();
			x1 -= x1 % (panel.getM() * panel.getBar());
			y1 = e.getY();
			y1 -= y1 % (panel.getM() * panel.getBar());
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
}
