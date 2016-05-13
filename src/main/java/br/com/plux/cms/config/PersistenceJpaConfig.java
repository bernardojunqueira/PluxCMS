package br.com.plux.cms.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "br.com.plux.cms.repository")
@PropertySource({ "classpath:persistence.properties" })
public class PersistenceJpaConfig {

	// The Environment class serves as the property holder
	// and stores all the properties loaded by the @PropertySource
	@Autowired
	private Environment env;

	// Translates exceptions into one of Spring's unified data-access exceptions
	@Bean
	public BeanPostProcessor persistenceTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	// Using Hikari as a connection pool
	@Bean
	public DataSource dataSource() {
		return new HikariDataSource(hikariConfig());
	}

	private HikariConfig hikariConfig() {
		HikariConfig config = new HikariConfig();
		config.setDataSourceClassName(env.getProperty("hikari.dataSourceClassName"));
		// config.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		// config.setJdbcUrl(env.getProperty("jdbc.url"));
		config.setUsername(env.getProperty("jdbc.user"));
		config.setPassword(env.getProperty("jdbc.pass"));
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(10);
		config.setPoolName(env.getProperty("hikari.poolName"));
		config.addDataSourceProperty("databaseName", env.getProperty("hikari.databaseName"));
		config.addDataSourceProperty("portNumber", env.getProperty("hikari.portNumber"));
		config.addDataSourceProperty("serverName", env.getProperty("hikari.serverName"));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		return config;

	}

	// Using Hibernate as a JPA implementation
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	// Using container-managed JPA to produce an EntityManagerFactory
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		System.out.println("## dataSource: " + dataSource());
		em.setJpaVendorAdapter(jpaVendorAdapter());
		System.out.println("## jpaVendorAdapter: " + jpaVendorAdapter());
		em.setJpaProperties(additionalProperties());
		em.setPackagesToScan("br.com.plux.cms.model");
		System.out.println("## entityManagerFactory: " + em);
		return em;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

	final Properties additionalProperties() {
		final Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		return hibernateProperties;
	}

}
