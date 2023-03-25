package Business;

import Common.Army;
import Common.Country;
import Common.Exceptions.*;
import Common.Player;
import Persistence.IPersistence;

import java.io.IOException;
import java.util.*;

public class GameManager implements IGameManager {

    private final IPersistence persistence;
    private final PlayerManager playerManagerFriend;
    private final WorldManager worldManagerFriend;
    private Map<String, Country> countryMap; //key is country name
    private final List<String> involvedCountries; //String: country name
    private int receivedUnits;


    public GameManager(IPlayerManager playerManager, IWorldManager worldManager, IPersistence persistence) throws IOException {

        this.persistence = persistence;
        playerManagerFriend = (PlayerManager) playerManager;
        worldManagerFriend = (WorldManager) worldManager;
        countryMap = worldManager.getCountryMap();
        involvedCountries = persistence.fetchGameStateInvolvedCountries();
    }
    @Override
    public boolean saveGame(int gameStep) throws IOException {

        if(gameStep == 2){
            persistence.saveInvolvedCountries(involvedCountries);
        }
        return persistence.saveGameStateArmies(countryMap) && playerManagerFriend.save(gameStep);
    }

    @Override
    public void quitGame() throws IOException {

        playerManagerFriend.clearPlayers();
        worldManagerFriend.clearWorld();
        countryMap.clear();
    }

    @Override
    public void newGame() throws IOException {

        persistence.resetGameState();
        quitGame();
        countryMap = worldManagerFriend.getCountryMap();
    }
    @Override
    public int getSavedGameStep() throws IOException {

        int[] roundAndStep = persistence.fetchGameRoundAndStep();
        return roundAndStep[2];
    }

    @Override
    public void startFirstRound(boolean missionRisk) throws ExceptionNotEnoughPlayer {

        List<Country> countryList = new ArrayList<>(countryMap.values());
        List<Player> playerList = new ArrayList<>((playerManagerFriend.getPlayerMap().values()));

        if(playerList.isEmpty() || playerList.size() == 1){
            throw new ExceptionNotEnoughPlayer();
        }

        Collections.shuffle(countryList);
        Collections.shuffle(playerList);

        Player lastPlayer = null;
        for (int i = 0; i < countryList.size(); i++) {
            Country country = countryList.get(i);
            Player player = playerList.get(i % playerList.size());

            country.setArmy(new Army(1, player.getPlayerName()));

            player.addConqueredCountry(country.getCountryName());
            lastPlayer = player;
        }
        if(lastPlayer != null){
            playerManagerFriend.setCurrentPlayer(lastPlayer.getPlayerName());
        }

        playerManagerFriend.setPlayerMission(missionRisk);
    }



    @Override
    public int receiveUnits() throws ExceptionObjectDoesntExist{

        involvedCountries.clear();

        String playerName = playerManagerFriend.getCurrentPlayerName();
        if(!playerManagerFriend.getPlayerMap().containsKey(playerName)){
           throw new ExceptionObjectDoesntExist(playerName);
        }

        List<String> playerCountries = playerManagerFriend.getCurrentPlayersCountries();
        int armySize = (playerCountries.size() < 9) ? 3 : playerCountries.size() / 3;

        receivedUnits = armySize + worldManagerFriend.getPointsForConqueredContinents(playerCountries);
        return receivedUnits;
    }

    @Override
    public void distributeUnits(String selectedCountry, int selectedUnits) throws ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryNotRecognized, ExceptionEmptyInput {

        if(selectedCountry == null){
            throw new ExceptionCountryNotRecognized();
        }

        if(selectedCountry.isEmpty()){
            throw new ExceptionEmptyInput();
        }


        if(!worldManagerFriend.getCountryOwner(selectedCountry).equals(playerManagerFriend.getCurrentPlayerName())){
            throw new ExceptionCountryNotOwned(selectedCountry, playerManagerFriend.getCurrentPlayerName());
        }

        if(receivedUnits - selectedUnits < 0){
            throw new ExceptionTooManyUnits(receivedUnits);
        }
        receivedUnits -= selectedUnits;
        countryMap.get(selectedCountry).getArmy().addUnits(selectedUnits);
    }
    @Override
    public int getReceivedUnits(){ return receivedUnits; }
    @Override
    public boolean allUnitsDistributed(){ return receivedUnits != 0; }

    @Override
    public List<Integer> attack(String attackingCountry, String attackedCountry, int units) throws ExceptionCountryNotOwned, ExceptionCountryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits, ExceptionCountryNotRecognized, ExceptionEmptyInput, ExceptionOwnCountryAttacked {

        if(attackingCountry == null || attackedCountry == null){
            throw new ExceptionCountryNotRecognized();
        }

        if(attackingCountry.isEmpty() || attackedCountry.isEmpty()){
            throw new ExceptionEmptyInput();
        }

        if(!worldManagerFriend.getCountryOwner(attackingCountry).equals(playerManagerFriend.getCurrentPlayerName())) {
            throw new ExceptionCountryNotOwned(attackingCountry, playerManagerFriend.getCurrentPlayerName());
        }

        if(worldManagerFriend.getCountryOwner(attackedCountry).equals(playerManagerFriend.getCurrentPlayerName())){
            throw new ExceptionOwnCountryAttacked();
        }


        if(!worldManagerFriend.getCountryNeighbours(attackingCountry).contains(attackedCountry)){
            throw new ExceptionCountryIsNoNeighbour(attackingCountry, attackedCountry);
        }

        int unitsAttacker = countryMap.get(attackingCountry).getArmy().getUnits();
        if(unitsAttacker < 2){
            throw new ExceptionTooLessUnits(2);
        }

        if(units > 3){
            throw new ExceptionTooManyUnits(3);
        }

        if(unitsAttacker == units){
            throw new ExceptionTooManyUnits(unitsAttacker - 1);
        }
        if(!involvedCountries.contains(attackedCountry)){
            involvedCountries.add(attackedCountry);
        }

        if(!involvedCountries.contains(attackingCountry)){
            involvedCountries.add(attackingCountry);
        }

        List<Integer> attackerDiceResult = new ArrayList<>();
        while(units != 0){
            attackerDiceResult.add(rollDice());
            units--;
        }
        return attackerDiceResult;
    }

    @Override
    public List<Integer> defend(String countryToDefend, String attackingCountry, List<Integer> attackerDiceResult, int attackerUnits) {

        List<Integer> defenderDiceResult = new ArrayList<>();
        int unitsDefender = worldManagerFriend.getUnitAmountOfCountry(countryToDefend);
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
                countryMap.get(countryToDefend).getArmy().removeUnits(1);
            } else {
                countryMap.get(attackingCountry).getArmy().removeUnits(1);
                lostPointsAttacker++;
            }
        }

        if(worldManagerFriend.getUnitAmountOfCountry(countryToDefend) == 0){
            String previousOwner = countryMap.get(countryToDefend).getArmy().getPlayerName();
            countryMap.get(countryToDefend).setArmy(new Army(attackerUnits - lostPointsAttacker, playerManagerFriend.getCurrentPlayerName()));
            countryMap.get(attackingCountry).getArmy().removeUnits(attackerUnits);
            playerManagerFriend.changeCountryOwner(playerManagerFriend.getCurrentPlayerName(), previousOwner, countryToDefend);
        }

        return defenderDiceResult;
     }

    @Override
    public void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) throws ExceptionInvolvedCountrySelected, ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryIsNoNeighbour, ExceptionCountryNotRecognized, ExceptionEmptyInput {

        if(sourceCountry == null || destinationCountry == null){
            throw new ExceptionCountryNotRecognized();
        }

        if(sourceCountry.isEmpty() || destinationCountry.isEmpty()){
            throw new ExceptionEmptyInput();
        }

        if(!afterConquering){
            if(involvedCountries.contains(sourceCountry) || involvedCountries.contains(destinationCountry)){
                throw new ExceptionInvolvedCountrySelected();
            }
        }

        if(!worldManagerFriend.getCountryOwner(sourceCountry).equals(playerManagerFriend.getCurrentPlayerName())){
            throw new ExceptionCountryNotOwned(sourceCountry, playerManagerFriend.getCurrentPlayerName());
        }

        if(worldManagerFriend.getUnitAmountOfCountry(sourceCountry) - units < 1){
            throw new ExceptionTooManyUnits(countryMap.get(sourceCountry).getArmy().getUnits() - 1);
        }

        if(!worldManagerFriend.getCountryNeighbours(sourceCountry).contains(destinationCountry)){
            throw new ExceptionCountryIsNoNeighbour(sourceCountry, destinationCountry);
        }

        countryMap.get(sourceCountry).getArmy().removeUnits(units);
        countryMap.get(destinationCountry).getArmy().addUnits(units);
    }

    private static int rollDice(){

        Random random = new Random();
        return random.nextInt(6) + 1;
    }
}
