package com.ivray.poker.business;

import java.util.Random;

/**
 * Stratégie d'IA : décide d'une action selon la main, l'état de la manche et un peu de hasard.
 */
public interface BotStrategy {

	Action decide(Player bot, RoundState state, HandRank rank, Random rng);
}
