package org.home.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class MainConfigurator {
    @Bean
    @ConfigurationProperties("app.datasource.primary")
    public DataSourceProperties mainDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("mainDataSource")
    @ConfigurationProperties("app.datasource.primary.dbcp2")
    public DataSource mainDataSource() {
        return mainDataSourceProperties().initializeDataSourceBuilder().type(BasicDataSource.class).build();
    }

    @Bean("mainJdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(mainDataSource());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource());
        em.setPackagesToScan("org.home.repository.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean("txManager")
    public PlatformTransactionManager mainTxManager() {
        return new DataSourceTransactionManager(mainDataSource());
    }

    @Bean("transactionManager")
    @Primary
    public PlatformTransactionManager mainJpaTxManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }
}
