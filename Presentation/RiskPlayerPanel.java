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
    public RiskPlayerPanel(IPlayerManager playerManager){

        this.playerManager = playerManager;
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
        list.setPreferredSize(new Dimension(150, 600));

        Color color = playerColorMap.get(playerManager.getPlayerColor(playerName));
        JLabel label = new JLabel(playerName);
        label.setOpaque(true);
        label.setBackground(color);

        JScrollPane scrollPane = new JScrollPane(list);
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