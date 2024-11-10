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