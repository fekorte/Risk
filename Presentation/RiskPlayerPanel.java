package Presentation;

import Business.IPlayerManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RiskPlayerPanel extends JPanel {

    private final IPlayerManager playerManager;
    private final Map<String, DefaultListModel<String>> listModels; //key is player name
    private final JPanel listPanel;
    private final Map<String, Color> colorMap;

    public RiskPlayerPanel(IPlayerManager playerManager){

        this.playerManager = playerManager;
        listModels = new HashMap<>();

        colorMap = new HashMap<>();
        colorMap.put("Yellow", Color.YELLOW);
        colorMap.put("Red", Color.RED);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("White", Color.WHITE);
        colorMap.put("Pink", Color.PINK);
        colorMap.put("Blue", Color.BLUE);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(listPanel);
    }

    public void addPlayerCountryList(String playerName) {

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String countryInfo : playerManager.getAllCountriesInfoPlayer(playerName)) {
            model.addElement(countryInfo);
        }
        JList<String> list = new JList<>(model);
        list.setPreferredSize(new Dimension(150, 600));

        Color color = colorMap.get(playerManager.getPlayerColorCode(playerName));
        JLabel label = new JLabel(playerName);
        label.setOpaque(true);
        label.setBackground(color);

        JScrollPane scrollPane = new JScrollPane(list);
        listPanel.add(label);
        listPanel.add(scrollPane);
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