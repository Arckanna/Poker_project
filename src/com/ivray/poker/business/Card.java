package com.ivray.poker.business;

import java.util.Objects;

public class Card {
	private String name;
	private int value;
	private Color color;

	public Card(int value, Color color) {
		super();
		this.value = value;
		this.color = color;

		switch (value) {
			case 11:
				name = "Jack";
				break;
			case 12:
				name = "Queen";
				break;
			case 13:
				name = "King";
				break;
			case 14:
				name = "Ace";
				break;
			default:
				name = String.valueOf(value);
				break;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, name, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		return Objects.equals(color, other.color) && Objects.equals(name, other.name) && value == other.value;
	}

	@Override
	public String toString() {
		return name + " of " + color;
	}
}
