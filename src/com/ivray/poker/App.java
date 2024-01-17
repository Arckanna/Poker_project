package com.ivray.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		System.out.println(players);

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
}
