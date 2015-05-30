package location;

/**
 * ������������ ���������� ����� ������������� � ������.
 * 
 * @author Pokrovskaya Oksana
 */
public class Law {

	/** �������������� �������� */
	double a = 0;
	/** ��������� */
	private double q = 0;

	/**
	 * @param a1
	 *            �������������� ��������
	 * @param q1
	 *            ���������
	 */
	public Law(double a1, double q1) {
		if (a1 >= 0)
			a = a1;
		if (q1 >= 0)
			q = q1;
	}

	/**
	 * ���������� �������������� ��������.
	 * 
	 * @param a1
	 *            �������������� ��������
	 */
	public void setA(int a1) {
		a = a1;
	}

	/**
	 * ���������� ���������.
	 * 
	 * @param q1
	 *            ���������
	 */
	public void setQ(int q1) {
		q = q1;
	}

	/**
	 * �������� �������������� ��������.
	 * 
	 * @return �������������� ��������
	 */
	public double getA() {
		return a;
	}

	/**
	 * �������� ���������.
	 * 
	 * @return ���������
	 */
	public double getQ() {
		return q;
	}
}
