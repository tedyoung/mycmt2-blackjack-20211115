package com.jitterted.ebp.blackjack.domain;

// An Entity (even though it doesn't have an ID)
public class Game {

    private final Deck deck;

    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();

    private boolean playerDone;

    public Game() {
        deck = new Deck();
    }

    public void initialDeal() {
        dealRoundOfCards();
        dealRoundOfCards();
    }

    private void dealRoundOfCards() {
        // why: players first because this is the rule
        playerHand.drawFrom(deck);
        dealerHand.drawFrom(deck);
    }

    public String determineOutcome() {
        if (playerHand.isBusted()) {
            return "You Busted, so you lose.  💸";
        } else if (dealerHand.isBusted()) {
            return "Dealer went BUST, Player wins! Yay for you!! 💵";
        } else if (playerHand.beats(dealerHand)) {
            return "You beat the Dealer! 💵";
        } else if (playerHand.pushes(dealerHand)) {
            return "Push: Nobody wins, we'll call it even.";
        } else {
            return "You lost to the Dealer. 💸";
        }
    }

    public void dealerTurn() {
        // Dealer makes its choice automatically based on a simple heuristic (<=16 must hit, =>17 must stand)
        if (!playerHand.isBusted()) {
            while (dealerHand.dealerMustDrawCard()) {
                dealerHand.drawFrom(deck);
            }
        }
    }

    // Options (must abide by "Query" rule: snapshot and immutable/unmodifiable)
    // 1. DTO - Data Transfer - implies belonging in an Adapter
    // 2. Clone Hand - clone() or just new Hand(hand.cards()) -
    //      * might end up with too many objects (will be garbage-collected quickly)
    //      * implies they have the "real deal", and can modify
    // 3. Interface - provides value(), cards()
    //      * HandView - is it still a "live" view?
    // 4. (DPO - Domain Presentation Object) --> Value Object
    //      * Only data, maybe some internal behavior relating to queries
    //      * Query-only and is a snapshot of underlying object
    // 5. Hand contains a "memento", which stores cards and has queries like isBusted
    public Hand playerHand() {
        return playerHand;
    }

    public Hand dealerHand() {
        return dealerHand;
    }

    public void playerHits() {
        playerHand.drawFrom(deck);
        playerDone = playerHand.isBusted();
    }

    public void playerStands() {
        playerDone = true;
    }

    public boolean isPlayerDone() {
        return playerDone;
    }

}
