package com.jitterted.ebp.blackjack.adapter.in.web;

import com.jitterted.ebp.blackjack.domain.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BlackjackController {

    private final Game game;

    @Autowired
    public BlackjackController(Game game) {
        this.game = game;
    }

    @PostMapping("/start-game")
    public String startGame() {
        game.initialDeal();
        return redirectBasedOnCurrentStateOfGame();
    }

    @GetMapping("/game")
    public String gameView(Model model) {
        populateGameViewInto(model);
        return "blackjack";
    }

    @PostMapping("/hit")
    public String hitCommand() {
        game.playerHits();
        return redirectBasedOnCurrentStateOfGame();
    }

    @GetMapping("/done")
    public String doneView(Model model) {
        populateGameViewInto(model);
        model.addAttribute("outcome", game.determineOutcome().display());
        return "done";
    }

    @PostMapping("/stand")
    public String standCommand() {
        game.playerStands();
        game.dealerTurn();
        return redirectBasedOnCurrentStateOfGame();
    }

    // Mapping of GAME STATE -> ADAPTER web page
    private String redirectBasedOnCurrentStateOfGame() {
        if (game.isPlayerDone()) {
            return "redirect:/done";
        }
        return "redirect:/game";
    }

    private void populateGameViewInto(Model model) {
        model.addAttribute("gameView", GameView.of(game));
    }
}
