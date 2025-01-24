package meetingteam.teamservice.services.impls;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.configs.ServiceUrlConfig;
import meetingteam.commonlibrary.services.CircuitBreakerFallbackHandler;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.services.UserService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CircuitBreakerFallbackHandler implements UserService{
    private final ServiceUrlConfig serviceUrlConfig;
    private final RestClient restClient;

    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker")
    public List<ResUserDto> getUsersByIds(List<TeamMember> members) {
        String jwtToken= AuthUtil.getJwtToken();

        String userIdsStr=members.stream()
                .reduce(new StringBuilder(),
                        (sb, member) -> sb.append(member.getUserId()).append(','),
                        StringBuilder::append)
                .toString();
        userIdsStr=userIdsStr.substring(0,userIdsStr.length()-1);

        URI uri= UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.userServiceUrl())
                .path("/by-ids?ids="+userIdsStr)
                .build().toUri();

        return restClient.get()
                .uri(uri)
                .headers(h->h.setBearerAuth(jwtToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
