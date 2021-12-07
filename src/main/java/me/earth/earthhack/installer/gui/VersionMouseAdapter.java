package me.earth.earthhack.installer.gui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Instead of an anonymous class because the VersionPanel
 * Constructor was big enough already.
 */
public class VersionMouseAdapter extends MouseAdapter
{
    private final JTable table;
    private final JButton install;
    private final JButton uninstall;
    private final Object[][] data;

    public VersionMouseAdapter(JTable table,
                               JButton install,
                               JButton uninstall,
                               Object[][] data)
    {
        this.table     = table;
        this.install   = install;
        this.uninstall = uninstall;
        this.data      = data;
    }

    @Override
    public void mouseClicked(MouseEvent evt)
    {
        int row = table.rowAtPoint(evt.getPoint());
        if (row >= 0)
        {
            Object[] o = data[row];
            if (! (boolean) o[3])
            {
                install.setEnabled(false);
                uninstall.setEnabled(false);
            }
            else if ((boolean) o[2])
            {
                install.setEnabled(false);
                uninstall.setEnabled(true);
            }
            else
            {
                install.setEnabled(true);
                uninstall.setEnabled(false);
            }
        }
        else
        {
            install.setEnabled(false);
            uninstall.setEnabled(false);
        }
    }

}
