package com.xg7plugins.xg7plugins.api.adapted.xg7menus.item;

import com.xg7plugins.xg7menus.api.menus.MenuException;
import com.xg7plugins.xg7menus.api.menus.builders.BaseItemBuilder;
import com.xg7plugins.xg7menus.api.utils.Log;
import com.xg7plugins.xg7menus.api.utils.NMSUtil;
import com.xg7plugins.xg7menus.api.utils.Text;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BookItemBuilder extends BaseItemBuilder<BookItemBuilder> {
    public BookItemBuilder() {
        super(new ItemStack(Material.WRITTEN_BOOK));
        title("Blank");
        author("None");
    }
    public BookItemBuilder(ItemStack book) {
        super(book);
        title("Blank");
        author("None");
    }

    @Contract("_ -> new")
    public static @NotNull BookItemBuilder from(@NotNull ItemStack book) {
        if (!book.getType().equals(Material.WRITTEN_BOOK)) throw new MenuException("This item isn't a writable book!");
        return new BookItemBuilder(book);
    }
    @Contract(" -> new")
    public static @NotNull BookItemBuilder builder() {
        return new BookItemBuilder();
    }
    public BookItemBuilder title(String title) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setTitle(Text.format(title).getText());
        super.meta(meta);
        return this;
    }
    public BookItemBuilder author(String author) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.setAuthor(author);
        super.meta(meta);
        return this;
    }
    public BookItemBuilder addPage(String text) {
        BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
        meta.addPage(Text.format(text).getText());
        super.meta(meta);
        return this;
    }
    public BookItemBuilder addPage(BaseComponent[] components) {

        try {
            BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
            meta.spigot().addPage(components);
            super.meta(meta);
            return this;
        } catch (Exception ignored) {
            if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) < 8) {
                Log.warn("Books with base component is not supported on this version!");
                return this;
            }
        }

        return this;
    }

    @SneakyThrows
    public void openBook(Player player) {

        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) > 13) {
            player.openBook(this.itemStack);
            return;
        }

        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) < 8) {
            Log.warn("Books is not supported on version under of 1.8!");
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, this.itemStack);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, 0);
        buf.writerIndex(1);

        Object packet = NMSUtil.getNMSClass("PacketPlayOutCustomPayload")
                .getConstructor(
                        String.class,
                        NMSUtil.getNMSClass("PacketDataSerializer")
                ).newInstance("MC|BOpen", NMSUtil.getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class).newInstance(buf));
        Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
        Object cPlayer = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").cast(player);
        Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(cPlayer);
        Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);
        playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packet);

        player.getInventory().setItem(slot, old);
    }
}
