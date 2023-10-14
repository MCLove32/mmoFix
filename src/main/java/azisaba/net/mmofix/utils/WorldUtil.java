package azisaba.net.mmofix.utils;

import azisaba.net.mmofix.MMOFix;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;

import java.util.*;

import static azisaba.net.mmofix.MMOFix.getMVInst;
import static azisaba.net.mmofix.MMOFix.ran;

public class WorldUtil {

    public static Map<Flag<?>, Object> globalRg = new HashMap<>();

    public static boolean isSelectedWorld(String name) {

        for (String s: MMOFix.getFix().getConfig().getStringList("Fields")) {
            if (s == null) continue;
            if (s.equals(name)) return true;
        }
        return false;
    }

    public static boolean containsSelectedWorld(String name) {

        for (String s: MMOFix.getFix().getConfig().getStringList("Fields")) {
            if (s == null) continue;
            if (s.contains(name)) return true;
        }
        return false;
    }

    public static boolean isSurvivalWorld(String name) {

        for (String s: MMOFix.getFix().getConfig().getStringList("SurvivalWorlds")) {
            if (s == null) continue;
            if (s.equals(name)) return true;
        }
        return false;
    }

    public static void genWorldLForAuto(String name, String type) {

        if (getMVInst().isMVWorld(name)) return;
        if (getMVInst().addWorld(name, World.Environment.NORMAL, null, WorldType.LARGE_BIOMES, true, type)) {

            World w = Bukkit.getWorld(name);
            if (w == null) return;
            worldSet(w);
        }
    }

    public static void worldSet(World w) {

        Bukkit.getScheduler().runTaskLater(MMOFix.getFix(), () -> {

            MultiverseWorld mvWorld = getMVInst().getMVWorld(w);
            WorldBorder border = w.getWorldBorder();

            //mvWorld.setSpawnLocation(new Location(w, 0, 64, 0));
            mvWorld.setPVPMode(false);
            mvWorld.setPlayerLimit(20);
            border.setCenter(0, 0);
            border.setSize(5000);

            //変更不可
            mvWorld.setKeepSpawnInMemory(false);
            w.setTime(14000L);
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            w.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            w.setGameRule(GameRule.KEEP_INVENTORY, true);
            w.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 0);
            w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            w.setGameRule(GameRule.DO_ENTITY_DROPS, false);
            w.setGameRule(GameRule.DISABLE_RAIDS, true);
            w.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
            w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            w.setGameRule(GameRule.MOB_GRIEFING, false);
            w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            w.setGameRule(GameRule.DO_MOB_LOOT, false);
            w.setGameRule(GameRule.DO_TILE_DROPS, false);
            w.setGameRule(GameRule.DO_ENTITY_DROPS, true);

            try {
                com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(w);
                RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
                if (manager == null) return;

                ProtectedRegion reg = manager.getRegion(ProtectedRegion.GLOBAL_REGION);
                if (globalRg != null && !globalRg.isEmpty() && reg != null) {
                    reg.setFlags(globalRg);
                }
                List<ProtectedRegion> list = new ArrayList<>();
                list.add(reg);

                manager.setRegions(list);
                manager.save();

            } catch (Exception ignored) {
            }
        }, 10L);

        Bukkit.getScheduler().runTaskLater(MMOFix.getFix(), ()-> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm re -a");
            MMOFix.worldSet.remove(w.getName());
        }, 40L);
    }

    public static String getGenRandom(String name) {

        List<String> list = new ArrayList<>(List.of("Terra:SUBSTRATUM",
                "Terra:SUBSTRATUM",
                "Terra:HYDRAXIA",
                "Terra:HYDRAXIA",
                "Terra:HYDRAXIA",
                "Terra:HYDRAXIA",
                "Terra:HYDRAXIA"));

        if (name != null) {

            for (int i = 0; i < 4; i++) {
                list.add(name);
            }
        }

        int get = ran.nextInt(list.size());
        return list.get(get);
    }
}
