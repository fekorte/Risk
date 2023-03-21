package Presentation;

import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RiskPlayerPanel extends JPanel {

    private final IPlayerManager playerManager;
    private final Map<String, DefaultListModel<String>> listModels; //key is player name
    private final Map<String, Color> colorMap;

    public RiskPlayerPanel(IPlayerManager playerManager){

        this.playerManager = playerManager;
        listModels = new HashMap<>();

        colorMap = new HashMap<>();
        colorMap.put("Yellow", Color.YELLOW);
        colorMap.put("Red", Color.RED);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("White", Color.WHITE);
        colorMap.put("Black", Color.BLACK);
        colorMap.put("Blue", Color.BLUE);
    }

    public void addPlayerList(String playerName) {

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String countryInfo : playerManager.getAllCountriesInfoPlayer(playerName)) {
            model.addElement(countryInfo);
        }
        JList<String> list = new JList<>(model);

        Color color = colorMap.get(playerManager.getPlayerColorCode(playerName));
        JLabel label = new JLabel(playerName);
        label.setOpaque(true);
        label.setBackground(color);

        JScrollPane scrollPane = new JScrollPane(list);
        add(label);
        add(scrollPane);
        listModels.put(playerName, model);
    }


    public void updateList(String playerName) {

        DefaultListModel<String> model = listModels.get(playerName);

        model.clear();
        for (String countryInfo : playerManager.getAllCountriesInfoPlayer(playerName)) {
            model.addElement(countryInfo);
        }
    }
}
