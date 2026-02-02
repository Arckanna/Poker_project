package com.ivray.poker.business;

/**
 * État d'une manche (1 vs 1) : pot, mise à égaler, joueurs couchés.
 */
public class RoundState {
	public int pot = 0;
	/** Mise à égaler (montant à payer pour suivre). */
	public int currentBet = 0;
	public boolean humanFolded = false;
	public boolean aiFolded = false;
}
