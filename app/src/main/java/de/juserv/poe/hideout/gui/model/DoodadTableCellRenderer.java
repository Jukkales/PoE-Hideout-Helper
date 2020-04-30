package de.juserv.poe.hideout.gui.model;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

/**
 * CellRenderer for {@link de.juserv.poe.hideout.model.DoodadTableInfo}
 */
public class DoodadTableCellRenderer extends DefaultTableCellRenderer {

    private int align;
    private Color ownedColor = new Color(155, 220, 97);

    public DoodadTableCellRenderer(int align) {
        this.align = align;
    }

    @Override
    public int getHorizontalAlignment() {
        return align;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        DoodadTableModel model = ((DoodadTableModel) table.getModel());
        Object formattedValue = value;

        if (model.getColumnClass(column).isAssignableFrom(Number.class)) {
            formattedValue = NumberFormat.getIntegerInstance().format(value);
        }

        int index = table.getRowSorter().convertRowIndexToModel(row);
        Component c = super.getTableCellRendererComponent(table, formattedValue, isSelected, hasFocus, row, column);
        if (!table.isRowSelected(row)) {
            if (model.getData().get(index).isOwned()) {
                c.setBackground(ownedColor);
            } else {
                c.setBackground(Color.white);
            }
        }
        if (align == JLabel.LEFT) {
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        }

        return c;
    }

}
