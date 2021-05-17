package com.sentry.boot.demo.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import oracle.jdbc.driver.OracleDriver;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DemoService {

    private DruidDataSource dataSource;

    {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@1.1.1.1:1521:orcl");
        dataSource.setUsername("aaa");
        dataSource.setPassword("bbb");
        dataSource.setDriverClassName(OracleDriver.class.getName());
    }

    List<String> sqls = new ArrayList<String>(){{
        add("select * from AREA where rownum < 2");
        add("select * from AREA where rownum = 1");
        add("select * from AREA where name = " + System.lineSeparator() + "'天长市'");
    }};

    public ResultSet test() {
        try {
            Random random = new Random();

            DruidPooledConnection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery(sqls.get(random.nextInt(3)));
            statement.execute(sqls.get(random.nextInt(3)));
            statement.executeUpdate(sqls.get(random.nextInt(3)));
            return r;
        } catch (Exception ignore) {
            return null;
        }
    }
}
