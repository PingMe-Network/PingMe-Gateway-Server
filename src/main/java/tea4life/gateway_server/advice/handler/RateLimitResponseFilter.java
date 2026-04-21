package tea4life.gateway_server.advice.handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class RateLimitResponseFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> setComplete() {
                if (getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    String json = "{\"errorMessage\": \"Bạn thao tác quá nhanh. Vui lòng thử lại sau ít phút!\", \"errorCode\": 429}";
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

                    getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    getHeaders().setContentLength(bytes.length);

                    DataBuffer buffer = bufferFactory().wrap(bytes);

                    return super.writeWith(Mono.just(buffer));
                }

                return super.setComplete();
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
}