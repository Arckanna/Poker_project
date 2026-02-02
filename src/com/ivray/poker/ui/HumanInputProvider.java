package com.ivray.poker.ui;

/**
 * Fournit les choix du joueur humain (console ou GUI).
 */
public interface HumanInputProvider {

	/** Appelé après showWelcome() : attend que l'utilisateur démarre (Entrée en console, clic en GUI). */
	void waitForStart();

	/** Retourne true pour miser, false pour check (quand personne n'a encore misé). */
	boolean wantsToBet();

	/** Montant à miser (min et max inclus). Appelé après wantsToBet() == true. */
	int getBetAmount(int min, int max);

	/** Retourne true pour suivre, false pour se coucher (quand une mise est à suivre). */
	boolean callOrFold(float toCall);
}
