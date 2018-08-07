package cn.BlockMC.Zao_hon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolManager {
	private final AnvilLogin plugin;

	private HikariDataSource dataSource;
	private String hostname;
	private String port;
	private String database;
	private String username;
	private String password;
	private int minimumConnections = 1;
	private int maximumConnections = 30;
	private long connectionTimeout = 18000;

	public ConnectionPoolManager(AnvilLogin plugin) {
		this.plugin = plugin;
		init();
		setupPool();
	}

	private void init() {
		hostname = plugin.getConfig().getString("MYSQL.Host");
		port = plugin.getConfig().getString("MYSQL.Port");
		database = plugin.getConfig().getString("MYSQL.DatabaseName");
		username = plugin.getConfig().getString("MYSQL.UserName");
		password = plugin.getConfig().getString("MYSQL.Password");
	}

	private void setupPool() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false"

		);
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setUsername(username);
		config.setPassword(password);
		config.setMinimumIdle(minimumConnections);
		config.setMaximumPoolSize(maximumConnections);
		config.setConnectionTimeout(connectionTimeout);
		// config.setConnectionTestQuery(testQuery);
		dataSource = new HikariDataSource(config);
	}

	public void close(Connection conn, PreparedStatement ps, ResultSet res) {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException ignored) {
			}
		if (ps != null)
			try {
				ps.close();
			} catch (SQLException ignored) {
			}
		if (res != null)
			try {
				res.close();
			} catch (SQLException ignored) {
			}
	}

	public void closePool() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
