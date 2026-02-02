package com.ivray.poker;

import com.ivray.poker.business.Action;
import com.ivray.poker.business.BotStrategy;
import com.ivray.poker.business.Card;
import com.ivray.poker.business.Color;
import com.ivray.poker.business.Deck;
import com.ivray.poker.business.HandRank;
import com.ivray.poker.business.Player;
import com.ivray.poker.business.RoundState;
import com.ivray.poker.business.SimpleBotStrategy;
import com.ivray.poker.util.CardComparatorOnValue;
import com.ivray.poker.util.GameDisplay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class App {
	private static final Set<Color> colors = new HashSet<>();
	private static final List<Player> players = new ArrayList<>();
	private static final BotStrategy botStrategy = new SimpleBotStrategy();

	/** Ante (mise fixe) payée par chaque joueur au début de la main. */
	private static final int ANTE = 2;
	/** Mise minimum pour un bet. */
	private static final int MISE_MIN = 2;

	private static float pot = 0;
	private static final Scanner scanner = new Scanner(System.in);
	private static final Random random = new Random();

	public static void main(String[] args) {
		initCouleurs();
		ajouterJoueurs();
		GameDisplay.titleScreen();
		scanner.nextLine();
		collecterAnte();
		melangerEtDistribuer();
		afficherMainJoueurHumain();
		boolean[] aFolded = tourDeMise();
		showdown(aFolded);
	}

	/** Distribue 5 cartes à un joueur depuis le paquet. */
	private static void deal5(Deck deck, Player p) {
		p.getHandCards().clear();
		for (int i = 0; i < 5; i++) {
			p.getHandCards().add(deck.draw());
		}
	}

	private static void collecterAnte() {
		pot = 0;
		for (Player p : players) {
			p.setBalance(p.getBalance() - ANTE);
			pot += ANTE;
		}
		GameDisplay.printAnte(ANTE, pot);
	}

	private static void melangerEtDistribuer() {
		Deck deck = new Deck(new ArrayList<>(colors));
		deck.shuffle(random);
		for (Player p : players) {
			deal5(deck, p);
		}
	}

	private static void afficherMainJoueurHumain() {
		Player humain = null;
		for (Player p : players) {
			if (p.isHuman()) {
				humain = p;
				break;
			}
		}
		if (humain == null) return;
		List<Card> main = new ArrayList<>(humain.getHandCards());
		Collections.sort(main, new CardComparatorOnValue());
		GameDisplay.printYourHand(main, getHandRank(humain).toString(), pot, humain.getBalance());
	}

	/**
	 * Un tour de mise : le joueur humain agit en premier, puis chaque IA répond.
	 * @return tableau aFolded[i] = true si le joueur i s'est couché
	 */
	private static boolean[] tourDeMise() {
		int n = players.size();
		boolean[] aFolded = new boolean[n];
		float[] miseActuelle = new float[n]; // montant misé par chaque joueur ce tour
		float miseMax = 0;
		int dernierRelance = 0;
		int idx = 0;

		GameDisplay.printBetRoundTitle();

		while (true) {
			if (aFolded[idx]) {
				idx = (idx + 1) % n;
				continue;
			}
			float aSuivre = miseMax - miseActuelle[idx];
			Player p = players.get(idx);

			if (p.isHuman()) {
				// Joueur humain : saisie clavier
				if (aSuivre <= 0) {
					String action = joueurHumainCheckOuBet(p);
					if ("m".equalsIgnoreCase(action)) {
						int montant = demanderMise(p);
						miseActuelle[idx] += montant;
						pot += montant;
						miseMax = miseActuelle[idx];
						dernierRelance = idx;
						GameDisplay.printAction(p.getPseudo(), "mise " + montant + ".", pot);
					} else {
						GameDisplay.printAction(p.getPseudo(), "check.", pot);
					}
				} else {
					boolean call = joueurHumainCallOuFold(p, aSuivre);
					if (call) {
						float montant = Math.min(aSuivre, p.getBalance());
						miseActuelle[idx] += montant;
						pot += montant;
						p.setBalance(p.getBalance() - montant);
						GameDisplay.printAction(p.getPseudo(), "suit (" + (int) montant + ").", pot);
					} else {
						aFolded[idx] = true;
						GameDisplay.printAction(p.getPseudo(), "se couche.", pot);
					}
				}
			} else {
				// IA : stratégie (RoundState + HandRank)
				RoundState state = new RoundState();
				state.pot = (int) pot;
				state.currentBet = (int) aSuivre;
				HandRank rank = getHandRank(p);
				Action action = botStrategy.decide(p, state, rank, random);

				switch (action.type()) {
					case FOLD -> {
						aFolded[idx] = true;
						GameDisplay.printAction(p.getPseudo(), "se couche.", pot);
					}
					case CHECK -> GameDisplay.printAction(p.getPseudo(), "check.", pot);
					case CALL -> {
						float montant = Math.min(aSuivre, p.getBalance());
						p.setBalance(p.getBalance() - montant);
						miseActuelle[idx] += montant;
						pot += montant;
						GameDisplay.printAction(p.getPseudo(), "suit (" + (int) montant + ").", pot);
					}
					case BET -> {
						int mise = Math.min(action.amount(), (int) p.getBalance());
						p.setBalance(p.getBalance() - mise);
						miseActuelle[idx] += mise;
						pot += mise;
						miseMax = miseActuelle[idx];
						dernierRelance = idx;
						GameDisplay.printAction(p.getPseudo(), "mise " + mise + ".", pot);
					}
					case RAISE -> {
						int total = (int) aSuivre + action.amount();
						int mise = Math.min(total, (int) p.getBalance());
						p.setBalance(p.getBalance() - mise);
						miseActuelle[idx] += mise;
						pot += mise;
						miseMax = miseActuelle[idx];
						dernierRelance = idx;
						GameDisplay.printAction(p.getPseudo(), "relance " + mise + ".", pot);
					}
				}
			}
			int next = (idx + 1) % n;
			while (next != idx && aFolded[next]) next = (next + 1) % n;
			if (next == idx) break; // seul joueur restant
			idx = next;
			// Terminer quand tout le monde a égalisé et on revient au dernier relanceur
			float m = miseMax;
			boolean tousEgalises = true;
			for (int i = 0; i < n; i++) {
				if (!aFolded[i] && miseActuelle[i] < m) { tousEgalises = false; break; }
			}
			if (tousEgalises && idx == dernierRelance) break;
		}

		return aFolded;
	}

	private static String joueurHumainCheckOuBet(Player p) {
		System.out.print(p.getPseudo() + " : Check (c) ou Miser (m) ? ");
		String line = scanner.nextLine().trim();
		if ("m".equalsIgnoreCase(line) || "mise".equalsIgnoreCase(line)) return "m";
		return "c";
	}

	private static boolean joueurHumainCallOuFold(Player p, float aSuivre) {
		System.out.print(p.getPseudo() + " : Suivre " + aSuivre + " (s) ou Se coucher (f) ? ");
		String line = scanner.nextLine().trim();
		return "s".equalsIgnoreCase(line) || "suivre".equalsIgnoreCase(line) || "c".equalsIgnoreCase(line) || "call".equalsIgnoreCase(line);
	}

	private static int demanderMise(Player p) {
		System.out.print("Montant à miser (min " + MISE_MIN + ", max " + (int) p.getBalance() + ") ? ");
		String line = scanner.nextLine().trim();
		int montant = MISE_MIN;
		try {
			montant = Integer.parseInt(line);
		} catch (NumberFormatException e) {
			// garde défaut
		}
		montant = Math.max(MISE_MIN, Math.min(montant, (int) p.getBalance()));
		p.setBalance(p.getBalance() - montant);
		return montant;
	}

	private static void showdown(boolean[] aFolded) {
		List<Player> encoreEnJeu = new ArrayList<>();
		for (int i = 0; i < players.size(); i++) {
			if (!aFolded[i]) encoreEnJeu.add(players.get(i));
		}
		if (encoreEnJeu.isEmpty()) return;
		if (encoreEnJeu.size() == 1) {
			Player gagnant = encoreEnJeu.get(0);
			gagnant.setBalance(gagnant.getBalance() + pot);
			GameDisplay.printShowdownTitle();
			GameDisplay.printWinnerByFold(gagnant.getPseudo(), pot);
			return;
		}
		GameDisplay.printShowdownTitle();
		for (Player p : encoreEnJeu) {
			List<Card> main = new ArrayList<>(p.getHandCards());
			Collections.sort(main, new CardComparatorOnValue());
			GameDisplay.printShowdownHand(p.getPseudo(), main, getHandRank(p).toString());
		}
		List<Player> gagnants = getGagnants(encoreEnJeu);
		for (Player g : gagnants) {
			g.setBalance(g.getBalance() + pot / gagnants.size());
		}
		afficherGagnants(gagnants);
	}

	private static void initCouleurs() {
		colors.clear();
		colors.add(new Color("heart"));
		colors.add(new Color("spade"));
		colors.add(new Color("diamond"));
		colors.add(new Color("club"));
	}

	private static void ajouterJoueurs() {
		players.clear();
		Player humain = new Player("Vous");
		humain.setHuman(true);
		players.add(humain);
		players.add(new Player("Louise"));
		players.add(new Player("Maman"));
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
		if (gagnants.isEmpty()) return;
		GameDisplay.printWinnersTitle();
		if (gagnants.size() == 1) {
			GameDisplay.printWinnerSingle(gagnants.get(0).getPseudo(), getHandRank(gagnants.get(0)).toString(), pot);
		} else {
			List<String> noms = new ArrayList<>();
			for (Player g : gagnants) noms.add(g.getPseudo());
			GameDisplay.printWinnersTie(noms, pot / gagnants.size());
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
