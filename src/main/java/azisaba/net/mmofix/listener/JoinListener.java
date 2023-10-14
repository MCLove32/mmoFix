package azisaba.net.mmofix.listener;

import azisaba.net.mmofix.MMOFix;
import azisaba.net.mmofix.PacketListener;
import azisaba.net.mmoutils.MMOUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class JoinListener implements Listener {

    private final MMOFix fix;
    public JoinListener(MMOFix fix) {
        this.fix = fix;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {

        Player p = e.getPlayer();
        MMOUtils.getUtils().packetSetUP(p, MMOFix.id, new PacketListener(p, fix));
    }
}
