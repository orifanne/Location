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

	/** Имя станции */
	protected String name = "station";

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
	 * Рассчитывает уровень сигнала, моделируя распространение сигнала.
	 * 
	 * @param tail
	 *            ячейка, в которой необходимо рассчитать уровень сигнала
	 * @param sigma
	 *            дисперсия уровня сигнала
	 * @param m
	 *            карта, в которую нужно записать результат
	 * @param s
	 *            базовая сила сигнала
	 */
	public abstract void explode(Tail tail, int sigma, Map m, double s);

	/**
	 * Строит карту уровней сигнала, моделируя распространение сигнала, и
	 * добавляет ее к списку карт данной базовой станции.
	 * 
	 * @param tails
	 *            ячейки, для которых необходимо построить карту уровней сигнала
	 * @param sigma
	 *            дисперсия уровня сигнала
	 * @param name
	 *            имя новой карты
	 * @param s
	 *            базовая сила сигнала
	 */
	public abstract void explode(ArrayList<Tail> tails, int sigma, String name,
			double s);

	/**
	 * Рассчитывает уровень сигнала с учетом плана помещения, моделируя
	 * распространение сигнала.
	 * 
	 * @param tail
	 *            ячейка, в которой необходимо рассчитать уровень сигнала
	 * @param plan
	 *            план помещения
	 * @param m
	 *            карта, в которую нужно записать результат
	 * @param s
	 *            базовая сила сигнала
	 */
	public abstract void explode(Tail tail, Plan plan, Map m, double s);

	/**
	 * Строит карту уровней сигнала с учетом плана помещения, моделируя
	 * распространение сигнала, и добавляет ее к списку карт данной базовой
	 * станции.
	 * 
	 * @param plan
	 *            план помещения
	 * @param name
	 *            имя новой карты
	 * @param s
	 *            базовая сила сигнала
	 */
	public abstract void explode(Plan plan, String name, double s);

	/**
	 * Строит карту уровней сигнала с учетом плана помещения, обучая станцию, и
	 * добавляет ее к списку карт данной базовой станции.
	 * 
	 * @param object
	 *            позиционируемый объект
	 * @param plan
	 *            план здания
	 * @param num
	 *            количество точек позиционирования (количество шагов объекта)
	 * @param name
	 *            имя новой карты
	 */
	public abstract void teach(PosObject object, Plan plan, int num, String name);

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

}
