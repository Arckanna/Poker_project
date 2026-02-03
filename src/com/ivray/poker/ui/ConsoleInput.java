package com.ivray.poker.ui;

import java.util.Scanner;

/** Saisie du joueur humain via la console. */
public class ConsoleInput implements HumanInputProvider {

	private final Scanner scanner = new Scanner(System.in);

	@Override
	public void waitForStart() {
		scanner.nextLine();
	}

	@Override
	public boolean wantsToBet() {
		System.out.print("Vous : Check (c) ou Miser (m) ? ");
		String line = scanner.nextLine().trim();
		return "m".equalsIgnoreCase(line) || "mise".equalsIgnoreCase(line);
	}

	@Override
	public int getBetAmount(int min, int max) {
		System.out.print("Montant à miser (min " + min + ", max " + max + ") ? ");
		String line = scanner.nextLine().trim();
		int montant = min;
		try {
			montant = Integer.parseInt(line);
		} catch (NumberFormatException ignored) {
		}
		return Math.max(min, Math.min(montant, max));
	}

	@Override
	public boolean callOrFold(float toCall) {
		System.out.print("Vous : Suivre " + (int) toCall + " (s) ou Se coucher (f) ? ");
		String line = scanner.nextLine().trim();
		return "s".equalsIgnoreCase(line) || "suivre".equalsIgnoreCase(line)
				|| "c".equalsIgnoreCase(line) || "call".equalsIgnoreCase(line);
	}

	@Override
	public boolean continuePlaying() {
		System.out.print("Continuer (o) ou Abandonner (a) ? ");
		String line = scanner.nextLine().trim();
		return "o".equalsIgnoreCase(line) || "oui".equalsIgnoreCase(line)
				|| "c".equalsIgnoreCase(line) || "continuer".equalsIgnoreCase(line);
	}
}
