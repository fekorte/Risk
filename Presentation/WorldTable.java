package Presentation;

import Common.Territory;

import javax.swing.table.AbstractTableModel;
import java.util.Comparator;
import java.util.List;


public class WorldTable extends AbstractTableModel {
    private final List<Territory> territories;
    private final String[] columnNames = {"Territory name", "Abbreviation", "Continent", "Owner", "Unit amount"};

    public WorldTable(List<Territory> currentTerritories){

        this.territories = currentTerritories;
        territories.sort(Comparator.comparing(Territory::getTerritoryName));
    }

    @Override
    public int getRowCount(){
        return territories.size();
    }
    @Override
    public int getColumnCount(){
        return columnNames.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){

        Territory selectedTerritory = territories.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> selectedTerritory.getTerritoryName();
            case 1 -> selectedTerritory.getAbbreviation();
            case 2 -> selectedTerritory.getContinentName();
            case 3 -> selectedTerritory.getArmy().getPlayerName();
            case 4 -> selectedTerritory.getArmy().getUnits();
            default -> null;
        };
    }
    @Override
    public String getColumnName(int column){
        return columnNames[column];
    }
}
