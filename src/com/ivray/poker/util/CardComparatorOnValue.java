package com.ivray.poker.util;

import java.util.Comparator;

import com.ivray.poker.business.Card;

public class CardComparatorOnValue implements Comparator<Card> {

    @Override
    public int compare(Card card1, Card card2) {
        return Integer.valueOf(card1.getValue()).compareTo(card2.getValue());
    }
}
