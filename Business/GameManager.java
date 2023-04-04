package Business;

import Common.Territory;
import Common.Exceptions.*;
import Common.Player;
import Persistence.IPersistence;

import java.io.IOException;
import java.util.*;

public class GameManager implements IGameManager {

    private final IPersistence persistence;
    private final PlayerManager playerManagerFriend;
    private final WorldManager worldManagerFriend;
    private final List<String> involvedTerritories; //String: territory name
    private int receivedUnits;


    public GameManager(IPlayerManager playerManager, IWorldManager worldManager) throws IOException {

        playerManagerFriend = (PlayerManager) playerManager;
        worldManagerFriend = (WorldManager) worldManager;
        this.persistence = worldManagerFriend.getPersistence();
        involvedTerritories = persistence.fetchGameStateInvolvedTerritories();
    }
    @Override
    public boolean saveGame(int gameStep) throws IOException {

        if(gameStep == 2){
            persistence.saveInvolvedTerritories(involvedTerritories);
        }
        return persistence.saveGameStateArmies(worldManagerFriend.getTerritoryMap()) && playerManagerFriend.save(gameStep);
    }

    @Override
    public void quitGame() throws IOException {

        playerManagerFriend.clearPlayers();
        worldManagerFriend.clearWorld();
    }

    @Override
    public void newGame() throws IOException {

        persistence.resetGameState();
        quitGame();
    }
    @Override
    public int getSavedGameStep() throws IOException {

        int[] roundAndStep = persistence.fetchGameRoundAndStep();
        return roundAndStep[2];
    }

    @Override
    public void startFirstRound(boolean missionRisk) throws ExceptionNotEnoughPlayer {

        List<Territory> territoryList = new ArrayList<>(worldManagerFriend.getTerritoryMap().values());
        List<Player> playerList = new ArrayList<>((playerManagerFriend.getPlayerMap().values()));

        if(playerList.isEmpty() || playerList.size() == 1){
            throw new ExceptionNotEnoughPlayer();
        }

        Collections.shuffle(territoryList);
        Collections.shuffle(playerList);

        Player lastPlayer = null;
        for (int i = 0; i < territoryList.size(); i++) {
            Territory territory = territoryList.get(i);
            Player player = playerList.get(i % playerList.size());

            worldManagerFriend.setCountryArmy(territory.getTerritoryName(), 1, player.getPlayerName());

            player.addConqueredTerritory(territory.getTerritoryName());
            lastPlayer = player;
        }
        if(lastPlayer != null){
            playerManagerFriend.setCurrentPlayer(lastPlayer.getPlayerName());
        }

        playerManagerFriend.setAllPlayerMissions(missionRisk);
    }



    @Override
    public void receiveUnits() throws ExceptionObjectDoesntExist{

        involvedTerritories.clear();

        String playerName = playerManagerFriend.getCurrentPlayerName();
        if(!playerManagerFriend.getPlayerMap().containsKey(playerName)){
           throw new ExceptionObjectDoesntExist(playerName);
        }

        List<String> playerCountries = playerManagerFriend.getCurrentPlayersCountries();
        int armySize = (playerCountries.size() < 9) ? 3 : playerCountries.size() / 3;

        this.receivedUnits = armySize + worldManagerFriend.getPointsForConqueredContinents(playerCountries);
    }
    @Override
    public int getReceivedUnits(){ return receivedUnits; }
    @Override
    public void distributeUnits(String selectedTerritory, int selectedUnits) throws ExceptionTerritorySelectedNotOwned, ExceptionTooManyUnits, ExceptionEmptyInput {


        if(selectedTerritory.isEmpty()){
            throw new ExceptionEmptyInput();
        }


        if(!worldManagerFriend.getTerritoryOwner(selectedTerritory).equals(playerManagerFriend.getCurrentPlayerName())){
            throw new ExceptionTerritorySelectedNotOwned(selectedTerritory, playerManagerFriend.getCurrentPlayerName());
        }

        if(receivedUnits - selectedUnits < 0){
            throw new ExceptionTooManyUnits(receivedUnits);
        }
        receivedUnits -= selectedUnits;
        worldManagerFriend.addUnitsToCountry(selectedTerritory, selectedUnits);
    }
    @Override
    public List<Integer> attack(String attackingTerritory, String attackedTerritory, int units) throws ExceptionTerritorySelectedNotOwned, ExceptionTerritoryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits, ExceptionEmptyInput, ExceptionOwnTerritoryAttacked {

        if(attackingTerritory.isEmpty() || attackedTerritory.isEmpty()){
            throw new ExceptionEmptyInput();
        }

        if(!worldManagerFriend.getTerritoryOwner(attackingTerritory).equals(playerManagerFriend.getCurrentPlayerName())) {
            throw new ExceptionTerritorySelectedNotOwned(attackingTerritory, playerManagerFriend.getCurrentPlayerName());
        }

        if(worldManagerFriend.getTerritoryOwner(attackedTerritory).equals(playerManagerFriend.getCurrentPlayerName())){
            throw new ExceptionOwnTerritoryAttacked();
        }


        if(!worldManagerFriend.getTerritoryNeighbours(attackingTerritory).contains(attackedTerritory)){
            throw new ExceptionTerritoryIsNoNeighbour(attackingTerritory, attackedTerritory);
        }

        int unitsAttacker = worldManagerFriend.getUnitAmountOfTerritory(attackingTerritory);
        if(unitsAttacker < 2){
            throw new ExceptionTooLessUnits(2);
        }

        if(units > 3){
            throw new ExceptionTooManyUnits(3);
        }

        if(unitsAttacker == units){
            throw new ExceptionTooManyUnits(unitsAttacker - 1);
        }
        if(!involvedTerritories.contains(attackedTerritory)){
            involvedTerritories.add(attackedTerritory);
        }

        if(!involvedTerritories.contains(attackingTerritory)){
            involvedTerritories.add(attackingTerritory);
        }

        List<Integer> attackerDiceResult = new ArrayList<>();
        while(units != 0){
            attackerDiceResult.add(rollDice());
            units--;
        }
        return attackerDiceResult;
    }

    @Override
    public List<Integer> defend(String territoryToDefend, String attackingTerritory, List<Integer> attackerDiceResult, int attackerUnits) {

        List<Integer> defenderDiceResult = new ArrayList<>();
        int unitsDefender = worldManagerFriend.getUnitAmountOfTerritory(territoryToDefend);
        if (unitsDefender != 1) {
            defenderDiceResult.add(rollDice());
        }
        defenderDiceResult.add(rollDice());

        attackerDiceResult.sort(Collections.reverseOrder());
        defenderDiceResult.sort(Collections.reverseOrder());
        int comparisonSize = Math.min(attackerDiceResult.size(), defenderDiceResult.size());

        int lostPointsAttacker = 0;

        for (int i = 0; i < comparisonSize; i++){
            if ((attackerDiceResult.get(i) > defenderDiceResult.get(i))) {
                worldManagerFriend.removeUnitsFromCountry(territoryToDefend, 1);
            } else {
                worldManagerFriend.removeUnitsFromCountry(attackingTerritory, 1);
                lostPointsAttacker++;
            }
        }

        if(worldManagerFriend.getUnitAmountOfTerritory(territoryToDefend) == 0){
            String previousOwner = worldManagerFriend.getTerritoryOwner(territoryToDefend);
            worldManagerFriend.setCountryArmy(territoryToDefend,attackerUnits - lostPointsAttacker , playerManagerFriend.getCurrentPlayerName());
            worldManagerFriend.removeUnitsFromCountry(attackingTerritory, attackerUnits);
            playerManagerFriend.changeTerritoryOwner(playerManagerFriend.getCurrentPlayerName(), previousOwner, territoryToDefend);
        }

        return defenderDiceResult;
     }

    @Override
    public void moveUnits(String sourceTerritory, String destinationTerritory, int units, boolean afterConquering) throws ExceptionInvolvedTerritorySelected, ExceptionTerritorySelectedNotOwned, ExceptionTooManyUnits, ExceptionTerritoryIsNoNeighbour, ExceptionEmptyInput {

        if(sourceTerritory.isEmpty() || destinationTerritory.isEmpty()){
            throw new ExceptionEmptyInput();
        }

        if(!afterConquering){
            if(involvedTerritories.contains(sourceTerritory) || involvedTerritories.contains(destinationTerritory)){
                throw new ExceptionInvolvedTerritorySelected();
            }
        }

        if(!worldManagerFriend.getTerritoryOwner(sourceTerritory).equals(playerManagerFriend.getCurrentPlayerName())){
            throw new ExceptionTerritorySelectedNotOwned(sourceTerritory, playerManagerFriend.getCurrentPlayerName());
        }

        if(worldManagerFriend.getUnitAmountOfTerritory(sourceTerritory) - units < 1){
            throw new ExceptionTooManyUnits(worldManagerFriend.getUnitAmountOfTerritory(sourceTerritory) - 1);
        }

        if(!worldManagerFriend.getTerritoryNeighbours(sourceTerritory).contains(destinationTerritory)){
            throw new ExceptionTerritoryIsNoNeighbour(sourceTerritory, destinationTerritory);
        }

        worldManagerFriend.removeUnitsFromCountry(sourceTerritory, units);
        worldManagerFriend.addUnitsToCountry(destinationTerritory, units);
    }

    private static int rollDice(){

        Random random = new Random();
        return random.nextInt(6) + 1;
    }
}
