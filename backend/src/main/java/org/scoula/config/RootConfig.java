package org.scoula.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySources({
        // 1) 배포/공통 설정 (Git에 올라가는 놈)
        @PropertySource("classpath:/application.properties"),
        // 2) 로컬 개발자마다 다른 설정 (선택적, 없어도 됨)
        @PropertySource(
                value = "classpath:/application-local.properties",
                ignoreResourceNotFound = true   // 없으면 무시 (지금 Docker에서 필요한 부분)
        )
})
@MapperScan(basePackages = {
        "org.scoula.register.mapper",
        "org.scoula.term.mapper",
        "org.scoula.category.mapper",
        "org.scoula.specialcontractrecommendation.mapper",
        "org.scoula.oauth.mapper",
        "org.scoula.checklist.mapper",
        "org.scoula.jeonseRate.mapper",
        "org.scoula.categorymanager.mapper",
        "org.scoula.termmanager.mapper",
        "org.scoula.specialcontractmanager.mapper",
        "org.scoula.contract.mapper",
        "org.scoula.specialclause.mapper",
        "org.scoula.finalreport.mapper"
})
@ComponentScan(basePackages = {"org.scoula", "org.scoula.oauth.service"})
@EnableTransactionManagement
@EnableScheduling
@Log4j2
public class RootConfig {
    @Value("${jdbc.driver}") String driver;
    @Value("${jdbc.url}") String url;
    @Value("${jdbc.username}") String username;
    @Value("${jdbc.password}") String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
        sqlSessionFactory.setDataSource(dataSource());
        return (SqlSessionFactory) sqlSessionFactory.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
        return manager;
    }
}