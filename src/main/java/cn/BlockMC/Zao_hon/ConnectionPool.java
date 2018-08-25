package cn.BlockMC.Zao_hon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

public class ConnectionPool {
	private PoolConfig config;
	private Vector<Connection> pool;

	public ConnectionPool(PoolConfig config) {
		this.config = config;
	}

	public Connection getConnection() {
		if (pool == null) {
			pool = new Vector<Connection>();
		}
		Connection conn;
		if (pool.isEmpty()) {
			conn = createConnection();
		} else {
			int last = pool.size() - 1;
			conn = (Connection) pool.get(last);
			pool.remove(conn);
		}
		return conn;
	}

	public synchronized void releaseConnection(Connection conn) {

		if (pool.size() > config.getMaxConnection()) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			pool.add(conn);
		}
	}

	public Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName(config.getDriverClassName());
			conn = DriverManager.getConnection(config.getJDBCUrl(), config.getUserName(), config.getPassworld());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;

	}

	public void close() {
//		runnable.cancel();
		pool.forEach(conn -> {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Vector<Connection> getConnections() {
		return pool;
	}

	public PoolConfig getConfig() {
		return config;
	}
}
