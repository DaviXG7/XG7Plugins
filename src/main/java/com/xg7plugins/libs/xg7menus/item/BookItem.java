package com.xg7plugins.libs.xg7menus.item;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenBook;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.nms.NMSUtil;
import com.xg7plugins.utils.reflection.nms.Packet;
import com.xg7plugins.utils.reflection.nms.PacketClass;
import com.xg7plugins.utils.reflection.nms.PlayerNMS;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookItem extends Item {

    public BookItem() {
        super(new ItemStack(Material.WRITTEN_BOOK));
        title("Blank");
        author("None");
    }
    public BookItem(ItemStack book) {
        super(book);
        if (!book.getType().equals(Material.WRITTEN_BOOK)) throw new IllegalArgumentException("This item isn't a writable book!");
        title("Blank");
        author("None");
    }

    public static BookItem newBook() {
        return new BookItem();
    }

    public static BookItem from(ItemStack book) {
        return new BookItem(book);
    }

    public BookItem title(String title) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setTitle(title);
        super.meta(meta);
        return this;
    }
    public BookItem author(String author) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setAuthor(author);
        super.meta(meta);
        return this;
    }
    public BookItem addPage(String text) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.addPage(text);
        super.meta(meta);
        return this;
    }
    public BookItem addPage(BaseComponent[] components) {

        try {
            BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
            meta.spigot().addPage(components);
            super.meta(meta);
            return this;
        } catch (Exception ignored) {
            if (XG7Plugins.getMinecraftVersion() < 8) {
                XG7Plugins.getInstance().warn("Books with base component is not supported on this version!");
                return this;
            }
        }

        return this;
    }

    @SneakyThrows
    public void openBook(Player player) {

        if (XG7Plugins.getMinecraftVersion() > 13) {
            player.openBook(this.itemStack);
            return;
        }

        if (XG7Plugins.getMinecraftVersion() < 8) {
            XG7Plugins.getInstance().warn("Books is not supported on version under of 1.8!");
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, this.itemStack);

        WrapperPlayServerOpenBook packet = new WrapperPlayServerOpenBook(InteractionHand.MAIN_HAND);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

        player.getInventory().setItem(slot, old);
    }
}
