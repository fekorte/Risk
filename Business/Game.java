package Business;

import Common.Army;
import Common.Country;
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

    public String getAllCountriesInfoPlayer(String playerName) {

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
    public Player startFirstRound() {

        List<Country> countryList = new ArrayList<>(countryMap.values());
        List<Player> playerList = new ArrayList<>((playerManager.getPlayerMap().values()));

        if(playerList.isEmpty() || playerList.size() == 1){
            return null;
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
    public int receiveUnits(String playerName) {

        if(!playerManager.getPlayerMap().containsKey(playerName)){
            return 0;
        }

        involvedCountries.clear();
        this.currentPlayerName = playerName;
        List<Country> playerCountries = new ArrayList<>(countryPlayerMap.get(playerName).values());
        int armySize = (playerCountries.size() < 9) ? 3 : playerCountries.size() / 3;

        List<String> conqueredContinents = worldManager.getConqueredContinents(playerCountries);
        if(conqueredContinents.contains("Australia") || conqueredContinents.contains("South America")){
            armySize += 2;
        }
        if(conqueredContinents.contains("Africa")){
            armySize += 3;
        }
        if(conqueredContinents.contains("Europe") || conqueredContinents.contains("North America")){
            armySize += 5;
        }
        if(conqueredContinents.contains("Asia")){
            armySize += 7;
        }
        return armySize;
    }

    @Override
    public boolean distributeUnits(String selectedCountry, int selectedUnits, int receivedUnits) {

        if(!getCountryOwner(selectedCountry).equals(currentPlayerName)){
            return false;
        }

        if(receivedUnits - selectedUnits < 0){
            return false;
        }
        countryMap.get(selectedCountry).getArmy().addUnits(selectedUnits);
        return true;
    }

    @Override
    public List<Integer> attack(String attackingCountry, String attackedCountry, int units) {

        if(!getCountryOwner(attackingCountry).equals(currentPlayerName)){
            //return "You can only attack from a country that belongs to you.";
            return null;
        }

        int unitsAttacker = countryMap.get(attackingCountry).getArmy().getUnits();
        if(!worldManager.getCountryNeighbours(attackingCountry).contains(attackedCountry) || unitsAttacker < 2){
            //return "Please select a neighbouring country and only attack with a country which has at least two units";
            return null;
        }
        if(units > 3){
            //return "You can only use three units for your attack.";
            return null;
        }

        if(unitsAttacker == units){
            //return "One unit has to remain in " + attackingCountry;
            return null;
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
    public void moveUnits(String sourceCountry, String destinationCountry, int units, boolean afterConquering) {

        if(!afterConquering){
            if(involvedCountries.contains(sourceCountry) || involvedCountries.contains(destinationCountry)){
                return;
            }
        }

        if(!getCountryOwner(sourceCountry).equals(currentPlayerName)){
            return;
        }

        if(countryMap.get(sourceCountry).getArmy().getUnits() - units <= 1){
            return;
        }

        if(!countryMap.get(sourceCountry).getNeighbours().contains(countryMap.get(destinationCountry))){
            return;
        }

        countryMap.get(sourceCountry).getArmy().removeUnits(units);
        countryMap.get(destinationCountry).getArmy().addUnits(units);
    }

    public static int rollDice(){

        Random random = new Random();
        return random.nextInt(6) + 1;
    }
}
