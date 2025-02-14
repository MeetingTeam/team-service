package meetingteam.teamservice.converters;

import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.dtos.Channel.ResChannelDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.models.Team;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamConverter {
    private final ModelMapper modelMapper;

    public ResTeamDto toDto(Team team) {
        var dto=new ResTeamDto();
        dto.setId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setUrlIcon(team.getUrlIcon());
        dto.setAutoAddMember(team.getAutoAddMember());
        if(team.getChannels()!=null){
            var channelDtos= team.getChannels().stream()
                    .map(channel->modelMapper.map(channel, ResChannelDto.class))
                    .toList();
            dto.setChannels(channelDtos);
        }
        return dto;
    }

    public List<ResTeamDto> toDtos(List<Team> teams) {
        return teams.stream()
                .map(team->toDto(team))
                .toList();
    }
}
