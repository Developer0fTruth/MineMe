package me.ddevil.core;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPlugin extends JavaPlugin implements Listener {

    public static CustomPlugin instance;
    protected static CommandMap commandMap;

    @Override
    public void onEnable() {
        instance = this;
        try {
            if (Bukkit.getServer() instanceof CraftServer) {
                final Field f = CraftServer.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getServer());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Bukkit.getServer().shutdown();
        }
    }

    public static void registerCommand(Command cmd) {
        CustomPlugin.registerCommand(instance, cmd);
    }

    public static void registerCommand(Plugin pl, Command cmd) {
        CustomPlugin.registerCommand(pl.getName(), cmd);
    }

    private static void registerCommand(String pl, Command cmd) {
        commandMap.register(pl, cmd);
    }

    public static boolean isPermissionRegistered(String permission) {
        for (Permission p : Bukkit.getPluginManager().getPermissions()) {
            if (p.getName().equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }

    public static void registerPermission(String permission) {
        if (!isPermissionRegistered(permission)) {
            Bukkit.getPluginManager().addPermission(new Permission(permission));
        }
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public FileConfiguration loadConfig() {
        FileConfiguration fc = getConfig();
        fc.addDefault("lol", "I did!");
        fc.options().copyDefaults(true);
        saveConfig();
        return fc;
    }
}
