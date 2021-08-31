package com.project;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by Sigal on 5/16/2016.
 */
@Configuration
@Profile("production")
public class AppConfig {

    @Bean
    public DataSource dataSource() throws Exception {
        Map<String, String> dbProps = getDbProps();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(String.format("jdbc:mysql://%s/%s?useSSL=false&useUnicode=true&characterEncoding=utf8&connectionCollation=utf8_general_ci&characterSetResults=utf8", dbProps.get("server"), dbProps.get("database")));
        dataSource.setUser(dbProps.get("user"));
        dataSource.setPassword(dbProps.get("password"));
        dataSource.setMaxPoolSize(20);
        dataSource.setMinPoolSize(5);
        dataSource.setIdleConnectionTestPeriod(3600);
        dataSource.setTestConnectionOnCheckin(true);
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() throws Exception {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.put("hibernate.jdbc.batch_size", 50);
        hibernateProperties.put("hibernate.connection.characterEncoding", "utf8");
//        hibernateProperties.put("hibernate.cache.use_second_level_cache", "true");
//       // hibernateProperties.put("hibernate.generate_statistics", "true");
//        hibernateProperties.put("net.sf.ehcache.configurationResourceName", "/conf_ehcache.xml");
//        hibernateProperties.put("hibernate.cache.use_query_cache", "true");
//        hibernateProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        sessionFactoryBean.setMappingResources("objects.hbm.xml");
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager() throws Exception{
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }


    private Map<String, String> getDbProps () throws Exception {
        Map<String, String> propsMap = new HashMap<>();
        propsMap.put("user", "root");
        propsMap.put("password", "");
        propsMap.put("server", "localhost");
        propsMap.put("database", "tracker");
        return propsMap;
    }
}
