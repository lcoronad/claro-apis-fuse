package com.claro.main;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;


/**
 * Assert Solutions
 *
 */

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableJms
@Configuration
public class Application{
	
	private Logger logger = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	private Environment env;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
	@Bean(destroyMethod = "")
	public DataSource dataSource() {
		logger.info("El datasource fue registrado.");
		BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("connection.driver"));
        dataSource.setUrl(env.getProperty("connection.url.claro"));
        dataSource.setUsername(env.getProperty("connection.user"));
        dataSource.setPassword(env.getProperty("connection.password"));
        dataSource.setInitialSize(Integer.parseInt(env.getProperty("connection.initial-size")));
        dataSource.setMaxIdle(Integer.parseInt(env.getProperty("connection.max.inactive.connections")));
        dataSource.setMinIdle(Integer.parseInt(env.getProperty("connection.min.inactive.connections")));
        dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(env.getProperty("connection.min.inactive.time")));
        dataSource.setValidationQuery(env.getProperty("connection.validation-query"));
        dataSource.setRemoveAbandonedOnBorrow(Boolean.valueOf(env.getProperty("connection.remove-abandoned-on-borrow")));
        dataSource.setRemoveAbandonedTimeout(Integer.valueOf(env.getProperty("connection.remove-abandoned-timeout")));
        return dataSource;
	}
}
