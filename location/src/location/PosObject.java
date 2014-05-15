package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/** 
* Представляет позиционируемый объект. 
* @author Pokrovskaya Oksana
*/
public class PosObject extends Point2D.Double {

	/** Определенная абсцисса */
	private double probX;
	/** Определенная ордината */
	private double probY;
	
	/** Вектор сил сигнала */
	private ArrayList<Integer> s;

	public PosObject() {
		x = 0;
		y = 0;
		probX = 0;
		probY = 0;
		s = new ArrayList<Integer>();
	}
	
	public PosObject(double x, double y) {
		super(x, y);
		probX = 0;
		probY = 0;
		s = new ArrayList<Integer>();
	}
	
	public void nextStep(Plan plan) {
		Random rand = new Random(new Date().getTime());
		int p = rand.nextInt(plan.getTails().size());
		x = plan.getTails().get(p).getX();
		y = plan.getTails().get(p).getY();
		
	}

}
