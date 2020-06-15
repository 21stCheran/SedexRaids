package com.twentyonec21c.sedexraids.Utils;

import java.sql.SQLException;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.twentyonec21c.sedexraids.SedexRaids;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLManager {
	
	private static SQLManager sqlManager = null;

	private SedexRaids plugin = SedexRaids.getSedexRaids();
	
	private String hostname;
    private String port;
    private String username;
    private String password;
    private String database;
    
    private HikariDataSource dataSource;
	
	private SQLManager(final String hostname, final String port, final String username,
            final String password, final String database) {

		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;

	}
	
	public static synchronized SQLManager getSQLManager(SedexRaids plugin) {
        final ConfigManager configManager = plugin.getConfigManager();

        if (sqlManager == null) {
            sqlManager = new SQLManager(configManager.getHostname(), configManager.getPort(), configManager.getUsername(),
                    configManager.getPassword(), configManager.getDatabase());
        }

        return sqlManager;
    }
	
	private void connect() {

        final String jdbcUrl = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;

        HikariConfig config = new HikariConfig();

        config.setPoolName("raidtwentyone");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(this.username);
        config.setPassword(this.password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);
        config.setConnectionTimeout(5000);
        config.setInitializationFailTimeout(-1);

        try {
            this.dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
	
	public void update(final String sql) {

        try (Connection connection = this.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            plugin.debugMessage("Preparing statement for update.");
            statement.executeUpdate();
            plugin.debugMessage("Successfully executed update statement. (" + sql + ")");

        } catch (SQLException e) {
            plugin.debugMessage("Error while attempting to execute update statement. (" + sql + ")");
            e.printStackTrace();
        }

    }
	
	public ResultSet query(final String sql) {

        try (Connection connection = this.getConnection()){

            PreparedStatement statement = connection.prepareStatement(sql);

          plugin.debugMessage("Preparing statement for query.");
            ResultSet resultSet = statement.executeQuery(sql);
          plugin.debugMessage("Successfully executed query statement. (" + sql + ")");

            return resultSet;
        } catch (SQLException e) {
          plugin.debugMessage("Error while attempting to execute query statement. (" + sql + ")");
            e.printStackTrace();

            return null;
        }
    }
	
	private CompletableFuture<Double> getPlayerAsyncPoints(final UUID uuid, final String type) {

        return CompletableFuture.supplyAsync(() -> {

            double RP = -1;

            plugin.debugMessage("Attempting to get player points for UUID (" + uuid + ")" + type);
            final String query = "SELECT * FROM sedexraid WHERE uuid='" + uuid + "'";

            try (ResultSet resultSet = this.query(query)) {

                if (resultSet == null) {
                    plugin.debugMessage("Query failed. ResultSet is null.");
                    return RP;
                }

                if (resultSet.next()) { // Either empty or contains value
                	
                	if (type.equals("weekly")) {
                		RP = resultSet.getDouble("weeklypoints");
                		plugin.debugMessage("Got Weekly As: " + RP);
                	} else {
                		RP = resultSet.getDouble("totalpoints");
                		plugin.debugMessage("Got Total As: " + RP);
                	}
                                  
                    resultSet.close();
                }

            } catch (SQLException e) {
                plugin.debugMessage("Failed to retrieve player points for UUID (" + uuid + ")");
                e.printStackTrace();
            }

            return RP;

        });
    }
	
	public double getPlayerPoints(final UUID uuid, final String type) {
        try {
            return getPlayerAsyncPoints(uuid,type).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            plugin.debugMessage("FAILED GETTING SQL VALUE");
        }
        return -1;
    }
	
	public void setUpTable() {
        this.connect();

        plugin.debugMessage("Attempting to set up tables if they do not exist.");
        final String update = "CREATE TABLE IF NOT EXISTS sedexraid("
        		+ "totalpoints double"
        		+ ", weeklypoints double"
        		+ ", bosskills int"
        		+ ", mobkills int"
        		+ ", uuid VARCHAR(36) NOT NULL UNIQUE);";
        this.update(update);
    }

}
