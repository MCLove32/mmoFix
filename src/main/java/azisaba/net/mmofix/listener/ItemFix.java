package azisaba.net.mmofix.listener;

import azisaba.net.mmofix.MMOFix;
import azisaba.net.mmofix.utils.ItemUtil;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemFix implements Listener {

    private static final Set<String> pickupCT = new HashSet<>();

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent e) {

        Player p = e.getPlayer();
        Item drop = e.getItemDrop();
        drop.setThrower(p.getUniqueId());

        Bukkit.getScheduler().runTaskLater(MMOFix.getFix(), () -> drop.setThrower(null), 200L);
    }

    @EventHandler
    public void onPickUp(@NotNull EntityPickupItemEvent e) {

        if (e.getEntity() instanceof Player p) {

            Item drop = e.getItem();
            if (drop.getThrower() != null && !drop.getThrower().equals(p.getUniqueId())) {
                e.setCancelled(true);

                if (pickupCT.contains(p.getName())) return;

                OfflinePlayer off = Bukkit.getOfflinePlayer(drop.getThrower());
                p.sendMessage(Component.text(off.getName() + "のドロップアイテムです。", NamedTextColor.RED));
                pickupCT.add(p.getName());
                Bukkit.getScheduler().runTaskLaterAsynchronously(MMOFix.getFix(), ()-> pickupCT.remove(p.getName()), 20L);
            }
        }
    }

    @EventHandler
    public void onHopper(@NotNull InventoryPickupItemEvent e) {

        Item drop = e.getItem();
        if (drop.getThrower() != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUpExp(@NotNull PlayerPickupExperienceEvent e) {

        Player p = e.getPlayer();
        List<ItemStack> list = ItemUtil.getItemList(p);

        for (ItemStack item : list) {
            ItemUtil.addCustomDur(item, e.getExperienceOrb().getExperience());
        }
    }

    @EventHandler
    public void onEnchant(@NotNull EnchantItemEvent e) {

        ItemStack item = e.getItem();
        if (!(item.hasItemMeta())) return;

        PersistentDataContainer pc = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("field", "modify2");
        if (pc.has(key, PersistentDataType.STRING)) e.setCancelled(true);
    }

    @EventHandler
    public void onAnvil(@NotNull InventoryClickEvent e) {

        Inventory inv = e.getInventory();
        if (inv.getType().equals(InventoryType.ANVIL)) {

            AnvilInventory inventory = (AnvilInventory) inv;
            ItemStack f = inventory.getFirstItem();
            ItemStack s = inventory.getSecondItem();

            if (f != null && f.hasItemMeta()) {

                PersistentDataContainer pc = f.getItemMeta().getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("field", "modify3");
                if (pc.has(key, PersistentDataType.STRING)) e.setCancelled(true);
                return;
            }

            if (s != null && s.hasItemMeta()) {

                PersistentDataContainer pc = s.getItemMeta().getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("field", "modify3");
                if (pc.has(key, PersistentDataType.STRING)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent e) {

        Inventory inv = e.getInventory();
        if (inv.getType().equals(InventoryType.GRINDSTONE)) {

            GrindstoneInventory inventory = (GrindstoneInventory) inv;
            ItemStack up = inventory.getUpperItem();
            ItemStack low = inventory.getLowerItem();

            if (up != null && up.hasItemMeta()) {

                PersistentDataContainer pc = up.getItemMeta().getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("field", "modify1");
                if (pc.has(key, PersistentDataType.STRING)) e.setCancelled(true);
                return;
            }

            if (low != null && low.hasItemMeta()) {

                PersistentDataContainer pc = low.getItemMeta().getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("field", "modify1");
                if (pc.has(key, PersistentDataType.STRING)) e.setCancelled(true);
            }
        }
    }
}
