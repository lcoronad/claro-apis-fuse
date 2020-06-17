package com.claro.configuration;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class JdbcConnectionLogs {

	private final DataSource dataSource;

	private static interface Singleton {
		final JdbcConnectionLogs INSTANCE = new JdbcConnectionLogs();
	}

	private JdbcConnectionLogs() {

		Properties properties = new Properties();
		String username = System.getenv("logs.username");
		String password = System.getenv("logs.password");
		String url = System.getenv("logs.url.connection");
		properties.setProperty("user", username);
		properties.setProperty("password", password);
		ConnectionFactory cf = new DriverManagerConnectionFactory(url, properties);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(cf, null);
		poolableConnectionFactory.setValidationQuery("select 1");

		GenericObjectPool<PoolableConnection> pool = new GenericObjectPool<>(poolableConnectionFactory);
		pool.setTestOnBorrow(false);
		pool.setMaxTotal(10);
		poolableConnectionFactory.setPool(pool);
		this.dataSource = new PoolingDataSource<>(pool);
	}

	public static java.sql.Connection getConn() throws SQLException {
		return Singleton.INSTANCE.dataSource.getConnection();
	}

}
