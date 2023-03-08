package Business;

import Common.Player;

import java.util.*;

public class PlayerManager implements IPlayerManager{

    Map<String, Player> playerMap;
    List<String> playerOrder;
    ArrayList<String> allowedColors;


    public PlayerManager(){

        playerMap = new HashMap<>();
        playerOrder = new ArrayList<>();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow"));
    }

    @Override
    public boolean addPlayer(String name, String color) {

        if(!playerMap.containsKey(name) && playerMap.size() != 6 &&  allowedColors.contains(color)){
            playerMap.put(name, new Player(name, color));
            playerOrder.add(name);
            allowedColors.remove(color);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlayer(String name) {

        if (playerMap.containsKey(name)){
            allowedColors.add(playerMap.get(name).getPlayerColor());
            playerMap.remove(name);
            playerOrder.remove(name);
            return true;
        }
        return false;
    }


    @Override
    public String nextPlayersTurn(String currentPlayer) {

        if(playerOrder.contains(currentPlayer)){
            int currentIndex = playerOrder.indexOf(currentPlayer);
            int nextIndex = (currentIndex + 1) % playerOrder.size();
            return playerOrder.get(nextIndex);
        }
        return null;
    }

    public String getAllowedColors(){

        return allowedColors.toString();
    }

    @Override
    public String getPlayersInfo(){

        StringBuilder playerInfo = new StringBuilder();
        for(Player player : playerMap.values()){
            playerInfo.append("Name: ").append(player.getPlayerName()).append(", Color: ").append(player.getPlayerColor()).append("\n");
        }
        return playerInfo.toString();
    }

    @Override
    public void clearPlayers(){

        playerMap.clear();
        playerOrder.clear();
        allowedColors  = new ArrayList<>(Arrays.asList("Red", "Blue", "Green", "White", "Yellow"));
    }

    @Override
    public boolean readyToStartGame() {

        return (playerMap.size() > 1);
    }
}
