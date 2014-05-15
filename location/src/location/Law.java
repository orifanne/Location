package location;

/** 
* ѕредставл€ет нормальный закон распределени€ в €чейке. 
* @author Pokrovskaya Oksana
*/
public class Law {
	/** ћатематическое ожидание */
	private double a = 0;
	/** ƒисперси€ */
	private double q = 0;

	public Law() {
		a = 0;
		q = 0;
	}
	
	/** 
	* @param a1 математическое ожидание
	*/
	public Law(double a1) {
		if (a1 >= 0)
			a = a1;
		q = 0;
	}

	/** 
	* @param a1 математическое ожидание
	* @param q1 дисперси€
	*/
	public Law(double a1, double q1) {
		if (a1 >= 0)
			a = a1;
		if (q1 >= 0)
			q = q1;
	}

	/** 
	* »змен€ет математическое ожидание.
	* @param a1 математическое ожидание
	*/
	public void chA(int a1) {
		a = a1;
	}
	
	/** 
	* »змен€ет дисперсию.
	* @param q1 дисперси€
	*/
	public void chQ(int q1) {
		q = q1;
	}

	public double getA() {
		return a;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(a);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(q);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Law other = (Law) obj;
		if (Double.doubleToLongBits(a) != Double.doubleToLongBits(other.a))
			return false;
		if (Double.doubleToLongBits(q) != Double.doubleToLongBits(other.q))
			return false;
		return true;
	}

}
