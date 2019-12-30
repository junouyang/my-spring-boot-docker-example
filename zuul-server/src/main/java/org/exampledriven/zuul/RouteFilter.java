package org.exampledriven.zuul;

import com.netflix.zuul.context.RequestContext;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientConnectionManagerFactory;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientFactory;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class RouteFilter extends ControllerSimpleHostRoutingFilter {
    private final ControllerHARequestHelper helper;
    private final SessionServerMapper sessionServerMapper;
    private static Logger logger = Logger.getLogger(RouteFilter.class.getName());

    public RouteFilter(ControllerHARequestHelper helper, SessionServerMapper sessionServerMapper) {
        super(helper, new ZuulProperties(), new DefaultApacheHttpClientConnectionManagerFactory(),
              new DefaultApacheHttpClientFactory());
        this.sessionServerMapper = sessionServerMapper;
        this.helper = helper;
    }

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        System.out.println("Inside Route Filter");
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
//        System.out.println("==============" + request.getHeaderNames());
//        System.out.println("==============" + request.getRequestURL());
//        System.out.println("==============" + request.getQueryString());
//        System.out.println("==============" + request.getRequestURI());
//        System.out.println("11111111111111111 ");
        String requestSession = request.getParameter("rqs");
        String responseSession = request.getParameter("rps");

        System.out.println("============== request session:" + requestSession);
        System.out.println("============== response session" + responseSession);
        URL routeHost = context.getRouteHost();
        if (request.getRequestURI().contains("controller")) {
            try {
                String host = request.getParameter("url");
                if (host == null) host = request.getRequestURL().toString();
                System.out.println("============== host: " + host);
                System.out.println("============== remote host: " + request.getRemoteHost());

                if (requestSession == null) {
                    ControllerHARequestHelper.parseSessionId(request.getHeader("cookie"));
                    System.out.printf("============= session id from cookie: %s", requestSession);
                } else {
                    requestSession = ControllerHARequestHelper.parseSessionId(
                            "LEFT_PANEL_MAXIMIZEDAPP_CONFIGURATION=false; JSESSIONID=" +
                                    requestSession +
                                    "; " +
                                    "X-CSRF-TOKEN=aba73181a367250d0f614e7e488f3d8453e531af; ");
                    System.out.printf("============= session id mocked: %s", requestSession);
                }
                sessionServerMapper.setRequestSession(requestSession);

                if (host != null) {
                    helper.addHeader("location", request.getRequestURL().toString() + request.getQueryString());
                    context.setRouteHost(new URL(host));
                    String path = request.getParameter("path");
                    if (path != null) {
                        context.set("requestURI", path);
                    }
                    context.setSendZuulResponse(false);
                }
                Object result = super.run();
                try {
                    String responseBody = IOUtils.toString(this.helper.getResponseStream(), "utf-8");
                    logger.severe(responseBody);
                    context.setResponseBody(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (sessionServerMapper.getResponseSession() == null) {
                    if (responseSession != null) {
                        sessionServerMapper.setResponseSession(responseSession);
                    } else {
                        sessionServerMapper.setResponseSession(requestSession);
                    }
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {
                context.setRouteHost(routeHost);
            }
        }
        return null;
    }
}
