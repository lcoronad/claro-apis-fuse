package com.claro.reportes;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan(basePackages = { "com.claro.*" })
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Configuration
public class ReportesAuditoriaClaroApplication {
	
	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(ReportesAuditoriaClaroApplication.class, args);
		
	}
	
	@Bean(destroyMethod = "")
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(env.getProperty("reporte.connection.driver-class-name"));
		basicDataSource.setUrl(env.getProperty("reporte.connection.url"));
		basicDataSource.setUsername(env.getProperty("reporte.connection.user"));
		basicDataSource.setPassword(env.getProperty("reporte.connection.password"));
		basicDataSource.setInitialSize(Integer.valueOf(env.getProperty("reporte.connection.dbcp2.initial-size")));
		basicDataSource.setMinIdle(Integer.valueOf(env.getProperty("reporte.connection.dbcp2.minconn")));
		basicDataSource.setMaxIdle(Integer.valueOf(env.getProperty("reporte.connection.dbcp2.maxconn")));
		basicDataSource.setMinEvictableIdleTimeMillis(Integer.valueOf(env.getProperty("reporte.connection.dbcp2.min.inactive.time")));
		basicDataSource.setValidationQuery(env.getProperty("reporte.connection.dbcp2.validation-query"));
		basicDataSource.setRemoveAbandoned(Boolean.valueOf(env.getProperty("reporte.connection.dbcp2.remove-abandoned-on-borrow")));
		basicDataSource.setRemoveAbandonedTimeout(Integer.valueOf(env.getProperty("reporte.connection.dbcp2.remove-abandoned-timeout")));
		return basicDataSource;
		
	}
	


}
