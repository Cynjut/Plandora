package com.pandora.gui.gantt;

import java.awt.CheckboxMenuItem;
import java.awt.Font;
import java.awt.HeadlessException;

/**
 * This class handler the events of Checkbox Menu item from gantt.
 */
public class MenuCheckItemGantt extends CheckboxMenuItem{

	private static final long serialVersionUID = 1L;

    /**
     * @param arg0
     * @throws java.awt.HeadlessException
     */
    public MenuCheckItemGantt(String label) throws HeadlessException {
        super(label);
        this.setFont(new Font("Arial", Font.PLAIN, 10));
    }
}
