package Business;

import Common.MissionConquerWorld;
import Common.Player;

import java.util.*;

public class PlayerManager implements IPlayerManager{

    Map<String, Player> playerMap;
    List<Player> playerOrder;
    ArrayList<String> allowedColors;


    public PlayerManager(){

        playerMap = new HashMap<>();
        playerOrder = new ArrayList<>();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow"));
    }

    @Override
    public String addPlayer(String name, String color) {

        if(!playerMap.containsKey(name) && playerMap.size() != 6 &&  allowedColors.contains(color)){
            Player newPlayer = new Player(name, color, new MissionConquerWorld());
            playerMap.put(newPlayer.getPlayerName(), newPlayer);
            playerOrder.add(newPlayer);
            allowedColors.remove(color);
        }
        return playerMap.get(name).getPlayerMission().getMissionText();
    }

    @Override
    public boolean removePlayer(String name) {

        if (playerMap.containsKey(name)){
            allowedColors.add(playerMap.get(name).getPlayerColor());
            playerOrder.remove(playerMap.get(name));
            playerMap.remove(name);
            return true;
        }
        return false;
    }


    @Override
    public Player nextPlayersTurn(String currentPlayer) {

        if(playerMap.containsKey(currentPlayer) && playerOrder.contains(playerMap.get(currentPlayer))){
            int currentIndex = playerOrder.indexOf(playerMap.get(currentPlayer));
            int nextIndex = (currentIndex + 1) % playerOrder.size();
            return playerOrder.get(nextIndex);
        }
        return null;
    }

    public String getAllowedColors(){ return allowedColors.toString(); }

    @Override
    public String getPlayersInfo(){

        StringBuilder playerInfo = new StringBuilder();
        for(Player player : playerMap.values()){
            playerInfo.append("Name: ").append(player.getPlayerName()).append(", Color: ").append(player.getPlayerColor()).append("\n");
        }
        return playerInfo.toString();
    }

    public Map<String, Player> getPlayerMap(){ return playerMap; }

    @Override
    public void clearPlayers(){

        playerMap.clear();
        playerOrder.clear();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow"));
    }

    @Override
    public boolean readyToStartGame() { return (playerMap.size() > 1); }
}
