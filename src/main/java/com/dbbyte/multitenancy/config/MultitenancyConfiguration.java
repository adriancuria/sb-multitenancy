package com.dbbyte.multitenancy.config;


import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.dbbyte.multitenancy.DataSourceBasedMultiTenantConnectionProviderImpl;

/**
 * setp_5 - Note that the object Map (attribute of class
 * AbstractDataSourceBasedMultiTenantConnectionProviderImpl) is configured when
 * the Spring context is loaded reading the multitenancy properties from
 * application.yml. This configuration is performed in a class annotated
 * with @Configuration. You can see the code below.
 *
 */

@Configuration
@EnableConfigurationProperties(MultitenancyConfigurationProperties.class)
public class MultitenancyConfiguration {
    
    @Autowired
    private MultitenancyConfigurationProperties multitenancyProperties;

    @Bean(name = "multitenantProvider")
    public DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProvider() {
        HashMap<String, DataSource> dataSources = new HashMap<String, DataSource>();
        
        multitenancyProperties.getTenants().stream().forEach(tc -> dataSources.put(tc.getName(), DataSourceBuilder
                .create()
                .driverClassName(tc.getDriverClassName())
                .username(tc.getUsername())
                .password(tc.getPassword())
                .url(tc.getUrl()).build()));
        
        return new DataSourceBasedMultiTenantConnectionProviderImpl(multitenancyProperties.getDefaultTenant().getName(), dataSources);
    }
    
    @Bean
    @DependsOn("multitenantProvider")
    public DataSource defaultDataSource() {
        return dataSourceBasedMultiTenantConnectionProvider().getDefaultDataSource();
    }

}
