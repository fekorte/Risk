package Business;

import Common.Country;
import Common.Exceptions.ExceptionColorAlreadyExists;
import Common.Exceptions.ExceptionObjectDoesntExist;
import Common.Exceptions.ExceptionPlayerAlreadyExists;
import Common.Exceptions.ExceptionTooManyPlayer;
import Common.MissionConquerWorld;
import Common.Player;
import Persistence.IPersistence;

import java.io.IOException;
import java.util.*;

public class PlayerManager implements IPlayerManager, PlayerManagerFriend{
    IWorldManager worldManager;
    WorldFriend worldFriend;
    Map<String, Player> playerMap;
    List<Player> playerOrder;
    ArrayList<String> allowedColors;
    Player currentPlayer;
    boolean continuePreviousGame;
    int playerTurns;
    int round;

    public PlayerManager(IWorldManager worldManager, IPersistence persistence) throws IOException {

        this.worldManager = worldManager;
        worldFriend = (WorldFriend) worldManager;
        playerMap = persistence.fetchPlayers();
        playerOrder = new ArrayList<>();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow", "Black"));

        if(!playerMap.isEmpty()){
            playerOrder.addAll(playerMap.values());
            this.currentPlayer = playerOrder.get(0);
            continuePreviousGame = true;
        } else {
            continuePreviousGame = false;
        }

        this.playerTurns = 0;
        this.round = 0;
    }

    @Override
    public void clearPlayers(){

        playerMap.clear();
        playerOrder.clear();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow", "Black"));
    }

    @Override
    public String addPlayer(String name, String color) throws ExceptionPlayerAlreadyExists, ExceptionTooManyPlayer, ExceptionColorAlreadyExists {

        if(playerMap.containsKey(name)){
            throw new ExceptionPlayerAlreadyExists(name);
        }
        if(playerMap.size() == 6){
            throw new ExceptionTooManyPlayer();
        }
        if(!allowedColors.contains(color)){
            throw new ExceptionColorAlreadyExists(color);
        }

        Player newPlayer = new Player(name, color, new MissionConquerWorld((new ArrayList<>(worldFriend.getCountryMap().values()))), new HashMap<>());
        playerMap.put(newPlayer.getPlayerName(), newPlayer);
        playerOrder.add(newPlayer);
        allowedColors.remove(color);

        return playerMap.get(name).getPlayerMission().getMissionText();
    }

    @Override
    public void removePlayer(String name) throws ExceptionObjectDoesntExist {

        if (!playerMap.containsKey(name)){
            throw new ExceptionObjectDoesntExist(name);
        }

        allowedColors.add(playerMap.get(name).getPlayerColor());
        playerOrder.remove(playerMap.get(name));
        playerMap.remove(name);
    }

    @Override
    public boolean nextPlayersTurn(){

        if(currentPlayer.getPlayerMission().isMissionCompleted(currentPlayer.getPlayerName())){
            return false;
        }

        int currentIndex = playerOrder.indexOf(playerMap.get(currentPlayer.getPlayerName()));
        int nextIndex = (currentIndex + 1) % playerOrder.size();
        this.currentPlayer = playerOrder.get(nextIndex);

        //Rotate the list, next player is on position one
        int startIndex = playerOrder.indexOf(playerMap.get(currentPlayer.getPlayerName()));
        Collections.rotate(playerOrder, -startIndex);
        playerTurns++;

        if(playerTurns == playerOrder.size()){
             round++;
             playerTurns = 0;
        }
        return true;
    }
    @Override
    public String getAllowedColors(){ return allowedColors.toString(); }

    @Override
    public Map<String, Player> getPlayerMap(){ return playerMap; }

    @Override
    public String getPlayersInfo(){

        StringBuilder playerInfo = new StringBuilder();
        for(Player player : playerMap.values()){
            playerInfo.append("Name: ").append(player.getPlayerName()).append(", Color: ").append(player.getPlayerColor()).append("\n");
        }
        return playerInfo.toString();
    }
    @Override
    public String getAllCountriesInfoPlayer(String playerName){

        return playerMap.get(playerName).conqueredCountriesToString();
    }
    @Override
    public boolean continuePreviousGame(){ return continuePreviousGame; }
    @Override
    public void setCurrentPlayer(String currentPlayerName){ this.currentPlayer = playerMap.get(currentPlayerName); }

    @Override
    public String getCurrentPlayerName() { return currentPlayer.getPlayerName(); }

    @Override
    public int getPlayerNumber() { return playerMap.size(); }
    @Override
    public int getRound(){ return round; }
}
