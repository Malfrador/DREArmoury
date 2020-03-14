package de.erethon.armoury;

import de.erethon.armoury.config.Config;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.projectiles.ProjectileSource;

import static java.lang.Math.round;

public class PlayerListener implements Listener {
    ArmorHandler armorHandler = new ArmorHandler();
    WeaponHandler weaponHandler = new WeaponHandler();
    DamageCalculation dmgcalc = new DamageCalculation();
    DREArmoury main = DREArmoury.getInstance();
    Config cfg = main.getDREConfig();
    @EventHandler
    public void pvpHandler(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final Entity damaged = event.getEntity();
        double dmg = event.getDamage();
        double armorreduction;
        double finaldmg;
        double armor;
        Player pDmg = null;
        Player pVict = null;
        if ((damager instanceof Player || damager instanceof Projectile) && (damaged instanceof Player)) {
            pVict = (Player) damaged;

            if ((event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) {
                    Projectile projectile = (Projectile)event.getDamager();
                    if (projectile.getShooter() instanceof Player) {
                        pDmg = (Player) projectile.getShooter();
                }
            }
            else {
                pDmg = (Player) damager;
            }

            if (pDmg != null) {
                WeaponType weapon = weaponHandler.getWeaponType(pDmg);
                ArmorType armorHead = armorHandler.getArmorType(pVict, EquipmentSlot.HEAD);
                ArmorType armorChest = armorHandler.getArmorType(pVict, EquipmentSlot.CHEST);
                ArmorType armorLegs = armorHandler.getArmorType(pVict, EquipmentSlot.LEGS);
                ArmorType armorFeet = armorHandler.getArmorType(pVict, EquipmentSlot.FEET);

                armorreduction = dmgcalc.calcDmg(weapon, armorHead) * dmgcalc.calcDmg(weapon, armorChest) * dmgcalc.calcDmg(weapon, armorLegs) *  dmgcalc.calcDmg(weapon, armorFeet);
                finaldmg = dmg * armorreduction;
                event.setDamage(finaldmg);
                if (main.isDebug()) {
                    pDmg.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + " " + round(finaldmg) + " DMG" + ChatColor.DARK_GRAY + " " + armorreduction + " - " + weapon + ")"));
                    pVict.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + " " + round(finaldmg) + " DMG" + ChatColor.DARK_GRAY + " (" + armorreduction + " - " + weapon + ")"));
                    pVict.sendMessage("ArmorType: " + armorHead + " / " + armorChest + " / " + armorLegs + " / " + armorFeet);
                    pVict.sendMessage("Reduction: " + dmgcalc.calcDmg(weapon, armorHead) + " * " + dmgcalc.calcDmg(weapon, armorChest) + " * " + dmgcalc.calcDmg(weapon, armorLegs) + " * " + dmgcalc.calcDmg(weapon, armorFeet) + " = " + armorreduction);

                }

            }
        }
    }

}