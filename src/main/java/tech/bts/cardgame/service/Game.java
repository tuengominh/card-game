package tech.bts.cardgame.service;

import tech.bts.cardgame.exception.*;
import tech.bts.cardgame.model.Card;
import tech.bts.cardgame.model.Deck;
import tech.bts.cardgame.model.Hand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    public enum State {OPEN, PLAYING, FINISHED}

    private Deck deck;
    private State state;
    private Map<String, Hand> players;
    private Map<String, Card> pickedCardbyUserName;
    private Map<String, Integer> discardedCounterbyUserName;
    private Map<String, Integer> points;

    public final static int HAND_SIZE = 3;
    public final static int MAXIMUM_DISCARD = 2;
    public final static int MAXIMUM_PLAYER_NUM = 2;
    public final static int MINIMUM_DECK_SIZE = 10;

    public Game(Deck deck) {
        this.deck = deck;
        this.state = State.OPEN;
        this.players = new HashMap<>();
        this.pickedCardbyUserName = new HashMap<>();
        this.discardedCounterbyUserName = new HashMap<>();
        this.points = new HashMap<>();
    }

    public State getState() {
        return state;
    }

    public Map<String, Hand> getPlayers() {
        return players;
    }

    public List<String> getPlayerNames() {
        return new ArrayList<>(players.keySet());
    }

    public Hand getPlayerHand(String username) {
        return players.get(username);
    }

    public int getBattlePoint(String username) {
        return players.get(username).getPoint();
    }

    public int getTotalPoints(String username) {
        return points.get(username);
    }

    public int getDiscardedCounterbyUserName(String username) {
        return discardedCounterbyUserName.get(username);
    }

    public Map<String, Card> getPickedCardbyUserName(String username) {
        return pickedCardbyUserName;
    }

    public void join(String username) {

        if (!state.equals(State.OPEN)) {
            throw new JoiningNotAllowedException();
        }

        players.put(username, new Hand());
        discardedCounterbyUserName.put(username, 0);
        points.put(username, 0);

        if (players.size() == MAXIMUM_PLAYER_NUM) {
            this.state = State.PLAYING;
        }
    }

    public Card pickCard(String username) {

        if (!state.equals(State.PLAYING)) {
            throw new CannotPickCardsIfNotPlayingException();
        }

        if (!this.getPlayerNames().contains(username)) {
            throw new PlayerNotInTheGameException();
        }

        Hand hand = getPlayerHand(username);
        if(hand.handSize() >= HAND_SIZE) {
            throw new HandSizeLimitExceededException();
        }

        Card pickedCard = pickedCardbyUserName.get(username);
        if (pickedCard != null) {
            throw new CannotPick2CardsInARowException();
        }

        Card newPickedCard = deck.pickCard();

        pickedCardbyUserName.put(username, newPickedCard);

        return newPickedCard;
    }

    public void discard(String username) {

        Card pickedCard = pickedCardbyUserName.get(username);
        int discardCounter = getDiscardedCounterbyUserName(username);

        if (pickedCard != null) {

            if(discardCounter >= MAXIMUM_DISCARD) {
                throw new CannotDiscard3CardsException();
            } else {
                discardCounter++;
            }

        } else {
            throw new CannotDiscardWithoutPreviouslyPickingException();
        }

        pickedCardbyUserName.remove(username);
        discardedCounterbyUserName.put(username, discardCounter);
    }

    public void keep(String username) {
        Card pickedCard = pickedCardbyUserName.get(username);
        Hand hand = getPlayerHand(username);

        if (pickedCard != null) {

            if(hand.handSize() >= HAND_SIZE) {
                throw new HandSizeLimitExceededException();
            } else {
                Hand hand1 = hand.keep(pickedCard);
                players.put(username, hand1);
            }

        } else {
            throw new CannotKeepWithoutPreviouslyPickingException();
        }

        pickedCardbyUserName.remove(username);
    }

    public void fillHand(String username) {

        int discardCounter = getDiscardedCounterbyUserName(username);
        System.out.println(discardCounter);
        Hand hand = getPlayerHand(username);

        if(discardCounter < MAXIMUM_DISCARD) {
            throw new HaventDiscard2CardsException();

        } else {
            if(hand.handSize() >= HAND_SIZE) {
                throw new HandSizeLimitExceededException();

            } else {
                for(int i = 0; i < HAND_SIZE - hand.handSize(); i++) {
                    pickCard(username);
                    keep(username);
                    discardCounter ++;
                }
            }
        }
    }

    public void battle (String username1, String username2) {

        if(deck.deckSize() < MINIMUM_DECK_SIZE) {
            this.state = State.FINISHED;
        }

        Hand hand1 = getPlayerHand(username1);
        Hand hand2 = getPlayerHand(username2);
        int points1 = 0;
        int points2 = 0;

        if(hand1.handSize() < HAND_SIZE) {
            fillHand(username1);
        } else if (hand2.handSize() < HAND_SIZE) {
            fillHand(username2);
        }

        Card accumulateCard1 = hand1.calculate();
        Card accumulateCard2 = hand2.calculate();

        if (accumulateCard1.getMagicPoint() > accumulateCard2.getMagicPoint()) {
            points1++;
        } else if (accumulateCard1.getMagicPoint() < accumulateCard2.getMagicPoint()) {
            points2++;
        }

        if (accumulateCard1.getStrengthPoint() > accumulateCard2.getStrengthPoint()) {
            points1++;
        } else if (accumulateCard1.getStrengthPoint() < accumulateCard2.getStrengthPoint()) {
            points2++;
        }

        if (accumulateCard1.getIntelligencePoint() > accumulateCard2.getIntelligencePoint()) {
            points1++;
        } else if (accumulateCard1.getIntelligencePoint() < accumulateCard2.getIntelligencePoint()) {
            points2++;
        }

        int result = points1 - points2;

        if (result < 0) {
            hand2.setPoint(1);

        } else if (result > 0) {
            hand1.setPoint(1);
        }

        players.put(username1, hand1);
        points.put(username1,hand1.getPoint());
        players.put(username2, hand2);
        points.put(username2,hand2.getPoint());
    }

    public void nextBattle() {

        this.players.put(getPlayerNames().get(0), new Hand());
        this.players.put(getPlayerNames().get(1), new Hand());
        this.pickedCardbyUserName = new HashMap<>();
        this.discardedCounterbyUserName = new HashMap<>();

    }

}