package location;

/**
 * ��������������� �����, ������������ ������� ��� �������� �� ������.
 * 
 * @author Pokrovskaya Oksana
 */
public class Frame extends Tail {

	/** ���� ������������� */
	private boolean used;

	/** ���� �������������� ������ */
	public boolean up = false;

	/** ���� �������������� ����� */
	public boolean down = false;

	/** ���� �������������� ������ */
	public boolean right = false;

	/** ���� �������������� ����� */
	public boolean left = false;

	public Frame() {
		super();
		used = false;
	}

	/**
	 * @param x11
	 *            �������� ������ �������� ���� (< x21)
	 * @param y11
	 *            �������� ������ �������� ���� (> y21)
	 * @param x21
	 *            �������� ������� ������� ����
	 * @param y21
	 *            �������� ������� ������� ����
	 * @throws Exception
	 */
	public Frame(int x11, int y11, int x21, int y21) throws Exception {
		super(x11, y11, x21, y21);
		used = false;
	}

	/**
	 * @param x11
	 *            �������� ������ �������� ���� (< x21)
	 * @param y11
	 *            �������� ������ �������� ���� (> y21)
	 * @param x21
	 *            �������� ������� ������� ����
	 * @param y21
	 *            �������� ������� ������� ����
	 */
	public Frame(double x11, double y11, double x21, double y21) {
		super(x11, y11, x21, y21);
		used = false;
	}

	/**
	 * �������� ���� �������������
	 * 
	 * @return ���� �������������
	 */
	public boolean isUsed() {
		return used;
	}

	/**
	 * ���������� ���� �������������
	 * 
	 * @param a
	 *            ���� �������������
	 */
	public void used(boolean a) {
		used = a;
	}
}
