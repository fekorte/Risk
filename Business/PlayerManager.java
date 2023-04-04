package Business;

import Common.Exceptions.*;
import Common.MissionConquerTerritories;
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
    private String currentPlayerName;
    private boolean continuePreviousGame;
    private int playerTurns;
    private int round;

    public PlayerManager(IWorldManager worldManager) throws IOException {

        this.worldManagerFriend = (WorldManager) worldManager;
        this.persistence = worldManagerFriend.getPersistence();
        this.playerOrder = persistence.fetchGameStatePlayers();
        this.playerMap = new HashMap<>();
        this.allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow", "Pink"));

        initializeGameContinuation();
    }

    private void initializeGameContinuation() throws IOException {

        if(!playerOrder.isEmpty()){
            for (Player player : playerOrder) {
                playerMap.put(player.getPlayerName(), player);

                if(player.getPlayerMission().getMissionNumber() == 4){
                    MissionConquerTerritories missionConquerTerritories = (MissionConquerTerritories) player.getPlayerMission();
                    missionConquerTerritories.setTerritoryMap(worldManagerFriend.getTerritoryMap());
                }
            }

            this.currentPlayerName = playerOrder.get(0).getPlayerName();
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
    @Override
    public boolean getContinuePreviousGame(){ return continuePreviousGame; }
    public Map<String, Player> getPlayerMap(){ return playerMap; }
    @Override
    public String getPlayerColor(String playerName){ return playerMap.get(playerName).getPlayerColor(); }
    @Override
    public List<String> getAllowedColors(){ return allowedColors; }
    @Override
    public String getCurrentPlayerName() { return currentPlayerName; }
    @Override
    public List<String> getPlayerNames(){ return new ArrayList<>(playerMap.keySet()); }
    @Override
    public int getPlayerAmount() { return playerMap.size(); }
    @Override
    public int getRound(){ return round; }
    @Override
    public String getPlayerMission(String playerName){ return playerMap.get(playerName).getPlayerMission().getMissionDescription(); }
    public List<String> getCurrentPlayersCountries(){ return playerMap.get(currentPlayerName).getConqueredTerritoryNames(); }
    @Override
    public List<String> getAllTerritoryInfosPlayer(String playerName){

        List<String> territoryInfos = new ArrayList<>();
        for(String territory : playerMap.get(playerName).getConqueredTerritoryNames()){
            territoryInfos.add(territory + ": " + worldManagerFriend.getUnitAmountOfTerritory(territory));
        }
        return territoryInfos;
    }
    @Override
    public String getPlayersInfo(){

        StringBuilder playerInfo = new StringBuilder();
        for(Player player : playerMap.values()){
            playerInfo.append("| ").append(player.getPlayerName()).append(": ").append(player.getPlayerColor()).append(" |");
        }
        return playerInfo.toString();
    }
    public void setCurrentPlayer(String currentPlayerName){

        Collections.rotate(playerOrder, playerOrder.indexOf(playerMap.get(currentPlayerName)));
        this.currentPlayerName = currentPlayerName;
    }
    public void setAllPlayerMissions(boolean standardRisk){

        MissionFactory factory = new MissionFactory(worldManagerFriend.getContinentMap(), worldManagerFriend.getTerritoryMap());

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
    public String isAnyMissionCompleted(){

        for(Player player : playerOrder){
            if(player.getPlayerMission().getMissionNumber() != 5){
                if(player.getPlayerMission().isMissionCompleted(player.getConqueredTerritoryNames())){
                    return player.getPlayerName();
                }
            } else {
                MissionDefeatOpponent missionDefeatOpponent = (MissionDefeatOpponent) player.getPlayerMission();
                String opponentName = missionDefeatOpponent.getOpponentName();
                if(player.getPlayerMission().isMissionCompleted(playerMap.get(opponentName).getConqueredTerritoryNames())){
                    return player.getPlayerName();
                }
            }
        }
        return null;
    }

    @Override
    public boolean isPlayerDefeated(String playerName){ return playerMap.get(playerName).getConqueredTerritoryNames().isEmpty(); }

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
    public void nextPlayersTurn(){

        int currentIndex = playerOrder.indexOf(playerMap.get(currentPlayerName));
        int nextIndex = (currentIndex + 1) % playerOrder.size();

        this.currentPlayerName = playerOrder.get(nextIndex).getPlayerName();

        Collections.rotate(playerOrder, -nextIndex);
        playerTurns++;

        if(playerTurns == playerOrder.size()){
            round++;
            playerTurns = 0;
        }
    }

    public void changeTerritoryOwner(String newOwnerName, String previousOwnerName, String territoryName){

        playerMap.get(newOwnerName).addConqueredTerritory(territoryName);
        playerMap.get(previousOwnerName).removeTerritory(territoryName);
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
}
