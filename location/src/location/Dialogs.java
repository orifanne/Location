package location;

import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

public class Dialogs {

	/** Количество выбранных карт в диалоге выбора карт для сравнения. */
	private int mapsSelected = 0;

	/**
	 * Максимально возможное число выбранных карт в диалоге выбора карт для
	 * сравнения.
	 */
	private int mapsSelectedLimit = 2;

	/**
	 * Показывает диалог файла для открытия
	 * 
	 * @return выбранный файл, либо null
	 */
	public static File showOpenDialog() {
		JFileChooser fileopen = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("xml", "xml");
		fileopen.setFileFilter(filter);
		int ret = fileopen.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION)
			return fileopen.getSelectedFile();
		else
			return null;
	}

	/**
	 * Показывает диалог выбора файла для сохранения
	 * 
	 * @return выбранный файл, либо null
	 */
	public static File showSaveDialog() {
		JFileChooser filesave = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("xml", "xml");
		filesave.setFileFilter(filter);
		int ret = filesave.showSaveDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION)
			return filesave.getSelectedFile();
		else
			return null;
	}

	/**
	 * Показывает диалог выбора и сравнения/оценки карт сил сигналов.
	 * 
	 * @param plan
	 *            план здания
	 * @param stationNumber
	 *            номер станции, для которой нужно показать карты для выбора
	 * @param object
	 *            позиционируемый объект
	 */
	public void showCompareMapsDialog(Plan plan, int stationNumber,
			PosObject object) {
		mapsSelectedLimit = 2;
		mapsSelected = 0;
		ArrayList<JCheckBox> j = new ArrayList<JCheckBox>();
		for (int i = 0; i < plan.getStation(stationNumber).getMaps().size(); i++) {
			JCheckBox c = new JCheckBox(plan.getStation(stationNumber)
					.getMap(i).getName());
			c.addChangeListener(new MapsSelectCheckboxListener());
			j.add(c);
		}

		Object[] params = j.toArray();
		int n = JOptionPane
				.showConfirmDialog(null, params,
						"Какие карты вы хотите сравнить?",
						JOptionPane.OK_CANCEL_OPTION);

		int m1 = -1;
		int m2 = -1;
		if (n == 0) {
			for (int i = 0; i < params.length; i++)
				if (((JCheckBox) params[i]).isSelected()) {
					if (m1 < 0) {
						m1 = i;
						continue;
					}
					if (m2 < 0) {
						m2 = i;
						break;
					}
				}
		}

		String s1 = null;
		double cmpRel = 0;

		if ((m1 >= 0) && (m2 >= 0)) {
			cmpRel = plan.getStation(stationNumber).cmpMapsRel(object, plan,
					1000, m1, m2);
			s1 = "Относительное различие: " + java.lang.Double.toString(cmpRel);
			Object[] results = { s1 };

			JOptionPane.showMessageDialog(null, results, "Результат",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Показывает диалог оценки карты сил сигналов.
	 * 
	 * @param plan
	 *            план здания
	 * @param stationNumber
	 *            номер станции
	 * @param object
	 *            позиционируемый объект
	 */
	public static void showEvaluateMapDialog(Plan plan, int stationNumber,
			PosObject object) {
		double[] res = new double[5];
		JTextField name = new JTextField();
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Пороговое значение вероятности:"), name };
		JOptionPane.showMessageDialog(null, inputs,
				"Введите пороговое значение вероятности",
				JOptionPane.PLAIN_MESSAGE);
		try {
			plan.getStation(stationNumber).evaluateMap(object, plan, 1000,
					java.lang.Double.valueOf(name.getText()), res);
			String s1 = "Ошибка позиционирования: "
					+ java.lang.Double.toString(res[0]);
			String s2 = "Процент угадываний: "
					+ java.lang.Double.toString(res[1]);
			String s3 = "Разброс по вероятности: "
					+ java.lang.Double.toString(res[2]);
			String s4 = "Разброс по площади: "
					+ java.lang.Double.toString(res[3]);
			String s5 = "Условная энтропия: "
					+ java.lang.Double.toString(res[4]);

			Object[] results = { s1, s2, s3, s4, s5 };

			JOptionPane.showMessageDialog(null, results, "Результат",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (NumberFormatException e1) {
			// ignoge
		}
	}

	/**
	 * Предлагает сохранить файл (и сохраняет, если поьзователь соглашается).
	 * (Да, Нет, Отмена)
	 * 
	 * @return true, если пользователь выбрал Да или Нет, false, если Отмена
	 */
	public static boolean saveChanged(Plan plan, File openedFile) {
		Object[] options = { "Да", "Нет", "Отмена" };
		int n = JOptionPane.showOptionDialog(null,
				"Вы хотите сохранить этот файл?", "Сохранить",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[2]);
		switch (n) {
		case 0:
			if (plan != null) {
				if (openedFile != null) {
					plan.save(openedFile);
				} else {
					JFileChooser filesave = new JFileChooser();
					FileFilter filter = new ExtensionFileFilter("xml", "xml");
					filesave.setFileFilter(filter);
					int ret = filesave.showSaveDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {
						File f = filesave.getSelectedFile();
						String s = f.getAbsolutePath();
						String s1 = null;
						int dotPos = s.lastIndexOf(".");
						if (dotPos > 0) {
							s1 = s.substring(dotPos);
							// System.out.println(s1);
							if (!s1.equals("xml")) {
								s += ".xml";
								f.renameTo(new File(s));
							}
						} else {
							s += ".xml";
							f.renameTo(new File(s));
						}
						plan.save(f);
					}
				}
			}
			break;
		case 1:
			break;
		case 2:
			return false;
		}
		return true;
	}

	/**
	 * Показывает диалог для ввода данных о базовой станции.
	 * 
	 * @return введенное пользователем имя станции
	 */
	public static String showStationDialog() {
		JTextField name = new JTextField();
		final JComponent[] inputs = new JComponent[] { new JLabel("Имя:"), name };
		JOptionPane.showMessageDialog(null, inputs, "Enter station data",
				JOptionPane.PLAIN_MESSAGE);
		return name.getText();
	}

	/**
	 * Показывает диалог для редактирования данных о базовой станции.
	 * 
	 * @param s
	 *            станция
	 * @return введенное пользователем имя станции
	 */
	public static String showStationDialog(Station s) {
		JTextField name = new JTextField();

		name.setText(s.getName());

		final JComponent[] inputs = new JComponent[] { new JLabel("Имя:"), name };
		JOptionPane.showMessageDialog(null, inputs, "Введите данные о станции",
				JOptionPane.PLAIN_MESSAGE);
		return name.getText();
	}

	/**
	 * Показывает диалог для ввода данных при расстановке станций.
	 * 
	 * @return введенное пользователем число станций
	 */
	public static String showPlaceStationsDialog() {
		JTextField name = new JTextField();
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Число станций:"), name };
		JOptionPane.showMessageDialog(null, inputs, "Введите число станций",
				JOptionPane.PLAIN_MESSAGE);
		return name.getText();
	}

	/**
	 * Обеспечивает возможность выбора mapsSelectedLimit карт в диалоге выбора
	 * карт.
	 */
	class MapsSelectCheckboxListener implements ChangeListener {
		boolean changed = false;

		@Override
		public void stateChanged(ChangeEvent arg0) {
			AbstractButton abstractButton = (AbstractButton) arg0.getSource();
			ButtonModel buttonModel = abstractButton.getModel();
			boolean armed = buttonModel.isArmed();
			boolean pressed = buttonModel.isPressed();
			boolean selected = buttonModel.isSelected();

			if (armed && pressed && selected) {
				changed = true;
			} else {
				if (changed) {
					if (armed && selected && !pressed) {
						if (mapsSelected == mapsSelectedLimit) {
							((JCheckBox) arg0.getSource()).setSelected(false);
						} else
							mapsSelected++;
					}
					if (armed && pressed && !selected) {
						if (mapsSelected > 0)
							mapsSelected--;
					}
					changed = false;
				}
			}
		}
	}

	/**
	 * Показывает диалог для ввода данных при моделировании карты.
	 * 
	 * @param n
	 *            строка для имени карты
	 * @return введенный пользователем базовый уровень сигнала
	 */
	public static double showMapModelDialog(String[] n)
			throws NumberFormatException {
		JTextField s = new JTextField();
		JTextField name = new JTextField();
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Имя карты:"), name,
				new JLabel("Базовый уровень сигнала:"), s };
		JOptionPane.showMessageDialog(null, inputs, "Введите данные о карте",
				JOptionPane.PLAIN_MESSAGE);
		n[0] = name.getText();
		return Double.parseDouble(s.getText());
	}

	/**
	 * Показывает диалог для ввода данных при обучении станции.
	 * 
	 * @param n
	 *            строка для имени карты
	 * @return число точек для обучения
	 */
	public static int showTeachStationDialog(String[] n) {
		JTextField s = new JTextField();
		JTextField name = new JTextField();
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Имя карты:"), name,
				new JLabel("Число точек для обучения:"), s };
		JOptionPane.showMessageDialog(null, inputs, "Введите данные о карте",
				JOptionPane.PLAIN_MESSAGE);
		n[0] = name.getText();
		return Integer.parseInt(s.getText());
	}
}
