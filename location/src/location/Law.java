package location;

/** 
* Представляет нормальный закон распределения в ячейке. 
* @author Pokrovskaya Oksana
*/
public class Law {
	/** Математическое ожидание */
	private int a;
	/** Дисперсия */
	private int q;
	/** Базовая станция, с которой связан закон */
	private Station station;

	public Law() {
		a = 0;
		q = 0;
		station = null;
	}
	
	/** 
	* @param a1 математическое ожидание
	*/
	public Law(int a1) {
		a = a1;
		q = 0;
		station = null;
	}

	/** 
	* @param a1 математическое ожидание
	* @param s1 базовая станция
	*/
	public Law(int a1, Station s1) {
		a = a1;
		q = 0;
		station = s1;
	}

	/** 
	* @param a1 математическое ожидание
	* @param s1 базовая станция
	* @param q1 дисперсия
	*/
	public Law(int a1, int q1, Station s1) {
		a = a1;
		q = q1;
		station = s1;
	}

	/** 
	* @param s1 базовая станция
	*/
	public Law(Station s1) {
		station = s1;
	}


	/** 
	* @param a1 математическое ожидание
	* @param q1 дисперсия
	*/
	public Law(int a1, int q1) {
		a = a1;
		q = q1;
		station = null;
	}
	
	/** 
	* Изменяет математическое ожидание.
	* @param a1 математическое ожидание
	*/
	public void chA(int a1) {
		a = a1;
	}
	
	/** 
	* Изменяет дисперсию.
	* @param q1 дисперсия
	*/
	public void chQ(int q1) {
		q = q1;
	}
	
	/** 
	* Изменяет базовую станцию.
	* @param s1 базовая станция
	*/
	public void chStation(Station s1) {
		station = s1;
	}
}
