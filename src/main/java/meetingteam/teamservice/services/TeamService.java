package meetingteam.teamservice.services;

import meetingteam.commonlibrary.dtos.PagedResponseDto;
import meetingteam.teamservice.dtos.Team.CreateTeamDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.Team.UpdateTeamDto;

public interface TeamService {
    ResTeamDto createTeam(CreateTeamDto teamDto);
    void updateTeam(UpdateTeamDto teamDto);
    void deleteTeam(String teamId);
    PagedResponseDto<ResTeamDto> getJoinedTeams(Integer pageNo, Integer pageSize);
}
