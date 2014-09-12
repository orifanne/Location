package location;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Для фильтрации названий файлов (рализует возможность открывать только .xml
 * файлы).
 * 
 * @author Pokrovskaya Oksana
 */
public class ExtensionFileFilter extends FileFilter {

	String description;
	String extensions[];

	/**
	 * @param description
	 *            описание
	 * @param extension
	 *            допустимое расширение
	 */
	public ExtensionFileFilter(String description, String extension) {
		this(description, new String[] { extension });
	}

	/**
	 * @param description
	 *            описание
	 * @param extensions
	 *            допустимые расширения
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
	 * Переводит в массиве строк все символы в незаглавные
	 * 
	 * @param array
	 *            массив строк
	 */
	private void toLower(String array[]) {
		for (int i = 0, n = array.length; i < n; i++) {
			array[i] = array[i].toLowerCase();
		}
	}

	/**
	 * Получить опсание
	 * 
	 * @return описание
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Тестирует файл на соответствие заданным расширениям
	 * 
	 * @param file
	 *            файл для проверки
	 * @return true, если расширение файла допустимо, false иначе
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
