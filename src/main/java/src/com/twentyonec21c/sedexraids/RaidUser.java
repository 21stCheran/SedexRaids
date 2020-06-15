package com.twentyonec21c.sedexraids;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.twentyonec21c.sedexraids.Utils.SQLManager;

public class RaidUser {
	
	private SedexRaids plugin;
	private SQLManager sqlManager;
	private Player user;
	private UUID uuid;
		
	public RaidUser(SedexRaids plugin, Player user) {
		this.plugin = plugin;
		this.user = Objects.requireNonNull(user, "Player could not be created!");
		uuid = user.getUniqueId();	
		sqlManager = plugin.getSqlManager();
       	}
	
	@Override
    public boolean equals(Object obj) {

        if (obj == null&&(!(obj instanceof RaidUser)))
            return false;

        @SuppressWarnings("unused")
		RaidUser other = (RaidUser) obj;

        for (int i = 0; i < getSignificantFields().length; i++) {

            if (!Objects.equals(this.getSignificantFields()[i], ((RaidUser) obj).getSignificantFields()[i]))
                return false;            
        }
        
        return true;
    }
	  @Override
	    public int hashCode() {
	        return Objects.hash(getSignificantFields());
	    }
	  @Override
	  public String toString() {
		  return "[Name: "
				  +this.user.getName()
				  +"\n UUID: "
				  +this.user.getUniqueId()
				  ;
	  }
	  private Object[] getSignificantFields() {
	        return new Object[] {
	                uuid, user
	        };
	    }
	  public Player getUser() {
	        return user;
	    }
	  public double getZeroCache() {
	       return 0;
	    }
	  
	  public double getPointsTotal() {
	        return sqlManager.getPlayerPoints(uuid, "total");
	    }
	  public double getPointsWeekly() {
	        return sqlManager.getPlayerPoints(uuid, "weekly");
	    }
	  
	  public void updateCachePoints(String internalName) {
				  final double baseValue = plugin.getConfigManager().getBaseModifier();
				  final Double modifier = plugin.getConfigManager().getModifier(internalName);
				  double cachePoints = plugin.getRaidUserCachePoints().get(this);
				  double points = (modifier==null)?baseValue+cachePoints:modifier*baseValue+cachePoints;
//				  if the mob has a modifier then it uses it, else it assigns the base value				  
				  plugin.getRaidUserCachePoints().put(this, points);
				  plugin.debugMessage(user.getDisplayName()+"'s cache set to: " + points);
	  }
	  public void updateCachePoints(double bossPoints) {
		  double newValue = bossPoints + plugin.getRaidUserCachePoints().get(this);
		  plugin.getRaidUserCachePoints().put(this, newValue);
	  }
	  	  
	  public void updatePoints() {
		  double cache = plugin.getRaidUserCachePoints().get(this);
		  final double newTotal = cache + this.getPointsTotal();
		  final double newWeekly = cache + this.getPointsWeekly();
		  
	                final String sql = "UPDATE sedexraid SET weeklypoints = " + newWeekly 
	                		+ ", totalpoints = " + newTotal + " WHERE uuid = '" + uuid + "';";
	                sqlManager.update(sql);
	                plugin.debugMessage("SQL Weekly and Total Updated");

	                plugin.getRaidUserCacheTotal().refresh(RaidUser.this);
	                plugin.getRaidUserCacheWeekly().refresh(RaidUser.this);
	                plugin.debugMessage("Total and Weekly caches have been refreshed");
	         

	    }
	  public void createUser() {
	        new BukkitRunnable() {

	            @Override
	            public void run() {

	                // UUID has the UNIQUE constraint. If the UUID already exists in the database, IGNORE will just
	                // cancel execution of this statement, thus preventing duplicates.

	                final String sql = "INSERT IGNORE INTO sedexraid(uuid, totalpoints, weeklypoints, mobkills, bosskills) "
	                		+ "VALUES ('"+ uuid + "'," + 0 + "," + 0 + "," + 0 + "," + 0 + ");";

	                sqlManager.update(sql);
	            }

	        }.runTaskAsynchronously(plugin);
	    }
}
