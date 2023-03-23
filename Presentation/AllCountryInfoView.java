package Presentation;

import Business.IWorldManager;

import javax.swing.*;
import java.util.ArrayList;

public class AllCountryInfoView extends JFrame {

    public AllCountryInfoView(IWorldManager worldManager){

        super("Country information");
        CountryTable countryTable = new CountryTable(new ArrayList<>(worldManager.getCountryMap().values()));
        setSize(600, 400);
        JTable table = new JTable(countryTable);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        setVisible(true);
    }
}
