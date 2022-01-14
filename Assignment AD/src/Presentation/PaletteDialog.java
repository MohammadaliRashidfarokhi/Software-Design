package Presentation;/*
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


import Application.CellColorRenderer;
import Data.BrickColors;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class PaletteDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -1703526546895376328L;
    private Data.PaletteTableModel paletteTM;
    private JTable colorTable;
    private JScrollPane scrollPane;
    private JButton okButton;
    private int userChoice = JOptionPane.CANCEL_OPTION;
    private JButton cancelButton;


    public PaletteDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        createDialog();
    }


    public PaletteDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        createDialog();
    }


    private void createDialog() {

        // really create the dialog
        //setPreferredSize(new Dimension(800,500));
        setLocationByPlatform(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(2, 2));
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout(2, 2));
        paletteTM = new Data.PaletteTableModel();
        colorTable = new JTable(paletteTM);
        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        scrollPane.setViewportView(colorTable);
        contentPane.add(pane, BorderLayout.CENTER);
        pane.add(scrollPane, BorderLayout.CENTER);
        paletteTM.setColorList(BrickColors.getAllColors());
        colorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        colorTable.setAutoCreateRowSorter(true);
        colorTable.setRowHeight(20);
        TableRowSorter<Data.PaletteTableModel> sorterFilter = new TableRowSorter<Data.PaletteTableModel>(paletteTM);
        colorTable.setRowSorter(sorterFilter);
        TableColumnModel tcl = colorTable.getColumnModel();
        tcl.getColumn(0).setPreferredWidth(50);
        tcl.getColumn(1).setPreferredWidth(200);
        tcl.getColumn(2).setPreferredWidth(30);
        tcl.getColumn(0).setCellRenderer(new CellColorRenderer(false));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        okButton = new JButton("OK");
        buttonPane.add(okButton);
        okButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);

        cancelButton = new JButton("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(this);


        pack();
    }


    public HashMap<Integer, Boolean> getSelected() {

        return ((Data.PaletteTableModel) colorTable.getModel()).getSelected();
    }


    @Override
    public void actionPerformed(ActionEvent ev) {

        if (ev.getSource() == okButton) {
            setVisible(false);
            userChoice = JOptionPane.OK_OPTION;
        } else if (ev.getSource() == cancelButton) {
            setVisible(false);
            userChoice = JOptionPane.CANCEL_OPTION;
        }

    }


    public int getResponse() {
        return userChoice;
    }


}
