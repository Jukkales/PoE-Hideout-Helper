package de.juserv.poe.hideout.gui;

import de.juserv.poe.hideout.App;
import de.juserv.poe.hideout.model.Hideout;
import de.juserv.poe.hideout.model.Language;
import de.juserv.poe.hideout.service.HideoutService;
import de.juserv.poe.hideout.service.PoEHelper;
import de.juserv.poe.hideout.service.UpdateCheck;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.util.stream.Stream;

public class HideoutSelector extends JDialog {

    private JButton buttonChoosePartialHideout;
    private JButton buttonFullHideoutInfo;
    private JButton buttonPartialHideoutInfo;
    private JButton buttonSubmit;
    private JFileChooser chooser = new JFileChooser();
    private JComboBox<Language> comboBoxGameLanguage;
    private Hideout fullHideout;
    private JLabel labelFullHideoutInfo;
    private JLabel labelPartialHideoutInfo;
    private Hideout partialHideout;
    private JTextField textFieldFullHideout;
    private JTextField textFieldPartialHideout;

    public HideoutSelector() {
        super((Window) null);
        createGUI();
    }

    public HideoutSelector(Hideout fullHideout, Hideout partialHideout, Language language) {
        super((Window) null);
        this.fullHideout = fullHideout;
        this.partialHideout = partialHideout;
        if (fullHideout != null) {
            chooser.setCurrentDirectory(new File(fullHideout.getHideoutFile()).getParentFile());
        }
        createGUI();
        comboBoxGameLanguage.setSelectedItem(language);
    }

    private void chooseFile(JTextField target) {
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PoE Hideout (.hideout)", "hideout");
        chooser.addChoosableFileFilter(filter);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (target == textFieldFullHideout) {
                fullHideout = new Hideout(selectedFile.getAbsolutePath());
            } else if (target == textFieldPartialHideout) {
                partialHideout = new Hideout(selectedFile.getAbsolutePath());
            }
        }
        updateItems();
    }

    private void createGUI() {
        setPreferredSize(new Dimension(500, 260));
        setTitle("Path of Exile HideoutHelper");
        setLayout(new BorderLayout());
        add(createPanel(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());
        updateItems();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                dispose(true);
            }
        });
    }

    private JComponent createPanel() {
        GridBagHelper helper = new GridBagHelper();
        helper.setAnchor(GridBagConstraints.FIRST_LINE_START);
        helper.fillBoth();
        helper.setInsets(3);

        helper.setInsetTop(12);
        //
        JLabel labelFullHideout = new JLabel(Messages.getString("label.fieldLabelChooseFullHideout"));
        labelFullHideout.setFont(labelFullHideout.getFont().deriveFont(Font.BOLD));
        helper.add(labelFullHideout);
        helper.newline();
        helper.setInsetTop(3);

        //
        textFieldFullHideout = new JTextField();
        textFieldFullHideout.setEditable(false);
        textFieldFullHideout.setBackground(Color.WHITE);
        helper.setWidth(5);
        helper.add(textFieldFullHideout);

        JButton buttonChooseFullHideout = new JButton(Messages.getString("button.SelectFile"));
        buttonChooseFullHideout.addActionListener((e) -> chooseFile(textFieldFullHideout));
        helper.setWidth(1);
        helper.add(buttonChooseFullHideout, 5);
        helper.newline();

        //
        labelFullHideoutInfo = new JLabel(Messages.getString("label.noHideoutSelected"));
        helper.setWidth(4);
        helper.setInsetLeft(15);
        helper.add(labelFullHideoutInfo);
        helper.setInsetLeft(3);

        buttonFullHideoutInfo = new JButton(Messages.getString("button.showHideoutInfo"));
        buttonFullHideoutInfo.addActionListener(e -> invokeShowInfo(false));
        helper.setWidth(1);
        helper.add(buttonFullHideoutInfo, 5);
        helper.newline();

        //
        helper.setWidth(6);
        helper.add(new JSeparator());
        helper.setWidth(1);
        helper.newline();

        //
        JLabel labelPartialHideout = new JLabel(Messages.getString("label.fieldLabelExportedHideout"));
        labelPartialHideout.setFont(labelPartialHideout.getFont().deriveFont(Font.BOLD));
        helper.add(labelPartialHideout);
        helper.newline();

        //
        textFieldPartialHideout = new JTextField();
        textFieldPartialHideout.setEditable(false);
        textFieldPartialHideout.setBackground(Color.WHITE);
        helper.setWidth(5);
        helper.add(textFieldPartialHideout);

        buttonChoosePartialHideout = new JButton(Messages.getString("button.SelectFile"));
        buttonChoosePartialHideout.addActionListener((e) -> chooseFile(textFieldPartialHideout));
        helper.setWidth(1);
        helper.add(buttonChoosePartialHideout, 5);
        helper.newline();

        //
        labelPartialHideoutInfo = new JLabel(Messages.getString("label.noHideoutSelected"));
        helper.setWidth(4);
        helper.setInsetLeft(15);
        helper.add(labelPartialHideoutInfo);
        helper.setInsetLeft(3);

        buttonPartialHideoutInfo = new JButton(Messages.getString("button.showDifference"));
        buttonPartialHideoutInfo.addActionListener(e -> invokeShowInfo(true));
        helper.setWidth(1);
        helper.add(buttonPartialHideoutInfo, 5);
        helper.newline();
        helper.newline();

        //
        helper.setWidth(6);
        helper.add(new JSeparator());
        helper.setWidth(1);
        helper.newline();

        //
        JLabel labelGameLanguage = new JLabel(Messages.getString("combo.clientLanguage"));
        labelGameLanguage.setFont(labelGameLanguage.getFont().deriveFont(Font.BOLD));
        helper.add(labelGameLanguage);
        helper.newline();

        helper.setInsetBottom(12);
        //
        comboBoxGameLanguage = new JComboBox<>();
        comboBoxGameLanguage.setBackground(Color.WHITE);
        Stream.of(Language.values()).forEach(comboBoxGameLanguage::addItem);
        helper.fillNone();
        helper.add(comboBoxGameLanguage);

        JButton buttonHowTo = new JButton(Messages.getString("button.howTo"));
        buttonHowTo.addActionListener(e -> invokeHowTo());
        helper.add(buttonHowTo, 3);

        buttonSubmit = new JButton(Messages.getString("button.start"));
        buttonSubmit.addActionListener((e) -> invokeSubmit());
        buttonSubmit.setEnabled(false);
        helper.fillNone();
        helper.setAnchor(GridBagConstraints.LAST_LINE_END);
        helper.add(buttonSubmit, 5);

        return helper.getContainer();
    }

    public void dispose(boolean exit) {
        super.dispose();
        if (exit) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    public void doModal() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        UpdateCheck.checkNewVersion();
        setVisible(true);
    }

    private void invokeHowTo() {
        try {
            Desktop.getDesktop().browse(new URI(App.getProperty("app.baseUrl") + "/blob/master/HOWTO.md"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    Messages.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void invokeShowInfo(boolean withDifference) {
        try {
            updateHideouts();
            HideoutInfoDialog info = new HideoutInfoDialog(fullHideout, withDifference ? partialHideout : null,
                    (Language) comboBoxGameLanguage.getSelectedItem());
            info.doModal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invokeSubmit() {
        updateHideouts();
        if (HideoutService.getINSTANCE().canPurchaseDecorations(fullHideout, partialHideout)) {
            if (PoEHelper.isRunning()) {
                GameOverlay overlay =
                        new GameOverlay(fullHideout, partialHideout, (Language) comboBoxGameLanguage.getSelectedItem());
                dispose(false);
                overlay.doModal();
            } else {
                JOptionPane.showMessageDialog(this, Messages.getString("error.gameNotRunning"),
                        Messages.getString("warning"),
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, Messages.getString("message.nothingToPurchase"));
        }
    }

    private boolean isNotEmpty(String s) {
        return s != null && !"".equals(s.trim());
    }

    private void updateHideouts() {
        if (fullHideout != null) {
            fullHideout.reload();
        }
        if (partialHideout != null) {
            partialHideout.reload();
        }
        updateItems();
    }

    private void updateItems() {
        if (fullHideout != null) {
            textFieldFullHideout.setText(fullHideout.getHideoutFile());
            if (fullHideout.getError() == null) {
                labelFullHideoutInfo.setText(Messages.getString("label.shortHideoutOverview",
                        fullHideout.getDoodadIds().size(),
                        NumberFormat.getIntegerInstance().format(fullHideout.getCost()),
                        fullHideout.usesMTX() ? Messages.getString("label.yes") : Messages.getString("label.no")));

            } else {
                labelFullHideoutInfo.setText(Messages.getString("error.hideoutLoadError"));
                fullHideout = null;
            }
        }

        if (partialHideout != null) {
            textFieldPartialHideout.setText(partialHideout.getHideoutFile());
            if (partialHideout.getError() == null) {
                int missing = fullHideout.getDoodadList().size() - partialHideout.getDoodadList().size();
                long cost = fullHideout.getCost() - partialHideout.getCost();
                labelPartialHideoutInfo.setText(Messages.getString("label.shortHideoutRemainingOverview",
                        missing, NumberFormat.getIntegerInstance().format(cost)));

            } else {
                labelPartialHideoutInfo.setText(Messages.getString("error.hideoutLoadError"));
                partialHideout = null;
            }
        }

        buttonSubmit.setEnabled(
                isNotEmpty(textFieldFullHideout.getText()) && isNotEmpty(textFieldPartialHideout.getText()));
        buttonFullHideoutInfo.setEnabled(fullHideout != null);
        buttonPartialHideoutInfo.setEnabled(fullHideout != null && partialHideout != null);
        buttonChoosePartialHideout.setEnabled(fullHideout != null);
    }
}
