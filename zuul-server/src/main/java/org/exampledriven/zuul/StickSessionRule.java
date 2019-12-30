package org.exampledriven.zuul;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

public class StickSessionRule extends AbstractLoadBalancerRule {
    private SessionServerMapper sessionServerMapper;
    private int index = 0;

    public void setSessionServerMapper(SessionServerMapper sessionServerMapper) {
        this.sessionServerMapper = sessionServerMapper;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        List<Server> liveServers = getLoadBalancer().getReachableServers();

        String requestSessionId = sessionServerMapper.getRequestSession();
        if (requestSessionId != null) {
            Server server = sessionServerMapper.getServer(requestSessionId);
            if( server != null) {
                System.out.printf("3333333333 mapped server host: %s, host id:%s, port:%s", server.getHost(), server
                        .getId(), server.getPort());
            }
            if (server != null && liveServers.stream().anyMatch(s -> s.getId().equals(server.getId()))) {
                System.out.printf("3333333333 Use sticky session server: %s", requestSessionId);
                return server;
            }
        }

        if (liveServers != null && liveServers.size() > 0) {
            int nextIndex = index++ % liveServers.size();
            Server server = liveServers.get(nextIndex);
            sessionServerMapper.setServer(server);
            System.out.printf("3333333333 index: %s, host: %s, host id:%s, port:%s", index, server.getHost(),
                              server.getId(), server.getPort());
            return server;
        }
        return null;
    }
}