package Presentation;

import Business.IWorldManager;

import javax.swing.*;
import java.util.ArrayList;

public class WorldTableView extends JFrame {

    public WorldTableView(IWorldManager worldManager){

        super("World information");
        WorldTable countryTable = new WorldTable(new ArrayList<>(worldManager.getTerritoryMap().values()));
        setSize(600, 400);
        JTable table = new JTable(countryTable);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        setVisible(true);
    }
}
