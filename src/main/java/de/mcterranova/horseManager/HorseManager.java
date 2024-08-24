package de.mcterranova.horseManager;

import org.bukkit.plugin.java.JavaPlugin;

public class HorseManager extends JavaPlugin {

    @Override
    public void onEnable() {

        HorseStallManager horseStallManager = new HorseStallManager(this);


        horseStallManager.registerEvents();

        getCommand("horse").setExecutor(new HorseStallCommand(horseStallManager));
    }

    @Override
    public void onDisable() {
        getLogger().info("Horse Stall Plugin disabled!");
    }
}
