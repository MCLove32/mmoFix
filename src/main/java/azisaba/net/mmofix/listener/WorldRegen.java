package azisaba.net.mmofix.listener;

import azisaba.net.mmofix.MMOFix;
import azisaba.net.mmofix.utils.WorldUtil;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.event.MVWorldDeleteEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;

public class WorldRegen implements Listener {

    @EventHandler
    public void onTeleport(@NotNull PlayerChangedWorldEvent e) {

        Player p = e.getPlayer();
        org.bukkit.World w = p.getWorld();
        if (w.getName().contains("Field_D") && MMOFix.worldSet.contains(w.getName())) {

            p.sendMessage(Component.text("ワールド生成中はTPできません。", NamedTextColor.RED));
            p.teleport(new Location(Bukkit.getWorld("world"), 0, 64, 0, 180, 0));
        }
    }

    @EventHandler
    public void onDelete(@NotNull MVWorldDeleteEvent e) {

        MultiverseWorld world = e.getWorld();
        String name = world.getName();

        if (name.contains("Field") && !name.equals("Field")) {
            World w = BukkitAdapter.adapt(world.getCBWorld());

            RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(w);
            if (manager == null) return;

            ProtectedRegion reg = manager.getRegion(ProtectedRegion.GLOBAL_REGION);
            if (reg != null) WorldUtil.globalRg = reg.getFlags();

            Bukkit.getScheduler().runTaskLater(MMOFix.getFix(), ()->
                    WorldUtil.genWorldLForAuto(name, WorldUtil.getGenRandom(world.getGenerator())), 30L);
        }
    }
}
