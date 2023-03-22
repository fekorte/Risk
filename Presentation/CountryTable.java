package Presentation;

import Common.Country;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CountryTable extends AbstractTableModel {

    private List<Country> countries;

    private String[] columnNames={"Country name", "Abbreviation", "Continent", "Owner", "Unit amount"};

    public CountryTable(List<Country> currentCountries){

        this.countries = currentCountries;
        Collections.sort(countries, Comparator.comparing(Country::getCountryName));
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
        switch(columnIndex){
            case 0:
                return selectedCountry.getCountryName();
            case 1:
                return selectedCountry.getAbbreviation();
            case 2:
                return selectedCountry.getContinentName();
            case 3:
                return selectedCountry.getArmy().getPlayerName();
            case 4:
                return selectedCountry.getArmy().getUnits();
            default:
                return null;
        }
    }
    @Override
    public String getColumnName(int column){
        return columnNames[column];
    }
}
