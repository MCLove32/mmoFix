package azisaba.net.mmofix.listener;

import azisaba.net.mmoutils.MMOUtils;
import azisaba.net.mmoutils.utils.MythicUtil;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicListener implements Listener {

    @EventHandler
    public void cancelVInv(@NotNull InventoryOpenEvent e) {

        if (!e.getInventory().getType().equals(InventoryType.MERCHANT)) return;
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof WanderingTrader || holder instanceof Villager) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(@NotNull PlayerInteractEvent e) {

        Player p = e.getPlayer();
        closeBad(checkAndRunItem(p.getInventory().getItemInMainHand()), p);
        closeBad(checkAndRunItem(p.getInventory().getItemInOffHand()), p);
    }

    @Nullable
    public ItemStack checkAndRunItem(ItemStack item) {

        String mmid = MythicUtil.getMythicID(item);
        if (mmid == null) return null;

        return MythicBukkit.inst().getItemManager().getItemStack(mmid);
    }

    public void closeBad(ItemStack item, Player p) {

        if (item == null) return;
        if (!(MMOUtils.getCrucible().getItemManager().getInventoryManager().isBag(item))) return;

        p.closeInventory();
    }
}
