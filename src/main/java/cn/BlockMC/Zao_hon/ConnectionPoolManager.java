package cn.BlockMC.Zao_hon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionPoolManager {
	private final AnvilLogin plugin;

//	private HikariDataSource dataSource;
	private String hostname;
	private String port;
	private String database;
	private String username;
	private String password;
//	private int minimumConnections = 1;
	private int maximumConnections = 30;
//	private long connectionTimeout = 18000;
	private ConnectionPool pool;

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
//		HikariConfig config = new HikariConfig();
		PoolConfig config = new PoolConfig();
		config.setJDBCUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false"

		);
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setUserName(username);
		config.setPassword(password);
//		config.setMinimumIdle(minimumConnections);
		config.setMaxConnection(maximumConnections);
//		config.setConnectionTimeout(connectionTimeout);
		// config.setConnectionTestQuery(testQuery);
		pool = new ConnectionPool(config);
	}

	public void close(Connection conn, PreparedStatement ps, ResultSet res) {
		if (conn != null)
			pool.releaseConnection(conn);
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
		pool.close();
	}

	public Connection getConnection() {
		return pool.getConnection();
	}
}
