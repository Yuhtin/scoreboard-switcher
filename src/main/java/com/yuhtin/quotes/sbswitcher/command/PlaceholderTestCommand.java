package com.yuhtin.quotes.sbswitcher.command;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class PlaceholderTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // create string with args
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        sender.sendMessage(PlaceholderAPI.replacePlaceholders((Player) sender, stringBuilder.toString()));
        return false;
    }
}
