package com.xg7plugins.xg7plugins.commands;

import com.xg7plugins.xg7plugins.Plugin;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Command {

    private Plugin plugin;

    private ICommand command;

}
