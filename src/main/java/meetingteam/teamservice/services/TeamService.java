package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.Team.CreateTeamDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.Team.UpdateTeamDto;

import java.util.List;

public interface TeamService {
    void createTeam(CreateTeamDto teamDto);
    String updateTeam(UpdateTeamDto teamDto);
    List<ResTeamDto> getJoinedTeams();
}
