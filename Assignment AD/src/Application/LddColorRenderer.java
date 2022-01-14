package Application;/*
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


import Data.BrickColors;

import javax.swing.*;
import java.awt.*;
import data.*;


public class LddColorRenderer extends JLabel implements ListCellRenderer {

    class LddIcon implements Icon {

        private Color c;

        public LddIcon(Color c) {
            this.c = c;
        }

        public int getIconHeight() {
            return 26;
        }

        public int getIconWidth() {
            return 26;
        }

        public void paintIcon(Component cp, Graphics g, int x, int y) {

            g.setColor(Color.BLACK);
            g.drawRect(0, 0, 25, 25);
            g.setColor(c);
            g.fillRect(1, 1, 24, 24);
        }
    }


    private static final long serialVersionUID = 6307685763094812103L;

    public LddColorRenderer() {

        setOpaque(true); //MUST do this for background to show up.
    }


    @SuppressWarnings("boxing")
    public Component getListCellRendererComponent(
            JList list, Object index,
            int idx,
            boolean isSelected, boolean hasFocus) {

        setText(BrickColors.getLddColor((Integer) index).lddName);
        setIcon(new LddIcon(BrickColors.getLddColor((Integer) index).color));
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

}
