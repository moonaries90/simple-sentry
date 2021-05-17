package com.sentry.demo;

import com.sentry.demo.service.CustomService;
import com.sentry.demo.service.DemoService;
import com.sentry.demo.service.HttpService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

public class DemoServlet implements Servlet {

    private CustomService customService = new CustomService();

    private DemoService demoService = new DemoService();

    private HttpService httpService = new HttpService();

    private Random random = new Random();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            customService.testCustom();
            demoService.testMysql();
            demoService.test();
            String str = httpService.testClient();
            response.getWriter().write(String.format("thanks for visiting %s ...\n, response is %s", request.getRequestURI(), str) + "\r\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
