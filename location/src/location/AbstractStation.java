package location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Представляет базовую станцию.
 * 
 * @author Pokrovskaya Oksana
 */
public abstract class AbstractStation {

	/** Абсцисса */
	protected double x;
	/** Ордината */
	protected double y;

	/** Базовая сила сигнала */
	protected double s = 150;

	/** Имя станции */
	protected String name = "station";

	/**
	 * Флаг того, что для станции построена карта сил сигналов (станция обучена)
	 */
	protected boolean taught = false;

	public AbstractStation() {
		x = 0;
		y = 0;
	}

	
	
	
	
	/**
	 * @param x1
	 *            абсцисса
	 * @param y1
	 *            ордината
	 * @param s1
	 *            базовая сила сигнала
	 */
	public AbstractStation(double x1, double y1, double s1) {
		x = x1;
		y = y1;
		s = s1;
	}

	/**
	 * @param x1
	 *            абсцисса
	 * @param y1
	 *            ордината
	 */
	public AbstractStation(double x1, double y1) {
		x = x1;
		y = y1;
	}

	/**
	 * @param x2
	 *            абсцисса
	 * @param y2
	 *            ордината
	 * @param name2
	 *            имя станции
	 */
	public AbstractStation(double x2, double y2, String name2) {
		x = x2;
		y = y2;
		name = name2;
	}

	
	
	
	
	/**
	 * Рассчитывает уровень сигнала.
	 * 
	 * @param tail
	 *            ячейка, в которой необходимо рассчитать уровень сигнала
	 */
	public abstract void explode(Tail tail);

	/**
	 * Строит карту уровней сигнала.
	 * 
	 * @param tails
	 *            ячейки, для которых необходимо построить карту уровней сигнала
	 */
	public abstract void explode(ArrayList<Tail> tails);

	/**
	 * Рассчитывает уровень сигнала с учетом плана помещения.
	 * 
	 * @param tail
	 *            ячейка, в которой необходимо рассчитать уровень сигнала
	 * @param plan
	 *            план помещения
	 */
	public abstract void explode(Tail tail, Plan plan);

	/**
	 * Строит карту уровней сигнала с учетом плана помещения.
	 * 
	 * @param tails
	 *            ячейки, для которых необходимо построить карту уровней сигнала
	 * @param plan
	 *            план помещения
	 */
	public abstract void explode(ArrayList<Tail> tails, Plan plan);

	/**
	 * Обучает станцию.
	 * 
	 * @param object
	 *            позиционируемый объект
	 * @param plan
	 *            план здания
	 * @param num
	 *            количество точек позиционирования (количество шагов объекта)
	 */
	public abstract void teach(PosObject object, Plan plan, int num);

	
	
	
	
	
	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}

	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}

	/** Узнать, обучена ли станция */
	public boolean isTaught() {
		return taught;
	}

	/** Установить флаг обучения */
	public void setTaught(boolean taught) {
		this.taught = taught;
	}

	@Override
	public String toString() {
		return name;
	}

	/** Установить имя */
	public String getName() {
		return name;
	}

	/** Получить имя */
	public void setName(String name) {
		this.name = name;
	}

}
