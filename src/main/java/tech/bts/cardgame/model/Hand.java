package tech.bts.cardgame.model;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private List<Card> hand;
    private int point;

    public Hand() {

        this.hand = new ArrayList<>();
        this.point = 0;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> cards) {
        this.hand = cards;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point += point;
    }

    public String toString(){

        String result = "";

        result += "This hand includes " + this.hand.size() + " cards: ";

        for (int i = 0; i < hand.size(); i++) {
            result += "\n" + hand.get(i).toString();
            result += " ";
        }

        return result;
    }

    public Hand keep (Card card){
        hand.add(card);
        return this;
    }

    public Card calculate() {

        int magic = 0;
        int strength = 0;
        int intelligence = 0;

        for (Card card : hand) {
            magic += card.getMagicPoint();
            strength += card.getStrengthPoint();
            intelligence += card.getIntelligencePoint();
        }

        return new Card(magic, strength, intelligence);
    }

    public int handSize() {
        return this.hand.size();
    }

}
