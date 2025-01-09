package com.xg7plugins.help.formhelp;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7geyserforms.forms.ModalForm;
import com.xg7plugins.libs.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandForm extends SimpleForm {

    private final Map<String, ICommand> commands;
    private final CommandForm superForm;

    @Getter
    private final HelpCommandForm guiOrigin;

    public CommandForm(List<ICommand> commands, String customTitle, CommandForm superForm) {
        super("command-form" + UUID.randomUUID(), customTitle == null ? "Commands" : customTitle, XG7Plugins.getInstance());

        this.commands = commands.stream().collect(
                java.util.stream.Collectors.toMap(
                        command -> command.getClass().getAnnotation(com.xg7plugins.commands.setup.Command.class).name(),
                        command -> command
                )
        );

        this.guiOrigin = plugin.getHelpCommandForm();
        this.superForm = superForm;

    }

    @Override
    public String content(Player player) {
        return "lang:[commands-form.content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = commands.values().stream().map(
                command -> ButtonComponent.of(command.getClass().getAnnotation(Command.class).name())
        ).collect(Collectors.toList());

        buttons.add(ButtonComponent.of(Text.format("lang:[commands-form.back]", plugin).getWithPlaceholders(player)));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        ButtonComponent clickedButton = buttons(player).get(result.clickedButtonId());

        if (result.clickedButtonId() == buttons(player).size() - 1) {
            guiOrigin.getForm("index").send(player);
            return;
        }

        ICommand command = commands.get(clickedButton.text());
        if (command == null) {
            guiOrigin.getForm("index").send(player);
            return;
        }
        CommandDescription commandDescription = new CommandDescription(this, command, command.getIcon());
        commandDescription.send(player);

    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {
        guiOrigin.getForm("index").send(player);
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {
        guiOrigin.getForm("index").send(player);
    }

    private class CommandDescription extends ModalForm {

        private CommandForm origin;
        private ICommand command;

        public CommandDescription(CommandForm origin, ICommand command, Item commandIcon) {
            super(
                    "command-desc" + UUID.randomUUID(),
                    "Contents of command: " + command.getClass().getAnnotation(Command.class).name(),
                    XG7Plugins.getInstance(),

                    commandIcon.getItemStack().getItemMeta().getLore().get(0) + "\n" +
                    commandIcon.getItemStack().getItemMeta().getLore().get(1) + "\n" +
                    commandIcon.getItemStack().getItemMeta().getLore().get(2) + "\n" +
                    commandIcon.getItemStack().getItemMeta().getLore().get(3),

                    command.getSubCommands().length == 0 ? "lang:[commands-form.no-subcommands]" : "lang:[commands-form.subcommands-label]",
                    "lang:[commands-form.subcommands-back]"
            );
            this.origin = origin;
            this.command = command;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void onFinish(org.geysermc.cumulus.form.ModalForm form, ModalFormResponse result, Player player) {
            if (result.clickedFirst()) {
                if (command.getSubCommands().length == 0) {
                    FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
                    return;
                }

                CommandForm commandMenu = new CommandForm(Arrays.asList(command.getSubCommands()), "Subcommands of: " + command.getClass().getAnnotation(Command.class).name(), origin);
                commandMenu.send(player);
            } origin.send(player);

        }

        @Override
        public void onError(org.geysermc.cumulus.form.ModalForm form, InvalidFormResponseResult<ModalFormResponse> result, Player player) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        }

        @Override
        public void onClose(org.geysermc.cumulus.form.ModalForm form, Player player) {
            origin.getGuiOrigin().getForm("index").send(player);
        }
    }
}
