package com.jitterted.ebp.blackjack;

public class ConsoleHand {

    // TRANSFORMER/TRANSLATOR: DOMAIN (Hand) -- first card --> CONSOLE ADAPTER (String)
    static String displayFirstCard(Hand hand) {
        return ConsoleCard.display(hand.faceUpCard());
    }

}
