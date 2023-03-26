package Business;

import Common.Exceptions.*;
import Common.MissionConquerCountries;
import Common.MissionDefeatOpponent;
import Common.Player;
import Persistence.IPersistence;

import java.io.IOException;
import java.util.*;

public class PlayerManager implements IPlayerManager{
    private final IPersistence persistence;
    private final WorldManager worldManagerFriend;
    private final Map<String, Player> playerMap;
    private final List<Player> playerOrder;
    private ArrayList<String> allowedColors;
    private Player currentPlayer;
    private boolean continuePreviousGame;
    private int playerTurns;
    private int round;

    public PlayerManager(IWorldManager worldManager, IPersistence persistence) throws IOException {

        this.persistence = persistence;
        worldManagerFriend = (WorldManager) worldManager;
        playerOrder = persistence.fetchGameStatePlayers();
        playerMap = new HashMap<>();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow", "Pink"));

        initializeGameContinuation();
    }

    private void initializeGameContinuation() throws IOException {

        if(!playerOrder.isEmpty()){
            for (Player player : playerOrder) {
                playerMap.put(player.getPlayerName(), player);

                if(player.getPlayerMission().getMissionNumber() == 4){
                    MissionConquerCountries missionConquerCountries = (MissionConquerCountries) player.getPlayerMission();
                    missionConquerCountries.setCountryMap(worldManagerFriend.getCountryMap());
                }
            }

            this.currentPlayer = playerOrder.get(0);
            int[] roundAndStep = persistence.fetchGameRoundAndStep();
            this.round = roundAndStep[0];
            this.playerTurns = roundAndStep[1];
            continuePreviousGame = true;
        } else {
            this.playerTurns = 0;
            this.round = 1;
            continuePreviousGame = false;
        }
    }
    public boolean save(int gameStep) throws IOException {

        persistence.saveGameRoundAndStep(round, playerTurns, gameStep);
        return persistence.saveGameStatePlayers(playerOrder);
    }

    public void clearPlayers() throws IOException {

        playerMap.clear();
        playerOrder.clear();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow", "Pink"));
        initializeGameContinuation();
    }

    @Override
    public void addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayers, ExceptionColorAlreadyExists, ExceptionEmptyInput {

        if(name == null ||color == null || name.isEmpty() || color.isEmpty()){
            throw new ExceptionEmptyInput();
        }

        if(playerMap.containsKey(name)){
            throw new ExceptionPlayerAlreadyExists(name);
        }
        if(playerMap.size() == 6){
            throw new ExceptionTooManyPlayers();
        }
        if(!allowedColors.contains(color)){
            throw new ExceptionColorAlreadyExists(color);
        }

        Player newPlayer = new Player(name, color);
        playerMap.put(newPlayer.getPlayerName(), newPlayer);
        playerOrder.add(newPlayer);
        allowedColors.remove(color);
    }

    @Override
    public void removePlayer(String name) throws ExceptionObjectDoesntExist, ExceptionEmptyInput {

        if(name == null || name.isEmpty()){
            throw new ExceptionEmptyInput();
        }

        if (!playerMap.containsKey(name)){
            throw new ExceptionObjectDoesntExist(name);
        }

        if(playerTurns != 0){
            playerTurns--;
        }

        allowedColors.add(playerMap.get(name).getPlayerColor());
        playerOrder.remove(playerMap.get(name));
        playerMap.remove(name);
    }

    @Override
    public void nextPlayersTurn(){

        int currentIndex = playerOrder.indexOf(playerMap.get(currentPlayer.getPlayerName()));
        int nextIndex = (currentIndex + 1) % playerOrder.size();
        this.currentPlayer = playerOrder.get(nextIndex);
        Collections.rotate(playerOrder, -nextIndex);
        playerTurns++;

        if(playerTurns == playerOrder.size()){
             round++;
             playerTurns = 0;
        }
    }
    @Override
    public String isAnyMissionCompleted(){

        for(Player player : playerOrder){
            if(player.getPlayerMission().getMissionNumber() != 5){
                if(player.getPlayerMission().isMissionCompleted(player.getConqueredCountryNames())){
                    return player.getPlayerName();
                }
            } else {
                MissionDefeatOpponent missionDefeatOpponent = (MissionDefeatOpponent) player.getPlayerMission();
                String opponentName = missionDefeatOpponent.getOpponentName();
                if(player.getPlayerMission().isMissionCompleted(playerMap.get(opponentName).getConqueredCountryNames())){
                    return player.getPlayerName();
                }
            }
        }
        return null;
    }
    @Override
    public List<String> getAllowedColors(){ return allowedColors; }
    @Override
    public String getPlayerColor(String playerName){ return playerMap.get(playerName).getPlayerColor(); }
    public Map<String, Player> getPlayerMap(){ return playerMap; }
    @Override
    public String getPlayersInfo(){

        StringBuilder playerInfo = new StringBuilder();
        for(Player player : playerMap.values()){
            playerInfo.append("| ").append(player.getPlayerName()).append(": ").append(player.getPlayerColor()).append(" |");
        }
        return playerInfo.toString();
    }
    @Override
    public List<String> getPlayerNames(){ return new ArrayList<>(playerMap.keySet()); }
    @Override
    public List<String> getAllCountriesInfoPlayer(String playerName){

        List<String> countryInfos = new ArrayList<>();
        for(String country : playerMap.get(playerName).getConqueredCountryNames()){
            countryInfos.add(country + ": " + worldManagerFriend.getUnitAmountOfCountry(country));
        }
        return countryInfos;
    }
    @Override
    public boolean isPlayerDefeated(String playerName){ return playerMap.get(playerName).getConqueredCountryNames().isEmpty(); }
    @Override
    public boolean continuePreviousGame(){ return continuePreviousGame; }
    public void setCurrentPlayer(String currentPlayerName){

        this.currentPlayer = playerMap.get(currentPlayerName);
        Collections.rotate(playerOrder, playerOrder.indexOf(currentPlayer));
    }

    @Override
    public String getCurrentPlayerName() { return currentPlayer.getPlayerName(); }
    @Override
    public int getPlayerAmount() { return playerMap.size(); }
    @Override
    public int getRound(){ return round; }
    public void setPlayerMission(boolean standardRisk){

        MissionFactory factory = new MissionFactory(worldManagerFriend.getContinents(), worldManagerFriend.getCountryMap());

        Random random = new Random();
        for(Player player : playerOrder){
            if(standardRisk){
               player.setPlayerMission(factory.createMission(6)); //6 = mission for everyone => conquer the world
            } else {
                List<String> opponentNames = new ArrayList<>(getPlayerMap().keySet());
                opponentNames.remove(player.getPlayerName());
                factory.setOpponents(opponentNames);
                int bound = (playerOrder.size() > 2) ?  5 : 4;
                player.setPlayerMission(factory.createMission(random.nextInt(bound) + 1));
            }
        }
    }

    @Override
    public String getPlayerMission(String playerName){ return playerMap.get(playerName).getPlayerMission().getMissionText(); }

    public List<String> getCurrentPlayersCountries(){ return playerMap.get(currentPlayer.getPlayerName()).getConqueredCountryNames(); }

    public void changeCountryOwner(String newOwnerName, String previousOwnerName, String countryName){

        playerMap.get(newOwnerName).addConqueredCountry(countryName);
        playerMap.get(previousOwnerName).removeCountry(countryName);
    }
}
