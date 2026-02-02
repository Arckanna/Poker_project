package com.ivray.poker.ui;

import com.ivray.poker.business.Card;
import com.ivray.poker.util.GameDisplay;
import java.util.List;

/** Affichage du jeu en console (GameDisplay). */
public class ConsoleView implements GameView {

	@Override
	public void showWelcome() {
		GameDisplay.titleScreen();
	}

	@Override
	public void showAnte(int ante, float pot) {
		GameDisplay.printAnte(ante, pot);
	}

	@Override
	public void showYourHand(List<Card> hand, String combination, float pot, float stack) {
		GameDisplay.printYourHand(hand, combination, pot, stack);
	}

	@Override
	public void showBetRoundStart() {
		GameDisplay.printBetRoundTitle();
	}

	@Override
	public void showAction(String playerName, String actionText, float potAfter) {
		GameDisplay.printAction(playerName, actionText, potAfter);
	}

	@Override
	public void showShowdown() {
		GameDisplay.printShowdownTitle();
	}

	@Override
	public void showShowdownHand(String playerName, List<Card> hand, String combination) {
		GameDisplay.printShowdownHand(playerName, hand, combination);
	}

	@Override
	public void showWinnerByFold(String playerName, float pot) {
		GameDisplay.printWinnersTitle();
		GameDisplay.printWinnerByFold(playerName, pot);
	}

	@Override
	public void showWinnerSingle(String playerName, String combination, float potWon) {
		GameDisplay.printWinnersTitle();
		GameDisplay.printWinnerSingle(playerName, combination, potWon);
	}

	@Override
	public void showWinnersTie(List<String> playerNames, float potEach) {
		GameDisplay.printWinnersTitle();
		GameDisplay.printWinnersTie(playerNames, potEach);
	}
}
