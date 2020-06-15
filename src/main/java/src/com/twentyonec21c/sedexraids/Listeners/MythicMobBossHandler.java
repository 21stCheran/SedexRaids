package com.twentyonec21c.sedexraids.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.twentyonec21c.sedexraids.RaidUser;
import com.twentyonec21c.sedexraids.SedexRaids;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class MythicMobBossHandler implements Listener{
	
	private SedexRaids plugin = SedexRaids.getSedexRaids();
	
	@EventHandler
	public void onMythicMobDamaged(EntityDamageByEntityEvent event) {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if ( event.getDamager() instanceof Player && 
					plugin.isMM(event.getEntity()) ) {
					
					RaidUser user = new RaidUser(plugin, (Player) event.getDamager());
					ActiveMob activeMob = plugin.getActiveMob(event.getEntity());
					String internalName = plugin.getInternalName(activeMob);
					double health =	plugin.getHealth(activeMob);
								
					if (plugin.getConfigManager().getBossList().contains(internalName)) {
						
						double damage = plugin.getRaidUserBossPoints().get(user) +
								event.getFinalDamage() / health * 100;    
												
						plugin.getRaidUserBossPoints().put(user, plugin.format(damage));
						plugin.debugMessage("Damage % = " + plugin.format(damage));
					} else {
					}
				}
			}
		
		}.runTaskAsynchronously(plugin);

	}

}
