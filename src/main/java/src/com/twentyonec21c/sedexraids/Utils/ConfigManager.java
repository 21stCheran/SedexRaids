package com.twentyonec21c.sedexraids.Utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	
	private FileConfiguration configuration;
	private HashMap<String, Double> internal_names;
	private List<?> bossList;
	
	private final String DEBUG_PATH = "debug";
	
	private final String USERNAME_PATH = "mysql.username";
    private final String PASSWORD_PATH = "mysql.password";
    private final String HOSTNAME_PATH = "mysql.hostname";
    private final String PORT_PATH = "mysql.port";
    private final String DATABASE_PATH = "mysql.database";
    
    private final String BASEVALUE_PATH = "raidpoints.basevalue";
    private final String MODIFIERS_PATH = "raidpoints.modifiers";
    private final String BOSS_PATH = "raidpoints.bosses";

    public ConfigManager(FileConfiguration configuration) {
        this.configuration = configuration;
        System.out.println("21SPEED: Loading Hash Map");
        loadHashMap();
        loadBossList();
    }
    
    public void loadHashMap() {
    	System.out.println("21Speed: Inside Map Loader");
    	internal_names = new HashMap<String, Double>();
    	String[] mapping = {null,null};
    	
    	List<?> internal_name_list = configuration.getList(MODIFIERS_PATH);

    	
    	for(int i = 0; internal_name_list.size() > i; i++) {
    		mapping = internal_name_list.get(i).toString().split(" ");
    		
    		internal_names.put(mapping[0], Double.valueOf(mapping[1]));
    	}
    	
    }
    public void loadBossList() {
    	System.out.println("21Speed: Inside Boss Loader");
    	bossList = configuration.getList(BOSS_PATH);
    }
    public HashMap<String, Double> getHashMap() {
    	return internal_names;
    }
    public List<?> getBossList() {
    	return bossList;
    }
    public String getUsername() {
        return configuration.getString(USERNAME_PATH);
    }

    public String getPassword() {
        return configuration.getString(PASSWORD_PATH);
    }

    public String getHostname() {
        return configuration.getString(HOSTNAME_PATH);
    }

    public String getPort() {
        return configuration.getString(PORT_PATH);
    }

    public String getDatabase() {
        return configuration.getString(DATABASE_PATH);
    }
    public boolean debugEnabled() {
        return configuration.getBoolean(DEBUG_PATH);
    }
    public double getBaseModifier() {
    	return configuration.getDouble(BASEVALUE_PATH);
    }
}
