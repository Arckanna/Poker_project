package com.ivray.poker.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Paquet de cartes : construction à partir des couleurs, mélange et pioche.
 */
public class Deck {
	private final List<Card> cards = new ArrayList<>();
	private int index = 0;

	public Deck(List<Color> colors) {
		for (Color c : colors) {
			for (int v = 2; v <= 14; v++) {
				cards.add(new Card(v, c));
			}
		}
	}

	public void shuffle(Random rng) {
		Collections.shuffle(cards, rng);
	}

	public Card draw() {
		if (index >= cards.size()) {
			throw new IllegalStateException("Deck empty");
		}
		return cards.get(index++);
	}
}
