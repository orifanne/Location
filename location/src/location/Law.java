package location;

/**
 * ѕредставл€ет нормальный закон распределени€ в €чейке.
 * 
 * @author Pokrovskaya Oksana
 */
public class Law {

	/** ћатематическое ожидание */
	double a = 0;
	/** ƒисперси€ */
	private double q = 0;

	/**
	 * @param a1
	 *            математическое ожидание
	 * @param q1
	 *            дисперси€
	 */
	public Law(double a1, double q1) {
		if (a1 >= 0)
			a = a1;
		if (q1 >= 0)
			q = q1;
	}

	/**
	 * ”становить математическое ожидание.
	 * 
	 * @param a1
	 *            математическое ожидание
	 */
	public void setA(int a1) {
		a = a1;
	}

	/**
	 * ”становить дисперсию.
	 * 
	 * @param q1
	 *            дисперси€
	 */
	public void setQ(int q1) {
		q = q1;
	}

	/**
	 * ѕолучить математическое ожидание.
	 * 
	 * @return математическое ожидание
	 */
	public double getA() {
		return a;
	}

	/**
	 * ѕолучить дисперсию.
	 * 
	 * @return дисперси€
	 */
	public double getQ() {
		return q;
	}
}
