package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ClickEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.gui.PageMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.PageMenuHolder;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class TaskMenu extends PageMenu {

    public TaskMenu(Plugin plugin) {
        super(plugin, "task-menu", "lang:[tasks-menu.title]", 54, Slot.of(2,2), Slot.of(5,8));
    }
    @Override
    public List<Item> pagedItems(Player player) {

        Collection<Task> tasks = XG7Plugins.taskManager().getTasks().values();

        List<Item> pagedItems = new ArrayList<>();

        Config lang = XG7Plugins.getInstance().getLangManager() == null ? XG7Plugins.getInstance().getConfig("messages") : XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join().getLangConfiguration();

        tasks.forEach(task -> {
            Item builder = Item.from(XMaterial.REPEATER.parseMaterial());
            builder.name("&e" + task.getName());
            builder.lore(lang.getList("tasks-menu.task-item", String.class).orElse(Collections.emptyList()));

            builder.setNBTTag("task-id", task.getPlugin().getName() + ":" + task.getName());
            builder.setNBTTag("task-state", task.getState().name());

            builder.setBuildPlaceholders(
                    Pair.of("plugin", task.getPlugin().getName()),
                    Pair.of("id", task.getPlugin().getName() + ":" + task.getName()),
                    Pair.of("state", task.getState().name()),
                    Pair.of("task_is_running", String.valueOf(task.getState() == TaskState.RUNNING)),
                    Pair.of("task_is_not_running", String.valueOf(task.getState() == TaskState.IDLE))
            );

            pagedItems.add(builder);
        });
        pagedItems.add(
                Item.from(XMaterial.CLOCK)
                        .name("&eTPS calculator")
                        .lore(lang.getList("tasks-menu.task-item", String.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(
                            Pair.of("plugin", "XG7Plugins"),
                                Pair.of("id", "TPS calculator"),
                                Pair.of("state", XG7Plugins.getInstance().getTpsCalculator().getState().name()),
                                Pair.of("task_is_running", String.valueOf(XG7Plugins.getInstance().getTpsCalculator().getState() == TaskState.RUNNING)),
                                Pair.of("task_is_not_running", String.valueOf(XG7Plugins.getInstance().getTpsCalculator().getState() == TaskState.IDLE))
                        ).setNBTTag("task-id", "TPS calculator")
                        .setNBTTag("task-state", XG7Plugins.getInstance().getTpsCalculator().getState())

        );


        return pagedItems;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected List<Item> items(Player player) {

        Config lang = XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join().getLangConfiguration();

        return Arrays.asList(
                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53),
                Item.from(XMaterial.ENDER_PEARL).name("lang:[refresh-item]").slot(0),
                Item.from(Material.PAPER).name(" ").lore(lang.getList("tasks-menu.notes", String.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(
                                Pair.of("tasks", String.valueOf(XG7Plugins.taskManager().getTasks().size())),
                                Pair.of("ram", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " / " + Runtime.getRuntime().totalMemory() / 1024 / 1024),
                                Pair.of("tps", String.format("%.2f", XG7Plugins.getInstance().getTpsCalculator().getTPS()))
                        ).slot(49));
    }

    @Override
    public void onClick(ClickEvent event) {
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        PageMenuHolder holder = (PageMenuHolder) event.getInventoryHolder();

        switch (event.getClickedSlot()) {
            case 0:
                refresh(holder);
                break;
            case 45:
                holder.previousPage();
                break;
            case 53:
                holder.nextPage();
                break;
            default:

                if (event.getClickedItem() == null || event.getClickedItem().isAir()) return;

                String taskId = event.getClickedItem().getTag("task-id", String.class).orElse(null);
                TaskState taskState = event.getClickedItem().getTag("task-state", TaskState.class).orElse(null);

                if (taskId == null) return;

                if (!XG7Plugins.taskManager().getTasks().containsKey(taskId) && !taskId.equals("TPS calculator")) {
                    Text.fromLang(player, XG7Plugins.getInstance(),"task-command.not-found").thenAccept(text -> text.send(player));
                    return;
                }

                if (event.getClickAction().isRightClick()) {
                    if (taskState == TaskState.RUNNING) {
                        if (taskId.equals("TPS calculator")) {
                            XG7Plugins.getInstance().getTpsCalculator().cancel();
                            Text.fromLang(player, XG7Plugins.getInstance(),"task-command.stopped").thenAccept(text -> text.send(player));
                            refresh(holder);
                            return;
                        }
                        XG7Plugins.taskManager().cancelTask(taskId);
                        Text.fromLang(player, XG7Plugins.getInstance(),"task-command.stopped").thenAccept(text -> text.send(player));
                        return;
                    }
                    if (taskId.equals("TPS calculator")) {
                        XG7Plugins.getInstance().getTpsCalculator().start();
                        Text.fromLang(player, XG7Plugins.getInstance(),"task-command.restarted").thenAccept(text -> text.send(player));
                        refresh(holder);
                        return;
                    }
                    XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(taskId));
                    Text.fromLang(player, XG7Plugins.getInstance(),"task-command.restarted").thenAccept(text -> text.send(player));

                    refresh(holder);
                    return;
                }
                if (event.getClickAction().isLeftClick()) {
                    Text.fromLang(player, XG7Plugins.getInstance(),"tasks-menu.copy-to-clipboard")
                            .thenAccept(text -> text.replace("id", taskId).send(player));
                }

                refresh(holder);

                break;
        }


    }
}
