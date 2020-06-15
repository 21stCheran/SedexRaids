package com.twentyonec21c.sedexraids;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.twentyonec21c.sedexraids.Listeners.MythicMobBossHandler;
import com.twentyonec21c.sedexraids.Listeners.MythicMobDeath;
import com.twentyonec21c.sedexraids.Listeners.PlayerJoin;
import com.twentyonec21c.sedexraids.Utils.ConfigManager;
import com.twentyonec21c.sedexraids.Utils.PlaceholderapiExpansion;
import com.twentyonec21c.sedexraids.Utils.RaidUserCache;
import com.twentyonec21c.sedexraids.Utils.SQLManager;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;

public class SedexRaids extends JavaPlugin {
	
	private static SedexRaids plugin = null;
	private ConfigManager configManager = null;
	private SQLManager sqlManager = null;
	private RaidUserCache raidUserCache = null;
	private BukkitAPIHelper mythic = null;
	private NumberFormat formatter = null;
	private boolean papiHooked = false;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		this.saveDefaultConfig();
		System.out.println("21Speed: Default Config Saved");
		
		configManager = new ConfigManager(this.getConfig());
		
		System.out.println("Now prepping to load SqlManager");
        sqlManager = SQLManager.getSQLManager(this);
        mythic = new BukkitAPIHelper();
        formatter = new DecimalFormat("#0.00");        
        sqlManager.setUpTable();
        
        //cache 1 for weekly placeholders
        raidUserCache = new RaidUserCache();
        
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new MythicMobDeath(), this);
        this.getServer().getPluginManager().registerEvents(new MythicMobBossHandler(), this);
        
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papiHooked = true;
            new PlaceholderapiExpansion().register();
            this.getLogger().info("Successfully hooked into PlaceholderAPI.");
        } else {
            this.getLogger().warning("Could not hook into PlaceholderAPI. Some placeholders may not work!");
        }
        
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
        	this.getLogger().info("Hooked into MythicMobs successfully");
        } else {
        	this.getLogger().warning("Could not hook into MythicMobs!");
        }
	}
	
	public static SedexRaids getSedexRaids() {
        return plugin;
    }
	public ConfigManager getConfigManager() {
        return configManager;
    }
	public SQLManager getSqlManager() {
        return sqlManager;
    }
	public BukkitAPIHelper getBukkitAPIHelper() {
		return mythic;
	}
	public double format(double no) {
		return Double.valueOf(formatter.format(no));
	}

	@Override
    public void reloadConfig() {
        super.reloadConfig();
        configManager = new ConfigManager(this.getConfig());
    }
	
	public void debugMessage(String message) {

        if (getConfigManager().debugEnabled())
            plugin.getLogger().log(Level.INFO, "[DEBUG]: " + message);

    }
	
	public boolean isPapiHooked() {
        return papiHooked;
    }
	
	public LoadingCache<RaidUser, Double> getRaidUserCacheTotal() {
        return raidUserCache.getRaidUserCacheTotal();
    }
	
	public LoadingCache<RaidUser, Double> getRaidUserCacheWeekly() {
        return raidUserCache.getRaidUserCacheWeekly();
    }
	
	public LoadingCache<RaidUser, Double> getRaidUserCachePoints() {
        return raidUserCache.getRaidUserCachePoints();
    }
	public LoadingCache<RaidUser, Double> getRaiduserBossPoints() {
        return raidUserCache.getRaidUserBossDamage();
    }
}
