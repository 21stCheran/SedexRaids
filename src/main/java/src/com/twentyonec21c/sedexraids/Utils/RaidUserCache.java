package com.twentyonec21c.sedexraids.Utils;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import com.twentyonec21c.sedexraids.RaidUser;

public class RaidUserCache {
	
	private LoadingCache<RaidUser, Double> raidCacheTotal, raidCacheWeekly, pointsCache, bossDamage;
	

	public RaidUserCache() {

        raidCacheTotal = Caffeine.newBuilder()
                    .maximumSize(50L)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build(RaidUser::getPointsTotal);
        
        raidCacheWeekly = Caffeine.newBuilder()
                .maximumSize(50L)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(RaidUser::getPointsWeekly);
        
        pointsCache = Caffeine.newBuilder()
        		.build(RaidUser::getZeroCache);
        
        bossDamage = Caffeine.newBuilder()
        		.build(RaidUser::getZeroCache);
        			

    }

    public LoadingCache<RaidUser, Double> getRaidUserCacheTotal() {
        return raidCacheTotal;
    }
    public LoadingCache<RaidUser, Double> getRaidUserCacheWeekly() {
        return raidCacheWeekly;
    }
    public LoadingCache<RaidUser, Double> getRaidUserCachePoints() {
    	//Do not refresh. RESHING WILL SET VALUE TO -1
        return pointsCache;
    }
    public LoadingCache<RaidUser, Double> getRaidUserBossDamage() {
    	//Do not refresh. RESHING WILL SET VALUE TO -1
        return bossDamage;
    }
    
}
