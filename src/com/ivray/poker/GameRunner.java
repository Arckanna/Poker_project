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
import com.ivray.poker.ui.GameView;
import com.ivray.poker.ui.HumanInputProvider;
import com.ivray.poker.util.CardComparatorOnValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * Moteur de jeu : parties en boucle jusqu'à un seul joueur avec des jetons ou abandon.
 * Chaque main ne fait jouer que les joueurs avec balance > 0 (les autres sont éliminés).
 */
public class GameRunner {

	private static final int ANTE = 2;
	private static final int MISE_MIN = 2;

	private final Set<Color> colors = new HashSet<>();
	private final List<Player> players = new ArrayList<>();
	private final BotStrategy botStrategy = new SimpleBotStrategy();
	private final Random random = new Random();

	private float pot = 0;

	public GameRunner() {
		initCouleurs();
		ajouterJoueurs();
	}

	private void initCouleurs() {
		colors.clear();
		colors.add(new Color("heart"));
		colors.add(new Color("spade"));
		colors.add(new Color("diamond"));
		colors.add(new Color("club"));
	}

	private void ajouterJoueurs() {
		players.clear();
		Player humain = new Player("Vous");
		humain.setHuman(true);
		players.add(humain);
		players.add(new Player("Louise"));
		players.add(new Player("Maman"));
	}

	/** Compte les joueurs encore en jeu (balance > 0). */
	private int countActive() {
		int n = 0;
		for (Player p : players) {
			if (p.getBalance() > 0) n++;
		}
		return n;
	}

	/** Retourne le premier joueur avec balance > 0 (gagnant de la partie), ou null. */
	private Player getWinner() {
		for (Player p : players) {
			if (p.getBalance() > 0) return p;
		}
		return null;
	}

	/** Stacks actuels (nom → jetons) pour affichage. */
	private Map<String, Float> getStacks() {
		Map<String, Float> m = new LinkedHashMap<>();
		for (Player p : players) {
			m.put(p.getPseudo(), p.getBalance());
		}
		return m;
	}

	/**
	 * Boucle de parties : plusieurs mains jusqu'à un seul joueur avec des jetons ou abandon.
	 */
	public void runGame(HumanInputProvider input, GameView view) {
		view.showWelcome();
		input.waitForStart();

		while (true) {
			int active = countActive();
			if (active < 2) {
				Player winner = getWinner();
				if (winner != null) {
					view.showGameOver(winner.getPseudo() + " remporte la partie !");
				} else {
					view.showGameOver("Partie terminée.");
				}
				return;
			}

			runOneHand(input, view);
			view.showHandOver(getStacks());

			if (countActive() < 2) {
				Player winner = getWinner();
				if (winner != null) {
					view.showGameOver(winner.getPseudo() + " remporte la partie !");
				} else {
					view.showGameOver("Partie terminée.");
				}
				return;
			}

			if (!input.continuePlaying()) {
				view.showGameOver("Vous avez abandonné la partie.");
				return;
			}
		}
	}

	/** Une seule main : seuls les joueurs avec balance > 0 participent. */
	private void runOneHand(HumanInputProvider input, GameView view) {
		int n = players.size();
		boolean[] inHand = new boolean[n];
		for (int i = 0; i < n; i++) {
			inHand[i] = players.get(i).getBalance() > 0;
		}
		int activeCount = 0;
		for (boolean b : inHand) if (b) activeCount++;
		if (activeCount < 2) {
			return;
		}

		pot = 0;
		for (int i = 0; i < n; i++) {
			if (inHand[i]) {
				Player p = players.get(i);
				p.setBalance(p.getBalance() - ANTE);
				pot += ANTE;
			}
		}
		view.showAnte(ANTE, pot);

		Deck deck = new Deck(new ArrayList<>(colors));
		deck.shuffle(random);
		for (int i = 0; i < n; i++) {
			if (inHand[i]) {
				deal5(deck, players.get(i));
			}
		}

		Player humain = getHumanPlayer();
		if (humain != null && inHand[players.indexOf(humain)]) {
			List<Card> main = new ArrayList<>(humain.getHandCards());
			Collections.sort(main, new CardComparatorOnValue());
			view.showYourHand(main, getHandRank(humain).toString(), pot, humain.getBalance());
		}

		boolean[] aFolded = new boolean[n];
		for (int i = 0; i < n; i++) {
			aFolded[i] = !inHand[i];
		}
		tourDeMise(input, view, aFolded);
		showdown(aFolded, view);
	}

	/** Exécute une seule main (comportement historique, pour compatibilité). */
	public void runHand(HumanInputProvider input, GameView view) {
		view.showWelcome();
		input.waitForStart();
		boolean[] aFolded = new boolean[players.size()];
		runOneHandWithFolded(input, view, aFolded);
	}

	/** Une main sans filtre inHand (tous jouent). Utilisé par runHand. */
	private void runOneHandWithFolded(HumanInputProvider input, GameView view, boolean[] aFolded) {
		pot = 0;
		for (Player p : players) {
			p.setBalance(p.getBalance() - ANTE);
			pot += ANTE;
		}
		view.showAnte(ANTE, pot);
		Deck deck = new Deck(new ArrayList<>(colors));
		deck.shuffle(random);
		for (Player p : players) {
			deal5(deck, p);
		}
		Player humain = getHumanPlayer();
		if (humain != null) {
			List<Card> main = new ArrayList<>(humain.getHandCards());
			Collections.sort(main, new CardComparatorOnValue());
			view.showYourHand(main, getHandRank(humain).toString(), pot, humain.getBalance());
		}
		tourDeMise(input, view, aFolded);
		showdown(aFolded, view);
	}

	private Player getHumanPlayer() {
		for (Player p : players) {
			if (p.isHuman()) return p;
		}
		return null;
	}

	private static void deal5(Deck deck, Player p) {
		p.getHandCards().clear();
		for (int i = 0; i < 5; i++) {
			p.getHandCards().add(deck.draw());
		}
	}

	private void tourDeMise(HumanInputProvider input, GameView view, boolean[] aFolded) {
		int n = players.size();
		float[] miseActuelle = new float[n];
		float miseMax = 0;
		int dernierRelance = 0;
		int idx = 0;

		view.showBetRoundStart();

		while (true) {
			if (aFolded[idx]) {
				idx = (idx + 1) % n;
				continue;
			}
			float aSuivre = miseMax - miseActuelle[idx];
			Player p = players.get(idx);

			if (p.isHuman()) {
				if (aSuivre <= 0) {
					if (input.wantsToBet()) {
						int maxBet = (int) p.getBalance();
						int montant = input.getBetAmount(MISE_MIN, maxBet);
						montant = Math.max(MISE_MIN, Math.min(montant, maxBet));
						p.setBalance(p.getBalance() - montant);
						miseActuelle[idx] += montant;
						pot += montant;
						miseMax = miseActuelle[idx];
						dernierRelance = idx;
						view.showAction(p.getPseudo(), "mise " + montant + ".", pot);
					} else {
						view.showAction(p.getPseudo(), "check.", pot);
					}
				} else {
					boolean call = input.callOrFold(aSuivre);
					if (call) {
						float montant = Math.min(aSuivre, p.getBalance());
						p.setBalance(p.getBalance() - montant);
						miseActuelle[idx] += montant;
						pot += montant;
						view.showAction(p.getPseudo(), "suit (" + (int) montant + ").", pot);
					} else {
						aFolded[idx] = true;
						view.showAction(p.getPseudo(), "se couche.", pot);
					}
				}
			} else {
				RoundState state = new RoundState();
				state.pot = (int) pot;
				state.currentBet = (int) aSuivre;
				HandRank rank = getHandRank(p);
				Action action = botStrategy.decide(p, state, rank, random);

				switch (action.type()) {
					case FOLD -> {
						aFolded[idx] = true;
						view.showAction(p.getPseudo(), "se couche.", pot);
					}
					case CHECK -> view.showAction(p.getPseudo(), "check.", pot);
					case CALL -> {
						float montant = Math.min(aSuivre, p.getBalance());
						p.setBalance(p.getBalance() - montant);
						miseActuelle[idx] += montant;
						pot += montant;
						view.showAction(p.getPseudo(), "suit (" + (int) montant + ").", pot);
					}
					case BET -> {
						int mise = Math.min(action.amount(), (int) p.getBalance());
						p.setBalance(p.getBalance() - mise);
						miseActuelle[idx] += mise;
						pot += mise;
						miseMax = miseActuelle[idx];
						dernierRelance = idx;
						view.showAction(p.getPseudo(), "mise " + mise + ".", pot);
					}
					case RAISE -> {
						int total = (int) aSuivre + action.amount();
						int mise = Math.min(total, (int) p.getBalance());
						p.setBalance(p.getBalance() - mise);
						miseActuelle[idx] += mise;
						pot += mise;
						miseMax = miseActuelle[idx];
						dernierRelance = idx;
						view.showAction(p.getPseudo(), "relance " + mise + ".", pot);
					}
				}
			}

			int next = (idx + 1) % n;
			while (next != idx && aFolded[next]) next = (next + 1) % n;
			if (next == idx) break;
			idx = next;

			float m = miseMax;
			boolean tousEgalises = true;
			for (int i = 0; i < n; i++) {
				if (!aFolded[i] && miseActuelle[i] < m) { tousEgalises = false; break; }
			}
			if (tousEgalises && idx == dernierRelance) break;
		}
	}

	private void showdown(boolean[] aFolded, GameView view) {
		List<Player> encoreEnJeu = new ArrayList<>();
		for (int i = 0; i < players.size(); i++) {
			if (!aFolded[i]) encoreEnJeu.add(players.get(i));
		}
		if (encoreEnJeu.isEmpty()) return;
		if (encoreEnJeu.size() == 1) {
			Player gagnant = encoreEnJeu.get(0);
			gagnant.setBalance(gagnant.getBalance() + pot);
			view.showShowdown();
			view.showWinnerByFold(gagnant.getPseudo(), pot);
			return;
		}
		view.showShowdown();
		for (Player p : encoreEnJeu) {
			List<Card> main = new ArrayList<>(p.getHandCards());
			Collections.sort(main, new CardComparatorOnValue());
			view.showShowdownHand(p.getPseudo(), main, getHandRank(p).toString());
		}
		List<Player> gagnants = getGagnants(encoreEnJeu);
		for (Player g : gagnants) {
			g.setBalance(g.getBalance() + pot / gagnants.size());
		}
		if (gagnants.size() == 1) {
			view.showWinnerSingle(gagnants.get(0).getPseudo(), getHandRank(gagnants.get(0)).toString(), pot);
		} else {
			List<String> noms = new ArrayList<>();
			for (Player g : gagnants) noms.add(g.getPseudo());
			view.showWinnersTie(noms, pot / gagnants.size());
		}
	}

	private int comparerMains(Player p1, Player p2) {
		HandRank r1 = getHandRank(p1);
		HandRank r2 = getHandRank(p2);
		int cmp = Integer.compare(r1.getForce(), r2.getForce());
		if (cmp != 0) return cmp;
		List<Integer> v1 = getValeursTrieesDesc(p1);
		List<Integer> v2 = getValeursTrieesDesc(p2);
		for (int i = 0; i < v1.size(); i++) {
			int c = Integer.compare(v1.get(i), v2.get(i));
			if (c != 0) return c;
		}
		return 0;
	}

	private List<Integer> getValeursTrieesDesc(Player player) {
		List<Integer> valeurs = new ArrayList<>();
		for (Card c : player.getHandCards()) {
			valeurs.add(c.getValue());
		}
		valeurs.sort(Collections.reverseOrder());
		return valeurs;
	}

	private List<Player> getGagnants(List<Player> joueurs) {
		if (joueurs == null || joueurs.isEmpty()) return List.of();
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

	private HandRank getHandRank(Player player) {
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
		if (hasFourOfAKind) return HandRank.CARRE;
		if (nbPairs == 1 && hasThreeOfAKind) return HandRank.FULL;
		if (flush) return HandRank.FLUSH;
		if (straight) return HandRank.QUINTE;
		if (hasThreeOfAKind) return HandRank.BRELAN;
		if (nbPairs == 2) return HandRank.DEUX_PAIRES;
		if (nbPairs == 1) return HandRank.PAIRE;
		return HandRank.RIEN;
	}

	private static boolean isFlush(List<Card> handCards) {
		if (handCards.isEmpty()) return false;
		Color first = handCards.get(0).getColor();
		for (int i = 1; i < handCards.size(); i++) {
			if (!handCards.get(i).getColor().equals(first)) return false;
		}
		return true;
	}

	private static boolean isStraight(List<Card> handCards) {
		if (handCards.size() != 5) return false;
		List<Integer> values = new ArrayList<>();
		for (Card c : handCards) values.add(c.getValue());
		Collections.sort(values);
		boolean consecutive = true;
		for (int i = 0; i < 4; i++) {
			if (values.get(i + 1) - values.get(i) != 1) {
				consecutive = false;
				break;
			}
		}
		return consecutive || (values.get(0) == 2 && values.get(1) == 3 && values.get(2) == 4
				&& values.get(3) == 5 && values.get(4) == 14);
	}

	private static boolean isRoyalStraight(List<Card> handCards) {
		List<Integer> values = new ArrayList<>();
		for (Card c : handCards) values.add(c.getValue());
		Collections.sort(values);
		return values.get(0) == 10 && values.get(1) == 11 && values.get(2) == 12
				&& values.get(3) == 13 && values.get(4) == 14;
	}
}
