package location;

/** 
* ������������ ���������� ����� ������������� � ������. 
* @author Pokrovskaya Oksana
*/
public class Law {
	/** �������������� �������� */
	private double a = 0;
	/** ��������� */
	private double q = 0;

	public Law() {
		a = 0;
		q = 0;
	}
	
	/** 
	* @param a1 �������������� ��������
	*/
	public Law(double a1) {
		if (a1 >= 0)
			a = a1;
		q = 0;
	}

	/** 
	* @param a1 �������������� ��������
	* @param q1 ���������
	*/
	public Law(double a1, double q1) {
		if (a1 >= 0)
			a = a1;
		if (q1 >= 0)
			q = q1;
	}

	/** 
	* �������� �������������� ��������.
	* @param a1 �������������� ��������
	*/
	public void chA(int a1) {
		a = a1;
	}
	
	/** 
	* �������� ���������.
	* @param q1 ���������
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
