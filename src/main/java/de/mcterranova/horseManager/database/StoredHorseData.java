package de.mcterranova.horseManager.database;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

public class StoredHorseData {

    private final Component name;
    private final double health;
    private final double jumpStrength;
    private final double movementSpeed;
    private final Horse.Color color;
    private final Horse.Style style;
    private final ItemStack saddle;
    private final ItemStack armor;

    public StoredHorseData(Component name, double health, double jumpStrength, double movementSpeed,
                           Horse.Color color, Horse.Style style, ItemStack saddle, ItemStack armor) {
        this.name = name;
        this.health = health;
        this.jumpStrength = jumpStrength;
        this.movementSpeed = movementSpeed;
        this.color = color;
        this.style = style;
        this.saddle = saddle;
        this.armor = armor;
    }

    // Getter-Methoden
    public Component getName() {
        return name;
    }

    public double getHealth() {
        return health;
    }

    public double getJumpStrength() {
        return jumpStrength;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public Horse.Color getColor() {
        return color;
    }

    public Horse.Style getStyle() {
        return style;
    }

    public ItemStack getSaddle() {
        return saddle;
    }

    public ItemStack getArmor() {
        return armor;
    }
}
