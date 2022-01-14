package Presentation;/*
	Copyright 2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickMosaic

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
import Application.DitheringTask;
import Application.ImageView;
import Data.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;


public class ColorReduceDialog extends JDialog implements ActionListener {


    private static final long serialVersionUID = 8933288964831247818L;
    private BufferedImage image, convertedImage = null;
    public PaletteTableModel paletteTM;
    private JTable colorTable;
    private JButton okButton;
    private JButton cancelButton;
    private int userChoice = JOptionPane.CANCEL_OPTION;
    private ImageView imagebox;
    private ImageView convertedbox;
    private JButton convertButton;
    private ColorCountTableModel colorCountTM;
    private JTable colorCountTable;
    private JLabel totalCount;
    private JComboBox colorMode;


    public ColorReduceDialog(Frame owner, String title, boolean modal, BufferedImage img) {
        super(owner, title, modal);

        image = img;
        createDialog();
    }


    private void createDialog() {


        // really create the dialog
        setLocationByPlatform(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(2, 2));

        // center image
        JPanel imagePane = new JPanel();
        imagePane.setLayout(new GridBagLayout());
        contentPane.add(imagePane, BorderLayout.CENTER);

        // controls and buttons
        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(new GridBagLayout());
        contentPane.add(controlsPane, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.ipady = 2;
        gbc.ipadx = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        //////////////////////////
        // setup controls pane

        paletteTM = new PaletteTableModel();
        colorTable = new JTable(paletteTM);
        paletteTM.setColorList(BrickColors.getAllColors());
        colorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        colorTable.setAutoCreateRowSorter(true);
        colorTable.setRowHeight(20);
        TableRowSorter<PaletteTableModel> sorterFilter = new TableRowSorter<PaletteTableModel>(paletteTM);
        colorTable.setRowSorter(sorterFilter);
        TableColumnModel tcl = colorTable.getColumnModel();
        tcl.getColumn(0).setPreferredWidth(15);
        tcl.getColumn(1).setPreferredWidth(120);
        tcl.getColumn(2).setPreferredWidth(15);
        tcl.getColumn(0).setCellRenderer(new CellColorRenderer(false));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(250, 250));
        scrollPane.setViewportView(colorTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select colors"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        controlsPane.add(scrollPane, gbc);


        gbc.gridwidth = 1;
        gbc.gridy += 1;
        gbc.gridx = 0;
        convertButton = new JButton("Convert!");
        convertButton.addActionListener(this);
        controlsPane.add(convertButton, gbc);

        gbc.gridx = 1;
        colorMode = new JComboBox();
        colorMode.addItem("Plain");
        colorMode.addItem("Dither");
        colorMode.addItem("Floyd-Steinberg");
        colorMode.setSelectedIndex(2);
        controlsPane.add(colorMode, gbc);

        gbc.gridwidth = 2;
        gbc.gridy += 1;
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        colorCountTM = new ColorCountTableModel();
        colorCountTable = new JTable(colorCountTM);
        colorCountTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        colorCountTable.setRowHeight(20);
        tcl = colorCountTable.getColumnModel();
        tcl.getColumn(0).setPreferredWidth(15);
        tcl.getColumn(1).setPreferredWidth(120);
        tcl.getColumn(2).setPreferredWidth(20);
        tcl.getColumn(0).setCellRenderer(new CellColorRenderer(false));
        createDialogHelper();
        // dialog standard buttons
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

    public void createDialogHelper() {

        // center image
        JPanel imagePane = new JPanel();
        JPanel controlsPane = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();

        JScrollPane scc = new JScrollPane();
        scc.setBorder(BorderFactory.createTitledBorder("Color Count"));
        scc.setPreferredSize(new Dimension(250, 250));
        scc.setViewportView(colorCountTable);
        controlsPane.add(scc, gbc);

        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridy += 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        controlsPane.add(new JLabel("Total:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        totalCount = new JLabel("-");
        controlsPane.add(totalCount, gbc);

        //////////////////////////////
        // setup image preview pane
        gbc.weightx = 0.01;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        imagebox = new ImageView(image, 350, 250);
        imagebox.setBorder(BorderFactory.createTitledBorder("Original"));
        imagePane.add(imagebox, gbc);

        gbc.weightx = 0.01;
        gbc.weighty = 0.01;
        gbc.gridy = 1;
        imagePane.add(new JLabel(new ImageIcon(ABrickmosaic.class.getResource("images/convert.png"))), gbc);

        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        try {
            convertedbox = new ImageView(ImageIO.read(ABrickmosaic.class.getResource("images/empty-image.png")), 350, 250);
        } catch (IOException e) {
            e.printStackTrace();
        }
        convertedbox.setBorder(BorderFactory.createTitledBorder("Converted"));
        imagePane.add(convertedbox, gbc);
    }


    public int getResponse() {
        return userChoice;
    }


    public BufferedImage getColorReducedImage() {

        return convertedImage;
    }


    @SuppressWarnings("boxing")
    public static BufferedImage doPaletteImage(HashMap<Integer, Boolean> sel) {

        int i;

        i = 0;
        for (int j : sel.keySet()) {
            if (sel.get(j)) {
                i++;
            }
        }
        BufferedImage img = new BufferedImage(20, 20 * i, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        i = 0;
        for (int j : sel.keySet()) {
            if (sel.get(j)) {
                g.setColor(BrickColors.getLddColor(j).color);
                g.fillRect(0, i * 20, 20, 20);
                i++;
            }
        }
        return img;
    }


    @SuppressWarnings("boxing")
    @Override
    public void actionPerformed(ActionEvent ev) {

        if (ev.getSource() == okButton) {
            setVisible(false);
            userChoice = JOptionPane.OK_OPTION;
        } else if (ev.getSource() == cancelButton) {
            setVisible(false);
            userChoice = JOptionPane.CANCEL_OPTION;
        } else if (ev.getSource() == convertButton) {
            // do image conversion
            HashMap<Integer, BrickColors> palt = new HashMap<Integer, BrickColors>();
            HashMap<Integer, Boolean> sel = paletteTM.getSelected();
            int i = 0;
            for (int j : sel.keySet()) {
                if (sel.get(j)) {
                    palt.put(j, BrickColors.getLddColor(j));
                    i++;
                }
            }
            if (i == 0)
                return;
            convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            convertedImage.setData(image.getData());
            BusyDialog busyDialog = new BusyDialog((JDialog) this, "Working...", true, true, null);
            busyDialog.setLocationRelativeTo(this);
            DitheringTask task = new DitheringTask(convertedImage, palt, colorMode.getSelectedIndex());
            busyDialog.setTask(task);
            busyDialog.setMsg("Reducing colors...");
            Timer timer = new Timer(200, busyDialog);
            task.execute();
            timer.start();
            busyDialog.setVisible(true);
            // after completing task return here
            timer.stop();
            busyDialog.dispose();
            convertedbox.setImage(convertedImage);
            // count colors and bricks
            Mosaic m = new Mosaic(convertedImage);
            m.check();
            m.colorCount();
            // display data
            HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
            HashMap<Integer, BrickColors> colors = new HashMap<Integer, BrickColors>();
            int total = 0;
            for (Color c : m.getOriginalColors().keySet()) {
                BrickColors b = BrickColors.getNearestColor(c);
                colors.put(b.ldd, b);
                count.put(b.ldd, m.getOriginalColors().get(c));
                total += m.getOriginalColors().get(c);
            }
            colorCountTM.setColorList(colors, count);
            totalCount.setText(Integer.toString(total));
        }

    }


}
