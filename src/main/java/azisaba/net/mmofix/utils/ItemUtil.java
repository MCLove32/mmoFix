package azisaba.net.mmofix.utils;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    public static void addCustomDur(ItemStack item, int repairAmount) {

        CompoundTag tag = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(item);
        if (tag != null && tag.containsKey("MYTHIC_MAX_CUSTOM_DURABILITY") && tag.containsKey("MYTHIC_CURRENT_CUSTOM_DURABILITY")) {

            int max = tag.getInt("MYTHIC_MAX_CUSTOM_DURABILITY");
            int now = Math.max(tag.getInt("MYTHIC_CURRENT_CUSTOM_DURABILITY") - repairAmount, 0);

            tag = tag.builder(compoundTagBuilder -> compoundTagBuilder.putInt("MYTHIC_CURRENT_CUSTOM_DURABILITY", Math.min(now, max)).build());
            MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setNBTData(item, tag);
        }
    }

    public static @NotNull List<ItemStack> getItemList(@NotNull Player p) {

        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : p.getInventory().getArmorContents()) {

            if (item == null || item.getType().isAir()) continue;
            if (hasCustomDur(item)) list.add(item);
        }

        ItemStack main = p.getInventory().getItemInMainHand();
        if (!main.getType().isAir() && hasCustomDur(main)) list.add(main);

        ItemStack off = p.getInventory().getItemInOffHand();
        if (!off.getType().isAir() && hasCustomDur(off)) list.add(off);

        return list;
    }

    public static boolean hasCustomDur(ItemStack item) {

        CompoundTag tag = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(item);
        if (tag == null) return false;
        return tag.containsKey("MYTHIC_MAX_CUSTOM_DURABILITY") && tag.containsKey("MYTHIC_CURRENT_CUSTOM_DURABILITY");
    }
}
