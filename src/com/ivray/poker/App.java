package com.ivray.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Map.Entry;

import com.ivray.poker.business.Card;
import com.ivray.poker.business.Color;
import com.ivray.poker.business.Player;

public class App {
	private static Set<Color> colors = new HashSet<>();
	private static List<Card> cards = new ArrayList<>();
	private static List<Player> players = new ArrayList<>();

	public static void main(String[] args) {
		printCards();
		addPlayer();
		shuffleCards();
		dealCards();
		for (Player player : players) {
			System.out.println(player);

			System.out.println(analyzeHand(player));

		}

	}

	private static void printCards() {
		Color color1 = new Color("heart");
		colors.add(color1);
		colors.add(new Color("spade"));
		colors.add(new Color("diamond"));
		colors.add(new Color("club"));

		for (Color color : colors) {
			for (int i = 2; i <= 14; i++) {
				cards.add(new Card(i, color));
			}
		}
	}

	private static void addPlayer() {
		Player player1 = new Player("Capucine");
		Player player2 = new Player("Louise");
		Player player3 = new Player("Maman");
		players.add(player1);
		players.add(player2);
		players.add(player3);
	}

	private static void shuffleCards() {
		Collections.shuffle(cards);
	}

	private static void dealCards() {
		for (Player player : players) {
			player.getHandCards().add(cards.remove(0));
			player.getHandCards().add(cards.remove(0));
			player.getHandCards().add(cards.remove(0));
			player.getHandCards().add(cards.remove(0));
			player.getHandCards().add(cards.remove(0));
		}

	}

	/**
	 * This method analyzes the hand of the player given as
	 * a parameter
	 * 
	 * @param player
	 * @return the best combination of cards
	 */
	private static String analyzeHand(Player player) {
		String result = "";
		List<Card> handCards = player.getHandCards();
		Map<Integer, Integer> occurencesMap = new HashMap<>();
		int nbPairs = 0;
		boolean nbThreeOfAKind = false;
		boolean nbFourOfAKind = false;

		for (Card card : handCards) {
			int value = card.getValue();
			if (occurencesMap.containsKey(value)) {
				occurencesMap.put(value, occurencesMap.get(value) + 1);
			} else {
				occurencesMap.put(value, 1);
			}
		}
		System.out.println(occurencesMap);
		for (Entry<Integer, Integer> entry : occurencesMap.entrySet()) {
			if (entry.getValue() == 2) {
				nbPairs++;
			} else if (entry.getValue() == 3) {
				nbThreeOfAKind = true;
			} else if (entry.getValue() == 4) {
				nbFourOfAKind = true;
			}
		}
		if (nbPairs == 1 && nbThreeOfAKind) {
			result = "Full\n";
		} else if (nbPairs == 2) {
			result = "Two pairs\n";
		} else if (nbPairs == 1) {
			result = "One pair\n";
		} else if (nbThreeOfAKind) {
			result = "Three of a kind\n";
		} else if (nbFourOfAKind) {
			result = "Four of a kind \n";
		} else {
			result = "Nothing \n";
		}
		return result;
	}

}
