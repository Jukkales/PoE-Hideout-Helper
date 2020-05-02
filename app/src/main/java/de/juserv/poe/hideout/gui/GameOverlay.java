package de.juserv.poe.hideout.gui;

import de.juserv.poe.hideout.model.DoodadInfo;
import de.juserv.poe.hideout.model.Hideout;
import de.juserv.poe.hideout.model.Language;
import de.juserv.poe.hideout.model.Master;
import de.juserv.poe.hideout.service.HideoutService;
import de.juserv.poe.hideout.service.PoEHelper;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameOverlay extends JDialog {

    private Map<Master, List<DoodadInfo>> allDoodads;
    private JButton buttonCopyItem;
    private JButton buttonNextItem;
    private JButton buttonNextMaster;
    private JButton buttonPrevItem;
    private JButton buttonPrevMaster;
    private int currentItemIndex;
    private Master currentMaster;
    private Map<Master, List<DoodadInfo>> distinctDoodads;
    private Master firstMaster;
    private Hideout fullHideout;
    private NativeKeyListener globalListener;
    private JLabel labelCurrentItem;
    private JLabel labelCurrentItemAmount;
    private JLabel labelCurrentMaster;
    private JLabel labelMasterDecorations;
    private JLabel labelMasterDecorationsRemains;
    private Language language;
    private Map<DoodadInfo, Long> masterDoodads;
    private Hideout partialHideout;
    private boolean shortKeysEnabled = true;

    public GameOverlay(Hideout fullHideout, Hideout partialHideout, Language language) {
        super((Window) null);
        this.fullHideout = fullHideout;
        this.partialHideout = partialHideout;
        this.language = language;
        createGUI();
        globalListener = createGlobalListener();
        GlobalScreen.addNativeKeyListener(globalListener);
        loadData();
    }

    private void createGUI() {
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(600, 200));
        setTitle("Path of Exile HideoutHelper");
        setLayout(new BorderLayout());
        add(createPanel(), BorderLayout.CENTER);
        pack();
        if (PoEHelper.isRunning()) {
            Rectangle rect = PoEHelper.getRect();
            if (rect.x >= 0) {
                setLocation(rect.x + rect.width - 700, rect.y + 30);
            } else {
                setLocationRelativeTo(getParent());
            }
        } else {
            setLocationRelativeTo(getParent());
        }
    }

    private NativeKeyListener createGlobalListener() {
        return new NativeKeyListener() {

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                if (shortKeysEnabled) {
                    if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F6) {
                        invokeMasterPrev();
                    }
                    if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F7) {
                        invokeMasterNext();
                    }
                    if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F9) {
                        invokeItemPrev();
                    }
                    if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F10) {
                        invokeItemCopy();
                    }
                    if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F11) {
                        invokeItemNext();
                    }
                }
            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
            }
        };
    }

    private JComponent createItemPanel() {
        GridBagHelper helper = new GridBagHelper();
        helper.fillBoth();
        helper.setWeight(1, 1);

        //
        labelCurrentItem = new JLabel();
        labelCurrentItem.setFont(labelCurrentItem.getFont().deriveFont(Font.BOLD).deriveFont(26f));
        labelCurrentItem.setHorizontalAlignment(JLabel.CENTER);
        helper.add(labelCurrentItem);
        helper.newline();

        //
        labelCurrentItemAmount = new JLabel();
        labelCurrentItemAmount.setFont(labelCurrentItemAmount.getFont().deriveFont(Font.BOLD).deriveFont(26f));
        labelCurrentItemAmount.setHorizontalAlignment(JLabel.CENTER);
        helper.add(labelCurrentItemAmount);
        helper.newline();

        //
        helper.fillNone();
        helper.setWeight(1, 0);
        JPanel panel = new JPanel(new GridBagLayout());
        buttonPrevItem = new JButton(Messages.getString("button.previousItem"));
        buttonPrevItem.addActionListener(e -> invokeItemPrev());
        buttonCopyItem = new JButton(Messages.getString("button.searchItem"));
        buttonCopyItem.addActionListener(e -> invokeItemCopy());
        buttonNextItem = new JButton(Messages.getString("button.nextItem"));
        buttonNextItem.addActionListener(e -> invokeItemNext());
        panel.add(buttonPrevItem);
        panel.add(buttonCopyItem);
        panel.add(buttonNextItem);
        helper.add(panel);
        return helper.getContainer();
    }

    private JComponent createMasterPanel() {
        GridBagHelper helper = new GridBagHelper();
        helper.fillNone();
        helper.setWeight(0, 0);

        //
        labelCurrentMaster = new JLabel();
        labelCurrentMaster.setFont(labelCurrentMaster.getFont().deriveFont(Font.BOLD).deriveFont(16f));
        helper.add(labelCurrentMaster);
        helper.newline();

        //
        labelMasterDecorations = new JLabel();
        helper.add(labelMasterDecorations);
        helper.newline();

        //
        labelMasterDecorationsRemains = new JLabel();
        helper.add(labelMasterDecorationsRemains);
        helper.newline();

        //
        helper.setWeight(1, 1);
        helper.fillBoth();
        helper.add(new JPanel());
        helper.fillNone();
        helper.newline();

        //
        helper.setWeight(0, 0);
        buttonPrevMaster = new JButton(Messages.getString("button.previousMaster"));
        buttonPrevMaster.addActionListener(e -> invokeMasterPrev());
        buttonNextMaster = new JButton(Messages.getString("button.nextMaster"));
        buttonNextMaster.addActionListener(e -> invokeMasterNext());
        helper.add(buttonPrevMaster);
        helper.newline();
        helper.add(buttonNextMaster);
        return helper.getContainer();
    }

    private JComponent createPanel() {
        GridBagHelper helper = new GridBagHelper();
        helper.setInsets(2);
        helper.setWeight(0, 1);
        helper.add(createMasterPanel());
        helper.add(new JSeparator(JSeparator.VERTICAL));
        helper.setWeight(1, 1);
        helper.add(createItemPanel());
        return helper.getContainer();
    }

    @Override
    public void dispose() {
        GlobalScreen.removeNativeKeyListener(globalListener);
        super.dispose();
        new HideoutSelector(fullHideout, partialHideout, language).doModal();
    }

    public void doModal() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setVisible(true);
    }

    private Master getNextMaster() {
        Master m = currentMaster.next();
        while (!distinctDoodads.containsKey(m)) {
            m = m.next();
            if (m == currentMaster) {
                break;
            }
        }
        return m;
    }

    private Master getPrevMaster() {
        Master m = currentMaster.prev();
        while (!distinctDoodads.containsKey(m)) {
            m = m.prev();
            if (m == currentMaster) {
                break;
            }
        }
        return m;
    }

    private void invokeItemCopy() {
        if (currentItemIndex == -1) {
            currentItemIndex = 0;
        }
        if (PoEHelper.isRunning()) {
            PoEHelper.performSearch(
                    distinctDoodads.get(currentMaster).get(currentItemIndex).getName().get(language.getLocalCode()));
        } else {
            shortKeysEnabled = false;
            JOptionPane
                    .showMessageDialog(null, Messages.getString("error.gameNotRunning"), Messages.getString("warning"),
                            JOptionPane.WARNING_MESSAGE);
            shortKeysEnabled = true;
        }
        updateItems();
    }

    private void invokeItemNext() {
        if (buttonNextItem.isEnabled()) {
            if (currentItemIndex >= distinctDoodads.get(currentMaster).size() - 1) {
                if (getNextMaster() != firstMaster) {
                    shortKeysEnabled = false;
                    JOptionPane.showMessageDialog(null,
                            Messages.getString("message.nextMaster", currentMaster.getName(),
                                    getNextMaster().getName()));
                    shortKeysEnabled = true;
                    invokeMasterNext();
                } else {
                    shortKeysEnabled = false;
                    JOptionPane.showMessageDialog(null,
                            Messages.getString("message.finish"));
                    shortKeysEnabled = true;
                }
            } else {
                currentItemIndex++;
                invokeItemCopy();
            }
        }
    }

    private void invokeItemPrev() {
        if (buttonPrevItem.isEnabled()) {
            currentItemIndex--;
            invokeItemCopy();
        }
    }

    private void invokeMasterNext() {
        if (buttonNextMaster.isEnabled()) {
            currentMaster = getNextMaster();
            masterDoodads = HideoutService.getINSTANCE().getDoodadAmounts(allDoodads.get(currentMaster));
            currentItemIndex = -1;
            updateItems();
        }
    }

    private void invokeMasterPrev() {
        if (buttonPrevMaster.isEnabled()) {
            currentMaster = getPrevMaster();
            masterDoodads = HideoutService.getINSTANCE().getDoodadAmounts(allDoodads.get(currentMaster));
            currentItemIndex = -1;
            updateItems();
        }
    }

    private void loadData() {
        distinctDoodads = HideoutService.getINSTANCE()
                .getUnownedDoodads(fullHideout.getDoodadList(), partialHideout.getDoodadList(), true);
        distinctDoodads.remove(Master.NON_MASTER);

        allDoodads = HideoutService.getINSTANCE()
                .getUnownedDoodads(fullHideout.getDoodadList(), partialHideout.getDoodadList());
        allDoodads.remove(Master.NON_MASTER);

        for (Master m : Master.values()) {
            if (distinctDoodads.containsKey(m)) {
                currentMaster = m;
                firstMaster = currentMaster;
            }
        }

        currentItemIndex = -1;
        masterDoodads = HideoutService.getINSTANCE().getDoodadAmounts(allDoodads.get(currentMaster));
        updateItems();
    }

    private void updateItems() {
        buttonPrevMaster.setEnabled(currentMaster != firstMaster);
        buttonNextMaster.setEnabled(getNextMaster() != firstMaster);

        int doodadCount = distinctDoodads.get(currentMaster).size();
        labelCurrentMaster.setText(currentMaster.getName());
        labelMasterDecorations.setText(Messages.getString("label.MasterDecorationCount", doodadCount));
        if (currentItemIndex < 0) {
            labelMasterDecorationsRemains.setText(Messages.getString("label.masterDecorationRemaining", doodadCount));
        } else {
            labelMasterDecorationsRemains
                    .setText(Messages.getString("label.masterDecorationRemaining", doodadCount - currentItemIndex - 1));
        }

        if (currentItemIndex == -1) {
            labelCurrentItem.setText(Messages.getString("label.talkToNextMaster", currentMaster.getName()));
            labelCurrentItemAmount.setFont(labelCurrentItemAmount.getFont().deriveFont(Font.PLAIN).deriveFont(16f));
            labelCurrentItemAmount.setText(Messages.getString("label.openDecorationPurchaseWindow"));
            buttonPrevItem.setEnabled(false);
            buttonNextItem.setEnabled(false);
            buttonCopyItem.setText(Messages.getString("button.startItemSearch"));
        } else {
            if (currentItemIndex >= 0 && currentItemIndex < distinctDoodads.get(currentMaster).size()) {
                DoodadInfo doodad = distinctDoodads.get(currentMaster).get(currentItemIndex);
                labelCurrentItem.setText(doodad.getName().get(language.getLocalCode()));
                labelCurrentItemAmount.setFont(labelCurrentItemAmount.getFont().deriveFont(Font.BOLD).deriveFont(26f));
                labelCurrentItemAmount.setText(Messages.getString("label.buyAmount", masterDoodads.get(doodad)));
                buttonPrevItem.setEnabled(currentItemIndex != 0);
                buttonCopyItem.setText(Messages.getString("button.searchItem"));
                buttonNextItem.setEnabled(true);
                boolean next = currentItemIndex < distinctDoodads.get(currentMaster).size() - 1;
                if (!next) {
                    if (getNextMaster() != firstMaster) {
                        buttonNextItem.setText(Messages.getString("button.nextMasterOnNextItemButton"));
                    } else {
                        buttonNextItem.setText(Messages.getString("button.finish"));
                    }
                } else {
                    buttonNextItem.setText(Messages.getString("button.nextItem"));
                }
            }
        }
    }
}
