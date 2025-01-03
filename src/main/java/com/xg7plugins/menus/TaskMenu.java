package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menus.gui.PageMenu;
import com.xg7plugins.libs.xg7menus.menus.holders.PageMenuHolder;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
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

        Config lang = XG7Plugins.getInstance().getLangManager() == null ? XG7Plugins.getInstance().getConfig("messages") : Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join());

        tasks.forEach(task -> {
            Item builder = Item.from(XMaterial.REPEATER.parseMaterial());
            builder.name("&e" + task.getName());
            builder.lore(lang.get("tasks-menu.task-item", List.class).orElse(Collections.emptyList()));

            builder.setNBTTag("task-id", task.getPlugin().getName() + ":" + task.getName());
            builder.setNBTTag("task-state", task.getState().name());

            builder.setBuildPlaceholders(new HashMap<String, String>() {{
                put("[PLUGIN]", task.getPlugin().getName());
                put("[ID]", task.getPlugin().getName() + ":" + task.getName());
                put("[STATE]", task.getState().name());
                put("%task_is_running%", String.valueOf(task.getState() == TaskState.RUNNING));
                put("%task_is_not_running%", String.valueOf(task.getState() == TaskState.IDLE));
            }});

            pagedItems.add(builder);
        });
        pagedItems.add(
                Item.from(XMaterial.CLOCK)
                        .name("&eTPS calculator")
                        .lore(lang.get("tasks-menu.task-item", List.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(new HashMap<String, String>() {{
                            put("[PLUGIN]", "XG7Plugins");
                            put("[ID]", "TPS calculator");
                            put("[STATE]", XG7Plugins.getInstance().getTpsCalculator().getState().name());
                            put("%task_is_running%", String.valueOf(XG7Plugins.getInstance().getTpsCalculator().getState() == TaskState.RUNNING));
                            put("%task_is_not_running%", String.valueOf(XG7Plugins.getInstance().getTpsCalculator().getState() == TaskState.IDLE));
                        }}).setNBTTag("task-id", "TPS calculator")
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

        Config lang = XG7Plugins.getInstance().getLangManager() == null ? XG7Plugins.getInstance().getConfig("messages") : Config.of(XG7Plugins.getInstance(), XG7Plugins.getInstance().getLangManager().getLangByPlayer(XG7Plugins.getInstance(), player).join());

        return Arrays.asList(
                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53),
                Item.from(XMaterial.ENDER_PEARL).name("lang:[refresh-item]").slot(0),
                Item.from(Material.PAPER).name(" ").lore(lang.get("tasks-menu.notes", List.class).orElse(Collections.emptyList()))
                        .setBuildPlaceholders(new HashMap<String, String>() {{
                            put("[TASKS]", String.valueOf(XG7Plugins.taskManager().getTasks().size()));
                            put("[RAM]", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " / " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
                            put("[TPS]", String.format("%.2f", XG7Plugins.getInstance().getTpsCalculator().getTPS()));
                        }}).slot(49));
    }

    @Override
    public <T extends MenuEvent> void onClick(T event) {
        event.setCancelled(true);
        if (!(event instanceof ClickEvent)) return;
        ClickEvent clickEvent = (ClickEvent) event;
        Player player = (Player) clickEvent.getWhoClicked();

        PageMenuHolder holder = (PageMenuHolder) clickEvent.getInventoryHolder();

        switch (clickEvent.getClickedSlot()) {
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

                if (clickEvent.getClickedItem() == null || clickEvent.getClickedItem().isAir()) return;

                String taskId = clickEvent.getClickedItem().getTag("task-id", String.class).orElse(null);
                TaskState taskState = clickEvent.getClickedItem().getTag("task-state", TaskState.class).orElse(null);

                if (taskId == null) return;

                if (!XG7Plugins.taskManager().getTasks().containsKey(taskId) && !taskId.equals("TPS calculator")) {
                    Text.format("lang:[task-command.not-found]", plugin).send(player);
                    return;
                }

                if (clickEvent.getClickAction().isRightClick()) {
                    if (taskState == TaskState.RUNNING) {
                        if (taskId.equals("TPS calculator")) {
                            XG7Plugins.getInstance().getTpsCalculator().cancel();
                            Text.format("lang:[task-command.stopped]", plugin).send(player);
                            refresh(holder);
                            return;
                        }
                        XG7Plugins.taskManager().cancelTask(taskId);
                        Text.format("lang:[task-command.stopped]", plugin).send(player);
                        refresh(holder);
                        return;
                    }
                    if (taskId.equals("TPS calculator")) {
                        XG7Plugins.getInstance().getTpsCalculator().start();
                        Text.format("lang:[task-command.restarted]", plugin).send(player);
                        refresh(holder);
                        return;
                    }
                    XG7Plugins.taskManager().runTask(XG7Plugins.taskManager().getTasks().get(taskId));
                    Text.format("lang:[task-command.restarted]", plugin).send(player);

                    refresh(holder);
                    return;
                }
                if (clickEvent.getClickAction().isLeftClick()) {
                    Text.formatComponent("lang:[tasks-menu.copy-to-clipboard]", plugin).replace("[ID]", taskId).send(player);
                }

                refresh(holder);

                break;
        }


    }
}
