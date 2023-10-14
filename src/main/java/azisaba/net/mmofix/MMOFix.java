package azisaba.net.mmofix;

import azisaba.net.mmofix.listener.*;
import azisaba.net.mmofix.utils.WorldUtil;
import azisaba.net.mmoutils.MMOUtils;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class MMOFix extends JavaPlugin {

    private static MMOFix fix;
    public static final Set<String> worldSet = new HashSet<>();
    private static MultiverseCore core;
    public static final Random ran = new Random();
    public static final String id = "mmo_fix";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        fix = this;
        // Plugin startup logic
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MythicListener(), this);
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new WorldFix(this), this);
        pm.registerEvents(new ItemFix(), this);
        pm.registerEvents(new WorldRegen(), this);

        Bukkit.getOnlinePlayers().forEach(player -> MMOUtils.getUtils().packetSetUP(player, id, new PacketListener(player, this)));

        if (pm.getPlugin("Multiverse-Core") != null) {
            core = (MultiverseCore) pm.getPlugin("Multiverse-Core");
        } else {
            pm.disablePlugin(this);
        }
        deleteAndReCreate();
    }

    public static MVWorldManager getMVInst() {return core.getMVWorldManager();}

    public void deleteAndReCreate() {

        String name = "Field_D_";
        for (int i = 1; i <= 6; i++) {

            World w = Bukkit.getWorld(name + i);
            if (w == null) {

                int finalI = i;
                Bukkit.getScheduler().runTaskLater(this, ()->
                        WorldUtil.genWorldLForAuto(name + finalI, WorldUtil.getGenRandom(null)), 1200 * i);
                continue;
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(Component.text("30秒後に" + w.getName() + "を再生成します。", NamedTextColor.AQUA));
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 0);
                }
            }, 600L + (1200L * (i - 1)));

            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {

                worldSet.add(w.getName());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(Component.text("10秒後に" + w.getName() + "を再生成します。", NamedTextColor.AQUA));
                    p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 0);
                }
            }, 1000L + (1200L * (i - 1)));

            Bukkit.getScheduler().runTaskLater(this, ()-> {

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvdelete " + w.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv confirm");
            }, 1200 * i);
        }
    }

    public static MMOFix getFix() {return fix;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getOnlinePlayers().forEach(player -> MMOUtils.getUtils().packetRemove(player, id));
    }
}
