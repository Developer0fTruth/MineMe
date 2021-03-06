/*
 * Copyright (C) 2016 Selma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.mineme.mines.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.ddevil.core.utils.PotionUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineType;
import me.ddevil.mineme.mines.impl.CircularMine;
import me.ddevil.mineme.mines.impl.CuboidMine;
import me.ddevil.mineme.mines.impl.PolygonMine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author Selma
 */
public class MineConfig {

    public static Mine loadMine(MineConfig c) {
        MineType type = c.getType();
        switch (type) {
            case CUBOID:
                return new CuboidMine(c);
            case CIRCULAR:
                return new CircularMine(c);
            case POLYGON:
                return new PolygonMine(c);
            default:
                return null;
        }
    }

    //General info
    private final boolean enabled;
    private final FileConfiguration config;
    private final World world;
    private final MineType type;
    private final String name;
    private final String alias;

    //Broadcast info
    private final String broadcastMessage;
    private final boolean broadcastOnReset;
    private final boolean broadcastNearby;
    private final double broadcastRadius;

    //Tecnical info
    private final int resetDelay;
    private final HashMap<ItemStack, Double> composition;
    private final boolean useCustomBroadcast;
    private final ArrayList<PotionEffect> potionEffects;

    public MineConfig(FileConfiguration mine) {
        this.config = mine;
        world = Bukkit.getWorld(mine.getString("world"));
        type = MineType.valueOf(mine.getString("type"));
        name = mine.getString("name");
        alias = mine.getString("alias");
        enabled = mine.getBoolean("enabled");
        broadcastOnReset = mine.getBoolean("broadcastOnReset");
        broadcastNearby = mine.getBoolean("broadcastToNearbyOnly");
        useCustomBroadcast = mine.getBoolean("useCustomBroadcast");
        broadcastMessage = mine.getString("customBroadcast");
        broadcastRadius = mine.getDouble("broadcastRadius");
        resetDelay = mine.getInt("resetDelay");
        HashMap<ItemStack, Double> comp = new HashMap();
        if (!mine.contains("composition")) {
            mine.set("composition", comp);
        } else {
            for (String s : mine.getStringList("composition")) {
                String[] split = s.split("=");

                String[] materialanddata = split[0].split(":");
                Material mat = Material.valueOf(materialanddata[0]);
                Byte b = null;
                if (materialanddata.length > 1) {
                    try {
                        b = Byte.valueOf(materialanddata[1]);
                    } catch (NumberFormatException exception) {
                        MineMe.getInstance().debug(split[1] + " in " + s + "isn't a number! Setting byte to 0");
                        b = 0;
                    }
                } else {
                    b = 0;
                }
                ItemStack f = new ItemStack(mat, 1, (short) 0, b);
                try {
                    comp.put(f, Double.valueOf(split[1]));
                } catch (NumberFormatException e) {
                    MineMe.getInstance().debug(split[1] + " in " + s + "isn't a number!");
                }
            }
        }
        this.composition = comp;
        this.potionEffects = new ArrayList();

        //Load effects
        List<String> stringList = config.getStringList("effects.list");

        if (!config.contains("effects.use")) {
            config.set("effects.use", false);
        } else {
            if (!config.contains("effects.list")) {
                config.set("effects.list", potionEffects);
            }
            for (String s : stringList) {
                try {
                    potionEffects.add(PotionUtils.parsePotionEffect(s));
                } catch (PotionUtils.PotionParseException ex) {
                    MineMe.instance.printException("There was an error parsing " + s + " from the effect list!", ex);
                }
            }
        }
    }

    public boolean isUseCustomBroadcast() {
        return useCustomBroadcast;
    }

    public HashMap<ItemStack, Double> getComposition() {
        return composition;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public boolean isNearbyBroadcast() {
        return broadcastNearby;
    }

    public boolean isBroadcastOnReset() {
        return broadcastOnReset;
    }

    public int getResetDelay() {
        return resetDelay;
    }

    public double getBroadcastRadius() {
        return broadcastRadius;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public World getWorld() {
        return world;
    }

    public MineType getType() {
        return type;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getAlias() {
        return alias;
    }

    public List<PotionEffect> getEffects() {
        return potionEffects;
    }

}
