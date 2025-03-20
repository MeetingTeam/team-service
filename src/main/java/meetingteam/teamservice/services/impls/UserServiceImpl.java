package meetingteam.teamservice.services.impls;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.configs.ServiceUrlConfig;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.services.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final ServiceUrlConfig serviceUrlConfig;
    private final RestClient restClient;
    private final ModelMapper modelMapper;

    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker")
    public List<ResUserDto> getUsersByIds(List<String> userIds) {
        String jwtToken= AuthUtil.getJwtToken();

        URI uri= UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.userServiceUrl())
                .path("/user/private/by-ids")
                .build().toUri();

        return restClient.post()
                .uri(uri)
                .headers(h->h.setBearerAuth(jwtToken))
                .body(userIds)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Retry(name="restApi")
    @CircuitBreaker(name="restCircuitBreaker")
    public List<ResTeamMemberDto> fetchUsersData(List<String> userIds, List<TeamMember> members){
        if(userIds==null||userIds.isEmpty()) return new ArrayList();

        List<ResUserDto> userDtos= getUsersByIds(userIds);

        var userDtosMap= new HashMap<String,ResUserDto>();
        userDtos.forEach(userDto->userDtosMap.put(userDto.getId(), userDto));

        var resMemberDtos=new ArrayList<ResTeamMemberDto>();
        for(var member: members){
            var resMemberDto= new ResTeamMemberDto(
                    modelMapper.map(userDtosMap.get(member.getUserId()), ResUserDto.class),
                    member.getRole()
            );
            resMemberDtos.add(resMemberDto);
        }
        return resMemberDtos;
    }
}
