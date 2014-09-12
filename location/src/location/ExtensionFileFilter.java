package location;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * ��� ���������� �������� ������ (�������� ����������� ��������� ������ .xml
 * �����).
 * 
 * @author Pokrovskaya Oksana
 */
public class ExtensionFileFilter extends FileFilter {

	String description;
	String extensions[];

	/**
	 * @param description
	 *            ��������
	 * @param extension
	 *            ���������� ����������
	 */
	public ExtensionFileFilter(String description, String extension) {
		this(description, new String[] { extension });
	}

	/**
	 * @param description
	 *            ��������
	 * @param extensions
	 *            ���������� ����������
	 */
	public ExtensionFileFilter(String description, String extensions[]) {
		if (description == null) {
			this.description = extensions[0];
		} else {
			this.description = description;
		}
		this.extensions = (String[]) extensions.clone();
		toLower(this.extensions);
	}

	/**
	 * ��������� � ������� ����� ��� ������� � �����������
	 * 
	 * @param array
	 *            ������ �����
	 */
	private void toLower(String array[]) {
		for (int i = 0, n = array.length; i < n; i++) {
			array[i] = array[i].toLowerCase();
		}
	}

	/**
	 * �������� �������
	 * 
	 * @return ��������
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * ��������� ���� �� ������������ �������� �����������
	 * 
	 * @param file
	 *            ���� ��� ��������
	 * @return true, ���� ���������� ����� ���������, false �����
	 */
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			String path = file.getAbsolutePath().toLowerCase();
			for (int i = 0, n = extensions.length; i < n; i++) {
				String extension = extensions[i];
				if ((path.endsWith(extension) && (path.charAt(path.length()
						- extension.length() - 1)) == '.')) {
					return true;
				}
			}
		}
		return false;
	}
}
