const {Builder, By, until} = require('selenium-webdriver');
const assert = require('assert');
const URL_ADDRESS = 'http://127.0.0.1:8081/'

async function winner2_game_2winner_quest() {
    console.log("Running winner2_game_2winner_quest test");
    const driver  = await new Builder().forBrowser('chrome').build();

    try {
        await driver.get(URL_ADDRESS);

        // start game
        await driver.findElement(By.xpath("//button[contains(text(), 'Start Game')]")).click();
        await rigGameState(driver, "winner2_game_2winner_quest");

        // Draw Card
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        // Sponsor Quest
        await driver.findElement(By.xpath("//button[contains(text(), 'Sponsor Quest')]")).click();
        await driver.sleep(750);

        // set stages of quest
        await setStages(driver, 1, {
            1: ["F5"],
            2: ["F5", "D5"],
            3: ["F10", "H10"],
            4: ["F10", "B15"]
        });

        // Participate
        await playerParticipateAndTrim(driver, 2, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4, "F10");
        await driver.sleep(750);

        // Build attack for stage 1
        await setAttacks(driver, {
            2: ["H10"],
            3: [],
            4: ["H10"]
        })

        // assert that only P2 and P4 remain as participants
        let participants = await driver.findElement(By.id('participants')).getText();
        assert.strictEqual(participants, "Quest Participants: P2, P4");

        // Stage 2
        await playerParticipateAndTrim(driver, 2); // P2 participates in stage 2
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4); // P4 participates in stage 2
        await driver.sleep(750);
        await setAttacks(driver, {
            2: ["S10"],  // P2 attacks with sword
            4: ["S10"]   // P4 attacks with sword
        });
        await driver.sleep(750);

        // Stage 3
        await playerParticipateAndTrim(driver, 2); // P2 participates in stage 3
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4); // P4 participates in stage 3
        await driver.sleep(750);
        await setAttacks(driver, {
            2: ["H10", "S10"],  // P2 attacks with horse + sword
            4: ["H10", "S10"]   // P4 attacks with horse + sword
        });
        await driver.sleep(750);

        // Stage 4
        await playerParticipateAndTrim(driver, 2); // P2 participates in stage 3
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4); // P4 participates in stage 3
        await driver.sleep(750);
        await setAttacks(driver, {
            2: ["S10", "B15"],  // P2 attacks with sword + axe
            4: ["S10", "B15"]   // P4 attacks with sword + axe
        });

        // assert that only P2 and P4 have 4 shields
        await driver.sleep(750);
        assert.strictEqual(await getShields(driver, 1), 0);
        assert.strictEqual(await getShields(driver, 3), 0);
        assert.strictEqual(await getShields(driver, 2), 4);
        assert.strictEqual(await getShields(driver, 4), 4);

        // assert that P2 and P4 are the winners of the quest
        let winners = await driver.findElement(By.id('participants')).getText();
        assert.strictEqual(winners, "Quest Winners: P2, P4");
        assert.strictEqual(await getHandSize(driver, 1), 16);

        // Trim P1 Hand
        await trimHand(driver, 1, ["F5", "F10", "F15", "F15"]);

        // Next Turn - Quest 3
        await driver.sleep(750);
        await driver.findElement(By.xpath("//button[contains(text(), 'Next Turn')]")).click();
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        await driver.findElement(By.xpath("//button[contains(text(), \"Don't Sponsor Quest\")]")).click(); // P2 Decline to Sponsor Quest
        await driver.sleep(750);
        await driver.findElement(By.xpath("//button[contains(text(), 'Sponsor Quest')]")).click(); // P3 Sponsor Quest
        await driver.sleep(750);

        // set stages of quest
        await setStages(driver, 3, {
            1: ["F5"],
            2: ["F5", "D5"],
            3: ["F5", "H10"]
        });
        await driver.sleep(750);

        // Participate
        await playerParticipateAndTrim(driver, 1, "", false);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 2);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        // Build attack for stage 1
        await setAttacks(driver, {
            2: ["D5"],
            4: ["D5"]
        })
        await driver.sleep(750);

        // Stage 2
        await playerParticipateAndTrim(driver, 2); // P2 participates in stage 2
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4); // P4 participates in stage 2
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["B15"],  // P2 attacks with axe
            4: ["B15"]   // P4 attacks with axe
        });
        await driver.sleep(750);

        // Stage 3
        await playerParticipateAndTrim(driver, 2); // P2 participates in stage 3
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4); // P4 participates in stage 3
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["E30"],  // P2 attacks with horse
            4: ["E30"]   // P4 attacks with horse
        });
        await driver.sleep(750);

        // P3 Trims
        await trimHand(driver, 3, ["F20", "F25", "F30"]);
        await driver.sleep(750);

        // assert that only P2 and P4 have 7 shields
        assert.strictEqual(await getShields(driver, 2), 7);
        assert.strictEqual(await getShields(driver, 4), 7);

        // assert that output text declares P2 and P4 as winners
        const outputText = await driver.findElement(By.xpath("//div[@id='output-text']/p")).getText();
        assert.strictEqual(outputText, "Game Over - The winners are: P2, P4");

        // assert P1 hand
        assert.deepEqual(await getHand(driver, 1),
            ["F15", "F15", "F20", "F20", "F20", "F20", "F25", "F25", "F30", "H10", "B15", "L20"]);
        assert.strictEqual(await getHandSize(driver, 1), 12);

        // assert P2 hand
        assert.deepEqual(await getHand(driver, 2),
            ["F10", "F15", "F15", "F25", "F30", "F40", "F50", "L20", "L20"]);
        assert.strictEqual(await getHandSize(driver, 2), 9);

        // assert P3 hand
        assert.deepEqual(await getHand(driver, 3),
            ["F20", "F40", "D5", "D5", "S10", "H10", "H10", "H10", "H10", "B15", "B15", "L20"]);
        assert.strictEqual(await getHandSize(driver, 3), 12);

        // assert P4 hand
        assert.deepEqual(await getHand(driver, 4),
            ["F15", "F15", "F20", "F25", "F30", "F50", "F70", "L20", "L20"]);
        assert.strictEqual(await getHandSize(driver, 4), 9);
    } catch (err) {
        console.error(err);
    } finally {
        await driver.quit();
    }
}

async function winner1_game_with_events() {
    console.log("Running winner1_game_with_events test");
    const driver  = await new Builder().forBrowser('chrome').build();

    try {
        await driver.get(URL_ADDRESS);

        // start game
        await driver.findElement(By.xpath("//button[contains(text(), 'Start Game')]")).click();
        await rigGameState(driver, "winner1_game_with_events");

        // Draw Card
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        // Sponsor Quest
        await driver.findElement(By.xpath("//button[contains(text(), 'Sponsor Quest')]")).click();
        await driver.sleep(750);

        // set stages of quest
        await setStages(driver, 1, {
            1: ["F5"],
            2: ["F10"],
            3: ["F15"],
            4: ["F20"]
        });

        // Participate
        await playerParticipateAndTrim(driver, 2, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3, "F10");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4, "F20");
        await driver.sleep(750);

        // Build attack for stage 1
        await setAttacks(driver, {
            2: ["S10"],
            3: ["S10"],
            4: ["S10"]
        })

        // assert that all participants have 11 cards
        assert.strictEqual(await getHandSize(driver, 2), 11);
        assert.strictEqual(await getHandSize(driver, 3), 11);
        assert.strictEqual(await getHandSize(driver, 4), 11);

        // Stage 2
        await playerParticipateAndTrim(driver, 2);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["H10"],
            3: ["H10"],
            4: ["H10"]
        });

        // Stage 3
        await playerParticipateAndTrim(driver, 2);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["B15"],
            3: ["B15"],
            4: ["B15"]
        });

        // Stage 4
        await playerParticipateAndTrim(driver, 2);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["L20"],
            3: ["L20"],
            4: ["L20"]
        });

        // assert that P2, P3, P4 have 4 shields
        assert.strictEqual(await getShields(driver, 2), 4);
        assert.strictEqual(await getShields(driver, 3), 4);
        assert.strictEqual(await getShields(driver, 4), 4);
        assert.strictEqual(await getShields(driver, 1), 0);

        // Trim P1 Hand
        await trimHand(driver, 1, ["F5", "F5", "F10", "F10"]);
        await driver.sleep(750);

        // Next Turn - Plague
        await driver.findElement(By.xpath("//button[contains(text(), 'Next Turn')]")).click();
        await driver.sleep(750);
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        // assert that P2 losses 2 shields and now has 2 shields
        assert.strictEqual(await getShields(driver, 2), 2);

        // Next Turn - Prosperity
        await driver.findElement(By.xpath("//button[contains(text(), 'Next Turn')]")).click();
        await driver.sleep(750);
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        // Trim hands
        await trimHand(driver, 3, ["F5"]);
        await clearHotseat(driver);
        await driver.sleep(750);
        await trimHand(driver, 4, ["F20"]);
        await clearHotseat(driver);
        await driver.sleep(750);
        await trimHand(driver, 1, ["F5", "F10"]);
        await clearHotseat(driver);
        await driver.sleep(750);
        await trimHand(driver, 2, ["F5"]);
        await clearHotseat(driver);
        await driver.sleep(750);

        // assert hands are all size 12
        for (let i = 1; i <= 4; i++) {
            assert.strictEqual(await getHandSize(driver, i), 12);
        }

        // Next Turn - Queen's Favor
        await driver.findElement(By.xpath("//button[contains(text(), 'Next Turn')]")).click();
        await driver.sleep(750);
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);
        await trimHand(driver, 4, ["F25", "F30"]);

        // Next Turn - 3 Stage Quest
        await driver.findElement(By.xpath("//button[contains(text(), 'Next Turn')]")).click();
        await driver.sleep(750);
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        await driver.findElement(By.xpath("//button[contains(text(), 'Sponsor Quest')]")).click();
        await driver.sleep(750);

        await setStages(driver, 1, {
            1: ["F15"],
            2: ["F15", "D5"],
            3: ["F20", "D5"]
        });

        // Participate
        await playerParticipateAndTrim(driver, 2, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3, "F10");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4, "F20");

        // Build attack for stage 1
        await setAttacks(driver, {
            2: ["B15"],
            3: ["B15"],
            4: ["H10"]
        });

        assert.strictEqual(await getHandSize(driver, 4), 11); // assert that P4 has 11 cards

        // Stage 2
        await playerParticipateAndTrim(driver, 2);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["B15", "H10"],
            3: ["B15", "S10"]
        });

        // Stage 3
        await playerParticipateAndTrim(driver, 2);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);

        await setAttacks(driver, {
            2: ["L20", "S10"],
            3: ["E30"]
        });

        // Sponsor trim
        await trimHand(driver, 1, ["F15", "F15", "F15"]);

        // assert final shields amounts
        assert.strictEqual(await getShields(driver, 1), 0);
        assert.strictEqual(await getShields(driver, 2), 5);
        assert.strictEqual(await getShields(driver, 3), 7);
        assert.strictEqual(await getShields(driver, 4), 4);

        // assert that output text declares P3 as the winner
        const outputText = await driver.findElement(By.xpath("//div[@id='output-text']/p")).getText();
        assert.strictEqual(outputText, "Game Over - The winners are: P3");

        // assert P1 hand
        assert.deepEqual(await getHand(driver, 1),
            ["F25", "F25", "F35", "D5", "D5", "S10", "S10", "S10", "S10", "H10", "H10", "H10"]);
        assert.strictEqual(await getHandSize(driver, 1), 12);
        assert.deepEqual(await getHand(driver, 2),
            ["F15", "F25", "F30", "F40", "S10", "S10", "S10", "H10", "E30"]);
        assert.strictEqual(await getHandSize(driver, 2), 9);
        assert.deepEqual(await getHand(driver, 3),
            ["F10", "F25", "F30", "F40", "F50", "S10", "S10", "H10", "H10", "L20"]);
        assert.strictEqual(await getHandSize(driver, 3), 10);
        assert.deepEqual(await getHand(driver, 4),
            ["F25", "F25", "F30", "F50", "F70", "D5", "D5", "S10", "S10", "B15", "L20"]);
        assert.strictEqual(await getHandSize(driver, 4), 11);

    } catch (err) {
        console.error(err);
    } finally {
        await driver.quit();
    }
}

async function winner0_quest() {
    console.log("Running 0_winner_quest test");
    const driver  = await new Builder().forBrowser('chrome').build();

    try {
        await driver.get(URL_ADDRESS);

        // start game
        await driver.findElement(By.xpath("//button[contains(text(), 'Start Game')]")).click();
        await rigGameState(driver, "winner0_quest");

        const player3Hand = await getHand(driver, 3);
        const player4Hand = await getHand(driver, 4);

        // Draw Card
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        // Sponsor Quest
        await driver.findElement(By.xpath("//button[contains(text(), 'Sponsor Quest')]")).click();
        await driver.sleep(750);

        // set stages of quest
        await setStages(driver, 1, {
            1: ["F50", "D5", "S10", "H10", "B15", "L20"],
            2: ["F70", "D5", "S10", "H10", "B15", "L20"]
        });

        // Participate
        await playerParticipateAndTrim(driver, 2, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3, "F15");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4, "F10");
        await driver.sleep(750);

        // Build attack for stage 1
        await setAttacks(driver, {
            2: ["E30"],
            3: [],
            4: []
        })

        await driver.sleep(750);

        // The quest ends with no winner but P1 discards 12 quests cards
        // • P1 draws 14 cards: 1xF5, 1xF10, 1xF15, 4 daggers, 4 horses, 3 swords
        assert.deepEqual(await getHand(driver, 1), ["F5", "F10", "F15", "D5", "D5", "D5", "D5", "S10", "S10", "S10", "H10", "H10", "H10", "H10"]);

        // • P1 discards 1xF5, 1x10
        await trimHand(driver, 1, ["F5", "F10"]);
        await driver.sleep(750);

        let winners = await driver.findElement(By.id('participants')).getText();
        assert.strictEqual(winners, "Quest Winners: No winners for this quest");

        // • P1’s hand: 1xF15, 4 daggers, 4 horses, 3 swords
        assert.deepEqual(await getHand(driver, 1), ["F15", "D5", "D5", "D5", "D5", "S10", "S10", "S10", "H10", "H10", "H10", "H10"]);
        assert.strictEqual(await getHandSize(driver, 1), 12);
        assert.strictEqual(await getShields(driver, 1), 0);

        // • P2 has 2xF5, 1xF10, 2xF15, 2xF20, 1xF25, 2xF30, 1xF40
        assert.deepEqual(await getHand(driver, 2), ["F5", "F5", "F10", "F15", "F15", "F20", "F20", "F25", "F30", "F30", "F40"]);
        assert.strictEqual(await getHandSize(driver, 2), 11);
        assert.strictEqual(await getShields(driver, 2), 0);

        // • P3 and P4 have their initial hands
        assert.deepEqual(await getHand(driver, 3), player3Hand);
        assert.strictEqual(await getShields(driver, 3), 0);
        assert.deepEqual(await getHand(driver, 4), player4Hand);
        assert.strictEqual(await getShields(driver, 4), 0);

    } catch (err) {
        console.error(err);
    } finally {
        await driver.quit();
    }
}

async function a1_scenario() {
    console.log("Running a1_scenario test");
    const driver  = await new Builder().forBrowser('chrome').build();

    try {
        await driver.get(URL_ADDRESS);

        // start game
        await driver.findElement(By.xpath("//button[contains(text(), 'Start Game')]")).click();
        await rigGameState(driver, "a1_scenario");

        // Draw Card
        await driver.findElement(By.xpath("//button[contains(text(), 'Draw Card')]")).click();
        await driver.sleep(750);

        // P1 Decline to Sponsor
        await driver.findElement(By.xpath("//button[contains(text(), \"Don't Sponsor Quest\")]")).click();
        await driver.sleep(750);

        // P2 Sponsor Quest
        await driver.findElement(By.xpath("//button[contains(text(), 'Sponsor Quest')]")).click();
        await driver.sleep(750);

        // set stages of quest
        await setStages(driver, 2, {
            1: ["F5", "H10"],
            2: ["F15", "S10"],
            3: ["F15", "D5", "B15"],
            4: ["F40", "B15"]
        });

        // Stage 1
        // Participate
        await playerParticipateAndTrim(driver, 1, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3, "F5");
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4, "F5");
        await driver.sleep(750);

        // Build attack for stage 1
        await setAttacks(driver, {
            1: ["D5", "S10"],
            3: ["S10", "D5"],
            4: ["D5", "H10"]
        });

        // Assert that P1, P3, P4 are participants
        let participants = await driver.findElement(By.id('participants')).getText();
        assert.strictEqual(participants, "Quest Participants: P1, P3, P4");

        // Stage 2
        await playerParticipateAndTrim(driver, 1);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        await setAttacks(driver, {
            1: ["H10", "S10"],
            3: ["B15", "S10"],
            4: ["H10", "B15"]
        });

        // assert P1 has no shields and their hand is F5 F10 F15 F15 F30 Horse Axe Axe Lance (displayed in this order)
        assert.strictEqual(await getShields(driver, 1), 0);
        assert.deepEqual(await getHand(driver, 1), ["F5", "F10", "F15", "F15", "F30", "H10", "B15", "B15", "L20"]);

        // Stage 3
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        await setAttacks(driver, {
            3: ["L20", "H10", "S10"],
            4: ["B15", "S10", "L20"]
        });

        // Stage 4
        await playerParticipateAndTrim(driver, 3);
        await driver.sleep(750);
        await playerParticipateAndTrim(driver, 4);
        await driver.sleep(750);

        await setAttacks(driver, {
            3: ["B15", "H10", "L20"],
            4: ["D5", "S10", "L20", "E30"]
        });

        // assert P3 has no shields and has the 5 cards: F5 F5 F15 F30 Sword
        assert.strictEqual(await getShields(driver, 3), 0);
        assert.deepEqual(await getHand(driver, 3), ["F5", "F5", "F15", "F30", "S10"]);

        // assert P4 has 4 shields and has the cards: F15 F15 F40 Lance
        assert.strictEqual(await getShields(driver, 4), 4);
        assert.deepEqual(await getHand(driver, 4), ["F15", "F15", "F40", "L20"]);

        // assert that P2 had 16 cards in hand (3 + 13 drawn)
        assert.strictEqual(await getHandSize(driver, 2), 16);

        await driver.sleep(1000);
        await trimHand(driver, 2, ["F10", "F30", "F20", "F20"]);

        // assert P2 has 12 cards in hand after trim
        assert.strictEqual(await getHandSize(driver, 2), 12);
    } catch (err) {
        console.error(err);
    } finally {
        await driver.quit();
    }
}

async function getHand(driver, playerId) {
    try {
        // Wait for the element to be located and visible
        await driver.sleep(500);
        await driver.wait(
            until.elementLocated(By.id(`player-${playerId}-hand`)),
            5000, // Adjust timeout as needed
            "Element did not appear after 5 seconds."
        );

        // Now you can safely get the element and its text
        const handElement = await driver.findElement(By.id(`player-${playerId}-hand`));
        let deckString = await handElement.getText();
        deckString = deckString.substring("Hand: ".length);
        return deckString.split(", ");

    } catch (error) {
        console.error(`Error getting player ${playerId} hand:`, error);
        throw error; // Re-throw the error to be handled elsewhere
    }
}

async function cardInput(driver, input) {
    await driver.findElement(By.id('card-index')).clear();
    await driver.findElement(By.id('card-index')).sendKeys(input);
    await driver.findElement(By.xpath("//button[contains(text(), 'Select Card')]")).click();
}

async function getCardIndex(driver, playerId, card) {
    const hand = await getHand(driver, playerId);
    return hand.indexOf(card);
}

async function setStages(driver, playerId, stages) {
    await driver.sleep(1000);
    for (let i = 1; i<=Object.keys(stages).length; i++) {
        let stage = stages[i];
        for (let card of stage) {
            await cardInput(driver, getCardIndex(driver, playerId, card));
            await driver.sleep(750);
        }
        await cardInput(driver, -1); // Finish stage
        await driver.sleep(750);
    }
}

async function clearHotseat(driver) {
    await driver.findElement(By.xpath(`//button[contains(text(), 'Clear Hotseat')]`)).click();
}

async function setAttacks(driver, attacks) {
    for (let playerId in attacks) {
        let attack = attacks[playerId];
        for (let card of attack) {
            await cardInput(driver, getCardIndex(driver, playerId, card));
            await driver.sleep(750);
        }
        await cardInput(driver, -1); // Finish attack
        await driver.sleep(750);
        await clearHotseat(driver);
    }
}

async function playerParticipateAndTrim(driver, playerId, card="", participate=true) {
    if (!participate) {
        await driver.findElement(By.xpath(`//button[contains(text(), \"Don't Participate in Quest\")]`)).click();
    } else {
        await driver.findElement(By.xpath(`//button[contains(text(), 'Participate')]`)).click();
        await driver.sleep(750);
        if (card !== "")
            await trimHand(driver, playerId, [card]);
    }
    await driver.sleep(750);
    await clearHotseat(driver);
}

async function trimHand(driver, playerId, cards) {
    for (let card of cards) {
        await cardInput(driver, getCardIndex(driver, playerId, card));
        await driver.sleep(750);
    }
}

async function getShields(driver, playerId) {
    await driver.sleep(750);
    let shields = await driver.findElement(By.id(`player-${playerId}-shields`)).getText();
    shields = shields.substring("Shields: ".length);
    return parseInt(shields);
}

async function getHandSize(driver, playerId) {
    let handSize = await driver.findElement(By.id(`player-${playerId}-hand-size`)).getText();
    handSize = handSize.substring("Hand Size: ".length);
    return parseInt(handSize);
}

async function rigGameState(driver, testVersion) {
    await fetch(`http://localhost:8080/test/setup?test=${testVersion}`, {"method": "POST"});
    await driver.executeScript("displayGameState(await getGameState())");
}

async function integration_tests(){
    console.time("winner2_game_2winner_quest");
    await winner2_game_2winner_quest();
    console.timeEnd("winner2_game_2winner_quest");

    console.time("winner1_game_with_events");
    await winner1_game_with_events();
    console.timeEnd("winner1_game_with_events");

    console.time("winner0_quest");
    await winner0_quest();
    console.timeEnd("winner0_quest");

    console.time("a1_scenario");
    await a1_scenario();
    console.timeEnd("a1_scenario");
}

integration_tests()
