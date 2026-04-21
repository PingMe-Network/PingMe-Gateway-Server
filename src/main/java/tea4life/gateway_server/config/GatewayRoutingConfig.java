package tea4life.gateway_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 21/04/2026, Tuesday
 **/
@Configuration
public class GatewayRoutingConfig {

    @Value("${service.url.auth}")
    private String authServiceUrl;

    @Value("${service.url.core}")
    private String coreServiceUrl;

    @Value("${service.url.music}")
    private String musicServiceUrl;

    @Value("${service.url.reel}")
    private String reelServiceUrl;

    @Value("${service.url.utility}")
    private String utilityServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            RedisRateLimiter redisRateLimiter,
            KeyResolver userIdKeyResolver
    ) {
        return builder.routes()
                .route("auth-service-route", r -> r
                        .path("/auth-service/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userIdKeyResolver)
                                )
                        )
                        .uri(authServiceUrl)
                )
                .route("core-service-route", r -> r
                        .path("/core-service/**")
                        .filters(f -> f
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userIdKeyResolver)
                                )
                        )
                        .uri(coreServiceUrl)
                )
                .route("music-service-route", r -> r
                        .path("/music-service/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userIdKeyResolver)
                                )
                        )
                        .uri(musicServiceUrl)
                )
                .route("reel-service-route", r -> r
                        .path("/reel-service/**")
                        .uri(reelServiceUrl)
                )
                .route("utility-service-route", r -> r
                        .path("/api/admin/**", "/utility-service/**")
                        .uri(utilityServiceUrl)
                )
                .build();
    }
}
