package com.ivray.poker.business;

/**
 * Action jouée au tour de mise (type + montant éventuel).
 */
public record Action(ActionType type, int amount) {

	public static Action fold() {
		return new Action(ActionType.FOLD, 0);
	}

	public static Action check() {
		return new Action(ActionType.CHECK, 0);
	}

	public static Action call(int amount) {
		return new Action(ActionType.CALL, amount);
	}

	public static Action bet(int amount) {
		return new Action(ActionType.BET, amount);
	}

	public static Action raise(int amount) {
		return new Action(ActionType.RAISE, amount);
	}
}
