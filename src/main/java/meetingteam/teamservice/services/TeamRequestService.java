package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.TeamRequest.CreateTeamRequestDto;
import meetingteam.teamservice.dtos.TeamRequest.ResTeamRequestDto;

import java.util.List;

public interface TeamRequestService {
    String requestToJoinTeam(CreateTeamRequestDto requestDto);
    void acceptNewMember(String requestId, boolean isAccepted);
    void deleteTeamRequest(String requestId);
    List<ResTeamRequestDto> getTeamRequestMessages(String teamId);
    List<ResTeamRequestDto> getSendedRequestMessages();
}
