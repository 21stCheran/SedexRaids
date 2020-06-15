package com.twentyonec21c.sedexraids.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.twentyonec21c.sedexraids.RaidUser;
import com.twentyonec21c.sedexraids.SedexRaids;

public class PlayerJoin implements Listener{
	
	private SedexRaids plugin = SedexRaids.getSedexRaids();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		RaidUser user = new RaidUser(plugin, event.getPlayer());
            user.createUser();
	}

}
