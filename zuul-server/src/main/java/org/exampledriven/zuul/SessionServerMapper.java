package org.exampledriven.zuul;

import com.netflix.loadbalancer.Server;

import java.util.HashMap;
import java.util.Map;

public class SessionServerMapper {
    private Map<String, Server> sessionServerMap = new HashMap<>();
    private ThreadLocal<String> threadRequestSession = new ThreadLocal<>();
    private ThreadLocal<String> threadResponseSession = new ThreadLocal<>();
    private ThreadLocal<Server> threadServer = new ThreadLocal<>();

    public void setRequestSession(String sessionId) {
        threadRequestSession.set(sessionId);
    }

    public String getRequestSession() {
        return threadRequestSession.get();
    }

    public void setResponseSession(String sessionId) {
        threadResponseSession.set(sessionId);
        map();
    }

    public String getResponseSession() {
        return threadResponseSession.get();
    }

    public void setServer(Server server) {
        threadServer.set(server);
        map();
    }

    private void map() {
        String responseSession = threadResponseSession.get();
        Server server = threadServer.get();
        if(responseSession != null && server !=null) {
            Server existingServer = sessionServerMap.get(responseSession);
            if(existingServer==null) {
                //            if (existingServer != null && !existingServer.getId().equals(server.getId())) {
                ////                throw new RuntimeException("Sessions conflict!");
                //                return;
                //            }
                sessionServerMap.put(responseSession, server);
                System.out.println("----------------- " + responseSession + "->" + server);
            }
        }
    }

    public Server getServer(String sessionId) {
        return sessionServerMap.get(sessionId);
    }
}
