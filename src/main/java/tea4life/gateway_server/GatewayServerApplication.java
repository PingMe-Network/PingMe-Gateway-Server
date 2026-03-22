package tea4life.gateway_server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServerApplication {
    @Value("${service.url.auth}")
    private String authServiceUrl;
    @Value("${service.url.core}")
    private String coreServiceUrl;
    @Value("${service.url.music}")
    private String musicServiceUrl;
    @Value("${service.url.reel}")
    private String reelServiceUrl;

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-service-route", r -> r
                        .path("/auth-service/**")
                        .uri(authServiceUrl)
                )

                .route("core-service-route", r -> r
                        .path("/core-service/**")
                        .filters(f -> f
                                // Vũ khí hạng nặng: Xóa các header CORS bị nhân đôi từ Core Service (SockJS)
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                        )
                        .uri(coreServiceUrl)
                )

                .route("music-service-route", r -> r
                        .path("/music-service/**")
                        .uri(musicServiceUrl)
                )

                .route("reel-service-route", r -> r
                        .path("/reel-service/**")
                        .uri(reelServiceUrl)
                )

                .build();
    }
}
