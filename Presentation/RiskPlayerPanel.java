package Presentation;

import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class RiskPlayerPanel extends JPanel {

    private final IPlayerManager playerManager;
    private final Map<String, DefaultListModel<String>> playerListModels; //key is player name
    private final JPanel playerListPanel;
    private final Map<String, Color> playerColorMap;
    private int playerNumber;

    public RiskPlayerPanel(IPlayerManager playerManager, int playerNumber){

        this.playerManager = playerManager;
        this.playerNumber = playerNumber;

        playerListModels = new HashMap<>();

        playerColorMap = new HashMap<>();
        addColorsToMap();

        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(playerListPanel);
    }

    private void addColorsToMap(){

        playerColorMap.put("Yellow", Color.YELLOW);
        playerColorMap.put("Red", Color.RED);
        playerColorMap.put("Green", Color.GREEN);
        playerColorMap.put("White", Color.WHITE);
        playerColorMap.put("Pink", Color.PINK);
        playerColorMap.put("Blue", Color.CYAN);
    }

    public void addPlayerCountryList(String playerName) {

        DefaultListModel<String> model = new DefaultListModel<>();
        List<String> territoryInfos = playerManager.getAllTerritoryInfosPlayer(playerName);
        Collections.sort(territoryInfos);
        for (String territoryInfo : territoryInfos) {
            model.addElement(territoryInfo);
        }
        JList<String> list = new JList<>(model);
        list.setFont(new Font("Arial", Font.PLAIN, 20)); // set font size for list items
        Color color = playerColorMap.get(playerManager.getPlayerColor(playerName));
        JLabel label = new JLabel(playerName);
        label.setFont(list.getFont()); // set font size for player name
        label.setOpaque(true);
        label.setBackground(color);

        JScrollPane scrollPane = new JScrollPane(list);

        switch(playerNumber){
            case(2) -> scrollPane.getViewport().setPreferredSize(new Dimension(320, 570));
            case(3),(4) -> scrollPane.getViewport().setPreferredSize(new Dimension(320, 310));
            case(5),(6) -> scrollPane.getViewport().setPreferredSize(new Dimension(320, 170));
        }

        playerListPanel.add(label);
        playerListPanel.add(scrollPane);
        playerListModels.put(playerName, model);
    }

    public void updateList(String playerName) {

        DefaultListModel<String> model = playerListModels.get(playerName);

        model.clear();
        List<String> territoryInfos = playerManager.getAllTerritoryInfosPlayer(playerName);
        Collections.sort(territoryInfos);
        for (String territoryInfo : territoryInfos) {
            model.addElement(territoryInfo);
        }
    }
}