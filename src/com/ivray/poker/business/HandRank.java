package com.ivray.poker.business;

/**
 * Classement des mains au poker, du plus fort au plus faible.
 */
public enum HandRank {
	QUINTE_ROYALE("Quinte royale", 10),
	QUINTE_FLUSH("Quinte flush", 9),
	CARRE("Carré", 8),
	FULL("Full", 7),
	FLUSH("Flush", 6),
	QUINTE("Quinte", 5),
	BRELAN("Brelan", 4),
	DEUX_PAIRES("Deux paires", 3),
	PAIRE("Paire", 2),
	RIEN("Rien", 1);

	private final String libelle;
	private final int force;

	HandRank(String libelle, int force) {
		this.libelle = libelle;
		this.force = force;
	}

	public String getLibelle() {
		return libelle;
	}

	public int getForce() {
		return force;
	}

	@Override
	public String toString() {
		return libelle;
	}
}
