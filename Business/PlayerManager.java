package Business;

import Common.Country;
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
    public boolean readyToStartGame() { return (playerMap.size() > 1); }

    @Override
    public String addPlayer(String name, String color) {

        if(!playerMap.containsKey(name) && playerMap.size() != 6 &&  allowedColors.contains(color)){
            Player newPlayer = new Player(name, color, new MissionConquerWorld((List<Country>) worldManager.getCountryMap().values()));
            playerMap.put(newPlayer.getPlayerName(), newPlayer);
            playerOrder.add(newPlayer);
            allowedColors.remove(color);
        }
        return playerMap.get(name).getPlayerMission().getMissionText();
    }

    @Override
    public void removePlayer(String name) {

        if (playerMap.containsKey(name)){
            allowedColors.add(playerMap.get(name).getPlayerColor());
            playerOrder.remove(playerMap.get(name));
            playerMap.remove(name);
        }
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
    public Map<String, Player> getPlayerMap(){ return playerMap; }

    public Player getPlayer(String playerName){ return playerMap.get(playerName); }

    @Override
    public String getPlayersInfo(){

        StringBuilder playerInfo = new StringBuilder();
        for(Player player : playerMap.values()){
            playerInfo.append("Name: ").append(player.getPlayerName()).append(", Color: ").append(player.getPlayerColor()).append("\n");
        }
        return playerInfo.toString();
    }
}
