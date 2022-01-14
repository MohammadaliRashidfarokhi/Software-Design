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
import Application.LddColorRenderer;
import Data.BrickColors;
import Data.MatchTableModel;
import Data.Mosaic;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class ColorMatchDialog extends JDialog implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = -1703526546895376328L;
    public MatchTableModel matchTM;
    private JTable colorTable;
    private JScrollPane scrollPane;
    private JButton okButton;
    //private JButton cancelButton;
    private HashMap<Color, Integer> colorMatch;
    private HashMap<Color, Integer> colorCount;
    private JComboBox lddColors;
    private JButton autoButton;
    private JButton setButton;


    public ColorMatchDialog(Frame owner, String title, boolean modal,
                            Mosaic m) {
        super(owner, title, modal);
        colorMatch = m.getColorMatch();
        colorCount = m.getOriginalColors();
        createDialog();
    }


    public ColorMatchDialog(Dialog owner, String title, boolean modal,
                            Mosaic m) {
        super(owner, title, modal);
        createDialog();
    }


    @SuppressWarnings("boxing")
    private void createDialog() {

        // really create the dialog
        //setPreferredSize(new Dimension(800,500));
        setLocationByPlatform(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(2, 2));
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout(2, 2));
        matchTM = new Data.MatchTableModel();
        colorTable = new JTable(matchTM);
        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        scrollPane.setViewportView(colorTable);
        contentPane.add(pane, BorderLayout.CENTER);
        pane.add(scrollPane, BorderLayout.CENTER);
        matchTM.setMatchList(colorMatch, BrickColors.getAllColors(), colorCount);
        colorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        colorTable.setAutoCreateRowSorter(true);
        colorTable.setRowHeight(25);
        colorTable.getSelectionModel().addListSelectionListener(this);
        TableRowSorter<Data.MatchTableModel> sorterFilter = new TableRowSorter<Data.MatchTableModel>(matchTM);
        colorTable.setRowSorter(sorterFilter);
        TableColumnModel tcl = colorTable.getColumnModel();
        tcl.getColumn(0).setPreferredWidth(50);
        tcl.getColumn(1).setPreferredWidth(50);
        tcl.getColumn(2).setPreferredWidth(50);
        tcl.getColumn(3).setPreferredWidth(200);
        tcl.getColumn(0).setCellRenderer(new CellColorRenderer(false));
        tcl.getColumn(2).setCellRenderer(new CellColorRenderer(false));
        JPanel editPane = new JPanel();
        editPane.setBorder(BorderFactory.createTitledBorder("Edit matching color"));
        editPane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.ipady = 2;
        gbc.ipadx = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        lddColors = new JComboBox();
        LddColorRenderer renderer = new LddColorRenderer();
        lddColors.setRenderer(renderer);
        for (int i : BrickColors.getAllColors().keySet()) {
            lddColors.addItem(i);
        }
        lddColors.setSelectedIndex(0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        editPane.add(new JLabel("LDD color: ", SwingConstants.RIGHT));
        gbc.gridx = 1;
        editPane.add(lddColors, gbc);
        lddColors.addActionListener(this);

        setButton = new JButton("Set");
        setButton.addActionListener(this);

        gbc.gridx = 2;
        editPane.add(setButton, gbc);


        autoButton = new JButton("Reset to auto");
        autoButton.addActionListener(this);

        gbc.gridx = 3;
        editPane.add(autoButton, gbc);

        pane.add(editPane, BorderLayout.SOUTH);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        okButton = new JButton("OK");
        buttonPane.add(okButton);
        okButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);

        pack();
    }


    @SuppressWarnings("boxing")
    @Override
    public void actionPerformed(ActionEvent ev) {

        if (ev.getSource() == okButton) {
            setVisible(false);
        } else if (ev.getSource() == setButton) {
            if (colorTable.getSelectedRow() < 0)
                return;
            Color c = matchTM.getColor(colorTable.convertRowIndexToModel(colorTable.getSelectedRow()));
            colorMatch.put(c, (Integer) lddColors.getSelectedItem());
            Data.MatchTableModel mt = (Data.MatchTableModel) colorTable.getModel();
            mt.fireTableDataChanged();
        } else if (ev.getSource() == autoButton) {
            if (colorTable.getSelectedRow() < 0)
                return;
            Color c = matchTM.getColor(colorTable.convertRowIndexToModel(colorTable.getSelectedRow()));
            colorMatch.put(c, BrickColors.getNearestColor(c).ldd);
            Data.MatchTableModel mt = (Data.MatchTableModel) colorTable.getModel();
            mt.fireTableDataChanged();
        }
    }


    @SuppressWarnings("boxing")
    @Override
    public void valueChanged(ListSelectionEvent e) {

        if (e.getSource() == colorTable.getSelectionModel()) {
            if (e.getValueIsAdjusting())
                return;
            if (colorTable.getSelectedRow() < 0)
                return;
            lddColors.setSelectedItem(matchTM.getLddColor(colorTable.convertRowIndexToModel(colorTable.getSelectedRow())));
        }

    }


}
