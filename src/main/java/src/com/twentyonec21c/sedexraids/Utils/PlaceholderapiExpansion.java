package com.twentyonec21c.sedexraids.Utils;

import org.bukkit.entity.Player;

import com.twentyonec21c.sedexraids.RaidUser;
import com.twentyonec21c.sedexraids.SedexRaids;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderapiExpansion extends PlaceholderExpansion{
	
	private SedexRaids plugin = SedexRaids.getSedexRaids();

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return "sedexraids";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	@Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null)
            return "";

        RaidUser user = new RaidUser(plugin, player);

        switch (identifier) {

            case "pointstotal":
            	plugin.debugMessage("Getting total points");
                return String.valueOf(plugin.getRaidUserCacheTotal().get(user));
                
            case "pointsweekly":
            	plugin.debugMessage("Getting weekly points");
                return String.valueOf(plugin.getRaidUserCacheWeekly().get(user));
            
            case "pointscache":
            	plugin.debugMessage("Attempting to get placeholder points cache");
            	return String.valueOf(plugin.getRaidUserCachePoints().get(user));
        }

        return "wrong";
    }

}
