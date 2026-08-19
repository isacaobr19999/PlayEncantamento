package me.manus.customenchants.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ProtectionHook {

    public static boolean canBreakBlock(Player player, Block block) {
        Location loc = block.getLocation();
        
        // WorldGuard Check
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (!query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BLOCK_BREAK)) {
                return false;
            }
        }
        
        // GriefPrevention Check
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
            String noBuildReason = GriefPrevention.instance.allowBreak(player, block, loc);
            if (noBuildReason != null) {
                return false;
            }
        }
        
        return true;
    }
}
