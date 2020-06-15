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
					
					if (plugin.getConfigManager().getBossList().contains
							(event.getMob().getType().getInternalName())) {
						plugin.debugMessage("Boss Killed");
						bossDeath();
						pointsSave();
//						plugin.get
					} else {
						user.updateCachePoints(event.getMob().getType().getInternalName());
						plugin.debugMessage("Non Boss Killed");
					}
				}
			}
			
		}.runTaskAsynchronously(plugin);
	}
	
	public void bossDeath() {
		plugin.getRaiduserBossPoints().asMap().forEach((key, v) -> {
			key.updateCachePoints(v);
		});
	}
	public void pointsSave() {
		plugin.getRaidUserCachePoints().asMap().forEach((key, v) -> {
			key.updatePoints();
		});
	}

}
