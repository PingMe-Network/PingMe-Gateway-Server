package tea4life.gateway_server.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 21/04/2026, Tuesday
 **/
@Configuration
public class RateLimiterConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 5, 1);
    }

    @Bean
    public KeyResolver userIdKeyResolver() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    String[] chunks = token.split("\\.");
                    if (chunks.length >= 2) {
                        String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(payload);
                        if (node.has("id")) {
                            return Mono.just("USER_" + node.get("id").asText());
                        }
                    }
                } catch (Exception ignored) {
                    // Lỗi parse thì bỏ qua, lát lấy IP
                }
            }

            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "UNKNOWN_IP";
            return Mono.just("IP_" + ip);
        };
    }
}
