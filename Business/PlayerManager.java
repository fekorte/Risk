package Business;

import Common.Exceptions.ExceptionColorAlreadyExists;
import Common.Exceptions.ExceptionObjectDoesntExist;
import Common.Exceptions.ExceptionPlayerAlreadyExists;
import Common.Exceptions.ExceptionTooManyPlayer;
import Common.MissionConquerWorld;
import Common.Player;

import java.util.*;

public class PlayerManager implements IPlayerManager{
    IWorldManager worldManager;
    Map<String, Player> playerMap;
    List<Player> playerOrder;
    ArrayList<String> allowedColors;

    public PlayerManager(IWorldManager worldManager){

        this.worldManager = worldManager;
        playerMap = new HashMap<>();
        playerOrder = new ArrayList<>();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow", "Black"));
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

        Player newPlayer = new Player(name, color, new MissionConquerWorld((new ArrayList<>(worldManager.getCountryMap().values()))));
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
    public Player nextPlayersTurn(String currentPlayer) throws ExceptionObjectDoesntExist {

        if(playerMap.containsKey(currentPlayer) && playerOrder.contains(playerMap.get(currentPlayer))){
            throw new ExceptionObjectDoesntExist(currentPlayer);
        }

        int currentIndex = playerOrder.indexOf(playerMap.get(currentPlayer));
        int nextIndex = (currentIndex + 1) % playerOrder.size();
        return playerOrder.get(nextIndex);
    }

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
}
