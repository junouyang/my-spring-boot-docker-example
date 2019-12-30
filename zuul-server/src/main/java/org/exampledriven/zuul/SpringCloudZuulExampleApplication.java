package org.exampledriven.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@SpringBootApplication
public class SpringCloudZuulExampleApplication {

	private final SessionServerMapper sessionServerMapper;

	public SpringCloudZuulExampleApplication() {
		this.sessionServerMapper = new SessionServerMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudZuulExampleApplication.class, args);
	}

	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter(new ControllerHARequestHelper(sessionServerMapper), sessionServerMapper);
	}

	@Bean
	public StickSessionRule createStickySessionRule() {
		StickSessionRule stickSessionRule = new StickSessionRule();
		stickSessionRule.setSessionServerMapper(sessionServerMapper);
		return stickSessionRule;
	}
}
