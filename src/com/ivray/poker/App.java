package com.ivray.poker;

import com.ivray.poker.ui.ConsoleInput;
import com.ivray.poker.ui.ConsoleView;

/**
 * Point d’entrée console : une main de poker 5 cartes.
 * Pour lancer l’interface graphique, exécuter {@link com.ivray.poker.ui.PokerGUI#main}.
 */
public class App {

	public static void main(String[] args) {
		// Si l’argument "gui" est passé, on lance l’interface graphique
		if (args.length > 0 && "gui".equalsIgnoreCase(args[0].trim())) {
			com.ivray.poker.ui.PokerGUI.main(args);
			return;
		}
		GameRunner runner = new GameRunner();
		runner.runGame(new ConsoleInput(), new ConsoleView());
	}
}
