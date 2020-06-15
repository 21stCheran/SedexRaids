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
				if (event.getDamager() instanceof Player && 
						plugin.getBukkitAPIHelper().isMythicMob(event.getEntity().getUniqueId())) {
					
					RaidUser user = new RaidUser(plugin, (Player) event.getDamager());
					ActiveMob activeMob = plugin.getBukkitAPIHelper().getMythicMobInstance(event.getEntity());
					double health =	plugin.getBukkitAPIHelper().getMythicMob(activeMob.getType().getInternalName()).getHealth(activeMob);
								
					if (plugin.getConfigManager().getBossList().contains(activeMob.getType().getInternalName())) {
						
						plugin.debugMessage("LIST CONTAINS " + activeMob.getType().getInternalName());
						double damage = plugin.getRaiduserBossPoints().get(user) +
								event.getFinalDamage() / health * 100;    
												
						plugin.getRaiduserBossPoints().put(user, plugin.format(damage));
						plugin.debugMessage("Damage % = " + plugin.format(damage));
					} else {
						
						plugin.debugMessage("LIST NOT CONTAINS " + activeMob.getType().getInternalName());
					}
				}//if
			}
		
		}.runTaskAsynchronously(plugin);

	}

}
