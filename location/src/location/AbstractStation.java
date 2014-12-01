package location;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Представляет базовую станцию.
 * 
 * @author Pokrovskaya Oksana
 */
public abstract class AbstractStation extends Point2D.Double {

	/** Базовая сила сигнала */
	protected double s = 150;

	/** Имя станции */
	protected String name = "station";

	/**
	 * Флаг того, что для станции построена карта сил сигналов (станция обучена)
	 */
	protected boolean taught = false;

	/**
	 * @param x1
	 *            абсцисса
	 * @param y1
	 *            ордината
	 * @param s1
	 *            базовая сила сигнала
	 */
	public AbstractStation(double x1, double y1, double s1) {
		super(x1, y1);
		s = s1;
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
		super(x2, y2);
		name = name2;
	}

	/**
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 * @param name2
	 *            имя станции
	 * @param s2
	 *            базовый уровень сигнала
	 */
	public AbstractStation(double x, double y, String name2, double s2) {
		super(x, y);
		name = name2;
		s = s2;
	}

	public AbstractStation() {
		super(0, 0);
	}

	/**
	 * @param x
	 *            абсцисса
	 * @param y
	 *            ордината
	 */
	public AbstractStation(double x, double y) {
		super(x, y);
	}

	/**
	 * Рассчитывает уровень сигнала.
	 * 
	 * @param tail
	 *            ячейка, в которой необходимо рассчитать уровень сигнала
	 * @param sigma
	 *            дисперсия уровня сигнала
	 */
	public abstract void explode(Tail tail, int sigma);

	/**
	 * Строит карту уровней сигнала.
	 * 
	 * @param tails
	 *            ячейки, для которых необходимо построить карту уровней сигнала
	 * @param sigma
	 *            дисперсия уровня сигнала
	 */
	public abstract void explode(ArrayList<Tail> tails, int sigma);

	/**
	 * Рассчитывает уровень сигнала с учетом плана помещения.
	 * 
	 * @param tail
	 *            ячейка, в которой необходимо рассчитать уровень сигнала
	 * @param plan
	 *            план помещения
	 * @param sigma
	 *            дисперсия уровня сигнала
	 */
	public abstract void explode(Tail tail, Plan plan, int sigma);

	/**
	 * Строит карту уровней сигнала с учетом плана помещения.
	 * 
	 * @param tails
	 *            ячейки, для которых необходимо построить карту уровней сигнала
	 * @param plan
	 *            план помещения
	 * @param sigma
	 *            дисперсия уровня сигнала
	 */
	public abstract void explode(ArrayList<Tail> tails, Plan plan, int sigma);

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

	/**
	 * Узнать, обучена ли станция
	 * 
	 * @return true - станция обучена, false - не обучена
	 */
	public boolean isTaught() {
		return taught;
	}

	/**
	 * Установить флаг обучения
	 * 
	 * @param taught
	 *            значение флага
	 */
	public void setTaught(boolean taught) {
		this.taught = taught;
	}

	/**
	 * Получить имя
	 * 
	 * @return имя
	 */
	public String getName() {
		return name;
	}

	/**
	 * Установить имя
	 * 
	 * @param name
	 *            имя
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Получить базовый уровень сигнала
	 * 
	 * @return базовый уровень сигнала
	 */
	public double getS() {
		return s;
	}

	/**
	 * Установить базовый уровень сигнала
	 * 
	 * @param s
	 *            базовый уровень сигнала
	 */
	public void setS(double s) {
		this.s = s;
	}

}
