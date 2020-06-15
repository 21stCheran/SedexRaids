package com.twentyonec21c.sedexraids;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
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
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class SedexRaids extends JavaPlugin {
	
	private static SedexRaids plugin = null;
	private ConfigManager configManager = null;
	private SQLManager sqlManager = null;
	private RaidUserCache raidUserCache = null;
	private BukkitAPIHelper mythic = null;
	private NumberFormat formatter = null;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();  
		loadObjects();
		loadEvents();
		hookPlugins();
		sqlManager.setUpTable();
                        
	}
	@Override
	public void onDisable() {
		saveUsers();
	}
	
	//loading methods
	public void loadEvents() {
		this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new MythicMobDeath(), this);
        this.getServer().getPluginManager().registerEvents(new MythicMobBossHandler(), this);
	}
	
	public void loadObjects() {
		plugin = this;
		configManager = new ConfigManager(this.getConfig());
        sqlManager = SQLManager.getSQLManager(this);
        mythic = new BukkitAPIHelper();
        formatter = new DecimalFormat("#0.00"); 
        raidUserCache = new RaidUserCache();
	}
	
	public void hookPlugins() {
		//check papi
		if (getPlugin("PlaceholderAPI") != null) {
            new PlaceholderapiExpansion().register();
            this.getLogger().info("Successfully hooked into PlaceholderAPI.");
        } else {
            this.getLogger().warning("Could not hook into PlaceholderAPI. Some placeholders may not work!");
        }
		//check mm
		if (getPlugin("MythicMobs") != null) {
            this.getLogger().info("Successfully hooked into MythicMobs.");
        } else {
            this.getLogger().warning("Could not hook into MythicMobs. Plugin will not work!");
        }
	}
	
	public void saveUsers() {
		//saveusers
	}
	//return methods
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
	public boolean isMM(Entity e) {
		return mythic.isMythicMob(e);
	}
	public String getInternalName(ActiveMob activeMob) {
		return activeMob.getType().getInternalName();
	}
	public double getHealth(ActiveMob activeMob) {
		return mythic.getMythicMob(activeMob.getType().getInternalName()).getHealth(activeMob);
	}
	public ActiveMob getActiveMob(Entity e) {
		return mythic.getMythicMobInstance(e);
	}
	public double format(double no) {
		return Double.valueOf(formatter.format(no));
	}
	public Plugin getPlugin(String plugin) {
		return Bukkit.getPluginManager().getPlugin(plugin);
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
	public LoadingCache<RaidUser, Double> getRaidUserBossPoints() {
        return raidUserCache.getRaidUserBossDamage();
    }
//reloads the config
    public void reload() {
        this.reloadConfig();
        configManager = new ConfigManager(this.getConfig());
    }
	
	public void debugMessage(String message) {
        if (getConfigManager().debugEnabled())
            plugin.getLogger().log(Level.INFO, "[DEBUG]: " + message);
    }
}
