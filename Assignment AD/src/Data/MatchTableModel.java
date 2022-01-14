package Data;/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickMosaic.

	BrickMosaic is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickMosaic is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickMosaic.  If not, see <http://www.gnu.org/licenses/>.

*/


import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;


public class MatchTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    protected HashMap<Color, Integer> colorMatch;
    private HashMap<Integer, BrickColors> allColor;
    private HashMap<Color, Integer> colorCount;
    private Object[] rowIndex;


    private static String[] columnNames = {
            "Original color",
            "Bricks",
            "LDD color",
            "LDD color name"
    };


    /*
     * sets whole data model for table
     */
    public void setMatchList(HashMap<Color, Integer> cm, HashMap<Integer, BrickColors> ac,
                             HashMap<Color, Integer> cc) {
        colorMatch = cm;
        allColor = ac;
        colorCount = cc;
        rowIndex = colorCount.keySet().toArray();
        fireTableDataChanged();
    }


    public HashMap<Color, Integer> getMatch() {
        return colorMatch;
    }


    @Override
    public int getColumnCount() {

        return columnNames.length;
    }


    @Override
    public int getRowCount() {
        if (colorCount != null)
            return colorCount.size();
        else
            return 0;
    }

    @Override
    public boolean isCellEditable(int row, int col) {

        return false;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class getColumnClass(int c) {

        switch (c) {
            case 0:
            case 2:
                return Color.class;
            case 1:
                return Integer.class;
            case 3:
                return String.class;
        }
        return String.class;
    }


    @Override
    public String getColumnName(int col) {

        return columnNames[col];
    }


    public void setValueAt(Object value, int row, int col) {

        return;
    }


    public Color getColor(int index) {

        return (Color) rowIndex[index];
    }


    public int getLddColor(int index) {

        return allColor.get(colorMatch.get(rowIndex[index])).ldd;
    }


    @Override
    public Object getValueAt(int arg0, int arg1) {
        switch (arg1) {
            case 0:
                return rowIndex[arg0];
            case 1:
                return colorCount.get(rowIndex[arg0]);
            case 2:
                return allColor.get(colorMatch.get(rowIndex[arg0])).color;
            case 3:
                return allColor.get(colorMatch.get(rowIndex[arg0])).lddName;
        }
        return null;
    }


}

