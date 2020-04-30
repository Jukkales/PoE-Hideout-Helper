package de.juserv.poe.hideout.gui;

import de.juserv.poe.hideout.gui.model.DoodadTableCellRenderer;
import de.juserv.poe.hideout.gui.model.DoodadTableModel;
import de.juserv.poe.hideout.gui.model.TableResizeListener;
import de.juserv.poe.hideout.model.DoodadTableInfo;
import de.juserv.poe.hideout.model.Hideout;
import de.juserv.poe.hideout.model.Language;
import de.juserv.poe.hideout.model.Master;
import de.juserv.poe.hideout.service.HideoutService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collections;

public class HideoutInfoDialog extends JDialog {

    private final Language language;
    private Hideout fullHideout;
    private Hideout partialHideout;
    private HideoutService service = HideoutService.getINSTANCE();

    public HideoutInfoDialog(Hideout fullHideout, Hideout partialHideout,
                             Language language) throws IOException {
        this.fullHideout = fullHideout;
        this.partialHideout = partialHideout;
        this.language = language;
        createGUI();
    }

    private JComponent createDoodadTable(Master master) {
        DoodadTableModel model = new DoodadTableModel();
        model.addColumn("", DoodadTableInfo::getAmount, Long.class);
        model.addColumn(Messages.getString("table.column.name"), DoodadTableInfo::getName, String.class);
        model.addColumn(Messages.getString("table.column.level"), DoodadTableInfo::getLevel, Long.class);
        model.addColumn(Messages.getString("table.column.cost"), DoodadTableInfo::getCost, Long.class);

        if (partialHideout != null) {
            model.setData(service.asTableInfo(fullHideout.getDoodadsByMaster(master),
                    partialHideout.getDoodadsByMaster(master), language));
        } else {
            model.setData(service.asTableInfo(fullHideout.getDoodadsByMaster(master), language));
        }


        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(null);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(20);
        table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(20);
        table.getTableHeader().getColumnModel().getColumn(0)
                .setCellRenderer(new DoodadTableCellRenderer(JLabel.CENTER));

        table.getTableHeader().getColumnModel().getColumn(1).setMinWidth(104);
        table.getTableHeader().getColumnModel().getColumn(1)
                .setCellRenderer(new DoodadTableCellRenderer(JLabel.LEFT));

        table.getTableHeader().getColumnModel().getColumn(2).setMinWidth(35);
        table.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(35);
        table.getTableHeader().getColumnModel().getColumn(2)
                .setCellRenderer(new DoodadTableCellRenderer(JLabel.CENTER));

        table.getTableHeader().getColumnModel().getColumn(3).setMinWidth(60);
        table.getTableHeader().getColumnModel().getColumn(3).setCellRenderer(new DoodadTableCellRenderer(JLabel.LEFT));

        DefaultRowSorter<?, ?> sorter = ((DefaultRowSorter<?, ?>) table.getRowSorter());
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
        sorter.sort();

        JPanel tablePane = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        addComponentListener(new TableResizeListener(table, scrollPane, 250));
        tablePane.add(scrollPane, BorderLayout.CENTER);
        tablePane.setPreferredSize(new Dimension(250, 400));
        return tablePane;
    }

    private JComponent createDoodadTableNonMaster(Master master) {
        DoodadTableModel model = new DoodadTableModel();
        model.addColumn("", DoodadTableInfo::getAmount, Long.class);
        model.addColumn(Messages.getString("table.column.name"), DoodadTableInfo::getName, String.class);
        model.addColumn(Messages.getString("table.column.coinCost"), DoodadTableInfo::getCost, Long.class);

        if (partialHideout != null) {
            model.setData(service.asTableInfo(fullHideout.getDoodadsByMaster(master),
                    partialHideout.getDoodadsByMaster(master), language));
        } else {
            model.setData(service.asTableInfo(fullHideout.getDoodadsByMaster(master), language));
        }

        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setPreferredScrollableViewportSize(null);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(20);
        table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(20);
        table.getTableHeader().getColumnModel().getColumn(0)
                .setCellRenderer(new DoodadTableCellRenderer(JLabel.CENTER));

        table.getTableHeader().getColumnModel().getColumn(1).setMinWidth(150);
        table.getTableHeader().getColumnModel().getColumn(1)
                .setCellRenderer(new DoodadTableCellRenderer(JLabel.LEFT));

        table.getTableHeader().getColumnModel().getColumn(2).setMinWidth(60);
        table.getTableHeader().getColumnModel().getColumn(2)
                .setCellRenderer(new DoodadTableCellRenderer(JLabel.LEFT));

        DefaultRowSorter<?, ?> sorter = ((DefaultRowSorter<?, ?>) table.getRowSorter());
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
        sorter.sort();

        JPanel tablePane = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        addComponentListener(new TableResizeListener(table, scrollPane, 250));
        tablePane.add(scrollPane, BorderLayout.CENTER);
        tablePane.setPreferredSize(new Dimension(250, 400));
        return tablePane;
    }

    private void createGUI() throws IOException {
        setTitle("Hideout Information");
        setLayout(new BorderLayout());
        add(createPanel(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());
    }

    private JComponent createMasterPanel(Master master) throws IOException {
        GridBagHelper helper = new GridBagHelper();
        helper.setAnchor(GridBagConstraints.FIRST_LINE_START);
        helper.fillNone();
        helper.setInsets(3);

        //
        helper.setWeight(0, 0);
        JLabel masterImage = new JLabel(new ImageIcon(getMasterImage(master)));
        masterImage.setPreferredSize(new Dimension(100, 120));
        masterImage.setMinimumSize(new Dimension(100, 120));
        helper.setHeight(8);
        helper.add(masterImage);
        helper.setHeight(1);

        helper.setInsetLeft(-10);
        helper.setWeight(0, 0);
        JLabel masterName = new JLabel(master.getName());
        masterName.setFont(masterName.getFont().deriveFont(16f).deriveFont(Font.BOLD));
        helper.add(masterName);
        helper.newline();

        //
        helper.setWidth(2);
        helper.fillHorizontal();
        helper.add(new JSeparator(), 1);
        helper.setWidth(1);
        helper.fillNone();
        helper.newline();

        //
        if (master != Master.NON_MASTER) {
            JLabel levelLabel = new JLabel(Messages.getString("label.requiredLevel"));
            levelLabel.setFont(levelLabel.getFont().deriveFont(Font.BOLD));
            helper.setInsetLeft(-10);
            helper.add(levelLabel, 1);
            helper.fillNone();
            helper.fillHorizontal();
            helper.setInsetLeft(3);
            helper.add(new JLabel(String.valueOf(fullHideout.getRequiredMasterLevel(master))), 2);
            helper.newline();
        } else {
            helper.add(new JLabel(), 1);
            helper.newline();
        }
        helper.newline();

        //
        JLabel favorLabel;
        if (master != Master.NON_MASTER) {
            favorLabel = new JLabel(Messages.getString("label.favorCost"));
        } else {
            favorLabel = new JLabel(Messages.getString("label.coinCost"));
        }
        favorLabel.setFont(favorLabel.getFont().deriveFont(Font.BOLD));
        helper.setInsetLeft(-10);
        helper.add(favorLabel, 1);

        //
        String cost = NumberFormat.getIntegerInstance().format(fullHideout.getCost(master));
        helper.setInsetLeft(3);
        helper.add(new JLabel(cost), 2);
        helper.newline();

        //
        if (partialHideout != null) {
            JLabel remainingLabel = new JLabel(Messages.getString("label.remainingFavor"));
            remainingLabel.setFont(remainingLabel.getFont().deriveFont(Font.BOLD));
            helper.setInsetLeft(-10);
            helper.add(remainingLabel, 1);
            helper.setInsetLeft(3);

            cost = NumberFormat.getIntegerInstance()
                    .format(fullHideout.getCost(master) - partialHideout.getCost(master));
            helper.add(new JLabel(cost), 2);
        }

        //
        helper.newline();

        //
        helper.setWidth(3);
        helper.fillBoth();
        helper.setWeight(100, 100);
        if (master != Master.NON_MASTER) {
            helper.add(createDoodadTable(master), 0, 8);
        } else {
            helper.add(createDoodadTableNonMaster(master), 0, 8);
        }

        return helper.getContainer();

    }

    private JComponent createPanel() throws IOException {
        GridBagHelper helper = new GridBagHelper();
        helper.fillBoth();

        if (fullHideout.getDoodadsByMaster(Master.ALVA).size() > 0) {
            helper.setWeight(1, 1);
            helper.add(createMasterPanel(Master.ALVA));
            helper.setWeight(0, 1);
            helper.add(new JSeparator(SwingConstants.VERTICAL));
        }
        if (fullHideout.getDoodadsByMaster(Master.NIKO).size() > 0) {
            helper.setWeight(1, 1);
            helper.add(createMasterPanel(Master.NIKO));
            helper.setWeight(0, 1);
            helper.add(new JSeparator(SwingConstants.VERTICAL));
        }
        if (fullHideout.getDoodadsByMaster(Master.EINHAR).size() > 0) {
            helper.setWeight(1, 1);
            helper.add(createMasterPanel(Master.EINHAR));
            helper.setWeight(0, 1);
            helper.add(new JSeparator(SwingConstants.VERTICAL));
        }
        if (fullHideout.getDoodadsByMaster(Master.JUN).size() > 0) {
            helper.setWeight(1, 1);
            helper.add(createMasterPanel(Master.JUN));
            helper.setWeight(0, 1);
            helper.add(new JSeparator(SwingConstants.VERTICAL));
        }
        if (fullHideout.getDoodadsByMaster(Master.ZANA).size() > 0) {
            helper.setWeight(1, 1);
            helper.add(createMasterPanel(Master.ZANA));
            helper.setWeight(0, 1);
            helper.add(new JSeparator(SwingConstants.VERTICAL));
        }
        if (fullHideout.getDoodadsByMaster(Master.NON_MASTER).size() > 0) {
            helper.setWeight(1, 1);
            helper.add(createMasterPanel(Master.NON_MASTER));
        }
        return helper.getContainer();
    }

    public void doModal() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setVisible(true);
    }

    private BufferedImage getMasterImage(Master master) throws IOException {
        BufferedImage masterImage =
                ImageIO.read(getClass().getResourceAsStream("/images/" + master.getName() + ".png"));
        Image tmp = masterImage.getScaledInstance(100, 120, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(100, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return scaled;
    }

}
