package de.mcterranova.horseManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HorseStallCommand implements CommandExecutor, TabCompleter {

    private final HorseStallManager stallManager;

    public HorseStallCommand(HorseStallManager stallManager) {
        this.stallManager = stallManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if(args.length == 1) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "stable":
                        return stallManager.stableHorseCommand(player);
                    case "open":
                        stallManager.openStableInventory(player);
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
        List<String> l = new ArrayList<String>();
        if(cmd.getName().equalsIgnoreCase("horse") && args.length >= 0){
            if(sender instanceof Player){

                l.add("open");
                l.add("stable");

                return l;

            }
        }
        return l;
    }
}
