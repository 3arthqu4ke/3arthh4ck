package me.earth.earthhack.installer.gui;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorPanel extends JPanel
{
    public ErrorPanel(Throwable throwable)
    {
        JTextArea text = new JTextArea();
        text.setEditable(false);
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        text.setText(sw.toString().replace("\t", "   "));
        text.setCaretPosition(0);

        JScrollPane scroller = new JScrollPane(text);

        JButton button = new JButton("Close");
        button.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(scroller);
        add(buttonPanel);
    }

}
