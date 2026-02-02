package com.ivray.poker.ui;

import com.ivray.poker.business.Card;
import java.util.List;

/**
 * Affichage du jeu (console ou GUI).
 */
public interface GameView {

	void showWelcome();

	void showAnte(int ante, float pot);

	void showYourHand(List<Card> hand, String combination, float pot, float stack);

	void showBetRoundStart();

	void showAction(String playerName, String actionText, float potAfter);

	void showShowdown();

	void showShowdownHand(String playerName, List<Card> hand, String combination);

	void showWinnerByFold(String playerName, float pot);

	void showWinnerSingle(String playerName, String combination, float potWon);

	void showWinnersTie(List<String> playerNames, float potEach);
}
