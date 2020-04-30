package de.juserv.poe.hideout.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * Helper to operate easier with {@link GridBagLayout}'s.
 */
public class GridBagHelper {

    @Getter
    private final JComponent container;
    @Getter
    private GridBagConstraints constraints;

    public GridBagHelper() {
        this.container = new JPanel(new GridBagLayout());
        constraints = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0,
                0);
    }

    public void add(JComponent component, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
        container.add(component, constraints);
    }

    public void add(JComponent component, int x) {
        constraints.gridx = x;
        container.add(component, constraints);
    }

    public void add(JComponent component) {
        container.add(component, constraints);
        constraints.gridx++;
    }

    public void fillBoth() {
        constraints.fill = GridBagConstraints.BOTH;
    }

    public void fillHorizontal() {
        constraints.fill = GridBagConstraints.HORIZONTAL;
    }

    public void fillNone() {
        constraints.fill = GridBagConstraints.NONE;
    }

    public void newline() {
        constraints.gridy++;
        constraints.gridx = 0;
    }

    public void setAnchor(int anchor) {
        constraints.anchor = anchor;
    }

    public void setHeight(int height) {
        constraints.gridheight = height;
    }

    public void setInsetBottom(int i) {
        constraints.insets.bottom = i;
    }

    public void setInsetLeft(int i) {
        constraints.insets.left = i;
    }

    public void setInsetTop(int i) {
        constraints.insets.top = i;
    }

    public void setInsets(int i) {
        constraints.insets = new Insets(i, i, i, i);
    }

    public void setWeight(int weightx, int weighty) {
        constraints.weightx = weightx;
        constraints.weighty = weighty;
    }

    public void setWidth(int width) {
        constraints.gridwidth = width;
    }
}