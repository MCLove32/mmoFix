package azisaba.net.mmofix;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

public class PacketListener extends ChannelDuplexHandler {

    private final Player p;
    private final MMOFix fix;
    public PacketListener(Player p, MMOFix fix) {
        this.fix = fix;
        this.p = p;
    }
    private static final Set<String> setCT = new HashSet<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof PacketPlayInWindowClick click) {

            Inventory inv = p.getOpenInventory().getInventory(click.i());
            if (inv != null && inv.getType().equals(InventoryType.CHEST)) {

                if (setCT.contains(p.getName())) return;
                setCT.add(p.getName());
                Bukkit.getScheduler().runTaskLaterAsynchronously(fix, () -> setCT.remove(p.getName()), 2);

            }
        }
        super.channelRead(ctx, msg);
    }
}
