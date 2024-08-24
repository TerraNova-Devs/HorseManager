package de.mcterranova.horseManager.database;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class HorseDataDAO {

    public static void saveHorseData(UUID playerUUID, StoredHorseData horseData) {
        try (Connection connection = HikariCPDatabase.getInstance().getConnection()) {
            String sql = "REPLACE INTO horse_data (player_uuid, horse_name, health, jump_strength, movement_speed, color, style, saddle, armor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, horseData.getName() != null ? horseData.getName().toString() : null);
                statement.setDouble(3, horseData.getHealth());
                statement.setDouble(4, horseData.getJumpStrength());
                statement.setDouble(5, horseData.getMovementSpeed());
                statement.setString(6, horseData.getColor().name());
                statement.setString(7, horseData.getStyle().name());
                statement.setBytes(8, serializeItemStack(horseData.getSaddle()));
                statement.setBytes(9, serializeItemStack(horseData.getArmor()));

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static StoredHorseData loadHorseData(UUID playerUUID) {
        try (Connection connection = HikariCPDatabase.getInstance().getConnection()) {
            String sql = "SELECT * FROM horse_data WHERE player_uuid = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return new StoredHorseData(
                                resultSet.getString("horse_name") != null ? Component.text(resultSet.getString("horse_name")) : null,
                                resultSet.getDouble("health"),
                                resultSet.getDouble("jump_strength"),
                                resultSet.getDouble("movement_speed"),
                                Horse.Color.valueOf(resultSet.getString("color")),
                                Horse.Style.valueOf(resultSet.getString("style")),
                                deserializeItemStack(resultSet.getBytes("saddle")),
                                deserializeItemStack(resultSet.getBytes("armor"))
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteHorseData(UUID playerUUID) {
        try (Connection connection = HikariCPDatabase.getInstance().getConnection()) {
            String sql = "DELETE FROM horse_data WHERE player_uuid = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static byte[] serializeItemStack(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(item);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ItemStack deserializeItemStack(byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (ItemStack) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
