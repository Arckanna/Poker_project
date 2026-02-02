package com.ivray.poker.util;

import com.ivray.poker.business.Card;
import java.util.List;

/**
 * Affichage console soigné : écran d'accueil, bordures, couleurs ANSI.
 * Pour de meilleurs résultats, utiliser un terminal supportant UTF-8 et ANSI (ex. Windows Terminal, PowerShell 7+).
 */
public final class GameDisplay {

	private static final boolean USE_COLOR = System.getenv("NO_COLOR") == null;

	// ANSI
	private static final String RESET = USE_COLOR ? "\033[0m" : "";
	private static final String BOLD = USE_COLOR ? "\033[1m" : "";
	private static final String DIM = USE_COLOR ? "\033[2m" : "";
	private static final String GOLD = USE_COLOR ? "\033[33m" : "";
	private static final String GREEN = USE_COLOR ? "\033[32m" : "";
	private static final String CYAN = USE_COLOR ? "\033[36m" : "";
	private static final String YELLOW = USE_COLOR ? "\033[33m" : "";

	// Symboles cartes (UTF-8)
	private static final String HEART = "\u2665";
	private static final String SPADE = "\u2660";
	private static final String DIAMOND = "\u2666";
	private static final String CLUB = "\u2663";

	private static final int BOX_WIDTH = 50;

	private GameDisplay() {}

	/** Affiche l'écran d'accueil du jeu. */
	public static void titleScreen() {
		blank(2);
		println(CYAN + BOLD + "  ╔══════════════════════════════════════════════╗");
		println(CYAN + BOLD + "  ║                                              ║");
		println(CYAN + BOLD + "  ║     " + GOLD + " ____  ___  _  _____ ____  ___  " + CYAN + "     ║");
		println(CYAN + BOLD + "  ║     " + GOLD + "|  _ \\| _ \\| |/ / _ \\|   \\| _ \\ " + CYAN + "    ║");
		println(CYAN + BOLD + "  ║     " + GOLD + "|  _ /|   /|   <|   /| |) |   / " + CYAN + "    ║");
		println(CYAN + BOLD + "  ║     " + GOLD + "|_|   |_|_\\|_|\\_\\_|_\\|___/|_|_\\ " + CYAN + "    ║");
		println(CYAN + BOLD + "  ║                                              ║");
		println(CYAN + BOLD + "  ║     " + DIM + "5 cartes  •  Vous vs Louise & Maman" + CYAN + "  ║");
		println(CYAN + BOLD + "  ║                                              ║");
		println(CYAN + BOLD + "  ╚══════════════════════════════════════════════╝" + RESET);
		blank(2);
		println(DIM + "  Appuyez sur Entrée pour commencer la main..." + RESET);
		blank(1);
	}

	/** Affiche une section avec bordure (ex. "ANTE", "VOTRE MAIN"). */
	public static void sectionTitle(String title) {
		int contentWidth = BOX_WIDTH - 4;
		int len = visibleLength(title);
		String pad = " ".repeat(Math.max(0, contentWidth - len - 2));
		String line = "═".repeat(contentWidth);
		println(CYAN + "  ╔" + line + "╗" + RESET);
		println(CYAN + "  ║ " + BOLD + title + pad + CYAN + " ║" + RESET);
		println(CYAN + "  ╚" + line + "╝" + RESET);
	}

	/** Affiche une ligne dans un style "info" (pot, stack). */
	public static void infoLine(String key, String value) {
		println("  " + DIM + key + RESET + " " + GOLD + value + RESET);
	}

	/** Affiche la section Ante. */
	public static void printAnte(int ante, float potTotal) {
		sectionTitle(" ANTE ");
		println("  Chaque joueur paie " + GOLD + ante + RESET + " jetons.");
		println("  Pot : " + GOLD + BOLD + potTotal + RESET + " jetons.");
		blank(1);
	}

	/** Formate une carte en court (ex. "A♠", "10♥"). */
	public static String formatCardShort(Card c) {
		String val = switch (c.getValue()) {
			case 14 -> "A";
			case 13 -> "K";
			case 12 -> "Q";
			case 11 -> "J";
			default -> String.valueOf(c.getValue());
		};
		String sym = switch (c.getColor().getName().toLowerCase()) {
			case "heart" -> HEART;
			case "spade" -> SPADE;
			case "diamond" -> DIAMOND;
			case "club" -> CLUB;
			default -> "";
		};
		return val + sym;
	}

	/** Affiche une main de cartes sous forme de cartes visuelles (lignes ASCII). */
	public static void printHandCards(List<Card> cards) {
		if (cards == null || cards.isEmpty()) return;
		int n = cards.size();
		String top = "  " + "┌─────┐".repeat(n);
		String mid = "  ";
		String bot = "  " + "└─────┘".repeat(n);
		for (Card c : cards) {
			String s = formatCardShort(c);
			mid += "│ " + String.format("%-3s", s) + " │";
		}
		println(CYAN + top + RESET);
		println(CYAN + mid + RESET);
		println(CYAN + bot + RESET);
	}

	/** Affiche la section "Votre main" avec combinaison, pot et stack. */
	public static void printYourHand(List<Card> hand, String combination, float pot, float stack) {
		sectionTitle(" VOTRE MAIN ");
		printHandCards(hand);
		println("  Combinaison : " + BOLD + GREEN + combination + RESET);
		println("  Pot : " + GOLD + (int) pot + RESET + "  │  Votre stack : " + GOLD + (int) stack + RESET);
		blank(1);
	}

	/** Affiche une action d'un joueur (mise, suit, check, se couche). */
	public static void printAction(String playerName, String actionText, float potAfter) {
		println("  " + CYAN + "• " + RESET + playerName + " " + actionText + "  " + DIM + "(Pot : " + (int) potAfter + ")" + RESET);
	}

	/** Affiche la section Tour de mise (titre). */
	public static void printBetRoundTitle() {
		sectionTitle(" TOUR DE MISE ");
	}

	/** Affiche la section Showdown. */
	public static void printShowdownTitle() {
		blank(1);
		sectionTitle(" SHOWDOWN ");
	}

	/** Affiche la main d'un joueur au showdown. */
	public static void printShowdownHand(String playerName, List<Card> hand, String combination) {
		println("  " + BOLD + playerName + RESET + "  →  " + hand + "  " + DIM + "(" + combination + ")" + RESET);
	}

	/** Affiche le titre "Gagnant(s)". */
	public static void printWinnersTitle() {
		sectionTitle(" GAGNANT(S) ");
	}

	/** Affiche le message de victoire (un gagnant). */
	public static void printWinnerSingle(String playerName, String combination, float potWon) {
		println("  " + GREEN + BOLD + "★ " + playerName + " remporte la main avec " + combination + "." + RESET);
		println("  Pot remporté : " + GOLD + (int) potWon + RESET + " jetons.");
	}

	/** Affiche le message de victoire (tous couchés). */
	public static void printWinnerByFold(String playerName, float potWon) {
		println("  " + GREEN + BOLD + "★ " + playerName + " remporte le pot (" + (int) potWon + ") : tout le monde s'est couché." + RESET);
	}

	/** Affiche un ex æquo. */
	public static void printWinnersTie(List<String> names, float potEach) {
		println("  " + YELLOW + "Égalité entre : " + String.join(", ", names) + RESET);
		println("  Pot partagé : " + GOLD + (int) potEach + RESET + " jetons chacun.");
	}

	/** Ligne vide. */
	public static void blank(int n) {
		for (int i = 0; i < n; i++) System.out.println();
	}

	private static void println(String s) {
		System.out.println(s);
	}

	private static int visibleLength(String s) {
		// Enlève les codes ANSI pour compter la longueur visible
		return s.replaceAll("\033\\[[0-9;]*m", "").length();
	}
}
