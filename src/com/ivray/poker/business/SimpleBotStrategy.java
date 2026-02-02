package com.ivray.poker.business;

import java.util.Random;

/**
 * IA "débutant solide" : joue selon la force de la main, le coût pour suivre,
 * et une part d'aléatoire pour éviter d'être trop prévisible.
 */
public class SimpleBotStrategy implements BotStrategy {

	@Override
	public Action decide(Player bot, RoundState state, HandRank rank, Random rng) {
		int toCall = state.currentBet;
		int force = rank.getForce();
		boolean canCheck = (toCall == 0);
		double bluff = rng.nextDouble();

		// RIEN ou PAIRE
		if (force <= HandRank.PAIRE.getForce()) {
			if (canCheck) {
				if (bluff < 0.10) {
					return Action.bet(2);
				}
				return Action.check();
			}
			if (toCall <= 2) {
				return Action.call(toCall);
			}
			return Action.fold();
		}

		// DEUX_PAIRES ou BRELAN
		if (force <= HandRank.BRELAN.getForce()) {
			if (canCheck) {
				return Action.bet(4);
			}
			if (toCall <= 6) {
				return Action.call(toCall);
			}
			return Action.fold();
		}

		// FULL et +
		if (canCheck) {
			return Action.bet(8);
		}
		if (toCall <= 12) {
			return Action.raise(6);
		}
		return Action.call(toCall);
	}
}
