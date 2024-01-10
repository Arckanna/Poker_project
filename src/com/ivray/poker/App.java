package com.ivray.poker;

import com.ivray.poker.business.Color;
import com.ivray.poker.business.Player;

public class App {

	public static void main(String[] args) {
		Color color1 = new Color("heart");
		System.out.println(color1);
		
		Player player1 = new Player("Capucine");
		System.out.println(player1);

	}

}
