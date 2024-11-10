Feature: Quest Game

  Scenario: A1_scenario
    Given a rigged game of Quest starts based on the jp_scenario from A1
    When P1 draws a 4 stage quest
    Then P2 sponsors the quest
    And P2 builds the 4 stages
      | stage | cards          |
      | 1     | [F5, H10]      |
      | 2     | [F15, S10]     |
      | 3     | [F15, D5, B15] |
      | 4     | [F40, B15]     |
    And Players '[P1, P3, P4]' participate in stage
      | player | discard |
      | P1     | [F5]    |
      | P3     | [F5]    |
      | P4     | [F5]    |
    And Players build their stage 1 attack
      | player | attack    |
      | P1     | [D5, S10] |
      | P3     | [S10, D5] |
      | P4     | [D5, H10] |
    And Players attack the stage 1
    And Players '[P1, P3, P4]' should pass stage 1
    And Players '[P1, P3, P4]' participate in stage
      | player | discard |
    And Players build their stage 2 attack
      | player | attack     |
      | P1     | [H10, S10] |
      | P3     | [B15, S10] |
      | P4     | [H10, B15] |
    And Players attack the stage 2
    And Players '[P1]' should 'earn' 0 shields
    And Player 'P1' should match cards '[F5, F10, F15, F15, F30, H10, B15, B15, L20]'
    And Players '[P3, P4]' should pass stage 2
    And Players '[P3, P4]' participate in stage
      | player | discard |
    And Players build their stage 3 attack
      | player | attack          |
      | P3     | [L20, H10, S10] |
      | P4     | [B15, S10, L20] |
    And Players attack the stage 3
    And Players '[P3, P4]' should pass stage 3
    And Players '[P3, P4]' participate in stage
      | player | discard |
    And Players build their stage 4 attack
      | player | attack              |
      | P3     | [B15, H10, L20]     |
      | P4     | [D5, S10, L20, E30] |
    And Players attack the stage 4
    And Players '[P3]' should 'earn' 0 shields
    And Player 'P3' should match cards '[F5, F5, F15, F30, S10]'
    And Players '[P4]' should 'earn' 4 shields
    And Player 'P4' should match cards '[F15, F15, F40, L20]'
    And P2's quest is cleaned up
      | player | discard  |
      | P2     | first[4] |
    And Player P2 should have 12 cards
    And P2 should discard and draw 13 cards

  Scenario: 2winner_game_2winner_quest
    Given a rigged 2winner game of Quest starts
    When P1 draws a 4 stage quest
    Then P1 sponsors the quest
    And P1 builds the 4 stages
      | stage | cards |
      | 1     | [F5]  |
      | 2     | [F10] |
      | 3     | [F15] |
      | 4     | [F20] |
    And Players '[P2, P3, P4]' participate in stage
      | player | discard  |
      | P2     | first[1] |
      | P3     | first[1] |
      | P4     | first[1] |
    And Players build their stage 1 attack
      | player | attack |
      | P2     | [H10]  |
      | P3     | []     |
      | P4     | [H10]  |
    And Players attack the stage 1
    And Players '[P2, P4]' should pass stage 1
    And Players '[P2, P4]' participate in stage
      | player | discard |
    And Players build their stage 2 attack
      | player | attack |
      | P2     | [B15]  |
      | P4     | [B15]  |
    And Players attack the stage 2
    And Players '[P2, P4]' should pass stage 2
    And Players '[P2, P4]' participate in stage
      | player | discard |
    And Players build their stage 3 attack
      | player | attack |
      | P2     | [B15]  |
      | P4     | [B15]  |
    And Players attack the stage 3
    And Players '[P2, P4]' should pass stage 3
    And Players '[P2, P4]' participate in stage
      | player | discard |
    And Players build their stage 4 attack
      | player | attack |
      | P2     | [L20]  |
      | P4     | [L20]  |
    And Players attack the stage 4
    And Players '[P2, P4]' should 'earn' 4 shields
    And P2 draws a 3 stage quest
    And P3 sponsors the quest
    And P3 builds the 3 stages
      | stage | cards |
      | 1     | [F5]  |
      | 2     | [F10] |
      | 3     | [F15] |
    And Players '[P2, P4]' participate in stage
      | player | discard  |
      | P2     | first[1] |
      | P4     | first[1] |
    And Players build their stage 1 attack
      | player | attack |
      | P2     | [S10]  |
      | P4     | [S10]  |
    And Players attack the stage 1
    And Players '[P2, P4]' should pass stage 1
    And Players '[P2, P4]' participate in stage
      | player | discard |
    And Players build their stage 2 attack
      | player | attack |
      | P2     | [S10]  |
      | P4     | [S10]  |
    And Players attack the stage 2
    And Players '[P2, P4]' should pass stage 2
    And Players '[P2, P4]' participate in stage
      | player | discard |
    And Players build their stage 3 attack
      | player | attack |
      | P2     | [L20]  |
      | P4     | [L20]  |
    And Players attack the stage 3
    And Players '[P2, P4]' should 'earn' 3 shields

  Scenario: 1winner_game_with_events
    Given a rigged 1winner game of Quest starts
    When P1 hosts a 4 stage quest
    Then Players '[P2, P3, P4]' should 'earn' 4 shields
    And P2 draws a 'Plague' event card
      | player | discard  |
    And Players '[P2]' should 'lose' 2 shields
    And P3 draws a 'Prosperity' event card
      | player | discard  |
      | P4     | first[1] |
      | P1     | [D5, D5] |
    And the number of cards in '[P1, P2, P3, P4]' hand increases by 2
    And P4 draws a 'Queen\'s favor' event card
      | player | discard  |
      | P1     | first[2] |
    And the number of cards in 'P4' hand increases by 2
    And P1 hosts a 3 stage quest
    And Players '[P2, P3]' should 'earn' 3 shields
    And Players '[P3]' should be declared the winner