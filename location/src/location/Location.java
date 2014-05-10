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

	//������ ����� �� �����������
	private ArrayList <Double> vDotes = null;
	//������ ����� �� ���������
	private ArrayList <Double> hDotes = null;

	//��������������� ������
	private Frame[][] frames;

	//��������� ������
	Frame[] finalFrames;
	//���������� ��������� ��������
	int finalFramesNum;
	
	JScrollPane scrollPane = new JScrollPane(panel);
	
	/** ������ �������� ������. */
	private int tailSize = 1;
	
	/** �������� ������. */
	private Tail[] tails;
	/** ���������� �������� �����. */
	private int tailsNum;
	//������ ������������� ���������� �������� �����
	private int maxTailsNum;

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
	
	/**
	* �������� �������� ������.
	*/
	public Tail[] getTails() {
		return tails;
	}
	
	/**
	* �������� ��������� ������.
	*/
	public Frame[][] getStartFrames() {
		return frames;
	}
	
	/**
	* �������� �������� ������.
	*/
	public int getTailsNum() {
		return tailsNum;
	}
	
	/*public void teach(Station st, PosObject object, int num) {
		for (int i = 0; i < num; i++) {
			object.nextStep(plan);
			
		}
	}*/

	/** 
  	* ��������� ������� ������� �� ������.
  	*/
     	public void devide() {
    		if (openedFile == null)
    			return;

		double x1, y1, x2, y2;
		vDotes = new ArrayList<Double>();
		hDotes = new ArrayList<Double>();

		for (int i = 0; i < plan.getWalls().length; i++) {
			
			x1 = plan.getWall(i).getX1();
			x2 = plan.getWall(i).getX2();
			y1 = plan.getWall(i).getY1();
			y2 = plan.getWall(i).getY2();

			if (!vDotes.contains(y1))
				vDotes.add(y1);
			if (!vDotes.contains(y2))
				vDotes.add(y2);
			if (!hDotes.contains(x1))
				hDotes.add(x1);
			if (!hDotes.contains(x2))
				hDotes.add(x2);
			
		}

		//��������� ������� �����
		for (int i = 0; i < plan.getBorder().xpoints.length; i++) {
			x1 = plan.getBorder().xpoints[i];
			y1 = plan.getBorder().ypoints[i];
			if (!vDotes.contains(y1))
				vDotes.add(y1);
			if (!hDotes.contains(x1))
				hDotes.add(x1);
		}
		
		Collections.sort(hDotes);
		Collections.sort(vDotes);
		
		//System.out.println(vDotesNum + " " + hDotesNum);

		//���������� ������ �� �������� �����
		frames = new Frame[vDotes.size() - 1][hDotes.size() - 1];
		maxTailsNum = 0;
		for (int i = 0; i < vDotes.size() - 1; i++) {
			for (int j = 0; j < hDotes.size() - 1; j++) {
				frames[i][j] = new Frame(hDotes.get(j), vDotes.get(i), 
						hDotes.get(j + 1), vDotes.get(i + 1));
				//���� ���� ����� �� ������ �������
				if (!plan.getBorder().isInternal(frames[i][j])) {
					//��������� ��� �� ����������� ������������
					frames[i][j].used(true);
					//System.out.println("external");
				}
				//����� ��������� �� ��������������
				else {
					boolean[] b = new boolean[4];
					b = plan.isBordered(frames[i][j]);
					if (b[0])
						frames[i][j].up = true;
					if (b[1])
						frames[i][j].down = true;
					if (b[2])
						frames[i][j].right = true;
					if (b[3])
						frames[i][j].left = true;
					//��������� ������������ ���������� �������� �����
					maxTailsNum += (frames[i][j].getX2() - frames[i][j].getX1()) * (frames[i][j].getY2() - frames[i][j].getY1());
				}
			}
		}

		//���������� ��������� ������
		finalFrames();
		
		//��������� �� �� �������� ������
		doTails();
	}
     	
    /** 
    * ��������� �� �������� ������.
    */
    private void doTails() {
		if (openedFile == null)
			return;
    	tailsNum = 0;
    	tails = new Tail[maxTailsNum];
    	for (int i = 0; i < finalFramesNum; i++) {
    		double a = finalFrames[i].getX2() - finalFrames[i].getX1();
    		double b = finalFrames[i].getY2() - finalFrames[i].getY1();
    		
    		int finalSizeA = tailSize;
    		if (a >= tailSize) {
	    		if ((a % tailSize) < ((double) tailSize / 2.0))
	    			while ((a % finalSizeA) != 0)
	    				finalSizeA++;
	    		else
	    			while ((a % finalSizeA) != 0)
						finalSizeA--;
    		}
    		
    		int finalSizeB = tailSize;
    		if (b >= tailSize) {
	    		if ((b % tailSize) < ((double) tailSize / 2.0))
	    			while ((b % finalSizeB) != 0) 
	    				finalSizeB++;
	    		else
	    			while ((b % finalSizeB) != 0)
	    				finalSizeB--;
    		}
    		
    		if ((a >= tailSize) && (b >= tailSize)) {
	    		for (double u = finalFrames[i].getX1(); u < finalFrames[i].getX2(); u += finalSizeA)
	    			for (double v = finalFrames[i].getY1(); v < finalFrames[i].getY2(); v += finalSizeB) {
	    				tails[tailsNum] = new Tail(u, v, u + finalSizeA, v + finalSizeB);
	    				tailsNum++;
	    			}
	    		continue;
    		}
    		
    		if (a >= tailSize) {
	    		for (double u = finalFrames[i].getX1(); u < finalFrames[i].getX2(); u += finalSizeA) {
	    				tails[tailsNum] = new Tail(u, finalFrames[i].getY1(), u + finalSizeA, finalFrames[i].getY2());
	    				tailsNum++;
	    			}
	    		continue;
    		}
    		
    		if (b >= tailSize) {
	    		for (double v = finalFrames[i].getY1(); v < finalFrames[i].getY2(); v += finalSizeB) {
	    			tails[tailsNum] = new Tail(finalFrames[i].getX1(), v, finalFrames[i].getX2(), v + finalSizeB);
	    			tailsNum++;
	    		}
	    		continue;
    		}
    		
    		tails[tailsNum] = new Tail(finalFrames[i].getX1(), finalFrames[i].getY1(), finalFrames[i].getX2(), finalFrames[i].getY2());
			tailsNum++;
    	}
    }

    /** 
    * ���������� ��������� ������.
    * � ������� ����� �������� �� ��������� �� �������� ������� �� ����������� �� ������� � ��������.
    */
	private void finalFrames() {
		if (openedFile == null)
			return;
		finalFrames = new Frame[(vDotes.size() - 1) * (hDotes.size() - 1)];
		finalFramesNum = 0;
		for (int i = 0; i < vDotes.size() - 1; i++) {
			for (int j = 0; j < hDotes.size() - 1; j++) {
				if (!frames[i][j].isUsed()) {
					int minW = hDotes.size();
					int k;
					for (k = i; k < vDotes.size() - 1; k++) {
						int t;
						if (frames[k][j].down == true) {
							for (t = j; t < hDotes.size() - 1; t++) {
								if (frames[k][t].right == true) 
									break;
							}
							if ((t - j) < minW)
								minW = (t - j);
							break;
						}
						else {
							for (t = j; t < hDotes.size() - 2; t++) {
								if ((frames[k][t].right == true) || (frames[k][t + 1].down == true)) 
									break;
							}
							if ((t - j) < minW) {
								minW = (t - j);
								//f = true;
							}
						}
						//System.out.println(t);
					}
					//System.out.println(i + " " + j + "     " + k + " " + (j + minW));
					finalFrames[finalFramesNum] = new Frame(frames[i][j].getX1(),
					frames[i][j].getY1(), frames[k][j + minW].getX2(),
					frames[k][j + minW].getY2());
					finalFramesNum++;
					for (int u = i; u <= k; u++)
						for (int v = j; v <= j + minW; v++) {
							frames[u][v].used(true);
							if (u > 0)
								frames[u - 1][v].down = true;
							if (v > 0)
								frames[u][v - 1].right = true;
							if (u < vDotes.size() - 2)
								frames[u + 1][v].up = true;
							if (v < hDotes.size() - 2)
								frames[u][v + 1].left = true;
						}
				}
			}
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
					devide();
					panel.repaint();
				}
		    	}
			if ("close".equals(command)) {
				openedFile = null;
				plan = null;
				vDotes = null;
				hDotes = null;
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
                devide();
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
