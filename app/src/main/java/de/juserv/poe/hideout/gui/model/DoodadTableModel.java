package de.juserv.poe.hideout.gui.model;

import de.juserv.poe.hideout.model.DoodadTableInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Table Model for {@link DoodadTableInfo}
 */
public class DoodadTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -91319475568928989L;

    private List<ColumnInfo> columns = new ArrayList<>();
    @Setter
    @Getter
    private List<DoodadTableInfo> data = new ArrayList<>();

    public <R> void addColumn(String text, Function<DoodadTableInfo, R> getter, Class<R> clazz) {
        ColumnInfo info = new ColumnInfo();
        info.setText(text);
        info.setGetter(getter);
        info.setClazz(clazz);
        columns.add(info);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getClazz();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getText();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getGetter().apply((DoodadTableInfo) data.get(rowIndex));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Data
    public static class ColumnInfo {
        private Class<?> clazz;
        private Function<DoodadTableInfo, ?> getter;
        private String text;
    }
}
