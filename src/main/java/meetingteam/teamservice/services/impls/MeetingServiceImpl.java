package meetingteam.teamservice.services.impls;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.services.CircuitBreakerFallbackHandler;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.configs.ServiceUrlConfig;
import meetingteam.teamservice.services.MeetingService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl extends CircuitBreakerFallbackHandler implements MeetingService {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Override
    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker")
    public void deleteMeetingsByChannelId(String channelId) {
        String jwtToken= AuthUtil.getJwtToken();

        URI uri= UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.meetingServiceUrl())
                .path("/meeting/private/channel/"+channelId)
                .build().toUri();

        restClient.delete()
                .uri(uri)
                .headers(h->h.setBearerAuth(jwtToken))
                .retrieve()
                .body(Void.class);
    }
}
