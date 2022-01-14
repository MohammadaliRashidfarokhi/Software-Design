package Presentation;/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils.

	BrickUtils is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickUtils is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickUtils.  If not, see <http://www.gnu.org/licenses/>.

*/


import Data.BrickColors;
import Data.Mosaic;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import data.*;


/*
 * Write down mosaic instructions in a OpenDocument Text file
 * @see javax.swing.SwingWorker
 */
public class DocWriterTask extends SwingWorker<Integer, Void> {

    private File fileOdt;
    private Mosaic mosaic;

    /*
     * @param blxml an XML from pyBrickUtils
     *
     */
    public DocWriterTask(File f, Mosaic m) {

        fileOdt = f;
        mosaic = m;
    }


    @SuppressWarnings("boxing")
    @Override
    protected Integer doInBackground() throws Exception {

        int xi, cx, yi, cy, worked = 0;
        HashMap<BrickColors, Integer> totalColor;
        TextDocument mosaicSheets = null;
        Table colorTable = null, instrTable = null;
        HashMap<BrickColors, Integer> tileColorCount;

        totalColor = new HashMap<BrickColors, Integer>();
        try {
            mosaicSheets = TextDocument.loadDocument("mosaic-template.odt");
        } catch (Exception e) {
            try {
                mosaicSheets = TextDocument.newTextDocument();
            } catch (Exception e1) {
                e1.printStackTrace();
                throw new FileNotFoundException("Unable to find a template. Also unable to create a new plain document.");
            }
        }

        // create styles
        Font coordsFont10 = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 10.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font coordsFont8 = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 8.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font theadFont = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 8.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font tcolorFont = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.REGULAR, 10.0, org.odftoolkit.odfdom.type.Color.BLACK);

        xi = 0;
        cx = 0;
        setProgress(0);
        while (xi <= mosaic.getSizeX() - mosaic.getTileX()) {
            yi = mosaic.getSizeY();
            cy = 0;
            while (yi >= mosaic.getTileY()) {
                worked++;
                BufferedImage tile = mosaic.getImage().getSubimage(xi, yi - mosaic.getTileY(), mosaic.getTileX(), mosaic.getTileY());
                // count colors for pixels
                tileColorCount = new HashMap<BrickColors, Integer>();

                for (int x = 0; x < mosaic.getTileX(); x++) {
                    for (int y = 0; y < mosaic.getTileY(); y++) {
                        Color c = new Color(tile.getRGB(x, y), true);
                        BrickColors bc = BrickColors.getLddColor(mosaic.getColorMatch().get(c));
                        if (tileColorCount.containsKey(bc)) {
                            // color already seen
                            int i = tileColorCount.get(bc);
                            i++;
                            tileColorCount.put(bc, i);
                        } else {
                            // first seen
                            tileColorCount.put(bc, 1);
                        }
                    }
                }
                // done: modify for more than 26 tiles in X:..., Y, Z, AA, AB, AC, ...
                doInbackgroundHelper3();
                //System.out.println(tileName);
                // updates total color counts
                for (BrickColors c : tileColorCount.keySet()) {
                    int i = tileColorCount.get(c);
                    if (totalColor.containsKey(c)) {
                        // color already seen
                        i += totalColor.get(c);
                        totalColor.put(c, i);
                    } else {
                        // first seen
                        totalColor.put(c, i);
                    }
                }
                // writing tile building instructions
                doInBackgroundHelper2();
            }
            mosaicSheets.addParagraph(" ");
            String tileName = null;
            // brick placing instruction table
            instrTable = Table.newTable(mosaicSheets, mosaic.getTileY() + 1, mosaic.getTileX() + 1, 0, 0);
            instrTable.setTableName("Placement" + tileName);
            for (int i = 1; i <= mosaic.getTileX(); i++) {
                Cell c = instrTable.getCellByPosition(i, 0);
                c.addParagraph(Integer.toString(i));
                if (mosaic.getTileX() > 24) {
                    c.setFont(coordsFont8);
                } else {
                    c.setFont(coordsFont10);
                }
                c.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            }
            instrTable.getColumnByIndex(1).getWidth();
            for (int i = 1; i <= mosaic.getTileY(); i++) {
                Cell c = instrTable.getCellByPosition(0, i);
                c.addParagraph(Integer.toString(i));
                if (mosaic.getTileY() > 24) {
                    c.setFont(coordsFont8);
                } else {
                    c.setFont(coordsFont10);
                }
                c.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
                c.setVerticalAlignment(VerticalAlignmentType.MIDDLE);
                Row r = instrTable.getRowByIndex(i);
                r.setHeight(instrTable.getColumnByIndex(1).getWidth(), false);
            }
            Border b = new Border(org.odftoolkit.odfdom.type.Color.SILVER, 0.5, StyleTypeDefinitions.SupportedLinearMeasure.PT);
            BufferedImage tile = mosaic.getImage().getSubimage(xi, yi - mosaic.getTileY(), mosaic.getTileX(), mosaic.getTileY());
            for (int c = 1; c <= mosaic.getTileX(); c++) {
                for (int r = 1; r <= mosaic.getTileY(); r++) {
                    Cell cl = instrTable.getCellByPosition(c, r);
                    cl.setCellBackgroundColor(org.odftoolkit.odfdom.type.Color.valueOf(
                            String.format("#%06x", tile.getRGB(c - 1, r - 1) & 0xffffff)));
                    cl.setBorders(CellBordersType.NONE, b);
                    cl.setBorders(CellBordersType.ALL_FOUR, b);
                }
            }
            mosaicSheets.addPageBreak();
            cy++;
            yi -= mosaic.getTileY();
            setProgress(worked * 100 / mosaic.getNumTiles());
        }
        cx++;
        xi += mosaic.getTileX();
        doInbackgroundHelper();

        return worked;
    }


    public void doInBackgroundHelper2() {

        int xi, cx, yi, cy, worked = 0;
        HashMap<BrickColors, Integer> totalColor;
        TextDocument mosaicSheets = null;
        Table colorTable = null, instrTable = null;
        HashMap<BrickColors, Integer> tileColorCount = new HashMap<BrickColors, Integer>();

        totalColor = new HashMap<BrickColors, Integer>();

        // create styles
        Font coordsFont10 = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 10.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font coordsFont8 = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 8.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font theadFont = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 8.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font tcolorFont = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.REGULAR, 10.0, org.odftoolkit.odfdom.type.Color.BLACK);


        // writing tile building instructions
        // title
        String tileName = null;
        Paragraph title = mosaicSheets.addParagraph(tileName);
        // heading level 1
        title.applyHeading(true, 1);
        // table of colors
        if (tileColorCount.keySet().size() > 8) {
            // rows = half of color sample number
            colorTable = Table.newTable(mosaicSheets, ((tileColorCount.keySet().size() + 1) / 2) + 1, 6, 0, 0);
            colorTable.getColumnByIndex(0).setWidth(15.0d);
            colorTable.getColumnByIndex(1).setWidth(15.0d);
            colorTable.getColumnByIndex(2).setWidth(50.0d);
            colorTable.getColumnByIndex(3).setWidth(15.0d);
            colorTable.getColumnByIndex(4).setWidth(15.0d);
            colorTable.getColumnByIndex(5).setWidth(50.0d);
            colorTable.setTableName("Colors" + tileName);
            //colorTable.setWidth(161.0d);
            Cell cell = colorTable.getCellByPosition(0, 0);
            cell.addParagraph("Color");
            cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(1, 0);
            cell.addParagraph("Num.");
            cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(2, 0);
            cell.addParagraph("Color name");
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(3, 0);
            cell.addParagraph("Color");
            cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(4, 0);
            cell.addParagraph("Num.");
            cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(5, 0);
            cell.addParagraph("Color name");
            cell.setFont(theadFont);

            int row = 1;
            for (BrickColors c : tileColorCount.keySet()) {
                if ((row % 2) == 0) {
                    cell = colorTable.getCellByPosition(3, (row + 1) / 2);
                    cell.setCellBackgroundColor(org.odftoolkit.odfdom.type.Color.valueOf(
                            String.format("#%02x%02x%02x", c.color.getRed(), c.color.getGreen(), c.color.getBlue())));
                    cell = colorTable.getCellByPosition(4, (row + 1) / 2);
                    cell.addParagraph(tileColorCount.get(c).toString());
                    cell.setFont(tcolorFont);
                    cell = colorTable.getCellByPosition(5, (row + 1) / 2);
                    cell.addParagraph(c.lddName);
                    cell.setFont(tcolorFont);
                } else {
                    cell = colorTable.getCellByPosition(0, (row + 1) / 2);
                    //cell.setFont(cellFont);
                    cell.setCellBackgroundColor(org.odftoolkit.odfdom.type.Color.valueOf(
                            String.format("#%02x%02x%02x", c.color.getRed(), c.color.getGreen(), c.color.getBlue())));
                    cell = colorTable.getCellByPosition(1, (row + 1) / 2);
                    cell.addParagraph(tileColorCount.get(c).toString());
                    cell.setFont(tcolorFont);
                    cell = colorTable.getCellByPosition(2, (row + 1) / 2);
                    cell.addParagraph(c.lddName);
                    cell.setFont(tcolorFont);
                }
                row++;
            }


        } else {
            colorTable = Table.newTable(mosaicSheets, tileColorCount.keySet().size() + 1, 3, 0, 0);
            colorTable.setTableName("Colors" + tileName);
            colorTable.setWidth(80.0);

            Cell cell = colorTable.getCellByPosition(0, 0);
            cell.addParagraph("Color");
            cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(1, 0);
            cell.addParagraph("Num.");
            cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
            cell.setFont(theadFont);
            cell = colorTable.getCellByPosition(2, 0);
            cell.addParagraph("Color name");
            cell.setFont(theadFont);
            int row = 1;
            for (BrickColors c : tileColorCount.keySet()) {
                cell = colorTable.getCellByPosition(0, row);
                cell.setCellBackgroundColor(org.odftoolkit.odfdom.type.Color.valueOf(
                        String.format("#%02x%02x%02x", c.color.getRed(), c.color.getGreen(), c.color.getBlue())));
                cell = colorTable.getCellByPosition(1, row);
                cell.addParagraph(tileColorCount.get(c).toString());
                cell.setFont(tcolorFont);
                cell = colorTable.getCellByPosition(2, row);
                cell.addParagraph(c.lddName);
                cell.setFont(tcolorFont);
                row++;
            }
        }
    }

    public void doInbackgroundHelper3() {
        int cx, cy;
        cx = 0;
        cy = 0;
        String tileName = null;

        if (cx < 26) {
            tileName = Character.toString((char) (cx + 65)) + Integer.toString(cy + 1);
        } else {
            tileName = Character.toString((char) ((cx / 26) + 65)) +
                    Character.toString((char) ((cx % 26) + 65)) + Integer.toString(cy + 1);
        }
    }


    public Integer doInbackgroundHelper() throws Exception {


        int xi, cx, yi, cy, worked = 0;
        HashMap<BrickColors, Integer> totalColor;
        TextDocument mosaicSheets = null;
        Table colorTable = null, instrTable = null;
        HashMap<BrickColors, Integer> tileColorCount;

        totalColor = new HashMap<BrickColors, Integer>();
        // create styles
        Font coordsFont10 = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 10.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font coordsFont8 = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 8.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font theadFont = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.BOLD, 8.0, org.odftoolkit.odfdom.type.Color.BLACK);
        Font tcolorFont = new Font("Liberation Sans", StyleTypeDefinitions.FontStyle.REGULAR, 10.0, org.odftoolkit.odfdom.type.Color.BLACK);


        Paragraph title = mosaicSheets.addParagraph("Totals");
        // heading level 1
        title.applyHeading(true, 1);
        // table of colors
        colorTable = Table.newTable(mosaicSheets, totalColor.keySet().size() + 2, 3, 0, 0);
        colorTable.setTableName("TotalColors");
        colorTable.setWidth(80.0);

        Cell cell = colorTable.getCellByPosition(0, 0);
        cell.addParagraph("Color");
        cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
        cell.setFont(coordsFont10);
        cell = colorTable.getCellByPosition(1, 0);
        cell.addParagraph("Num.");
        cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
        cell.setFont(coordsFont10);
        cell = colorTable.getCellByPosition(2, 0);
        cell.addParagraph("Color name");
        cell.setFont(coordsFont10);
        int row = 1;
        int totals = 0;
        for (BrickColors c : totalColor.keySet()) {
            cell = colorTable.getCellByPosition(0, row);
            cell.setCellBackgroundColor(org.odftoolkit.odfdom.type.Color.valueOf(
                    String.format("#%02x%02x%02x", c.color.getRed(), c.color.getGreen(), c.color.getBlue())));
            cell = colorTable.getCellByPosition(1, row);
            cell.addParagraph(totalColor.get(c).toString());
            totals += totalColor.get(c);
            cell.setFont(tcolorFont);
            cell = colorTable.getCellByPosition(2, row);
            cell.addParagraph(c.lddName);
            cell.setFont(tcolorFont);
            row++;
        }
        cell = colorTable.getCellByPosition(0, row);
        cell.addParagraph("Total");
        cell.setFont(coordsFont10);
        cell.setHorizontalAlignment(HorizontalAlignmentType.RIGHT);
        cell = colorTable.getCellByPosition(1, row);
        cell.addParagraph(Integer.toString(totals));
        cell.setFont(tcolorFont);
        cell = colorTable.getCellByPosition(2, row);
        cell.addParagraph("bricks");
        cell.setFont(tcolorFont);

        mosaic.setMappedColors(totalColor);

        try {
            mosaicSheets.save(fileOdt);
        } catch (Exception e) {
            throw new IOException("Error saving OpenDocument file " + fileOdt.getName());
        }

        return worked;
    }
}