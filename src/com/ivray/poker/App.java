package com.ivray.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ivray.poker.business.Card;
import com.ivray.poker.business.Color;
import com.ivray.poker.business.HandRank;
import com.ivray.poker.business.Player;
import com.ivray.poker.util.CardComparatorOnValue;

public class App {
	private static final Set<Color> colors = new HashSet<>();
	private static final List<Card> cards = new ArrayList<>();
	private static final List<Player> players = new ArrayList<>();

	public static void main(String[] args) {
		printCards();
		addPlayer();
		shuffleCards();
		dealCards();
		sortPlayers();

		for (Player player : players) {
			System.out.println(player);
			System.out.println(analyzeHand(player));
		}

		List<Player> gagnants = getGagnants(players);
		afficherGagnants(gagnants);

		sortCards();
	}

	/**
	 * Compare les mains de deux joueurs via HandRank.getForce(), puis cartes hautes si égalité.
	 * @return &lt; 0 si p1 bat p2, 0 si égalité, &gt; 0 si p2 bat p1
	 */
	private static int comparerMains(Player p1, Player p2) {
		HandRank r1 = getHandRank(p1);
		HandRank r2 = getHandRank(p2);
		int cmp = Integer.compare(r1.getForce(), r2.getForce());
		if (cmp != 0) {
			return cmp;
		}
		// Égalité de combinaison : départage par les valeurs des cartes (triées décroissant)
		List<Integer> v1 = getValeursTrieesDesc(p1);
		List<Integer> v2 = getValeursTrieesDesc(p2);
		for (int i = 0; i < v1.size(); i++) {
			int c = Integer.compare(v1.get(i), v2.get(i));
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}

	/** Retourne les valeurs des cartes de la main, triées de la plus forte à la plus faible. */
	private static List<Integer> getValeursTrieesDesc(Player player) {
		List<Integer> valeurs = new ArrayList<>();
		for (Card c : player.getHandCards()) {
			valeurs.add(c.getValue());
		}
		valeurs.sort(Collections.reverseOrder());
		return valeurs;
	}

	/**
	 * Détermine le ou les gagnants parmi les joueurs (ex æquo possibles).
	 */
	private static List<Player> getGagnants(List<Player> joueurs) {
		if (joueurs == null || joueurs.isEmpty()) {
			return List.of();
		}
		List<Player> gagnants = new ArrayList<>();
		Player meilleur = joueurs.get(0);
		gagnants.add(meilleur);
		for (int i = 1; i < joueurs.size(); i++) {
			Player p = joueurs.get(i);
			int cmp = comparerMains(p, meilleur);
			if (cmp > 0) {
				gagnants.clear();
				gagnants.add(p);
				meilleur = p;
			} else if (cmp == 0) {
				gagnants.add(p);
			}
		}
		return gagnants;
	}

	private static void afficherGagnants(List<Player> gagnants) {
		if (gagnants.isEmpty()) {
			return;
		}
		System.out.println("--- Gagnant(s) ---");
		if (gagnants.size() == 1) {
			System.out.println(gagnants.get(0).getPseudo() + " remporte la main avec " + getHandRank(gagnants.get(0)) + ".");
		} else {
			System.out.print("Égalité entre : ");
			for (int i = 0; i < gagnants.size(); i++) {
				if (i > 0) {
					System.out.print(", ");
				}
				System.out.print(gagnants.get(i).getPseudo());
			}
			System.out.println();
		}
	}

	/**
	 * Calcule le rang de la main du joueur (sans effet de bord).
	 */
	private static HandRank getHandRank(Player player) {
		List<Card> handCards = new ArrayList<>(player.getHandCards());
		Collections.sort(handCards, new CardComparatorOnValue());

		Map<Integer, Integer> occurencesMap = new HashMap<>();
		for (Card card : handCards) {
			occurencesMap.merge(card.getValue(), 1, Integer::sum);
		}

		int nbPairs = 0;
		boolean hasThreeOfAKind = false;
		boolean hasFourOfAKind = false;
		for (Entry<Integer, Integer> entry : occurencesMap.entrySet()) {
			switch (entry.getValue()) {
				case 2 -> nbPairs++;
				case 3 -> hasThreeOfAKind = true;
				case 4 -> hasFourOfAKind = true;
				default -> { }
			}
		}

		boolean flush = isFlush(handCards);
		boolean straight = isStraight(handCards);

		if (flush && straight) {
			return isRoyalStraight(handCards) ? HandRank.QUINTE_ROYALE : HandRank.QUINTE_FLUSH;
		}
		if (hasFourOfAKind) {
			return HandRank.CARRE;
		}
		if (nbPairs == 1 && hasThreeOfAKind) {
			return HandRank.FULL;
		}
		if (flush) {
			return HandRank.FLUSH;
		}
		if (straight) {
			return HandRank.QUINTE;
		}
		if (hasThreeOfAKind) {
			return HandRank.BRELAN;
		}
		if (nbPairs == 2) {
			return HandRank.DEUX_PAIRES;
		}
		if (nbPairs == 1) {
			return HandRank.PAIRE;
		}
		return HandRank.RIEN;
	}

	private static void sortCards() {
		Collections.sort(cards, new CardComparatorOnValue());
	}

	private static void sortPlayers() {
		Collections.sort(players);
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
	 * Analyse la main du joueur et affiche les occurrences (debug).
	 * @param player le joueur dont on analyse la main
	 * @return le rang de la main (HandRank)
	 */
	private static HandRank analyzeHand(Player player) {
		List<Card> handCards = new ArrayList<>(player.getHandCards());
		Collections.sort(handCards, new CardComparatorOnValue());
		Map<Integer, Integer> occurencesMap = new HashMap<>();
		for (Card card : handCards) {
			occurencesMap.merge(card.getValue(), 1, Integer::sum);
		}
		System.out.println(occurencesMap);
		return getHandRank(player);
	}

	/** Retourne true si les 5 cartes sont de la même couleur. */
	private static boolean isFlush(List<Card> handCards) {
		if (handCards.isEmpty()) {
			return false;
		}
		Color first = handCards.get(0).getColor();
		for (int i = 1; i < handCards.size(); i++) {
			if (!handCards.get(i).getColor().equals(first)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retourne true si les 5 cartes forment une quinte (valeurs consécutives).
	 * Gère la quinte à l'as (A-2-3-4-5).
	 */
	private static boolean isStraight(List<Card> handCards) {
		if (handCards.size() != 5) {
			return false;
		}
		List<Integer> values = new ArrayList<>();
		for (Card c : handCards) {
			values.add(c.getValue());
		}
		Collections.sort(values);

		// Quinte classique (ex: 2-3-4-5-6 ou 10-J-Q-K-A)
		boolean consecutive = true;
		for (int i = 0; i < 4; i++) {
			if (values.get(i + 1) - values.get(i) != 1) {
				consecutive = false;
				break;
			}
		}
		// Quinte à l'as (A-2-3-4-5) : valeurs 2,3,4,5,14
		return consecutive || (values.get(0) == 2 && values.get(1) == 3 && values.get(2) == 4
				&& values.get(3) == 5 && values.get(4) == 14);
	}

	/** Quinte royale = 10, J, Q, K, A de la même couleur. */
	private static boolean isRoyalStraight(List<Card> handCards) {
		List<Integer> values = new ArrayList<>();
		for (Card c : handCards) {
			values.add(c.getValue());
		}
		Collections.sort(values);
		return values.get(0) == 10 && values.get(1) == 11 && values.get(2) == 12
				&& values.get(3) == 13 && values.get(4) == 14;
	}
}
