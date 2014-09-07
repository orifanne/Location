package location;

import javax.swing.*;

/**
 * Создает и отображает основное окно программы.
 * 
 * @author Pokrovskaya Oksana
 */

public class Main {
	public static void main(String[] args) {
		// создаем окно
		JFrame app = new Location();
		// отображаем его
		app.setVisible(true);
	}
}
