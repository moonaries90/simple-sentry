package com.sentry.demo.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mysql.jdbc.Driver;
import oracle.jdbc.OracleDriver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DemoService {

    private final DruidDataSource dataSource, mysqlDataSource;

    {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@1.1.1.1:1521:orcl");
        dataSource.setUsername("aaa");
        dataSource.setPassword("bbb");
        dataSource.setDriverClassName(OracleDriver.class.getName());

        mysqlDataSource = new DruidDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://localhost:3306/xxl_job?connectTimeout=3000&socketTimeout=60000&characterEncoding=utf-8&useSSL=false&&serverTimezone=Hongkong");
        mysqlDataSource.setUsername("aaa");
        mysqlDataSource.setPassword("bbb");
        mysqlDataSource.setDriverClassName(Driver.class.getName());
    }

    List<String> sqls = new ArrayList<String>(){{
        add("select * from AREA where rownum < 2");
        add("select * from AREA where rownum = 1");
        add("select * from AREA where name = " + System.lineSeparator() + "'天长市'");
    }};

    List<String> mysqlSqls = new ArrayList<String>(){{
        add("select count(1) from xxl_job_info");
        add("select * from xxl_job_user");
        add("select * from xxl_job_group");

    }};

    public ResultSet test() throws SQLException {

        DruidPooledConnection connection = null;
        try {
            Random random = new Random();
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(sqls.get(random.nextInt(3)));
            statement.executeQuery(sqls.get(random.nextInt(3)));
            statement.executeUpdate(sqls.get(random.nextInt(3)));
            PreparedStatement prepareStatement = connection.prepareStatement(sqls.get(random.nextInt(3)));
            ResultSet r = prepareStatement.executeQuery();
            prepareStatement = connection.prepareStatement(sqls.get(random.nextInt(3)));
            prepareStatement.execute();
            prepareStatement = connection.prepareStatement(sqls.get(random.nextInt(3)));
            prepareStatement.executeUpdate();
            return r;
        } catch (Exception ignore) {
            return null;
        } finally {
            if(connection != null) {
                connection.commit();
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public ResultSet testMysql() throws SQLException {
        DruidPooledConnection connection = null;
        try {
            Random random = new Random();

            connection = mysqlDataSource.getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(mysqlSqls.get(random.nextInt(3)));
            statement.executeQuery(mysqlSqls.get(random.nextInt(3)));
            statement.executeUpdate("update xxl_job_info set author = \\\"auto\\\" where id < 1000000");
            PreparedStatement prepareStatement = connection.prepareStatement(mysqlSqls.get(random.nextInt(3)));
            ResultSet r = prepareStatement.executeQuery();
            prepareStatement = connection.prepareStatement(mysqlSqls.get(random.nextInt(3)));
            prepareStatement.execute();
            prepareStatement = connection.prepareStatement("update xxl_job_info set author = \"auto\" where id < 1000000");
            prepareStatement.executeUpdate();
            return r;
        } catch (Throwable ignore) {
            System.out.println(ignore.getMessage());
        } finally {
            if(connection != null) {
                connection.commit();
                connection.setAutoCommit(true);
                connection.close();
            }
        }
        return null;
    }
}
