package Business;

import Common.Army;
import Common.Country;
import Common.Player;

import java.util.*;

public class Game implements GameManager {

    IPlayerManager playerManager;
    IWorldManager worldManager;
    String currentPlayerName;

    //the following two maps keep track of country ownership
    Map<String, List<Country>> countryPlayerMap; //key is player name
    Map<String, Country> countryMap; //key is country name


    public Game(IPlayerManager playerManager, IWorldManager worldManager){

        this.playerManager = playerManager;
        this.worldManager = worldManager;
        countryMap = worldManager.getCountryMap();
        countryPlayerMap = new HashMap<>();
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
        for(Country country : countryPlayerMap.get(playerName)){
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
            countryPlayerMap.put(player.getPlayerName(), new ArrayList<>());
        }

        Collections.shuffle(countryList);
        Collections.shuffle(playerList);

        Player lastPlayer = null;
        for (int i = 0; i < countryList.size(); i++) {
            Country country = countryList.get(i);
            Player player = playerList.get(i % playerList.size());

            country.setArmy(new Army(1, player));

            countryMap.put(country.getCountryName(), country);
            countryPlayerMap.get(player.getPlayerName()).add(country);

            lastPlayer = player;
        }
        return lastPlayer;
    }

    @Override
    public int receiveUnits(String playerName) {

        this.currentPlayerName = playerName;
        List<Country> playerCountries = new ArrayList<>(countryPlayerMap.get(playerName));
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
    public boolean distributeUnits(String selectedCountry, int units) {

        if(!getCountryOwner(selectedCountry).equals(currentPlayerName)){
            return false;
        }
        countryMap.get(selectedCountry).getArmy().addUnits(units);
        return true;
    }

    @Override
    public String attack(String attackingCountry, String attackedCountry, int units) {

        int unitsAttacker = countryMap.get(attackingCountry).getArmy().getUnits();
        if(!worldManager.getCountryNeighbours(attackingCountry).contains(attackedCountry) || unitsAttacker < 2){
            return "Please select a neighbouring country and only attack with a country which has at least two units";
        }
        if(units > 3){
            return "You can only use three units for your attack.";
        }

        if(unitsAttacker == units){
            return "One unit has to remain in " + attackingCountry;
        }
        List<Integer> diceResult = new ArrayList<>();
        while(units != 0){
            diceResult.add(rollDice());
            units--;
        }
        return "You attacked " + attackedCountry + "! You rolled: " + diceResult;
    }

    @Override
    public String defend(String countryToDefend, String attackingCountry, int units) {
        return null;
    }

    @Override
    public String moveUnits(String sourceCountry, String destinationCountry, int units) {

        Player currentPlayer = countryMap.get(sourceCountry).getArmy().getPlayer();
        currentPlayer.getPlayerMission().setCountries(countryPlayerMap.get(currentPlayer.getPlayerName()));
        return null;
    }

    public static int rollDice(){

        Random random = new Random();
        return random.nextInt(6) + 1;
    }
}
