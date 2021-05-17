package com.sentry.spring.demo.service;

import com.alibaba.druid.pool.DruidDataSource;
import oracle.jdbc.driver.OracleDriver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class TestService implements ApplicationContextAware {

    private ApplicationContext context;

    private DruidDataSource dataSource;

    {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@1.1.1.1:1521:orcl");
        dataSource.setUsername("aaa");
        dataSource.setPassword("bbb");
        dataSource.setDriverClassName(OracleDriver.class.getName());
    }

    public String test1() {
        try {
            Connection connection = dataSource.getConnection();
            Statement stat = connection.createStatement();
            stat.execute("select * from AREA where rownum < 10");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return "test1";
    }

    public String test2() {
        return "test2";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
