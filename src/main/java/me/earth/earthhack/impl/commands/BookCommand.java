package me.earth.earthhack.impl.commands;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.gui.chat.util.ColorEnum;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.EnumHand;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Scanner;

public class BookCommand extends Command implements Globals
{

    private static final int MAX_CHARACTERS_PER_PAGE = 256;
    private static final int MAX_PAGES = 50;

    public static final String NUMBER_TOKEN = "\\{NUMBER\\}";

    public static final String NEW_PAGE = ":PAGE:";

    private static final Collection<Character> CHARS_NO_REPEATING =
            Lists.newArrayList(' ', '\n', '\t', '\r');

    private int page = 0;

    public BookCommand()
    {
        super(new String[][]{{"book"},
                {"file"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 2)
        {
            page = 0;
            File file = new File(args[1]);
            if (!file.exists()
                    || file.isDirectory())
            {
                ChatUtil.sendMessage(ColorEnum.Red + "File does not exist or is a directory!");
                return;
            }

            ItemStack book = mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (book.getItem() != Items.WRITABLE_BOOK)
            {
                ChatUtil.sendMessage(ColorEnum.Red + "Not holding a book!");
                return;
            }

            String contents = loadFile(file);
            Scanner scanner = newScanner(contents);

            mc.addScheduledTask(() -> {
                mc.player.openBook(mc.player.getHeldItemMainhand(), EnumHand.MAIN_HAND);
                sendBook(mc.player.getHeldItemMainhand(), scanner);
                mc.displayGuiScreen(null);
            });
        }
    }

    private static String parseText(String text, boolean wrap)
    {
        text = text.replace('\r', '\n').replace('\t', ' ').replace("\0", "");

        StringBuilder builder = new StringBuilder();

        char next = '\0', last;
        int ls = -1; // last space index
        for (int i = 0, p = i; i < text.length(); i++, p++, p %= MAX_CHARACTERS_PER_PAGE) {
            // previous character
            last = next;
            // next character
            next = text.charAt(i);

            // start a new page at the initial position
            if (p == 0) {
                builder.append(NEW_PAGE);
            }

            // if this index contains a space, save the index
            if (next == ' ') {
                ls = i;
            }

            // prevent annoying repeating characters
            if (CHARS_NO_REPEATING.contains(next) && CHARS_NO_REPEATING.contains(last)) {
                // do not append, go back 1 position to act as if this was never processed
                p--;
                continue;
            }

            // word wrapping logic
            if (wrap && ls != -1 && last == ' ') {
                // next space index
                int ns = text.indexOf(' ', i);
                // distance from next space to last space
                int d = ns - ls;

                // if the word (distance between two spaces) is less than the max chars allowed (to prevent
                // words greater than it from causing an infinite loop), and
                // the word will not fit onto the current page.
                if (d < MAX_CHARACTERS_PER_PAGE && (p + d) > MAX_CHARACTERS_PER_PAGE) {
                    // insert new page
                    builder.append(NEW_PAGE);
                    // start at position 0
                    p = 0;
                }
            }

            builder.append(next);
        }

        return builder.toString();
    }

    private String loadFile(File file) throws RuntimeException
    {
        Path data = file.getAbsoluteFile().toPath();

        if (!Files.exists(data)) {
            throw new RuntimeException("File not found");
        }
        if (!Files.isRegularFile(data)) {
            throw new RuntimeException("Not a file type");
        }

        String text;
        try {
            text = new String(Files.readAllBytes(data), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file");
        }

        String name = data.getFileName().toString();
        if (name.endsWith(".txt") || name.endsWith(".book")) {
            return text;
        } else {
            throw new RuntimeException("File is not a .txt or .book type");
        }
    }

    private Scanner newScanner(String contents)
    {
        return new Scanner(contents).useDelimiter(NEW_PAGE);
    }

    private void sendBook(ItemStack stack, Scanner parser)
    {
        NBTTagList pages = new NBTTagList(); // page tag list

        // copy pages into NBT
        for (int i = 0; i < MAX_PAGES && parser.hasNext(); i++) {
            pages.appendTag(new NBTTagString(parser.next().trim()));
            page++;
        }

        // set our client side book
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setTag("pages", pages);
        } else {
            stack.setTagInfo("pages", pages);
        }

        // publish the book
        stack.setTagInfo("author", new NBTTagString(mc.player.getName()));
        stack.setTagInfo(
                "title",
                new NBTTagString("megyn own u".trim()));

        PacketBuffer buff = new PacketBuffer(Unpooled.buffer());
        buff.writeItemStack(stack);
        NetworkUtil.send(new CPacketCustomPayload("MC|BSign", buff));
    }

    public int getBook()
    {
        return page > 0 ? (int) Math.ceil((double) (page) / (double) (MAX_PAGES)) : 0;
    }

}
