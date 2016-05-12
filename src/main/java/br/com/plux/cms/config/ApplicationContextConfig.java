package br.com.plux.cms.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan("br.com.plux.cms.*")
@EnableTransactionManagement
@PropertySource("classpath:application.properties") // Load to Environment.
public class ApplicationContextConfig {

	// The Environment class serves as the property holder
	// and stores all the properties loaded by the @PropertySource
	@Autowired
	private Environment env;

	@Bean(name = "viewResolver")
	public InternalResourceViewResolver getViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	// @Bean//(name = "dataSource")
	// public DataSource getDataSource() {
	// DriverManagerDataSource dataSource = new DriverManagerDataSource();
	//
	// // See: application.properties
	// dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
	// dataSource.setUrl(env.getProperty("spring.datasource.url"));
	// dataSource.setUsername(env.getProperty("spring.datasource.username"));
	// dataSource.setPassword(env.getProperty("spring.datasource.password"));
	// System.out.println("## getDataSource: " + dataSource);
	//
	// return dataSource;
	// }

	@Bean
	public DataSource dataSource() {
		return new HikariDataSource(hikariConfig());
	}

	private HikariConfig hikariConfig() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setJdbcUrl("jdbc:mysql://localhost:3306/plux?useSSL=false");
		config.setUsername("root");
		config.setPassword("123456");
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(10);
		config.setPoolName("PluxPool-1");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		return config;

	}

	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager(); // does this need an emf???
	}

	@Bean
	public HibernateJpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setShowSql(true);
		adapter.setGenerateDdl(true);
		System.out.println("## jpaVendorAdapter: " + adapter);
		return adapter;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
		emfb.setDataSource(dataSource);
		emfb.setJpaVendorAdapter(jpaVendorAdapter);
		emfb.setPackagesToScan("br.com.plux.cms.model");
		System.out.println("## entityManagerFactory: " + emfb);
		System.out.println("## getDataSource: " + dataSource);
		return emfb;
	}

}
