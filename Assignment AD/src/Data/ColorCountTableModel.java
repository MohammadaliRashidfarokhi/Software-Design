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


public class ColorCountTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private HashMap<Integer, BrickColors> allColor;
    private HashMap<Integer, Integer> count;
    private Object[] rowIndex;


    private static String[] columnNames = {
            "LDD Color",
            "LDD color name",
            "Count"
    };


    /*
     * sets whole data model for table
     */
    public void setColorList(HashMap<Integer, BrickColors> ac, HashMap<Integer, Integer> count) {
        allColor = ac;
        this.count = count;
        rowIndex = allColor.keySet().toArray();
        fireTableDataChanged();
    }


    public HashMap<Integer, Integer> getSelected() {
        return count;
    }


    @Override
    public int getColumnCount() {

        return columnNames.length;
    }


    @Override
    public int getRowCount() {
        if (allColor != null)
            return allColor.size();
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
                return Color.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
        }
        return String.class;
    }


    @Override
    public String getColumnName(int col) {

        return columnNames[col];
    }


    public void setValueAt(Object value, int row, int col) {

        if (col != 2)
            return;
        else {
            count.put((Integer) rowIndex[row], (Integer) value);
        }
    }


    @Override
    public Object getValueAt(int arg0, int arg1) {
        switch (arg1) {
            case 0:
                return allColor.get(rowIndex[arg0]).color;
            case 1:
                return allColor.get(rowIndex[arg0]).lddName;
            case 2:
                return count.get(rowIndex[arg0]);
        }
        return null;
    }


}

