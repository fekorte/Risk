package Business;

import Common.Army;
import Common.Continent;
import Common.Country;
import Common.Exceptions.*;
import Common.Player;

import java.util.*;

public class Game implements GameManager {

    IPlayerManager playerManager;
    IWorldManager worldManager;
    String currentPlayerName;
    Map<String, Map<String, Country>> countryPlayerMap; //key 1 is player name, key 2 is country name
    Map<String, Country> countryMap; //key is country name
    List<String> involvedCountries; //String: country name


    public Game(IPlayerManager playerManager, IWorldManager worldManager){

        this.playerManager = playerManager;
        this.worldManager = worldManager;
        countryMap = worldManager.getCountryMap();
        countryPlayerMap = new HashMap<>();
        involvedCountries = new ArrayList<>();
    }


    @Override
    public void saveGame() {

    }

    @Override
    public void quitGame() {

        playerManager.clearPlayers();
        countryPlayerMap.clear();
    }

    public String getAllCountriesInfoPlayer(String playerName){

        StringBuilder playerCountries = new StringBuilder();
        for (Country country : countryPlayerMap.get(playerName).values()) {
            playerCountries.append(country.getCountryName()).append(": ");
            playerCountries.append(country.getArmy().getUnits()).append("\n");
        }
        return playerCountries.toString();
    }

    @Override
    public String getCountryOwner(String country){

        return countryMap.get(country).getArmy().getPlayer().getPlayerName();
    }


    @Override
    public Player startFirstRound() throws ExceptionNotEnoughPlayer {

        List<Country> countryList = new ArrayList<>(countryMap.values());
        List<Player> playerList = new ArrayList<>((playerManager.getPlayerMap().values()));

        if(playerList.isEmpty() || playerList.size() == 1){
            throw new ExceptionNotEnoughPlayer();
        }

        for(Player player : playerList){
            countryPlayerMap.put(player.getPlayerName(), new HashMap<>());
        }

        Collections.shuffle(countryList);
        Collections.shuffle(playerList);

        Player lastPlayer = null;
        for (int i = 0; i < countryList.size(); i++) {
            Country country = countryList.get(i);
            Player player = playerList.get(i % playerList.size());

            country.setArmy(new Army(1, player));

            countryMap.put(country.getCountryName(), country);
            countryPlayerMap.get(player.getPlayerName()).put(country.getCountryName(), country);

            lastPlayer = player;
        }
        return lastPlayer;
    }

    @Override
    public int receiveUnits(String playerName) throws ExceptionObjectDoesntExist{

        if(!playerManager.getPlayerMap().containsKey(playerName)){
           throw new ExceptionObjectDoesntExist(playerName);
        }

        involvedCountries.clear();
        this.currentPlayerName = playerName;
        List<Country> playerCountries = new ArrayList<>(countryPlayerMap.get(playerName).values());
        int armySize = (playerCountries.size() < 9) ? 3 : playerCountries.size() / 3;

        List<String> conqueredContinents = worldManager.getConqueredContinents(playerCountries);
        Map<String, Continent> continents = worldManager.getContinents();

        for(String conqueredContinent : conqueredContinents){
            armySize += continents.get(conqueredContinent).getPointsForConquering();
        }
        return armySize;
    }

    @Override
    public void distributeUnits(String selectedCountry, int selectedUnits, int receivedUnits) throws ExceptionCountryNotOwned, ExceptionTooManyUnits{

        if(!getCountryOwner(selectedCountry).equals(currentPlayerName)){
            throw new ExceptionCountryNotOwned(selectedCountry, currentPlayerName);
        }

        if(receivedUnits - selectedUnits < 0){
            throw new ExceptionTooManyUnits(receivedUnits);
        }
        countryMap.get(selectedCountry).getArmy().addUnits(selectedUnits);
    }

    @Override
    public List<Integer> attack(String attackingCountry, String attackedCountry, int units) throws ExceptionCountryNotOwned, ExceptionCountryIsNoNeighbour, ExceptionTooLessUnits, ExceptionTooManyUnits{

        if(!getCountryOwner(attackingCountry).equals(currentPlayerName)){
            throw new ExceptionCountryNotOwned(attackingCountry, currentPlayerName);
        }


        if(!worldManager.getCountryNeighbours(attackingCountry).contains(attackedCountry)){
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
        int unitsDefender = countryMap.get(countryToDefend).getArmy().getUnits();
        if (unitsDefender != 1) {
            defenderDiceResult.add(rollDice());
        }
        defenderDiceResult.add(rollDice());

        attackerDiceResult.sort(Collections.reverseOrder());
        defenderDiceResult.sort(Collections.reverseOrder());
        int comparisonSize = Math.min(attackerDiceResult.size(), defenderDiceResult.size());

        int lostPointsAttacker = 0;

        for (int i = 0; i < comparisonSize - 1; i++){
            if ((attackerDiceResult.get(i) > defenderDiceResult.get(i))) {
                countryMap.get(countryToDefend).getArmy().removeUnits(1);
            } else {
                countryMap.get(attackingCountry).getArmy().removeUnits(1);
                lostPointsAttacker++;
            }
        }

        if(countryMap.get(countryToDefend).getArmy().getUnits() == 0){
            countryMap.get(countryToDefend).setArmy(new Army(attackerUnits - lostPointsAttacker, countryMap.get(attackingCountry).getArmy().getPlayer()));
            countryPlayerMap.get(countryMap.get(countryToDefend).getArmy().getPlayer().getPlayerName()).remove(countryToDefend);
            countryPlayerMap.get(currentPlayerName).put(countryToDefend, countryMap.get(countryToDefend));
        }

        return defenderDiceResult;
     }

    @Override
    public void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) throws ExceptionInvolvedCountrySelected, ExceptionCountryNotOwned, ExceptionTooManyUnits, ExceptionCountryIsNoNeighbour {

        if(!afterConquering){
            if(involvedCountries.contains(sourceCountry) || involvedCountries.contains(destinationCountry)){
                throw new ExceptionInvolvedCountrySelected();
            }
        }

        if(!getCountryOwner(sourceCountry).equals(currentPlayerName)){
            throw new ExceptionCountryNotOwned(sourceCountry, currentPlayerName);
        }

        if(countryMap.get(sourceCountry).getArmy().getUnits() - units < 1){
            throw new ExceptionTooManyUnits(countryMap.get(sourceCountry).getArmy().getUnits() - 1);
        }

        if(!countryMap.get(sourceCountry).getNeighbours().contains(countryMap.get(destinationCountry))){
            throw new ExceptionCountryIsNoNeighbour(sourceCountry, destinationCountry);
        }

        countryMap.get(sourceCountry).getArmy().removeUnits(units);
        countryMap.get(destinationCountry).getArmy().addUnits(units);
    }

    public static int rollDice(){

        Random random = new Random();
        return random.nextInt(6) + 1;
    }
}
