package org.quest;

import static org.quest.GameLogic.processAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class QuestController {
    private final GameState gameState;

    @Autowired // Inject GameState for session
    public QuestController(GameState gameState) {
        this.gameState = gameState;
    }

    @GetMapping("/state")
    public GameState getGameState() {
        return gameState;
    }

    @GetMapping("/quest-state")
    public QuestState getQuestState() {
        return gameState.questState;
    }

    @GetMapping("/trim-players")
    public List<Player> getTrimReqPlayers() {
        return Arrays.stream(gameState.players)
                .filter(player -> player.getDeck().size() > 12)
                .toList();
    }

    @GetMapping("/start")
    public Response startGame() {
        return processAction(gameState, ActionType.START_GAME);
    }

    @GetMapping("/draw")
    public Map<String, Object> drawQuestCard() {
        Map<String, Object> map = new HashMap<>();
        map.put("res", processAction(gameState, ActionType.DRAW_QUEST_CARD));
        map.put("card", gameState.currentCard);
        return map;
    }

    @GetMapping("/plague")
    public Response plague() {
        return processAction(gameState, ActionType.PLAGUE);
    }

    @GetMapping("/queens-favor")
    public Response queensFavor() {
        return processAction(gameState, ActionType.QUEENS_FAVOR);
    }

    @GetMapping("/prosperity")
    public Response prosperity() {
        return processAction(gameState, ActionType.PROSPERITY);
    }

    @PostMapping("/trim-card")
    public Response trimCard(@RequestParam int playerId, @RequestParam int cardIndex) {
        Card c = gameState.players[playerId-1].getCard(cardIndex);
        if (c == null) return Response.INVALID_INPUT;
        return processAction(gameState, ActionType.TRIM_CARD, playerId, c);
    }

    @GetMapping("/start-quest")
    public Response startQuest() {
        return processAction(gameState, ActionType.START_QUEST);
    }

    @PostMapping("/test/setup")
    public Response setupGameState(@RequestParam String test) {
        gameState.clear();
        boolean success = GameLogic.setupGameState(gameState, test);
        if (!success) return Response.INVALID_INPUT;
        return Response.SUCCESS;
    }

    @PostMapping("/sponsor")
    public Response sponsorQuest(@RequestParam int playerId) {
        return processAction(gameState, ActionType.SPONSOR_QUEST, playerId);
    }

    @PostMapping("/add-card-to-stage")
    public Response addCardToStage(@RequestParam int cardIndex) {
        Card c = gameState.questState.sponsor.getCard(cardIndex);
        if (c == null) return Response.INVALID_INPUT;
        return processAction(gameState, ActionType.ADD_CARD_TO_STAGE, c);
    }

    @GetMapping("/quit-stage-setup")
    public Response quitStageSetup() {
        return processAction(gameState, ActionType.QUIT_STAGE_SETUP);
    }

    @PostMapping("/participate-in-quest")
    public Response participateInQuest(@RequestParam int playerId, @RequestParam boolean participate) {
        return processAction(gameState, ActionType.PARTICIPATE_IN_QUEST, playerId, participate);
    }

    @PostMapping("/add-card-to-attack")
    public Response addCardToAttack(@RequestParam int playerId, @RequestParam int cardIndex) {
        Card c = gameState.players[playerId-1].getCard(cardIndex);
        if (c == null) return Response.INVALID_INPUT;
        return processAction(gameState, ActionType.ADD_CARD_TO_ATTACK, playerId, c);
    }

    @PostMapping("/quit-attack-setup")
    public Response quitAttackSetup(@RequestParam int playerId) {
        return processAction(gameState, ActionType.QUIT_ATTACK_SETUP, playerId);
    }

    @GetMapping("/resolve-attacks")
    public Response resolveAttacks() {
        return processAction(gameState, ActionType.RESOLVE_ATTACKS);
    }

    @GetMapping("/end-quest")
    public Response endQuest() {
        return processAction(gameState, ActionType.END_QUEST);
    }

    @GetMapping("/cleanup-quest")
    public Response cleanupQuest() {
        return processAction(gameState, ActionType.CLEANUP_QUEST);
    }

    @GetMapping("/next-turn")
    public Response nextTurn() {
        return processAction(gameState, ActionType.NEXT_TURN);
    }

    @GetMapping("/end-game")
    public Response endGame() {
        return processAction(gameState, ActionType.END_GAME);
    }
}
