package meetingteam.teamservice.services.impls;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.services.CircuitBreakerFallbackHandler;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.configs.ServiceUrlConfig;
import meetingteam.teamservice.services.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends CircuitBreakerFallbackHandler implements ChatService {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Override
    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker", fallbackMethod="handleBodilessFallback")
    public void deleteMessagesByChannelId(String channelId) {
        String jwtToken= AuthUtil.getJwtToken();

        URI uri= UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.chatServiceUrl())
                .path("/message/private/channel/"+channelId)
                .build().toUri();

        restClient.delete()
                .uri(uri)
                .headers(h->h.setBearerAuth(jwtToken))
                .retrieve()
                .body(Void.class);
    }

    @Override
    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker", fallbackMethod="handleBodilessFallback")
    public void deleteMessagesByTeamId(String teamId) {
        String jwtToken= AuthUtil.getJwtToken();

        URI uri= UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.chatServiceUrl())
                .path("/message/private/team/"+teamId)
                .build().toUri();

        restClient.delete()
                .uri(uri)
                .headers(h->h.setBearerAuth(jwtToken))
                .retrieve()
                .body(Void.class);
    }
}
