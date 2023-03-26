package Presentation;

import Common.Country;

import javax.swing.table.AbstractTableModel;
import java.util.Comparator;
import java.util.List;


public class CountryTable extends AbstractTableModel {
    private final List<Country> countries;
    private final String[] columnNames={"Country name", "Abbreviation", "Continent", "Owner", "Unit amount"};

    public CountryTable(List<Country> currentCountries){

        this.countries = currentCountries;
        countries.sort(Comparator.comparing(Country::getCountryName));
    }

    @Override
    public int getRowCount(){
        return countries.size();
    }
    @Override
    public int getColumnCount(){
        return columnNames.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){

        Country selectedCountry = countries.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> selectedCountry.getCountryName();
            case 1 -> selectedCountry.getAbbreviation();
            case 2 -> selectedCountry.getContinentName();
            case 3 -> selectedCountry.getArmy().getPlayerName();
            case 4 -> selectedCountry.getArmy().getUnits();
            default -> null;
        };
    }
    @Override
    public String getColumnName(int column){
        return columnNames[column];
    }
}
