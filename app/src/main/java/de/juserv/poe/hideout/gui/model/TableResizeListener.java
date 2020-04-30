package de.juserv.poe.hideout.gui.model;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import static javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

/**
 * Listener on the Window to enable/disable column/resize on a Table. <br/>
 * Also adds this listener to the table header automatically.
 */
public class TableResizeListener implements ComponentListener {
    private JScrollPane scrollPane;
    private int size;
    private JTable table;

    public TableResizeListener(JTable table, JScrollPane scrollPane, int size) {
        this.table = table;
        this.scrollPane = scrollPane;
        this.size = size;
        table.getModel().addTableModelListener(new ColumnListener(table, scrollPane, size));
    }

    public void componentHidden(final ComponentEvent event) {
    }

    public void componentMoved(final ComponentEvent event) {
    }

    public void componentResized(final ComponentEvent event) {
        table.setAutoResizeMode(scrollPane.getWidth() <= size ? AUTO_RESIZE_OFF : AUTO_RESIZE_ALL_COLUMNS);
    }

    public void componentShown(final ComponentEvent event) {
    }

    private static class ColumnListener implements TableModelListener {
        private JScrollPane scrollPane;
        private int size;
        private JTable table;

        ColumnListener(JTable table, JScrollPane scrollPane, int size) {
            this.table = table;
            this.scrollPane = scrollPane;
            this.size = size;
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                table.setAutoResizeMode(scrollPane.getWidth() <= size ? AUTO_RESIZE_OFF : AUTO_RESIZE_ALL_COLUMNS);
            }
        }
    }
}
