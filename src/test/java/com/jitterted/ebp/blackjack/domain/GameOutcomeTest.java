package com.jitterted.ebp.blackjack.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameOutcomeTest {

    @Test
    public void playerHitsAndGoesBustThenPlayerLoses() throws Exception {
        Game game = new Game(StubDeck.playerHitsAndGoesBustDeck());
        game.initialDeal();

        game.playerHits();

        assertThat(game.determineOutcome().display())
                .isEqualTo("You Busted, so you lose.  💸");
    }

    @Test
    public void playerDealtBetterHandThanDealerAndStandsThenPlayerBeatsDealer() throws Exception {
        Game game = new Game(StubDeck.playerStandsAndBeatsDealer());
        game.initialDeal();

        game.playerStands();
        game.dealerTurn();

        assertThat(game.determineOutcome().display())
                .isEqualTo("You beat the Dealer! 💵");
    }

}