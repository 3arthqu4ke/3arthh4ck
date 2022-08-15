package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;

public enum ConsoleColors implements Function<ITextComponent, String> {
    Unformatted() {
        @Override
        public String apply(ITextComponent iTextComponent) {
            return iTextComponent.getUnformattedText();
        }
    },
    Formatted() {
        @Override
        public String apply(ITextComponent iTextComponent) {
            return iTextComponent.getFormattedText();
        }
    },
    Ansi() {
        @Override
        public String apply(ITextComponent iTextComponent) {
            String formatted = iTextComponent.getFormattedText();
            StringBuilder builder = new StringBuilder(
                formatted.length() + AnsiColors.RESET.length());
            boolean readSectionSign = false;
            boolean readCustomColor = false;
            int customColorCounter = 0;
            for (int i = 0; i < formatted.length(); i++) {
                char ch = formatted.charAt(i);
                if (readCustomColor) {
                    if (++customColorCounter == 8) {
                        readCustomColor = false;
                        customColorCounter = 0;
                    }
                } else if (ch == TextColor.SECTIONSIGN) {
                    if (readSectionSign) {
                        builder.append(ch);
                    }

                    readSectionSign = true;
                } else if (readSectionSign) {
                    // switch because it's faster than looking up the char in TextColors
                    switch (ch) {
                        case 'z':
                            readCustomColor = true;
                            break;
                        case '0':
                            builder.append(AnsiColors.BLACK);
                            break;
                        case '1':
                        case '9':
                            builder.append(AnsiColors.BLUE);
                            break;
                        case '2':
                        case 'a':
                            builder.append(AnsiColors.GREEN);
                            break;
                        case '3':
                        case 'b':
                            builder.append(AnsiColors.CYAN);
                            break;
                        case '4':
                        case 'c':
                            builder.append(AnsiColors.RED);
                            break;
                        case '5':
                        case 'd':
                            builder.append(AnsiColors.MAGENTA);
                            break;
                        case '6':
                        case 'e':
                            builder.append(AnsiColors.YELLOW);
                            break;
                        case '7':
                        case '8':
                            builder.append(AnsiColors.WHITE);
                            break;
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'y':
                        case '+': // TODO: RAINBOW!!!
                        case '-':
                        case 'p':
                            break;
                        default:
                            builder.append(AnsiColors.RESET);
                            break;
                    }

                    readSectionSign = false;
                } else {
                    builder.append(ch);
                }
            }

            return builder.append(AnsiColors.RESET).toString();
        }
    }

}
