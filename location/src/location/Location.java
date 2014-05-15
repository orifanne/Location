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
* @author Pokrovskaya Oksana
*/
public class Location extends JFrame {

	/** ������ ����. */
    private JMenuBar menu = null;
   	/** ������ ����. */
   	private int width = 900;
   	/** ������ ����. */
   	private int height = 500;

   	/** ������� �����������, �������� �� �������� ������������ ������ ������������ � ���� ��� ���������. */
   	private JPanel mainpanel = new JPanel();
   	
   	/** �������� �� �������� ������������ ������������ �� ������. */
   	private JPanel instrumentsPanel = new JPanel();
   	
   	/** ������� ��� ����������� ��������. */
   	private JSlider scale;
   	
   	/** ������� ��� ����������� ������� �����. */
   	private JSlider scaleTail;

   	/** ������, �������������� ���� ������. */
   	private ImagePanel panel = new ImagePanel(width , height, this);

   	/** ���� ������. */
   	private Plan plan = null;
   	/** �������� ����. */
   	private File openedFile = null;
   	
   	//������ ��� ����������������
   	PosObject object;

	JScrollPane scrollPane = new JScrollPane(panel);
	
	/** ������ �������� ������. */
	private int tailSize = 1;
	


   	public Location() {
		//��������� ����
	   	super("Location");
		//���������� ������ �������� ���� + ������ � ������
	   	setBounds(0, 0, width, height);
		//������ �������� ������ ����
	   	setResizable(false);
	   	createMenu();
	   	setJMenuBar(menu);
		//��������� ��������� ��� �������� ����
	   	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//����� ������������ ������� ���� 
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
		
		//���������� ������ ������������
		instrumentsPanel.setLayout(new BoxLayout(instrumentsPanel, BoxLayout.Y_AXIS));
		instrumentsPanel.add(scale);
		instrumentsPanel.add(scaleTail);

		//���������� ������� ������
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.X_AXIS));
		mainpanel.add(scrollPane);
		mainpanel.add(instrumentsPanel);

        mainpanel.setDoubleBuffered(true);
		Container container = getContentPane();
        container.add(mainpanel);
        
        object = new PosObject();
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
	
	/*public void teach(Station st, PosObject object, int num) {
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);
			
		}
	}*/

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
    	* �������������� ������� ����.
    	* ��� ������� �� ������ "Open" �������� ������ ������ �����.
    	* ��� ������� �� ������ "Close" ��������� ����.
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
					int ret = fileopen.showDialog(null, "������� ����");
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
    	* �������������� ������� �������� ��������.
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
    	* �������������� ������� �������� ������� ������.
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
    	* �������������� ����������� ����.
    	*/
    	private class NewMouseMotionListener implements MouseMotionListener {
	
		/** ������ ����������. */
		public void mouseDragged(MouseEvent e) {}
		/** ������ ����������. */
		public void mouseMoved(MouseEvent e) {}
    	}

    	/** 
    	* �������������� ������� ����.
    	*/
    	private class NewMouseListener implements MouseListener {

		/** ������ ����������. */
		public void mouseReleased(MouseEvent e) {}
		/** ������ ����������. */
		public void mouseEntered(MouseEvent e) {}
		/** ������ ����������. */
		public void mouseExited(MouseEvent e) {}
		/** ������ ����������. */
		public void mousePressed(MouseEvent e) {}
		/** ������ ����������. */
		public void mouseClicked(MouseEvent e) {}
    	}

}
