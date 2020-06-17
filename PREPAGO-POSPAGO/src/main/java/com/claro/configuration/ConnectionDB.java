package com.claro.configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionDB {
	
	@Bean(destroyMethod = "")
	@ConfigurationProperties(prefix = "bscs")
	public DataSource dataSourceBSCS() {
		return DataSourceBuilder.create().type(BasicDataSource.class).build();
	}

}
