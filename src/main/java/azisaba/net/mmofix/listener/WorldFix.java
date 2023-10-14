package azisaba.net.mmofix.listener;

import azisaba.net.mmofix.MMOFix;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class WorldFix implements Listener {

    private final MMOFix fix;
    public WorldFix(MMOFix fix) {
        this.fix = fix;
    }

    @EventHandler
    public void onCreate(@NotNull PortalCreateEvent e) {
        String name = e.getWorld().getName();
        if (name.equalsIgnoreCase("world")) e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {


        Block b = e.getClickedBlock();
        if (b == null) return;

        if (!b.getWorld().getName().equalsIgnoreCase("world")) return;
        if (b.getType() == Material.END_PORTAL_FRAME) e.setCancelled(true);
    }

    @EventHandler
    public void onRapid(@NotNull PlayerRiptideEvent e) {

        Player p = e.getPlayer();
        Location loc = p.getLocation();
        World w = p.getWorld();
        if (!(w.getName().toLowerCase().contains("end") && w.getName().toLowerCase().contains("resource"))) return;
        if (w.getGameTime() < 72000) {
            p.sendMessage(Component.text("ワールド生成後1時間は探索に有利な行動はできません", NamedTextColor.RED));
            setVec(p, 1L).setVec(p, 2L).setVec(p, 4L).setTP(p, 5L, loc);
        }
    }

    public WorldFix setVec(Player p, Long delay) {
        Bukkit.getScheduler().runTaskLater(fix, () -> p.setVelocity(new Vector(0,0,0)), delay);
        return this;
    }

    public void setTP(Player p, Long delay, Location loc) {
        Bukkit.getScheduler().runTaskLater(fix, ()-> p.teleport(loc), delay);
    }

    @EventHandler
    public void onEBEvent(@NotNull PlayerElytraBoostEvent e) {

        Player p = e.getPlayer();
        World w = p.getWorld();
        if (!(w.getName().toLowerCase().contains("end") && w.getName().toLowerCase().contains("resource"))) return;
        if (w.getGameTime() < 72000) {
            p.sendMessage(Component.text("ワールド生成後1時間は探索に有利な行動はできません", NamedTextColor.RED));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(@NotNull EntityTeleportEvent e) {

        if (e.getFrom().getWorld().getName().toLowerCase().contains("end")) {
            Location loc = e.getTo();
            if (loc == null) {
                e.setCancelled(true);
                return;
            }
            if (loc.getWorld().getName().equalsIgnoreCase("world")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotion(@NotNull EntityPotionEffectEvent e) {

        if (!(e.getEntity() instanceof Player p)) return;

        if (e.getCause().equals(EntityPotionEffectEvent.Cause.POTION_DRINK) ||
                e.getCause().equals(EntityPotionEffectEvent.Cause.POTION_SPLASH) ||
                e.getCause().equals(EntityPotionEffectEvent.Cause.FOOD)) {

            PotionEffect effect = e.getNewEffect();
            if (effect == null) return;

            if (p.hasPotionEffect(effect.getType())) p.removePotionEffect(effect.getType());

            int i = effect.getDuration() * 8;
            PotionEffect newP = new PotionEffect(effect.getType(), i, effect.getAmplifier() + 1, effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
            e.setCancelled(true);
            p.addPotionEffect(newP);
        }
    }

    @EventHandler
    public void onDamaged(@NotNull EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof ShulkerBullet && e.getEntity() instanceof Shulker) e.setCancelled(true);
    }

    @EventHandler
    public void onUnload(@NotNull ChunkLoadEvent e) {

        for (Entity entity : e.getChunk().getEntities()) {
            if (entity instanceof Item item) {
                item.remove();
            }
        }
    }
}
