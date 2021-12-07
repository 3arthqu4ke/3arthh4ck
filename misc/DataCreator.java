package datacreator;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Scanner;

public class DataCreator
{
    public static final String DATA =
        "import me.earth.earthhack.api.module.data.DefaultData;\n\n"+
        "final class DATAData extends DefaultData<DATA>\n" +
        "{\n" +
        "    public DATAData(DATA module)\n" +
        "    {\n" +
        "        super(module);\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public int getColor()\n" +
        "    {\n" +
        "        return 0xffffffff;\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public String getDescription()\n" +
        "    {\n" +
        "        return \"\";\n" +
        "    }\n" +
        "\n" +
        "}";

    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        while (true)
        {
            String line = scan.nextLine();
            if (line.equalsIgnoreCase("exit"))
            {
                return;
            }

            String replaced = DATA.replace("DATA", line);
            StringSelection stringSelection = new StringSelection(replaced);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }
    }

}
