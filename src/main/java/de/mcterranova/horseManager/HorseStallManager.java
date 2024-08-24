package de.mcterranova.horseManager;

import de.mcterranova.horseManager.database.HorseDataDAO;
import de.mcterranova.horseManager.database.StoredHorseData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

public class HorseStallManager implements Listener {

    private final HorseManager plugin;

    public HorseStallManager(HorseManager plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }

    public boolean stableHorseCommand(Player player) {
        if (player.getVehicle() instanceof Horse horse) {
            if (horse.isTamed() && Objects.equals(horse.getOwner(), player)) {
                StoredHorseData horseData = new StoredHorseData(
                        horse.customName(),
                        horse.getHealth(),
                        horse.getJumpStrength(),
                        Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue(),
                        horse.getColor(),
                        horse.getStyle(),
                        horse.getInventory().getSaddle(),
                        horse.getInventory().getArmor()
                );

                HorseDataDAO.saveHorseData(player.getUniqueId(), horseData); // Speichere in der Datenbank
                horse.remove();

                player.sendMessage(Component.text("Your horse has been stabled!"));
                return true;
            } else {
                player.sendMessage(Component.text("You can only stable tamed horses that you own."));
            }
        } else {
            player.sendMessage(Component.text("You are not riding a horse."));
        }
        return false;
    }

    public void openStableInventory(Player player) {
        StoredHorseData stabledHorse = HorseDataDAO.loadHorseData(player.getUniqueId()); // Lade aus der Datenbank

        if (stabledHorse != null) {
            Inventory stableInventory = plugin.getServer().createInventory(null, 9, Component.text("Your Stable"));

            ItemStack horseItem = new ItemStack(Material.SADDLE);
            ItemMeta meta = horseItem.getItemMeta();
            Component horseName = stabledHorse.getName() != null ? stabledHorse.getName() : Component.text("Your Horse");
            meta.displayName(horseName);
            horseItem.setItemMeta(meta);

            stableInventory.setItem(0, horseItem);
            player.openInventory(stableInventory);
        } else {
            player.sendMessage(Component.text("You have no horse stabled."));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventory = event.getView();

        if (inventory.title().equals(Component.text("Your Stable"))) {
            event.setCancelled(true);

            StoredHorseData stabledHorse = HorseDataDAO.loadHorseData(player.getUniqueId()); // Lade aus der Datenbank
            if (stabledHorse != null) {
                Horse spawnedHorse = player.getWorld().spawn(player.getLocation(), Horse.class);
                spawnedHorse.setTamed(true);
                spawnedHorse.setOwner(player);
                spawnedHorse.customName(stabledHorse.getName());
                spawnedHorse.setHealth(stabledHorse.getHealth());
                spawnedHorse.setJumpStrength(stabledHorse.getJumpStrength());
                spawnedHorse.setColor(stabledHorse.getColor());
                spawnedHorse.setStyle(stabledHorse.getStyle());

                // Setze die gespeicherte Bewegungsgeschwindigkeit
                AttributeInstance movementSpeedAttribute = spawnedHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (movementSpeedAttribute != null) {
                    movementSpeedAttribute.setBaseValue(stabledHorse.getMovementSpeed());
                }

                // Wiederherstellen des Equipments (Sattel und Rüstung)
                spawnedHorse.getInventory().setSaddle(stabledHorse.getSaddle());
                spawnedHorse.getInventory().setArmor(stabledHorse.getArmor());

                // Entferne die Datenbankeinträge nach dem Spawnen des Pferdes
                HorseDataDAO.deleteHorseData(player.getUniqueId());

                player.sendMessage(Component.text("Your horse has been spawned!"));

                player.closeInventory();
            }
        }
    }
}
