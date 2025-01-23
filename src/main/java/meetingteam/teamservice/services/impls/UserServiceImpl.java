package meetingteam.teamservice.services.impls;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.configs.ServiceUrlConfig;
import meetingteam.commonlibrary.services.CircuitBreakerFallbackHandler;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.services.UserService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CircuitBreakerFallbackHandler implements UserService{
    private final ServiceUrlConfig serviceUrlConfig;
    private final RestClient restClient;

    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker")
    public List<ResUserDto> getUsersByIds(List<String> userIds) {
        String jwtToken= AuthUtil.getJwtToken();
        String userIdsStr= userIds.toString().replace("[","").replace("]","");
        URI uri= UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.userServiceUrl())
                .path("/by-ids?ids="+userIdsStr)
                .build().toUri();
        return restClient.get()
                .uri(uri)
                .headers(h->h.setBearerAuth(jwtToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ResUserDto>>() {});
    }
}
