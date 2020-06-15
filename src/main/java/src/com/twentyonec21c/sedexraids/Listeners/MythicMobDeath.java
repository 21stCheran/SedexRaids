package com.twentyonec21c.sedexraids.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.twentyonec21c.sedexraids.RaidUser;
import com.twentyonec21c.sedexraids.SedexRaids;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;

public class MythicMobDeath implements Listener{
	
	private SedexRaids plugin = SedexRaids.getSedexRaids();
	
	@EventHandler
	public void onMythicMobDeath(MythicMobDeathEvent event) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (event.getKiller() instanceof Player) {
					RaidUser user = new RaidUser(plugin, (Player) event.getKiller());
					String internalName = event.getMob().getType().getInternalName();
					
					if (plugin.getConfigManager().getBossList().contains(internalName)) {
						plugin.debugMessage("Boss Killed: " + internalName);
						updatePoints();
						savePoints();
						invalidateCaches();
					} else {
						plugin.debugMessage("Non Boss Killed: " + internalName);
						user.updateCachePoints(internalName);
					}
				}
			}
			
		}.runTaskAsynchronously(plugin);
	}
	
	public void updatePoints() {
		plugin.getRaidUserBossPoints().asMap().forEach((key, v) -> {
			key.updateCachePoints(v);
		});
	}
	public void savePoints() {
		plugin.getRaidUserCachePoints().asMap().forEach((key, v) -> {
			key.updatePoints();
		});
	}
	public void invalidateCaches() {
		plugin.getRaidUserBossPoints().invalidateAll();
		plugin.getRaidUserCachePoints().invalidateAll();
	}

}
