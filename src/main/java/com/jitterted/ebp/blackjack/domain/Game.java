package com.jitterted.ebp.blackjack.domain;

import com.jitterted.ebp.blackjack.domain.port.GameMonitor;

// An Entity (even though it doesn't have an ID)
public class Game {

    private final Deck deck;
    private final GameMonitor gameMonitor;

    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();

    private boolean playerDone;

    public Game(Deck deck) {
        this(deck, game -> {
        });
    }

    public Game(Deck deck, GameMonitor gameMonitor) {
        this.deck = deck;
        this.gameMonitor = gameMonitor;
    }

    public void initialDeal() {
        dealRoundOfCards();
        dealRoundOfCards();
        if (playerHand.hasBlackjack()) {
            playerDone = true;
            gameMonitor.roundCompleted(this);
        }
    }

    private void dealRoundOfCards() {
        // why: players first because this is the rule
        playerHand.drawFrom(deck);
        dealerHand.drawFrom(deck);
    }

    public GameOutcome determineOutcome() {
        // should we allow this method to be called if the game isn't done?
        // no, we really shouldn't - the answer wouldn't make sense
        if (playerHand.isBusted()) {
            return GameOutcome.PLAYER_BUSTED;
        } else if (dealerHand.isBusted()) {
            return GameOutcome.DEALER_BUSTED;
        } else if (playerHand.hasBlackjack()) {
            return GameOutcome.PLAYER_WINS_BLACKJACK;
        } else if (playerHand.beats(dealerHand)) {
            return GameOutcome.PLAYER_BEATS_DEALER;
        } else if (playerHand.pushes(dealerHand)) {
            return GameOutcome.PLAYER_PUSHES;
        } else {
            return GameOutcome.PLAYER_LOSES;
        }
    }

    private void dealerTurn() {
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
        // "require": can't already be done
        playerHand.drawFrom(deck);
        playerDone = playerHand.isBusted();
        if (playerDone) {
            gameMonitor.roundCompleted(this);
        }
    }

    public void playerStands() {
        // "require": can't already be done
        playerDone = true; // idempotent
        dealerTurn();
        gameMonitor.roundCompleted(this);
    }

    public boolean isPlayerDone() {
        return playerDone;
    }

}
