const backendUrl = 'http://localhost:8080';

async function startGame() {
    // enable all buttons
    enableButtons();
    try {
        await fetch(backendUrl + '/start')
            .then(response => response.json());
        clearOutput();
        const gs = await getGameState(); // Wait for game state
        displayGameState(gs); // Display updated state
        displayOutput("Game started! Player " + (gs.currentPlayer+1) + " is up first.");
    } catch (error) {
        console.error("Error starting game:", error);
        displayOutput("An error occurred while starting the game.");
    }
}

async function drawCard() {
    try {
        const response = await fetch(backendUrl + '/draw');
        const message = await response.json();
        if (message.res === "SUCCESS") {
            displayGameState(await getGameState()); // Display updated state
            await handleDrawnCard(message.card); // Pass card name and game state
        } else {
            displayOutput(message.res);
        }
    } catch (error) {
        console.error("Error drawing card:", error);
        displayOutput("An error occurred while drawing a card.");
    }
}

async function nextTurn() {
    try {
        const message = (await fetch(backendUrl + '/next-turn')
            .then(response => response.json()));
        const gs = await getGameState(); // Wait for game state
        await displayGameState(gs); // Display updated state

        if ("TRIM_REQUIRED" === message) {
            console.log("Message: ", message);
            await trimCards(gs.currentPlayer+1);
        } else {
            displayOutput("Player " + (gs.currentPlayer+1) + " is up next!");
        }
    } catch (error) {
        console.error("Error moving to next turn:", error);
        displayOutput("An error occurred while moving to the next turn.");
    }
}

async function handleDrawnCard(card) { // Receive card name and game state
    switch (card.toLowerCase()) { // Use card name in switch
        case "plague":
            await handlePlague();
            break;
        case "queen's favor":
            await handleQueensFavor();
            break;
        case "prosperity":
            await handleProsperity(); // Wait for handleProsperity to complete
            break;
        default:
            await handleQuestCard() ;
    }
}

async function handlePlague() {
    try {
        await fetch(backendUrl + '/plague')
            .then(response => response.json());
        displayGameState(await getGameState());
        displayOutput("You lose 2 Shields!");
    } catch (error) {
        console.error("Error handling plague:", error);
        displayOutput("An error occurred during plague.");
    }
}

async function handleProsperity() {
    try {
        const message = await fetch(backendUrl + '/prosperity')
            .then(response => response.json());
        displayOutput("All players gain 2 Cards!");

        if (message !== "SUCCESS")
            await forceTrimAllPlayers(await getGameState()); // Trim all players' hands
    } catch (error) {
        console.error("Error handling prosperity:", error);
        displayOutput("An error occurred during prosperity.");
    }
}

async function handleQueensFavor() {
    try {
        const response = await fetch(backendUrl + '/queens-favor');
        const message = await response.json();
        displayOutput("You Gain 2 Cards!");

        if (message !== "SUCCESS")
            await forceTrim((await getGameState()).currentPlayer+1);
    } catch (error) {
        console.error("Error handling Queen's Favor:", error);
        displayOutput("An error occurred during Queen's Favor.");
    }
}

async function endQuest(){
    return await fetch(backendUrl + '/end-quest').then(response => response.json());
}

function cleanupQuestDisplay() {
    let sponsorBlock = document.getElementById("sponsor-id");
    sponsorBlock.textContent = "";
    sponsorBlock.style.display = "none";

    let stageBlock = document.getElementById("stage-number");
    stageBlock.textContent = "";
    stageBlock.style.display = "none";

    let participantsBlock = document.getElementById("participants");
    participantsBlock.textContent = "";
    participantsBlock.style.display = "none";
}

async function cleanupQuest(sponsorId){
    let message = await fetch(backendUrl + '/cleanup-quest').then(response => response.json());
    displayGameState(await getGameState());

    // if message is TRIM_REQUIRED call trimCards
    if (message === "TRIM_REQUIRED")
        await forceTrim(sponsorId);

}

async function setupStage(stage) {
    disableButtons();
    const inputButton = document.getElementById('input-button');
    inputButton.disabled = false;
    const cardIndexInput = document.getElementById('card-index'); // Get the card index input element

    return new Promise(async (resolve) => {
        let questState = await getQuestState();
        let stageCards = questState.stages.length > stage ? questState.stages[stage] : [];
        displayOutput(`Setting up stage ${stage + 1}, Cards: ${stageCards.join(', ')}<br>` +
            'Enter Next Card Index or -1 to finish stage setup');

        inputButton.onclick = async () => {
            const cardIndex = cardIndexInput.value;
            let responseMessage;

            if (cardIndex === "-1") {
                responseMessage = await quitStageSetup();
                if (responseMessage === "SUCCESS" || responseMessage === "ALL_STAGES_SET") {
                    displayOutput("Stage setup successful!");
                    enableButtons();
                    resolve();
                    await displayGameState(await getGameState());
                    return;
                }
            } else {
                responseMessage = await addCardToStage(cardIndex);
            }

            await displayGameState(await getGameState());
            questState = await getQuestState();
            stageCards = questState.stages.length > stage ? questState.stages[stage] : [];
            displayOutput(`Setting up stage ${stage + 1}, Cards: ${stageCards.join(', ')}<br>` +
                `Enter Next Card Index or -1 to finish stage setup - ${responseMessage}`);
        };
    });
}

async function quitStageSetup() {
    try {
        return await fetch(backendUrl + '/quit-stage-setup')
            .then(response => response.json());
    } catch (error) {
        console.error("Error quitting stage setup:", error);
        return "An error occurred while quitting stage setup.";
    }
}

async function addCardToStage(cardIndex) {
    try {
        return await fetch(backendUrl + '/add-card-to-stage?' + new URLSearchParams({
            cardIndex: cardIndex
        }).toString(), { method: 'POST' })
            .then(response => response.json());
    } catch (error) {
        console.error("Error adding card to stage:", error);
        return "An error occurred while adding the card to the stage.";
    }
}

async function askPlayersToParticipate(questState) {
    const participants = [];
    displayQuestState(await getQuestState());
    for (let {id} of questState.participants) {
        // Set up buttons and ask player to participate
        disableButtons()

        if (await askToParticipate(id)) {
            participants.push(id);
        }
        displayQuestState(await getQuestState());
        displayGameState(await getGameState());
        await clearHotseat()
    }
    return participants;
}

async function askToParticipate(playerId) {
    return new Promise(resolve => {
        disableButtons();

        const participateButton = document.getElementById('participate-button');
        const noParticipateButton = document.getElementById('no-participate-button');
        participateButton.style.visibility = 'visible';
        participateButton.disabled = false;
        noParticipateButton.style.visibility = 'visible';
        noParticipateButton.disabled = false;

        displayOutput('Player ' + playerId + ', do you want to participate in the quest?');

        let handler = async (participate) => {
            resolve(await participateInQuest(playerId, participate));
            enableButtons();
        }
        participateButton.onclick = ()=> handler(true);
        noParticipateButton.onclick = () => handler(false);

    });
}

async function participateInQuest(playerId, participate) {
    try {
        let message = await fetch(backendUrl + '/participate-in-quest?' + new URLSearchParams({
            playerId,
            participate
        }).toString(), { method: 'POST' })
            .then(response => response.json());
        if (message === "TRIM_REQUIRED") {
            await forceTrim(playerId);
        }

        return participate
    } catch (error) {
        console.error("Error participating in quest:", error);
        return "An error occurred while participating in the quest.";
    }
}

async function quitAttackSetup(playerId) {
    try {
        return await fetch(backendUrl + '/quit-attack-setup?' + new URLSearchParams({
            playerId: playerId
        }).toString(), { method: 'POST' })
            .then(response => response.json());
    } catch (error) {
        console.error("Error quitting attack setup:", error);
        return "An error occurred while quitting attack setup.";
    }
}

async function setupAttack(playerId, stage) {
    disableButtons();
    const inputButton = document.getElementById('input-button');
    inputButton.disabled = false;
    const cardIndexInput = document.getElementById('card-index');

    return new Promise(async (resolve) => {
        let questState = await getQuestState();
        let attack = questState.attacks.hasOwnProperty(playerId) ? questState.attacks[playerId] : [];
        displayOutput(`Player ${playerId}, Stage ${stage+1}, Setting up attack, Cards: ${attack.join(', ')}<br>` +
            `Enter Next Card Index or -1 to finish attack setup`);

        inputButton.onclick = async () => {
            const cardIndex = cardIndexInput.value;
            let responseMessage ;

            if (cardIndex === "-1") {
                responseMessage = await quitAttackSetup(playerId);
                if (responseMessage === "SUCCESS") {
                    displayOutput("Attack setup successful!");
                    enableButtons();
                    resolve();
                    return;
                }
            } else {
                responseMessage = await addCardToAttack(playerId, cardIndex);
            }

            displayGameState(await getGameState());
            questState = await getQuestState();
            attack = questState.attacks.hasOwnProperty(playerId) ? questState.attacks[playerId] : [];
            displayOutput(`Player ${playerId}, Stage ${stage+1}, Setting up attack, Cards: ${attack.join(', ')}<br>` +
                `Enter Next Card Index or -1 to finish attack setup - ${responseMessage}`);
        };
    });
}

async function addCardToAttack(playerId, cardIndex) {
    try {
        return await fetch(backendUrl + '/add-card-to-attack?' + new URLSearchParams({
            playerId: playerId,
            cardIndex: cardIndex
        }).toString(), { method: 'POST' })
            .then(response => response.json());
    } catch (error) {
        console.error("Error adding card to attack:", error);
        return "An error occurred while adding the card to the attack.";
    }
}

async function resolveAttacks() {
    try {
        return await fetch(backendUrl + '/resolve-attacks')
            .then(response => response.json());
    } catch (error) {
        console.error("Error resolving attacks:", error);
        return "An error occurred while resolving attacks.";
    }
}

function displayWinners(winners) {
    // display the winners
    displayOutput("Game Over - The winners are: " + winners.map(p=>`P${p.id}`).join(', '));
    // disable all buttons
    disableButtons();
    // enable the start button
    document.getElementById('start-game').disabled = false;
}

function displayQuestState(questState) {
    const sponsorId = questState.sponsor.id;
    const sponsorBlock = document.getElementById("sponsor-id");
    sponsorBlock.textContent = `Quest Sponsor: P${sponsorId}`;
    sponsorBlock.style.display = "block";

    const currentStage = questState.currentStage;
    const currentAttackStage = questState.currentAttackStage;
    const stageBlock = document.getElementById("stage-number");
    const stage = Math.min(questState.questSize, currentStage <= questState.questSize ? currentStage : currentAttackStage);
    stageBlock.textContent = `Current Stage: ${stage}`;
    stageBlock.style.display = "block";

    const participants = questState.participants.map(p => `P${p.id}`);
    const participantsBlock = document.getElementById("participants");
    participantsBlock.textContent = `Quest Participants: ${participants.join(', ')}`;
    participantsBlock.style.display = "block";
}

async function handleQuestCard() {
    let gameState = await getGameState();
    // Start the quest
    await fetch(backendUrl + '/start-quest').then(response => response.json());

    // Find sponsor
    let sponsorId = await findSponsor(gameState);
    if (sponsorId === null) {
        console.log(await endQuest());
        await cleanupQuest(sponsorId);
        displayOutput("No sponsor found. Quest Ended.");
        return;
    }

    // Sponsor quest
    await fetch(backendUrl + '/sponsor?' + new URLSearchParams({
        playerId: sponsorId
    }).toString(), { method: 'POST' });
    displayQuestState(await getQuestState())

    // Get quest state
    let questState = await getQuestState();
    let stages = questState.questSize;

    // Set up Quest
    // for each stage, set up stage cards
    for (let i = 0; i < stages; i++) {
        await setupStage(i)
        displayQuestState((questState = await getQuestState()));
    }

    // for each stage
    for (let stage = 0; stage < stages; stage++) {
        // ask players to participate
        const participants = await askPlayersToParticipate(questState);
        if (participants.length === 0) {
            break;
        }

        // set up attack
        displayGameState(await getGameState());
        for (let j = 0; j < participants.length; j++) {
            await setupAttack(participants[j], stage);
            await clearHotseat();
        }

        // resolve attacks
        await resolveAttacks(gameState);
        questState = await getQuestState();
        displayQuestState(questState);
    }
    const message = await endQuest()

    questState = await getQuestState();
    gameState = await getGameState();
    displayGameState(gameState);
    const winners = questState.participants.map(p => `P${p.id}`);

    let participantsBlock = document.getElementById("participants");
    participantsBlock.textContent = "Quest Winners: None";
    participantsBlock.textContent = `Quest Winners: ${winners.join(', ')}`;

    await cleanupQuest(sponsorId);
    if (message === "NO_WINNERS") {
        displayOutput("Everyone lost the quest.");
    } else {
        displayOutput(`Quest winners are: ${winners.join(', ')}\nThey Gain ${questState.questSize} Shields!`);
    }
    cleanupQuestDisplay();

    // check for game winner, if there is call winners()
    if (gameState.winners.length > 0) {
        displayWinners(gameState.winners);
    }
}

async function askSponsor(player) {
    // make the sponsor and no sponsor buttons visible and if the player clicks sponsor return true else return false
    return new Promise(resolve => {
        disableButtons();

        const sponsorButton = document.getElementById('sponsor-button');
        const noSponsorButton = document.getElementById('no-sponsor-button');
        sponsorButton.style.visibility = 'visible';
        sponsorButton.disabled = false;
        noSponsorButton.style.visibility = 'visible';
        noSponsorButton.disabled = false;

        displayOutput('Player ' + player + ', do you want to sponsor the quest?');

        sponsorButton.onclick = () => {
            resolve(true);
            enableButtons();
        };

        noSponsorButton.onclick = () => {
            resolve(false);
            enableButtons();
        };
    });
}

async function findSponsor(gameState) {
    // starting from the current player ask each player if they want to sponsor the quest and if they do return that player
    let sponsor = null;
    for (let i = 0; i < gameState.players.length; i++) {
        let currentPlayerId = (gameState.currentPlayer + i) % gameState.players.length +1;
        if (await askSponsor(currentPlayerId)) {
            sponsor = currentPlayerId;
            break;
        }
    }
    return sponsor;
}

async function forceTrim(playerId) {
    disableButtons();

    // enable the button with id input-button
    const inputButton = document.getElementById('input-button');
    inputButton.disabled = false;

    // set the input Button text to Trim Card
    displayOutput('Player ' + playerId + ' must trim a card.');
    await displayGameState(await getGameState());

    await new Promise(resolve => {
        // set the input Button onclick to trimCard
        inputButton.onclick = async () => {
            const result = await trimCard(playerId); // Wait for trimCard to complete
            if (result === "SUCCESS") {
                resolve(); // Resolve the promise after trimming (regardless of success)
                enableButtons(); // Enable all buttons
            }
            await displayGameState(await getGameState());
        };
    });
}

async function forceTrimAllPlayers(gameState) {
    const trimPlayers = await fetch(backendUrl + '/trim-players')
        .then(response => response.json());

    // Find the player who needs trimming:
    for (let i = 0; i < trimPlayers.length; i++) {
        let currentPlayerId = (gameState.currentPlayer + i) % trimPlayers.length + 1;
        if (trimPlayers.some(player => player.id === currentPlayerId))
            await forceTrim(currentPlayerId);
            await clearHotseat();
    }
    displayOutput("All players have trimmed their cards.");
}

async function trimCard(playerId) {
    try {
        displayOutput("You must trim a card.");
        const cardIndex = document.getElementById('card-index').value;
        let message = await trimCardHelper(playerId, cardIndex);

        if (message === "SUCCESS") {
            displayOutput("Card trimmed successfully!");
        } else if (message === "INVALID_INPUT") {
            displayOutput("Invalid card index.");
        } else if (message === "NO_TRIM_REQUIRED") {
            displayOutput("No card trimming required.");
        }
        return message;
    } catch (error) {
        console.error("Error trimming card:", error);
        displayOutput("An error occurred while trimming the card.");
    }
    return "ERROR";
}

async function trimCardHelper(playerId, cardIndex) {
    let message = await fetch(backendUrl + '/trim-card?' + new URLSearchParams({
        playerId,
        cardIndex
    }).toString(), { method: 'POST' })
        .then(response => response.json());

    await displayGameState(await getGameState()); // Display updated state
    return message;
}

async function clearHotseat() {
    // await for a promise of the user clicking the clear hotseat button
    await new Promise(resolve => {
        disableButtons()

        const hotseatButton = document.getElementById('clear-hotseat-button');
        hotseatButton.disabled = false;
        displayOutput("Click the button to clear the hotseat.");

        hotseatButton.onclick = () => {
            clearOutput()
            enableButtons();
            resolve();
        };
    });
}

function disableButtons() {
    const buttons = document.querySelectorAll('button');
    buttons.forEach(button => button.disabled = true);
}

function enableButtons() {
    const buttons = document.querySelectorAll('button');
    buttons.forEach(button => button.disabled = false);
    document.getElementById('input-button').disabled = true;
    document.getElementById('clear-hotseat-button').disabled = true;
    // Hide sponsor buttons
    document.getElementById('sponsor-button').style.visibility = 'hidden';
    document.getElementById('no-sponsor-button').style.visibility = 'hidden';
    // Hide participant buttons
    document.getElementById('participate-button').style.visibility = 'hidden';
    document.getElementById('no-participate-button').style.visibility = 'hidden';
}

async function getGameState() {
    return await fetch(backendUrl + '/state').then(response => response.json());
}

async function getQuestState() {
    return await fetch(backendUrl + '/quest-state').then(response => response.json());
}

function displayGameState(gameState) {
    const playersDiv = document.getElementById('players');
    playersDiv.innerHTML = '';
    gameState.players.forEach(player => {
        const playerDiv = document.createElement('div');
        playerDiv.innerHTML = `
                    <h3 id="player-${player.id}" style="margin: 0">Player P${player.id}</h3>
                    <p id="player-${player.id}-hand" style="margin: 0">Hand: ${player.deck.join(', ')}</p>
                    <p id="player-${player.id}-hand-size" style="margin: 0">Hand Size: ${[player.deck.length]}</p>
                    <p id="player-${player.id}-shields" style="margin-top: 0">Shields: ${player.shields}</p>
                `;
        playersDiv.appendChild(playerDiv);
    });

    const turnSpan = document.getElementById('turn');
    turnSpan.textContent = gameState.currentPlayer + 1;

    const drawnCardSpan = document.getElementById('drawn-card');
    drawnCardSpan.textContent = gameState.currentCard;
}

function displayOutput(message) {
    const outputDiv = document.getElementById('output-text');
    outputDiv.innerHTML = ''; // Clear previous output
    const messageP = document.createElement('p');
    messageP.innerHTML = message;
    outputDiv.appendChild(messageP);
}

function clearOutput() {
    document.getElementById('output-text').innerHTML = '';
    document.getElementById('card-index').value = '';
}