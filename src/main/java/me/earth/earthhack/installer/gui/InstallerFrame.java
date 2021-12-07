package me.earth.earthhack.installer.gui;



import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class InstallerFrame
{
    private final JFrame frame;

    public InstallerFrame()
    {
        FlatLightLaf.setup(new FlatDarculaLaf());
        frame = new JFrame("3arthh4ck-Installer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setSize(550, 400);
        panel.setPreferredSize(new Dimension(550, 400));
        panel.setLayout(null);

        frame.setSize(550, 400);
        frame.setResizable(false);
        frame.getContentPane().add(panel);
        frame.pack();
    }

    public void display()
    {
        frame.setVisible(true);
    }

    public void schedule(JPanel panel)
    {
        SwingUtilities.invokeLater(() -> setPanel(panel));
    }

    public void setPanel(JPanel panel)
    {
        frame.setContentPane(panel);
        frame.invalidate();
        frame.validate();
    }

}
