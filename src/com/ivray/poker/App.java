package com.ivray.poker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ivray.poker.business.Card;
import com.ivray.poker.business.Color;
import com.ivray.poker.business.Player;

public class App {
	private static Set<Color> colors = new HashSet<>();
	private static List<Card> cards = new ArrayList<>();

	public static void main(String[] args) {
		printCards();
		System.out.println(cards);

		System.out.println(colors);
		Player player1 = new Player("Capucine");
		System.out.println(player1);

		Player player2 = new Player("Louise");
		System.out.println(player2);

	}

	private static void printCards() {
		Color color1 = new Color("heart");
		System.out.println(color1);
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

}
