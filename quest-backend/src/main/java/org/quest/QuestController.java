package org.quest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class QuestController {
    private final GameLogic gameLogic;

    @Autowired // Inject GameLogic instance
    public QuestController(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @GetMapping("/state")
    public GameState getGameState() {
        return gameLogic.getGameState();
    }

    @GetMapping("/quest-state")
    public QuestState getQuestState() {
        return gameLogic.getGameState().questState;
    }

    @GetMapping("/trim-players")
    public List<Player> getTrimReqPlayers() {
        return Arrays.stream(gameLogic.getGameState().players)
                .filter(player -> player.getDeck().size() > 12)
                .toList();
    }

    @GetMapping("/start")
    public Response startGame() {
        return gameLogic.processAction(ActionType.START_GAME);
    }

    @GetMapping("/draw")
    public Map<String, Object> drawQuestCard() {
        Map<String, Object> map = new HashMap<>();
        map.put("res", gameLogic.processAction(ActionType.DRAW_QUEST_CARD));
        map.put("card", gameLogic.getGameState().currentCard);
        return map;
    }

    @GetMapping("/plague")
    public Response plague() {
        return gameLogic.processAction(ActionType.PLAGUE);
    }

    @GetMapping("/queens-favor")
    public Response queensFavor() {
        return gameLogic.processAction(ActionType.QUEENS_FAVOR);
    }

    @GetMapping("/prosperity")
    public Response prosperity() {
        return gameLogic.processAction(ActionType.PROSPERITY);
    }

    @PostMapping("/trim-card")
    public Response trimCard(@RequestParam int playerId, @RequestParam int cardIndex) {
        Card c = gameLogic.getGameState().players[playerId-1].getCard(cardIndex);
        if (c == null) return Response.INVALID_INPUT;
        return gameLogic.processAction(ActionType.TRIM_CARD, playerId, c);
    }

    @GetMapping("/start-quest")
    public Response startQuest() {
        return gameLogic.processAction(ActionType.START_QUEST);
    }

    @PostMapping("/test/setup")
    public Response setupGameState(@RequestParam String test) {
        return gameLogic.setupGameState(test);
    }

    @PostMapping("/sponsor")
    public Response sponsorQuest(@RequestParam int playerId) {
        return gameLogic.processAction(ActionType.SPONSOR_QUEST, playerId);
    }

    @PostMapping("/add-card-to-stage")
    public Response addCardToStage(@RequestParam int cardIndex) {
        Card c = gameLogic.getGameState().questState.sponsor.getCard(cardIndex);
        if (c == null) return Response.INVALID_INPUT;
        return gameLogic.processAction(ActionType.ADD_CARD_TO_STAGE, c);
    }

    @GetMapping("/quit-stage-setup")
    public Response quitStageSetup() {
        return gameLogic.processAction(ActionType.QUIT_STAGE_SETUP);
    }

    @PostMapping("/participate-in-quest")
    public Response participateInQuest(@RequestParam int playerId, @RequestParam boolean participate) {
        return gameLogic.processAction(ActionType.PARTICIPATE_IN_QUEST, playerId, participate);
    }

    @PostMapping("/add-card-to-attack")
    public Response addCardToAttack(@RequestParam int playerId, @RequestParam int cardIndex) {
        Card c = gameLogic.getGameState().players[playerId-1].getCard(cardIndex);
        if (c == null) return Response.INVALID_INPUT;
        return gameLogic.processAction(ActionType.ADD_CARD_TO_ATTACK, playerId, c);
    }

    @PostMapping("/quit-attack-setup")
    public Response quitAttackSetup(@RequestParam int playerId) {
        return gameLogic.processAction(ActionType.QUIT_ATTACK_SETUP, playerId);
    }

    @GetMapping("/resolve-attacks")
    public Response resolveAttacks() {
        return gameLogic.processAction(ActionType.RESOLVE_ATTACKS);
    }

    @GetMapping("/end-quest")
    public Response endQuest() {
        return gameLogic.processAction(ActionType.END_QUEST);
    }

    @GetMapping("/cleanup-quest")
    public Response cleanupQuest() {
        return gameLogic.processAction(ActionType.CLEANUP_QUEST);
    }

    @GetMapping("/next-turn")
    public Response nextTurn() {
        return gameLogic.processAction(ActionType.NEXT_TURN);
    }

    @GetMapping("/end-game")
    public Response endGame() {
        return gameLogic.processAction(ActionType.END_GAME);
    }
}
